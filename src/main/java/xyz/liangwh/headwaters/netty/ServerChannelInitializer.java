package xyz.liangwh.headwaters.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.liangwh.headwaters.core.interfaces.IDGenerator;
@Component
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private   ServerHandler serverHandler ;

    private StringDecoder decoder = new StringDecoder(CharsetUtil.UTF_8);
    private StringEncoder encoder = new StringEncoder(CharsetUtil.UTF_8);


    public ServerChannelInitializer() {
        System.out.println("ServerChannelInitializer");
    }

    /**
     * 每次tcp连接都会调用
     * @param socketChannel
     * @throws Exception
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
//        System.out.println("initchanel");
        socketChannel.pipeline().addLast("decoder",decoder);
        socketChannel.pipeline().addLast("encoder",encoder);
        socketChannel.pipeline().addLast(serverHandler);
    }
}
