import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import io.netty.handler.codec.redis.RedisBulkStringAggregator;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.liangwh.headwaters.core.exception.HedisException;
import xyz.liangwh.headwaters.netty.ServerHandler;

import java.net.InetSocketAddress;

public class test {


    public static void main(String[] args) {
        t();
    }

    public static void t(){
        EventLoopGroup bossGroup = createLoopGroup(1);
        EventLoopGroup workerGroup = createLoopGroup(20);
        InetSocketAddress address = new InetSocketAddress( 8080);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    //.channel(EpollSocketChannel.class)
                    .localAddress(address)
                    .childHandler(new Initializer())
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);
            if(Epoll.isAvailable()){
                bootstrap.channel(EpollServerSocketChannel.class);
            }else{
                bootstrap.channel(NioServerSocketChannel.class);
            }

            ChannelFuture future = bootstrap.bind(address).sync();
            future.channel().closeFuture().sync();

        }catch (Exception e){
            e.printStackTrace();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
    private static EventLoopGroup createLoopGroup(int nums ) {
        EventLoopGroup group = null;
        if(Epoll.isAvailable()){
            group = new EpollEventLoopGroup(nums);
        }else{
            group  = new NioEventLoopGroup(nums);
        }
        return group;
    }
}
 class Initializer extends ChannelInitializer<SocketChannel> {

//    @Autowired
    private Handleer serverHandler = new Handleer();

    private StringDecoder decoder = new StringDecoder(CharsetUtil.UTF_8);
    private StringEncoder encoder = new StringEncoder(CharsetUtil.UTF_8);


    public Initializer() {
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
//        socketChannel.pipeline().addLast(new RedisDecoder());
//        socketChannel.pipeline().addLast(new RedisBulkStringAggregator());
//        socketChannel.pipeline().addLast(new RedisArrayAggregator());
//        socketChannel.pipeline().addLast(new RedisEncoder());
////                socketChannel.pipeline().addLast("encoder",encoder);
        socketChannel.pipeline().addLast(serverHandler);
    }
}
@ChannelHandler.Sharable
class Handleer extends ChannelInboundHandlerAdapter {
    @Override // sequence test\n
    public void channelRead(ChannelHandlerContext ctx, Object msg)  {
            ctx.write("1\r\n");
            ctx.flush();


    }
}