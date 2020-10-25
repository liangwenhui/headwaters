package xyz.liangwh.headwaters.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.lang.StringUtils;
import xyz.liangwh.headwaters.core.interfaces.IDGenerator;
import xyz.liangwh.headwaters.core.model.Result;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private IDGenerator idGenerator;

    public ServerHandler(IDGenerator idGenerator) {
        this.idGenerator= idGenerator;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("------->channel active");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("------->server channel read");
        if(msg!=null){
            if(StringUtils.startsWith((String) msg,"seq")){
                Result test = idGenerator.getId("test");
                ctx.write(test.getId()+"\n");
            }else{
                ctx.write("-Err");
            }
        }
        //System.out.println(ctx.channel().remoteAddress()+"----->Server :"+ msg.toString());
        //将客户端的信息直接返回写入ctx

        //刷新缓存区
        ctx.flush();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
