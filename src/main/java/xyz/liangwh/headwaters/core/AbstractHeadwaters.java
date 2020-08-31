package xyz.liangwh.headwaters.core;

import lombok.extern.slf4j.Slf4j;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.liangwh.headwaters.core.dao.HwMarkDao;
import xyz.liangwh.headwaters.core.interfaces.IDGenerator;
import xyz.liangwh.headwaters.core.interfaces.NeadInit;
import xyz.liangwh.headwaters.core.model.Bucket;
import xyz.liangwh.headwaters.core.model.BucketBuffer;
import xyz.liangwh.headwaters.core.model.HeadwatersPo;
import xyz.liangwh.headwaters.core.model.HwMarkSamplePo;
import xyz.liangwh.headwaters.core.model.Result;
import xyz.liangwh.headwaters.core.utils.IdUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Slf4j
public abstract class AbstractHeadwaters implements IDGenerator, NeadInit {
    /**
     * cache 未初始化完成标识
     */
    public static final int RESULT_OK = 200;
    /**
     * cache 未初始化完成标识
     */
    public static final int EXCEPTION_ID_CACHE_INIT_FALSE = -101;
    /**
     * key不存在标识
     */
    public static final int EXCEPTION_ID_KEY_NOT_EXISTS = 404;
    /**
     * 两个桶都为初始化完成
     */
    public static final  int EXCEPTION_ID_BOTH_BUCKET_NULL = -202;
    /**
     * 最大步长
     */
    protected static final int MAX_STEP = 1000000;
    /**
     * 一个BUCKET使用的持续时间，用于修改动态步长
     */
    protected static final long BUCKET_DURATION = 10*50*1000L;

    protected volatile boolean initStatus = false;

    protected Map<String, BucketBuffer> cache = new ConcurrentHashMap<>();


    protected ExecutorService service = new ThreadPoolExecutor(
            5,
            10,
            120L,
            TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(),
            new UpdateHeadwaterTheadFactory());

    public static class UpdateHeadwaterTheadFactory implements ThreadFactory{
        private static AtomicInteger times = new AtomicInteger();
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"Thread-Update-Headwaters-Bucket-"+times.incrementAndGet());
        }
    }
    public static class UpdateRegularlyTheadFactory implements ThreadFactory{

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "Thread-Regularly-Update-Headwaters-Bucket");
            thread.setDaemon(true);
            return thread;
        }
    }


    /**
     * 模板方法-初始化
     */
    @Override
    public void init() {
        log.info("init...");
        updateCache();
        this.initStatus = true;
        //定时更新
         updateRegularly();
    }
    protected void updateRegularly(){
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(new UpdateRegularlyTheadFactory());
        service.scheduleWithFixedDelay(()->{updateCache();},60,60,TimeUnit.SECONDS);
    }

    /**
     * 钩子函数-更新缓存池
     */
    protected abstract void updateCache();


    @Override
    public Result getId(final String key) {
        if(!this.initStatus){
            return new Result(EXCEPTION_ID_CACHE_INIT_FALSE,null);
        }
        if(cache.containsKey(key)){
            BucketBuffer bb = cache.get(key);
            if(!bb.isInitStatus()){
                synchronized (bb){
                    if(!bb.isInitStatus()){
                        try {
                            updateBucket(key,bb.getCurrent());
                            log.info("init BucketBuffer,update key {} {}",key,bb.getCurrent());
                            bb.setInitStatus(true);
                        }catch (Exception e){
                            log.error("init BucketBuffer faild,key = {}",key,e);
                        }
                    }
                }
            }
            return getIdFromBucketBuffer(key);
        }else {
            return new Result(EXCEPTION_ID_KEY_NOT_EXISTS,null);
        }

    }

    /**
     * 钩子函数-获取id
     * @param key
     * @return
     */
    protected abstract Result getIdFromBucketBuffer(final String key);

    protected void  waitSomeTime(BucketBuffer bb){
        int times = 0;
        while (bb.getBackupThreadRunning().get())
        {
            times++;
            if(times<3000){
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    log.warn("Thread {} Interrupted",Thread.currentThread().getName());
                    break;
                }
            }else if(times<10000){
                try {
                    TimeUnit.MILLISECONDS.sleep(20);
                } catch (InterruptedException e) {
                    log.warn("Thread {} Interrupted",Thread.currentThread().getName());
                    break;
                }
            }else{
                break;
            }
        }
    }

    /**
     * 钩子函数-更新bucket
     * @param key
     * @param bucket
     * @throws Exception
     */
    protected abstract void updateBucket (final String key,Bucket bucket) throws Exception;
}
