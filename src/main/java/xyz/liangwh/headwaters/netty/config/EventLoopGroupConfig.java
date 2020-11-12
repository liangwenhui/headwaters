package xyz.liangwh.headwaters.netty.config;


import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
public class EventLoopGroupConfig {


    private int workerNums = 12;


    @Bean( name = "bossGroup" )
    public EventLoopGroup bossGroup() {
        //,new DefaultThreadFactory("netty-boos-")
       return createLoopGroup(1,null);
    }

    @Bean( name = "workerGroup" )
    public EventLoopGroup workerGroup() {
       return createLoopGroup(workerNums,null);
    }

//    @Bean( name = "busiGroup" )
//    public EventLoopGroup[] busiGroup() {
//        EventLoopGroup[] busis = new EventLoopGroup[4];
//        busis[0] =  createLoopGroup(2,null);
//        busis[1] =  createLoopGroup(2,null);
//        busis[2] =  createLoopGroup(2,null);
//        busis[3] =  createLoopGroup(2,null);
//
//        return busis;
//    }
    @Bean( name = "busiService" )
    public ExecutorService[] busiService(){
        ExecutorService[] busiService= new ExecutorService[4];
        busiService[0] =  Executors.newFixedThreadPool(3);
        busiService[1] =  Executors.newFixedThreadPool(3);
        busiService[2] =  Executors.newFixedThreadPool(3);
        busiService[3] =  Executors.newFixedThreadPool(3);

        return busiService;
    }

    private EventLoopGroup createLoopGroup(int nums , ThreadFactory threadFactory) {
        EventLoopGroup group = null;
        if(Epoll.isAvailable()){
            group = new EpollEventLoopGroup(nums);
        }else{
            group  = new NioEventLoopGroup(nums);
        }
        return group;
    }
}
