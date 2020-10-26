package xyz.liangwh.headwaters.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.apache.commons.lang.StringUtils;
import xyz.liangwh.headwaters.core.interfaces.IDGenerator;
import xyz.liangwh.headwaters.core.model.Result;

import java.util.ArrayList;
import java.util.List;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private IDGenerator idGenerator;

    public ServerHandler(IDGenerator idGenerator) {
        this.idGenerator= idGenerator;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("------->channel active");
    }


    public static void main(String[] args) {
        String command = "sequence     test   \n";
        String[] commands = command.split(" ");
        List<String> cs = new ArrayList<>();
        String tmp ;
        if(commands.length>1){
            for(String arg:commands){
                tmp = arg.trim();
                if(StringUtils.isNotEmpty(tmp)){
                    cs.add(tmp);
                }
            }
        }
        System.out.println(cs);
    }
//    private List<String> parse(String command){
//        command = "sequence   test     \n";
//
//    }

    @Override//sequence test\n
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("------->server channel read");
        if(msg!=null){
            if(StringUtils.startsWith((String) msg,"sequence")){
                Result test = idGenerator.getId("test");
                StringBuffer id = new StringBuffer();
                id.append(test.getId());
                ctx.write("$"+id.toString().length());
                //ctx.write(id.toString().length());
                ctx.write("\n");
                ctx.write(id.toString());
                ctx.write("\n");
            }else{
                ctx.write("-Error headwaters only suport command [sequence]\n");
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
