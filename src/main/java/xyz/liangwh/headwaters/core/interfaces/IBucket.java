package xyz.liangwh.headwaters.core.interfaces;

import lombok.Data;

/**
 * 一个桶（Bucket）的顶级接口，Bucket用来存放运行期的序列值
 * 以及步长等信息，由parent IBuffer管理
 * @see IBuffer
 * @param <T>
 */
@Data
public abstract class IBucket<T> {

    protected long max = 0;
    protected long inside = 0;
    protected int step = 0;
    //可指向buffer
    protected IBuffer parent;

    /**
     * 获取id余量
     * @return
     */
    public abstract long getIdle();

    public abstract IBuffer getPar();

    public abstract T getAndIncrement();
}
