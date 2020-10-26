package xyz.liangwh.headwaters.core.utils;

import xyz.liangwh.headwaters.core.model.RESPResult;
import xyz.liangwh.headwaters.core.model.RESPSysResult;

import java.util.HashSet;
import java.util.Set;

/**
 * 解析redis协议请求数据
 * 封装redis协议返回数据
 */
public class RESPUtil {


    /**
     * redis 协议 PREFIX FLAG s
     */
    public final static String PREFIX_ARG_NUMS = "*";
    public final static String PREFIX_ARG_LENGTH = "$";
    public final static String CRLF = "\r\n";
    public final static String FLAG_SUCCESS = "+";
    public final static String FLAG_ERROR = "-";
    public final static String OK = "OK";
    public final static String ERROR = "ERROR";
    public final static String WRONG = "WRONG";

    /**
     * commands
     */
    public final static Set<String> COMMANDS_SET = new HashSet();

    static {
        COMMANDS_SET.add("PING");
        COMMANDS_SET.add("sequence");
    }


    public static String makeSystemResult(RESPSysResult sys, String msg){

        try {
            StringBuffer res = new StringBuffer();
            switch (sys){
                case OK:
                    res.append(sys.getFlag()).append(sys.getType());
                    break;
                default:
                    res.append(sys.getFlag()).append(sys.getType());
                    res.append(" ").append(msg);
            }
            res.append(CRLF);
            return res.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String TranslateToRESPString(RESPResult result,boolean isRes){

        try {
            StringBuffer resp = new StringBuffer();
            if(!isRes||result.getArgList().size()>1){
                resp.append(PREFIX_ARG_NUMS).append(result.getArgNums()).append(CRLF);
            }
            for(Object o:result.getArgList()){
                String ostr = o.toString();
                resp.append(PREFIX_ARG_LENGTH).append(ostr.length()).append(CRLF);
                resp.append(ostr).append(CRLF);
            }
            return resp.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public static RESPResult TranslateToRESPResult(String resp){
        RESPResult respResult = null;

        try {
            String[] respArr = resp.split("\r\n");
            String numstr = respArr[0];
            int num = Integer.parseInt(numstr.replace( PREFIX_ARG_NUMS,""));
            respResult = new RESPResult();
            for(int i=1;i<=num;i++){
                respResult.append(respArr[i<<1]);
            }
            return respResult;
        }catch (Exception e){
            respResult = null;
            e.printStackTrace();

        }
        return respResult;
    }

}
