package xyz.liangwh.headwaters.core.model;

import lombok.Getter;
import xyz.liangwh.headwaters.core.utils.RESPUtil;

public enum RESPSysResult {

    OK(RESPUtil.FLAG_SUCCESS, RESPUtil.OK), ERROR(RESPUtil.FLAG_ERROR, RESPUtil.ERROR), WRONG(RESPUtil.FLAG_ERROR,
        RESPUtil.WRONG);

    @Getter
    private String flag;

    @Getter
    private String type;

    RESPSysResult(String flag, String type) {
        this.flag = flag;
        this.type = type;
    }
}
