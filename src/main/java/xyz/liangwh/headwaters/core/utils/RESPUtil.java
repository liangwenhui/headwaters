package xyz.liangwh.headwaters.core.utils;

import java.util.HashSet;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import xyz.liangwh.headwaters.core.exception.HedisError;
import xyz.liangwh.headwaters.core.exception.HedisException;
import xyz.liangwh.headwaters.core.model.RESPResult;
import xyz.liangwh.headwaters.core.model.RESPSysResult;

/**
 * 解析redis协议请求数据 封装redis协议返回数据
 */
@Slf4j
public class RESPUtil {

    /**
     * redis 协议 PREFIX FLAG s
     */
    public static final String PREFIX_ARG_NUMS = "*";

    public static final String PREFIX_ARG_LENGTH = "$";

    public static final String PREFIX_ARG_INTEAGE = ":";

    public static final String CRLF = "\r\n";

    public static final String FLAG_SUCCESS = "+";

    public static final String FLAG_ERROR = "-";

    public static final String OK = "OK";

    public static final String ERROR = "Err";

    public static final String WRONG = "WRONG";

    /**
     * commands
     */
    public static final Set<String> COMMANDS_SET = new HashSet();

    static {
        // COMMANDS_SET.add("PING");
        COMMANDS_SET.add("sequence");
    }

    public static String makeSystemResult(RESPSysResult sys, String msg) {

        try {
            StringBuffer res = new StringBuffer();
            switch (sys) {
                case OK:
                    res.append(sys.getFlag()).append(sys.getType());
                    break;
                default:
                    res.append(sys.getFlag()).append(sys.getType());
                    res.append(" ").append(msg);
            }
            res.append(CRLF);
            return res.toString();
        }
        catch (Exception e) {
            log.error("执行异常", e);
            throw e;
        }
    }

    public static String translateToRESPString(RESPResult result, boolean isRes) {
        try {
            StringBuffer resp = new StringBuffer();
            if (!isRes || result.getArgList().size() > 1) {
                resp.append(PREFIX_ARG_NUMS).append(result.getArgNums()).append(CRLF);
            }
            for (Object o : result.getArgList()) {
                String ostr = o.toString();
                resp.append(PREFIX_ARG_LENGTH).append(ostr.length()).append(CRLF);
                resp.append(ostr).append(CRLF);
            }
            return resp.toString();
        }
        catch (Exception e) {
            log.error("执行异常", e);
        }
        return null;
    }

    public static RESPResult translateToRESPResult(String resp) throws HedisException {
        RESPResult respResult = null;

        try {
            String[] respArr = resp.split("\r\n");
            String numstr = respArr[0];
            int num = Integer.parseInt(numstr.replace(PREFIX_ARG_NUMS, ""));
            respResult = new RESPResult();
            for (int i = 1; i <= num; i++) {
                respResult.append(respArr[i << 1]);
            }
            return respResult;
        }
        catch (Exception e) {
            log.error("执行异常", e);
            throw new HedisException(HedisError.REDIS_PROTOCOL_ERROR, "The data does not conform to redis(hw) protocol", e);
        }
    }

    public static String translateToRESPInteage(long l) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_ARG_INTEAGE).append(l).append(CRLF);
        return sb.toString();
    }

}
