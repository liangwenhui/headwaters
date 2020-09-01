package xyz.liangwh.headwaters.core.model;

import lombok.Data;
import xyz.liangwh.headwaters.core.interfaces.IBucket;
import xyz.liangwh.headwaters.core.interfaces.IBuffer;

import java.util.concurrent.atomic.AtomicInteger;
@Data
public class Bucket extends IBucket<Integer> {
    private AtomicInteger value = new AtomicInteger(0);



    @Override
    public int getIdle(){
        return this.inside - value.get();
    }



    @Override
    public IBuffer getPar() {
        return parent;
    }

    @Override
    public Integer getAndIncrement() {
        return value.getAndIncrement();
    }
}
