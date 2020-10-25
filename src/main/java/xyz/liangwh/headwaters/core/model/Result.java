package xyz.liangwh.headwaters.core.model;

import lombok.Data;
import xyz.liangwh.headwaters.core.AbstractHeadwaters;

import java.io.Serializable;

/**
 * 序列返回结果集
 * @see AbstractHeadwaters
 */
@Data
public class Result implements Serializable {

    private int state;
    private Long id;

    public Result(int state, Long id) {
        this.state = state;
        this.id = id;
    }
}
