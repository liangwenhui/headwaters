package xyz.liangwh.headwaters.core.exception;

import org.springframework.core.env.Environment;

import xyz.liangwh.headwaters.core.utils.SpringContextUtils;

public class HedisError {

    // 不符合redis协议
    public static final String REDIS_PROTOCOL_ERROR = "REDIS_ERROR_0001";

    // 申请id异常
    public static final String REDIS_APPLY_ID_ERROR = "REDIS_ERROR_0002";

    private String errorCode;

    private String errorName;

    public HedisError(String errorCode) {
        initialize(errorCode);
        this.errorCode = errorCode;
    }

    protected void initialize(String errorCode) {
        Environment env = SpringContextUtils.getBean(Environment.class);
        errorName = env.getProperty("HedisError." + errorCode);
    }

    @Override
    public String toString() {
        return "errorCode:" + errorCode + ",errorName:" + errorName;
    }

    public String getError() {
        return errorCode + ":" + errorName;
    }
}
