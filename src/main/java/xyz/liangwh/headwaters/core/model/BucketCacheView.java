package xyz.liangwh.headwaters.core.model;

import lombok.Data;

/**
 * Bucket 展示属性对象
 */
@Data
public class BucketCacheView {

    private String gid;
    private String key;
    private int step;
    private int autoStep;
    private int currentBucketIndex;
    private boolean nextReady;
    private boolean backupThreadRunning;
    private boolean initStatus;
    private int idle;
    private long currentValue;
    private int currentInsideValue;
    private long max;
    private int inside;

}
