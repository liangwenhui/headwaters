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
        //,new DefaultThreadFactory("netty-boos-")
       return createLoopGroup(1,null);
    }

    @Bean( name = "workerGroup" )
    public EventLoopGroup workerGroup() {
       return createLoopGroup(workerNums,null);
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
