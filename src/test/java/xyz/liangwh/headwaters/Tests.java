package xyz.liangwh.headwaters;

import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;
import xyz.liangwh.headwaters.core.disruptor.model.IdEvent;
import xyz.liangwh.headwaters.core.model.Bucket;

public class Tests {


    @Test
    public void test1(){
        long i = 1;
        long id = 1000;

        System.out.println((i<<32) | id);
    }

    @Test
    public void test2(){
        int i = 1;

        System.out.println(i<<2);
        System.out.println(i);
        IdEvent id = new IdEvent();
        System.out.println(ClassLayout.parseInstance(id).toPrintable());
    }
}
