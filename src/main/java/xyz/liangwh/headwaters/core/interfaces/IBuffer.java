package xyz.liangwh.headwaters.core.interfaces;

import lombok.Data;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
@Data
public abstract class IBuffer <T extends IBucket>{
    protected int id;
    protected String key;
    protected T[] buckets ;
    protected volatile int currentBucket = 0;//当前使用中的bucket下标
    protected volatile boolean nextReady = false;//备用bucket是否可用
    protected volatile boolean initStatus = false;//是否完成初始化
    //异步线程是否运行中
    protected final AtomicBoolean backupThreadRunning = new AtomicBoolean(false);
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();
    protected volatile int step;
    protected volatile int autoStep;
    protected volatile long updateTs=0;

    public abstract T getCurrent();
    public abstract int getNextIndex();
    public abstract void checkoutCurrent();


}
