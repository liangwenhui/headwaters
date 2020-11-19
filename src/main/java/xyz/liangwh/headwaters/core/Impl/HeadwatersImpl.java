package xyz.liangwh.headwaters.core.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import xyz.liangwh.headwaters.core.AbstractHeadwaters;
import xyz.liangwh.headwaters.core.dao.HwMarkRedisDao;
import xyz.liangwh.headwaters.core.exception.HedisError;
import xyz.liangwh.headwaters.core.exception.HedisException;
import xyz.liangwh.headwaters.core.interfaces.Monitor;
import xyz.liangwh.headwaters.core.model.Bucket;
import xyz.liangwh.headwaters.core.model.BucketBuffer;
import xyz.liangwh.headwaters.core.model.BucketCacheView;
import xyz.liangwh.headwaters.core.model.HeadwatersPo;
import xyz.liangwh.headwaters.core.model.HwMarkSamplePo;
import xyz.liangwh.headwaters.core.model.Result;

@Service
@Slf4j
public class HeadwatersImpl extends AbstractHeadwaters<BucketBuffer, Bucket> implements Monitor {

    @Autowired
    private HwMarkRedisDao hwMarkDao;

    @PostConstruct
    @Override
    public void init() {
        super.init();
    }

    @Override
    public void updateCache() {
        log.info("update cache from redis");
        StopWatch stopWatch = new Slf4JStopWatch();
        try {
            List<HwMarkSamplePo> dbHws = hwMarkDao.getAllKeyMap();
            if (dbHws == null || dbHws.isEmpty()) {
                return;
            }
            List<String> dbList = dbHws.stream().map(HwMarkSamplePo::getKey).collect(Collectors.toList());
            List<String> cacheKeys = new ArrayList<>(cache.keySet());
            Set<String> addSet = dbHws.stream().map(HwMarkSamplePo::getKey).collect(Collectors.toSet());
            Set<String> removeSet = new HashSet<>(cache.keySet());
            Map<String, String> keyIdMap = new HashMap<>();
            dbHws.stream().forEach(o -> {
                keyIdMap.put(o.getKey(), o.getGid());
            });

            String tmp;
            for (int i = 0; i < cacheKeys.size(); i++) {
                tmp = cacheKeys.get(i);
                if (addSet.contains(tmp)) {
                    addSet.remove(tmp);
                }
            }
            for (String key : addSet) {
                BucketBuffer buffer = new BucketBuffer();
                buffer.setGid(keyIdMap.get(key));
                buffer.setKey(key);
                cache.put(key, buffer);
                log.info("[updateCache] add buckerbuffer {}", buffer);
            }
            for (int i = 0; i < dbList.size(); i++) {
                tmp = dbList.get(i);
                if (removeSet.contains(tmp)) {
                    removeSet.remove(tmp);
                }
            }
            for (String key : removeSet) {
                cache.remove(key);
                log.info("[updateCache] remove buckerbuffer key:{}", key);

            }
        }
        catch (Exception e) {
            log.error("update cache from redis  faild!", e);
        }
        finally {
            stopWatch.stop("updateCache");
        }

    }

    private void insterCache(HeadwatersPo po){
        BucketBuffer buffer = new BucketBuffer();
        buffer.setGid(po.getGid());
        buffer.setKey(po.getKey());
        cache.put(po.getKey(), buffer);
        log.info("[insterCache] add buckerbuffer {}", buffer);
    }


    // public long makeTrueId(int keyId,Object arg){
    // return IdUtils.makeTrueId(keyId,(Integer) arg);
    // }

