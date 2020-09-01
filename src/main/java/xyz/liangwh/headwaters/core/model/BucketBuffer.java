package xyz.liangwh.headwaters.core.model;

import lombok.Data;
import lombok.ToString;
import xyz.liangwh.headwaters.core.interfaces.IBucket;
import xyz.liangwh.headwaters.core.interfaces.IBuffer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
@Data
public class BucketBuffer extends IBuffer<Bucket> {

    public BucketBuffer(){
        buckets = new Bucket[2];
        Bucket b1 = new Bucket();
        b1.setParent(this);
        Bucket b2 = new Bucket();
        b2.setParent(this);
        buckets[0] = b1;
        buckets[1] = b2;
    }
    @Override
    public Bucket getCurrent(){
        return buckets[currentBucket];
    }
    @Override
    public int getNextIndex(){
        return (currentBucket+1) % 2;
    }
    @Override
    public void checkoutCurrent(){
        currentBucket = getNextIndex();
    }

    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("").append(this.id);
        sb.append("").append(this.initStatus);
        sb.append("").append(this.updateTs);
        sb.append("").append(this.key);
        sb.append("").append(this.autoStep);
        sb.append("").append(this.step);
        sb.append("}");
        return sb.toString();

    }

}
