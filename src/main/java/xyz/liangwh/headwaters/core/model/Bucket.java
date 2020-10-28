package xyz.liangwh.headwaters.core.model;

import lombok.Data;
import xyz.liangwh.headwaters.core.interfaces.IBucket;
import xyz.liangwh.headwaters.core.interfaces.IBuffer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * IBucket实现类
 *
 * @see IBucket
 * @author liangwh
 */
@Data
public class Bucket extends IBucket<Long> {
    //private AtomicInteger value = new AtomicInteger(0);
    private AtomicLong value = new AtomicLong(0);



    @Override
    public long getIdle(){
        return this.inside - value.get();
    }

    @Override
    public IBuffer getPar() {
        return parent;
    }

    @Override
    public Long getAndIncrement() {
        return value.getAndIncrement();
    }
}
