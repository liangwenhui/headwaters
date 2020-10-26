package xyz.liangwh.headwaters;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import xyz.liangwh.headwaters.netty.NettyServer;

import java.net.InetSocketAddress;

@SpringBootApplication
@Slf4j
public class HeadwatersApplication implements CommandLineRunner {
    @Value("${netty.port:8080}")
    private int port;
    @Value("${netty.url:localhost}")
    private String url;
    public static void main(String[] args) {
        SpringApplication.run(HeadwatersApplication.class, args);
        log.info("############################Headwaters启动成功#########################");
    }

    @Autowired
    private NettyServer server;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(args);
        if(args.length==1){//port
            String arg1 = args[0];
            if(StringUtils.isNumeric(arg1)){
                port = Integer.parseInt(arg1);
            }
        }else
        if(args.length==2){//ip port
            String arg1 = args[0];
            url = arg1;
            String arg2 = args[1];
            if(StringUtils.isNumeric(arg2)){
                port = Integer.parseInt(arg2);
            }
        }


        InetSocketAddress address = new InetSocketAddress(url, port);
        System.out.println("run rseq4J...");
        server.start(address);
    }
}
