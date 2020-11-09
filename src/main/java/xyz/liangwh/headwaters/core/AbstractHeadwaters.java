package xyz.liangwh.headwaters.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;
import xyz.liangwh.headwaters.core.exception.HedisException;
import xyz.liangwh.headwaters.core.interfaces.IBucket;
import xyz.liangwh.headwaters.core.interfaces.IBuffer;
import xyz.liangwh.headwaters.core.interfaces.IDGenerator;
import xyz.liangwh.headwaters.core.interfaces.NeadInit;
import xyz.liangwh.headwaters.core.model.Result;

/**
 * 抽象的Headwaters序列容器，实现了IDGenerator，NeadInit接口 定义了返回状态码，应用最大步长，动态步长计算阈值 注册的IBuffer是否可用等熟悉 定义了容器初始化标准流程 定义了获取序列的标准流程
 * 子类需要实现抽象容器的钩子函数
 * 
 * @see IDGenerator
 * @see NeadInit
 * @param <T>
 * @param <Y>
 */
@Slf4j
public abstract class AbstractHeadwaters<T extends IBuffer, Y extends IBucket> implements IDGenerator, NeadInit {
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
    public static final int EXCEPTION_ID_BOTH_BUCKET_NULL = -202;

    /**
     * 最大步长
     */
    protected static final int MAX_STEP = 200_0000;

    /**
     * 一个BUCKET使用的持续时间，用于修改动态步长
     */
    protected static final long BUCKET_DURATION = 10 * 50 * 1000L;

    protected volatile boolean initStatus = false;

    protected Map<String, T> cache = new ConcurrentHashMap<>();

    protected Object olock = new Object();

    protected ExecutorService service = new ThreadPoolExecutor(3, 10, 120L, TimeUnit.SECONDS,
        new SynchronousQueue<Runnable>(), new UpdateHeadwaterTheadFactory());

    public static class UpdateHeadwaterTheadFactory implements ThreadFactory {
        private static AtomicInteger times = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread-Update-Headwaters-Bucket-" + times.incrementAndGet());
        }
    }

    public static class UpdateRegularlyTheadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "Thread-Regularly-Update-Headwaters-Bucket");
            thread.setDaemon(true);
            return thread;
        }
    }

    /**
     * 初始化-模板方法
     */
    @Override
    public void init() {
        log.info("init...");
        updateCache();
        this.initStatus = true;
        // 定时更新 集群方式才需要
        //updateRegularly();
    }

    /**
     * 定时更新任务设置
     */
    protected void updateRegularly() {
        // 定时执行 完成后间隔一分钟执行
        ScheduledExecutorService service = Executors
            .newSingleThreadScheduledExecutor(new UpdateRegularlyTheadFactory());
        service.scheduleWithFixedDelay(() -> {
            updateCache();
        }, 60, 60, TimeUnit.SECONDS);
    }

    /**
     * 钩子函数-更新id缓存池
     */
    protected abstract void updateCache();

    /**
     * 模板方法-获取id
     * 
     * @param key
     * @return
     */
    @Override
    public Result getId(final String key) {
        if (!this.initStatus) {
            return new Result(EXCEPTION_ID_CACHE_INIT_FALSE, null);
        }
        if (cache.containsKey(key)) {
            T bb = cache.get(key);
            if (!bb.isInitStatus()) {
                synchronized (bb) {
                    if (!bb.isInitStatus()) {
                        try {
                            updateBucket(key, (Y) bb.getCurrent());
                            log.info("init BucketBuffer,update key {} {}", key, bb.getCurrent());
                            bb.setInitStatus(true);
                        }
                        catch (Exception e) {
                            log.error("init BucketBuffer faild,key = {}", key, e);
                        }
                    }
                }
            }
            return getIdFromBucketBuffer(key);
        }
        else {
            synchronized (olock) {
                if (!cache.containsKey(key)) {
                    return nullStrategy(key);
                }
                else {
                    return getId(key);
                }
            }
        }

    }

    protected Result nullStrategy(String key) {
        return new Result(EXCEPTION_ID_KEY_NOT_EXISTS, null);
    }

    /**
     * 钩子函数-从id资源池中获取id 如果备用buffer未初始化成功，则异步初始化备用buffer 如果id消耗过快，则自旋等待，直到能获取id
     * 
     * @param key
     * @return
     */
    protected abstract Result getIdFromBucketBuffer(final String key);

    // protected abstract long makeTrueId(int keyId,Object arg);

    /**
     * 自旋等待
     * 
     * @param bb
     */
    protected void waitSomeTime(T bb) {
        int times = 0;
        while (bb.getBackupThreadRunning().get()) {
            // System.out.println("waitSomeTime");
            times++;
            if (times < 3000) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                }
                catch (InterruptedException e) {
                    log.warn("Thread {} Interrupted", Thread.currentThread().getName());
                    break;
                }
            }
            else if (times < 10000) {
                try {
                    TimeUnit.MILLISECONDS.sleep(20);
                }
                catch (InterruptedException e) {
                    log.warn("Thread {} Interrupted", Thread.currentThread().getName());
                    break;
                }
            }
            else {
                break;
            }
        }
    }

    /**
     * 钩子函数-更新id buffer
     * 
     * @param key
     * @param bucket
     * @throws Exception
     */
    protected abstract void updateBucket(final String key, Y bucket) throws HedisException;
}
