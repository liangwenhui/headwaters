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


    private int workerNums = 6;


    @Bean( name = "bossGroup" )
    public EventLoopGroup bossGroup() {
        //,new DefaultThreadFactory("netty-boos-")
       return createLoopGroup(1,null);
    }

    @Bean( name = "workerGroup" )
    public EventLoopGroup workerGroup() {
       return createLoopGroup(workerNums,null);
    }

    @Bean( name = "busiGroup" )
    public EventLoopGroup[] busiGroup() {
        EventLoopGroup[] busis = new EventLoopGroup[4];
        busis[0] =  createLoopGroup(3,null);
        busis[1] =  createLoopGroup(3,null);
        busis[2] =  createLoopGroup(3,null);
        busis[3] =  createLoopGroup(3,null);

        return busis;
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
