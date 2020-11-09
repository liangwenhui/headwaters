package xyz.liangwh.headwaters.netty;

import java.util.List;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.IntegerRedisMessage;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang.StringUtils;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.liangwh.headwaters.core.exception.HedisError;
import xyz.liangwh.headwaters.core.exception.HedisException;
import xyz.liangwh.headwaters.core.interfaces.IDGenerator;
import xyz.liangwh.headwaters.core.model.RESPResult;
import xyz.liangwh.headwaters.core.model.RESPSysResult;
import xyz.liangwh.headwaters.core.model.Result;
import xyz.liangwh.headwaters.core.utils.RESPUtil;

@Slf4j
@ChannelHandler.Sharable
@Component
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private IDGenerator idGenerator;

    public ServerHandler() {
        System.out.println("ServerHandler");
//        this.idGenerator = idGenerator;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx)  {
        // System.out.println("------->channel active");
    }

    @Override // sequence test\n
    public void channelRead(ChannelHandlerContext ctx, Object msg)  {
        ArrayRedisMessage arm = (ArrayRedisMessage)msg;
        try{
            String key = assertVerificat(arm);
            getId(ctx,key);
        }catch (HedisException hex){
            ctx.write(new ErrorRedisMessage(hex.getMessage()));
            return;
        }catch (DecoderException de){
            ctx.write(new ErrorRedisMessage( de.getMessage()));
            return;
        }
        catch(Exception e){
            ctx.write(new ErrorRedisMessage( e.getLocalizedMessage()));
            return;
        }finally {
            ctx.flush();
        }
    }

    private String assertVerificat(ArrayRedisMessage arm) throws HedisException {
        if(arm==null){
            throw new HedisException(HedisError.REDIS_PROTOCOL_ERROR,"The data does not conform to redis(hw) protocol");
        }
        else if(arm.children().size()==0){
            throw new HedisException(HedisError.REDIS_PROTOCOL_ERROR,"The data does not conform to redis(hw) protocol");
        }
        String command = ((FullBulkStringRedisMessage) arm.children().get(0)).content().toString(CharsetUtil.UTF_8);
        if(!RESPUtil.COMMANDS_SET.contains(command)){
            throw new HedisException(HedisError.REDIS_PROTOCOL_ERROR,"Only command 'sequence'is supported !");
        }
        else if(arm.children().size()!=2){
            throw new HedisException(HedisError.REDIS_PROTOCOL_ERROR,"The format of command 'sequence' must be sequence key !");
        }
        return ((FullBulkStringRedisMessage) arm.children().get(1)).content().toString(CharsetUtil.UTF_8);

    }

    private void getId(ChannelHandlerContext ctx,String key){
        Result id = idGenerator.getId(key);
        if (id.getId() == null) {

            //ctx.write(RESPUtil.makeSystemResult(RESPSysResult.ERROR, "code:" + id.getState()));
            ctx.write( new ErrorRedisMessage("get id failed , err code:" + id.getState()));
        }
        else {
            //ctx.write(RESPUtil.translateToRESPInteage(id.getId()));
            ctx.write( new IntegerRedisMessage(id.getId()));
            //return id.getId();
        }
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws HedisException {
       // cause.printStackTrace();
        ctx.close();
    }
}
