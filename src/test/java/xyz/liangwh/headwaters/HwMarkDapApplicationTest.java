package xyz.liangwh.headwaters;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import xyz.liangwh.headwaters.core.utils.RedisUtil;

@SpringBootTest
public class HwMarkDapApplicationTest {
    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void test1() {
        String tb = "gid";

        redisUtil.hincr(tb, "a", 1000);
        redisUtil.hincr(tb, "b", 1000);
        Integer a = (Integer) redisUtil.hget(tb, "a");
        System.out.println(a.intValue());
    }

    @Test
    public void test2() {
        String tb = "gid";

        Map<Object, Object> hmget = redisUtil.hmget(tb);
        System.out.println(hmget.keySet());

    }

    // @Test
    // public void test3() throws InterruptedException {
    //
    //
    // for(int i=0;i<1;i++){
    // new Thread(()->{
    // Result test ;
    // for(int j=0;j<10000;j++){
    // //test = gen.getId("test");
    // System.out.println(Thread.currentThread().getName()+":"+test.getId());
    // }
    //
    // },"T"+i).start();
    // }
    // TimeUnit.SECONDS.sleep(10);
    //
    // }

}
