package xyz.liangwh.headwaters.core.interfaces;

/**
 *序列前缀处理，通过实现本接口，完成对prefix的处理
 * 生成32位整形，与实际序列号组装成64位整形
 * @see xyz.liangwh.headwaters.core.utils.IdUtils
 */
@FunctionalInterface
public interface IdPrefixHandler {
     int handle(Object prefix);
}
