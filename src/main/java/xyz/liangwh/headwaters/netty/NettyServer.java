package xyz.liangwh.headwaters.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import xyz.liangwh.headwaters.core.interfaces.IDGenerator;

import java.net.InetSocketAddress;

@Component
public class NettyServer {

    @Autowired
    private IDGenerator idGenerator;

    @Autowired
    @Qualifier("bossGroup")
    private EventLoopGroup bossGroup;
    @Autowired
    @Qualifier("workerGroup")
    private EventLoopGroup workerGroup;

    @Autowired
    private ServerChannelInitializer serverChannelInitializer;

    public void start(InetSocketAddress address){
        //EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //EventLoopGroup workerGroup = new NioEventLoopGroup(6);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    //.channel(EpollSocketChannel.class)
                    .localAddress(address)
                    .childHandler(serverChannelInitializer)
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
}