    public Result getIdFromBucketBuffer(final String key) {
        final BucketBuffer bb = cache.get(key);
        while (true) {
            bb.getLock().readLock().lock();
            try {
                final Bucket bucket = bb.getCurrent();
                // 异步初始化刷新备用水桶
                if ((!bb.isNextReady()) && (bucket.getIdle() < 0.8 * bucket.getStep())
                    && bb.getBackupThreadRunning().compareAndSet(false, true)) {
                    service.execute(() -> {
                        Bucket next = bb.getBuckets()[bb.getNextIndex()];
                        boolean flag = false;
                        try {
                            updateBucket(bb.getKey(), next);
                            flag = true;
                        }
                        catch (Exception e) {
                            log.error("异步更新[{}]失败", key);
                        }
                        finally {
                            if (flag) {
                                bb.getLock().writeLock().lock();
                                bb.setNextReady(true);
                                bb.getBackupThreadRunning().set(false);
                                bb.getLock().writeLock().unlock();
                            }
                            else {
                                bb.getBackupThreadRunning().set(false);
                            }
                        }
                    });
                }
                // long value = IdUtils.makeTrueId(0,bucket.getValue().getAndIncrement());
                long value = bucket.getValue().getAndIncrement();
                if (value < bucket.getMax()) {
                    return new Result(RESULT_OK, value);
                }
            }
            finally {
                bb.getLock().readLock().unlock();
            }
            // 上方获取不到id，表示id消费的太快了，等待异步线程初始化
            waitSomeTime(bb);
            bb.getLock().writeLock().lock();
            try {
                final Bucket bucket = bb.getCurrent();
                // long value = IdUtils.makeTrueId(0,bucket.getValue().getAndIncrement());
                long value = bucket.getValue().getAndIncrement();
                if (value < bucket.getMax()) {
                    return new Result(RESULT_OK, value);
                }
                if (bb.isNextReady()) {
                    bb.checkoutCurrent();
                    bb.setNextReady(false);
                }
                else {
                    log.error("boths bucket in {} are not ready to use!", bb.getKey());
                    return new Result(EXCEPTION_ID_BOTH_BUCKET_NULL, null);
                }
            }
            finally {
                bb.getLock().writeLock().unlock();
            }
        }

    }

    public void updateBucket(final String key, Bucket bucket) throws HedisException {
        StopWatch stopWatch = new Slf4JStopWatch();
        BucketBuffer bb = (BucketBuffer) bucket.getParent();

        try {
            HeadwatersPo po;
            if (!bb.isInitStatus()) {
                po = hwMarkDao.updateAndGetHeadwaters(bb.getMax(),key);
                bb.setStep(po.getStep());
                bb.setAutoStep(po.getStep());
                bb.setUpdateTs(System.currentTimeMillis());
                bb.setMax(po.getInsideId());
            }
            else {
                long duration = System.currentTimeMillis() - bb.getUpdateTs();
                int autoStep = bb.getAutoStep();
                if (duration < BUCKET_DURATION) {
                    if ((autoStep << 1) <= MAX_STEP) {
                        autoStep <<= 1;
                    }
                }
                else {
                    if ((autoStep >> 1) >= bb.getStep()) {
                        autoStep >>= 1;
                    }
                }
                log.info("key[{}],step[{}],autoStep[{}],duration[{}ms]", key, bb.getStep(), autoStep, duration);
                bb.setAutoStep(autoStep);
                po = hwMarkDao.updateAutoAndGetHeadwaters(bb.getMax(),key, autoStep);
                bb.setUpdateTs(System.currentTimeMillis());
                bb.setStep(po.getStep());
                bb.setMax(po.getInsideId());
            }
            int value = po.getInsideId() - bb.getAutoStep() + 1;
            bucket.getValue().set(value);
            bucket.setInside(po.getInsideId());
            bucket.setMax(bucket.getInside());
            bucket.setStep(bb.getAutoStep());
        }
        catch (Exception e) {
            log.error("redis申请id异常", e);
            throw new HedisException(HedisError.REDIS_APPLY_ID_ERROR, "redis apply id failed ", e);
        }
        finally {
            stopWatch.stop("updateBucket");
        }
    }

    private void updateFromFile(){

    }

    @Override
    public Map getInfo() {
        Map res = new HashMap();
//        if (cache == null || cache.size() == 0) {
//            return null;
//        }
//        Set<String> keySet = cache.keySet();
//        for (String key : keySet) {
//            BucketCacheView view = new BucketCacheView();
//            view.setKey(key);
//            BucketBuffer bucketBuffer = cache.get(key);
//            view.setGid(bucketBuffer.getGid());
//            view.setStep(bucketBuffer.getStep());
//            view.setAutoStep(bucketBuffer.getAutoStep());
//            view.setCurrentBucketIndex(bucketBuffer.getCurrentBucket());
//            view.setNextReady(bucketBuffer.isNextReady());
//            view.setBackupThreadRunning(bucketBuffer.getBackupThreadRunning().get());
//            view.setInitStatus(bucketBuffer.isInitStatus());
//            Bucket current = bucketBuffer.getCurrent();
//            view.setIdle(current.getIdle());
//
//            // view.setCurrentValue(makeTrueId(0,current.getValue().get()));
//            view.setCurrentValue(current.getValue().get());
//            view.setCurrentInsideValue(current.getValue().get());
//            view.setMax(current.getMax());
//            view.setInside(current.getInside());
//            res.put(key, view);
//        }
        return res;
    }

    @Override
    protected Result nullStrategy(String key) throws HedisException {
        HeadwatersPo po = hwMarkDao.updateAndGetHeadwaters(0,key);
        insterCache(po);//updateCache();
        return super.getId(key);
    }

}
