package xyz.liangwh.headwaters;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

public class Tests {


    @Test
    public void test1(){
        long i = 1;
        long id = 1000;

        System.out.println((i<<32) | id);
    }


    public static void main(String[] args) {
        t();
    }

    public static void t(){
        EventLoopGroup bossGroup = createLoopGroup(1);
        EventLoopGroup workerGroup = createLoopGroup(12);
        InetSocketAddress address = new InetSocketAddress( 8080);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    //.channel(EpollSocketChannel.class)
                    .localAddress(address)
                    //.childHandler(serverChannelInitializer)
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
