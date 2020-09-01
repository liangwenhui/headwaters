package xyz.liangwh.headwaters.core.interfaces;

import lombok.Data;

@Data
public abstract class IBucket<T> {

    protected long max = 0;
    protected int inside = 0;
    protected int step = 0;
    //可指向buffer
    protected IBuffer parent;

    public abstract int getIdle();



    public abstract IBuffer getPar();

    public abstract T getAndIncrement();
}
