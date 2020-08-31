package xyz.liangwh.headwaters.core.impl;

import lombok.extern.slf4j.Slf4j;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.liangwh.headwaters.core.AbstractHeadwaters;
import xyz.liangwh.headwaters.core.dao.HwMarkDao;
import xyz.liangwh.headwaters.core.model.Bucket;
import xyz.liangwh.headwaters.core.model.BucketBuffer;
import xyz.liangwh.headwaters.core.model.HwMarkSamplePo;
import xyz.liangwh.headwaters.core.model.Result;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class DiruptorHeadWatersImpl extends AbstractHeadwaters {


    @Autowired
    private HwMarkDao hwMarkDao;



    @PostConstruct
    @Override
    public void init() {
        super.init();
    }


    @Override
    protected void updateCache() {
        log.info("update cache from db hw_mark");
        StopWatch stopWatch = new Slf4JStopWatch();
        try {
            List<HwMarkSamplePo> dbHws = hwMarkDao.getAllKeyMap();
            if(dbHws==null||dbHws.isEmpty()){
                return;
            }
            List<String> dbList = dbHws.stream().map(HwMarkSamplePo::getKey).collect(Collectors.toList());
            List<String> cacheKeys = new ArrayList<>(cache.keySet());
            Set<String> addSet = dbHws.stream().map(HwMarkSamplePo::getKey).collect(Collectors.toSet());
            Set<String> removeSet = new HashSet<>(cache.keySet());
            Map<String,Integer> keyIdMap = new HashMap<>();
            dbHws.stream().forEach(o->{
                keyIdMap.put(o.getKey(),o.getId());
            });

            String tmp ;
            for(int i=0;i<cacheKeys.size();i++){
                tmp = cacheKeys.get(i);
                if(addSet.contains(tmp)){
                    addSet.remove(tmp);
                }
            }
            for (String key:addSet){
                BucketBuffer buffer = new BucketBuffer();
                buffer.setId(keyIdMap.get(key));
                buffer.setKey(key);
                cache.put(key, buffer);
                log.info("[updateCache] add buckerbuffer {}",buffer);
            }
            for(int i=0;i<dbList.size();i++){
                tmp = dbList.get(i);
                if(removeSet.contains(tmp)){
                    removeSet.remove(tmp);
                }
            }
            for(String key:removeSet){
                cache.remove(key);
                log.info("[updateCache] remove buckerbuffer key:{}",key);

            }
        }catch (Exception e){
            log.error("update cache from db hw_mark faild!",e);
        }finally {
            stopWatch.stop("updateCache");
        }

    }

    @Override
    protected Result getIdFromBucketBuffer(String key) {
        return null;
    }

    @Override
    protected void updateBucket(String key, Bucket bucket) throws Exception {

    }


    @Override
    public Result getId(final String key) {
        if(!this.initStatus){
            return new Result(EXCEPTION_ID_CACHE_INIT_FALSE,null);
        }
        if(false){


        }
        else {
            return new Result(EXCEPTION_ID_KEY_NOT_EXISTS,null);
        }
        return null;
    }
}
