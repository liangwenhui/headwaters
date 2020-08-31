package xyz.liangwh.headwaters.core.disruptor.model;

import lombok.Data;
import sun.misc.Contended;

/**
 * 缓存行对齐注解
 */
@Contended
@Data
public class IdEvent {

    private int id;


}
