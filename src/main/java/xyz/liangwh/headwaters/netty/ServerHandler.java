package xyz.liangwh.headwaters.netty;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
    public void channelActive(ChannelHandlerContext ctx) throws HedisException {
        // System.out.println("------->channel active");
    }

    @Override // sequence test\n
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws HedisException {

        if (StringUtils.isNotBlank((String) msg)) {
            try {
                RESPResult respResult = RESPUtil.translateToRESPResult((String) msg);
                List<String> argList = respResult.getArgList();
                String command = argList.get(0);
                boolean contains = RESPUtil.COMMANDS_SET.contains(command);
                if (contains) {
                    if (argList.size() > 1) {
                        String key = argList.get(1);
                        Result id = idGenerator.getId(key);
                        if (id.getId() == null) {
                            ctx.write(RESPUtil.makeSystemResult(RESPSysResult.ERROR, "code:" + id.getState()));
                        }
                        else {
                            ctx.write(RESPUtil.translateToRESPInteage(id.getId()));
                        }
                    }
                    else {
                        ctx.write(RESPUtil.makeSystemResult(RESPSysResult.ERROR,
                            "The format of command 'sequence' must be sequence key !"));
                    }
                }
                else {
                    ctx.write(
                        RESPUtil.makeSystemResult(RESPSysResult.ERROR, "Only command 'PING','sequence'is supported !"));
                }
            }
            catch (Exception e) {
                ctx.write(RESPUtil.makeSystemResult(RESPSysResult.ERROR, e.getLocalizedMessage()));
            }
            // 刷新缓存区
            ctx.flush();
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws HedisException {
        cause.printStackTrace();
        ctx.close();
    }
}
