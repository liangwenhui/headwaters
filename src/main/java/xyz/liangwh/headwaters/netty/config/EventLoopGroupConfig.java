package xyz.liangwh.headwaters.netty.config;


import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadFactory;

@Configuration
public class EventLoopGroupConfig {


    private int workerNums = 3;


    @Bean( name = "bossGroup" )
    public EventLoopGroup bossGroup() {
       return createLoopGroup(1,new DefaultThreadFactory("netty-boos-"));
    }

    @Bean( name = "workerGroup" )
    public EventLoopGroup workerGroup() {
       return createLoopGroup(workerNums,new DefaultThreadFactory("netty-worker-"));
    }


    private EventLoopGroup createLoopGroup(int nums , ThreadFactory threadFactory) {
        EventLoopGroup group = null;
        if(Epoll.isAvailable()){
            group = new EpollEventLoopGroup(nums, threadFactory);
        }else{
            group  = new NioEventLoopGroup(nums,threadFactory);
        }
        return group;
    }
}
