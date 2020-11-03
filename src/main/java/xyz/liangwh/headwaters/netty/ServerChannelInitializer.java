package xyz.liangwh.headwaters.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import xyz.liangwh.headwaters.core.interfaces.IDGenerator;

public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private IDGenerator idGenerator;
    private   ServerHandler SERVER_HANDLER ;

    public ServerChannelInitializer(IDGenerator idGenerator) {
        System.out.println("ServerChannelInitializer");
        this.idGenerator=idGenerator;
        SERVER_HANDLER = new ServerHandler(idGenerator);
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast("decoder",new StringDecoder(CharsetUtil.UTF_8));
        socketChannel.pipeline().addLast("encoder",new StringEncoder(CharsetUtil.UTF_8));
        socketChannel.pipeline().addLast(SERVER_HANDLER);
    }
}
