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
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
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

    @Test
    @SneakyThrows
    public void testUpdateFormFile()  {
        String key = "a";
        File dir = new File("./dats");
        if(!dir.exists()||!dir.isDirectory()){
            dir.mkdirs();
        }
        File configFile = new File(dir,key);
        if(!configFile.exists()||!configFile.isFile()){
            configFile.createNewFile();
        }
        RandomAccessFile accessFile = new RandomAccessFile(configFile, "rw");
        FileChannel channel = accessFile.getChannel();
        MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 8, 8);
        long aLong = map.getLong();
        if(aLong==0){
            map.position(0);
            map.putLong(1201);
        }
        map.position(0);
        long aLong1 = map.getLong();
        System.out.println(aLong1);
    }
    private void read(RandomAccessFile rafile) throws IOException {
        System.out.println(rafile.readLong());
    }
    private void write(RandomAccessFile rafile,long maxId) throws IOException {
        rafile.writeLong(maxId);

    }

}
