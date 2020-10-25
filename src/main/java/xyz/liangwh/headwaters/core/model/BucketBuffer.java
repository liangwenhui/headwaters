package xyz.liangwh.headwaters.core.model;

import lombok.Data;
import xyz.liangwh.headwaters.core.interfaces.IBuffer;

/**
 * IBuffer实现类，双重缓存Buckets
 * 通过取模方式，循环更新使用Bucket
 *
 * @see Bucket
 * @see IBuffer
 * @author liangwh
 */
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

    /**
     * 获取当前值班的bucket
     * @return
     */
    @Override
    public Bucket getCurrent(){
        return buckets[currentBucket];
    }

    /**
     * 获取下任bucket的下标，通过取模的方式获取
     * @return
     */
    @Override
    public int getNextIndex(){
        return (currentBucket+1) % 2;
    }

    /**
     * 切换值班bucket
     */
    @Override
    public void checkoutCurrent(){
        currentBucket = getNextIndex();
    }

    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("").append(this.gid);
        sb.append("").append(this.initStatus);
        sb.append("").append(this.updateTs);
        sb.append("").append(this.key);
        sb.append("").append(this.autoStep);
        sb.append("").append(this.step);
        sb.append("}");
        return sb.toString();

    }

}
