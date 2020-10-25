package xyz.liangwh.headwaters.core.utils;

import xyz.liangwh.headwaters.core.interfaces.IdPrefixHandler;

import java.util.Date;

/**
 * 序列生成/解析工具类
 * @see IdPrefixHandler
 */
public class IdUtils {

    /**
     * 通过prefix与序列值 生成一个64位的唯一序列
     * prefix为32位以内的时间戳，保证在多个Hw实例同时运行，仍然能保证全局有序
     * prefix为32位以内的标识，则可以通过getMarkId方法解析该序列是归宿哪一个gid或者key
     * 默认prefix=0，可通过实现IdPrefixHandler的hendle方法，自定义prefix的逻辑
     *...
     * @see IdPrefixHandler
     * @param prefix 前缀
     * @param insideId 当前号段中获取的值
     * @return
     */
    public  static long makeTrueId(int prefix,int insideId){
        return (prefix<<32)|insideId;
    }
    public  static long makeTrueIdWhitHandle(int prefix, int insideId, IdPrefixHandler handler){
        prefix= handler.handle(prefix);
        return (prefix<<32)|insideId;
    }

    /**
     * 溯源
     * @param tid
     * @return
     */
    public  static int getMarkId(long tid){
        long id = tid>>32;
        return (int)id;
    }

}
