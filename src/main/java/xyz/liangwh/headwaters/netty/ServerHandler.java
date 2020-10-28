package xyz.liangwh.headwaters.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import xyz.liangwh.headwaters.core.interfaces.IDGenerator;
import xyz.liangwh.headwaters.core.model.RESPResult;
import xyz.liangwh.headwaters.core.model.RESPSysResult;
import xyz.liangwh.headwaters.core.model.Result;
import xyz.liangwh.headwaters.core.utils.RESPUtil;

import java.util.ArrayList;
import java.util.List;
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {


    private IDGenerator idGenerator;

    public ServerHandler(IDGenerator idGenerator) {
        this.idGenerator= idGenerator;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("------->channel active");
    }




    @Override//sequence test\n
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg!=null){
            RESPResult respResult = RESPUtil.TranslateToRESPResult((String) msg);
            List<String> argList = respResult.getArgList();
            String command = argList.get(0);
            boolean contains = RESPUtil.COMMANDS_SET.contains(command);
            if(contains){
                if(!command.equals("PING")){
                    if(argList.size()>1){
                        String key = argList.get(1);
                        Result id = idGenerator.getId(key);
                        respResult = new RESPResult();
                        ctx.write(RESPUtil.TranslateToRESPString(respResult.append(id.getId()+""),true));
                        //log.info(id.getId()+"");
                    }else{
                        ctx.write(RESPUtil.makeSystemResult(RESPSysResult.ERROR, "The format of command 'sequence' must be sequence key !"));
                    }
                }else{
                    ctx.write(RESPUtil.FLAG_SUCCESS+"PONG");
                    ctx.write(RESPUtil.CRLF);
                }
            }else {
                ctx.write(RESPUtil.makeSystemResult(RESPSysResult.ERROR, "Only command 'PING','sequence'is supported !"));
            }
            //刷新缓存区
            ctx.flush();
        }
        //System.out.println(ctx.channel().remoteAddress()+"----->Server :"+ msg.toString());
        //将客户端的信息直接返回写入ctx


    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
