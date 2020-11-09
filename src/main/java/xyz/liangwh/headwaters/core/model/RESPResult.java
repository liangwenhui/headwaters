package xyz.liangwh.headwaters.core.model;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import xyz.liangwh.headwaters.core.utils.RESPUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * RESP数据结构
 */
@ToString
public class RESPResult {
    @Getter
    private int argNums;
    @Getter
    private List<String> argList;



    public RESPResult(){
        argNums = 0;
        argList = new ArrayList<>();
    }

    public RESPResult append(String... args){

        if(args!=null&&args.length>0){
            for(String arg:args){
                argList.add(arg);
                argNums++;
            }
        }
        return this;
    }




    public static void main(String[] args) {
        RESPResult r = new RESPResult();
        r.append("auth","lwh@13660161032","2000");
        String s = RESPUtil.translateToRESPString(r,false);
        System.out.println(s);

       // RESPResult respResult = RESPUtil.translateToRESPResult(s);
      //  System.out.println(respResult.toString());
//        RESPUtil.COMMANDS_SET.contains("PONG");
//        RESPUtil.COMMANDS_SET.contains("PONG1");
//        String s1 = RESPUtil.makeSystemResult(RESPSysResult.ERROR, "message sss ss as ");
//        System.out.println(s1);

    }




}
