package xyz.liangwh.headwaters.core.interfaces;

import xyz.liangwh.headwaters.core.exception.HedisException;
import xyz.liangwh.headwaters.core.model.Result;

/**
 * 序列器，子类实现getId()方法
 * 对外提供获取序列的窗口。
 * @author liangwh
 */
public interface IDGenerator {
    /**
     * 根据序列名获取唯一序列
     * @param key
     * @return
     */
    Result getId(String key) throws HedisException;
}
