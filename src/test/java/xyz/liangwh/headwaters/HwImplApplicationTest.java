package xyz.liangwh.headwaters;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.liangwh.headwaters.core.impl.HeadwatersImpl;

@SpringBootTest
public class HwImplApplicationTest {

    @Autowired
    HeadwatersImpl headwaters;

    @Test
    void testInit(){
        headwaters.getId("1");
    }





}
