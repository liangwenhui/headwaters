package xyz.liangwh.headwaters.core.disruptor.model;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import lombok.Data;
import sun.misc.Contended;
import xyz.liangwh.headwaters.core.interfaces.IBucket;
import xyz.liangwh.headwaters.core.model.BucketBuffer;

import java.util.concurrent.atomic.AtomicInteger;

@Data
@Contended
public class DBucket implements IBucket {
    private AtomicInteger value = new AtomicInteger(0);

    private long max = 0;
    private int inside = 0;
    private int step = 0;
    //可指向buffer
    private DBucketBuffer parent;

    private int bufferSize = 1024;

    private Disruptor<IdEvent> disruptor;

    private RingBuffer<IdEvent> ringBuffer = disruptor.getRingBuffer();

    public int getIdle(){
        return this.inside - value.get();
    }
}
