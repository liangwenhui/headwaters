package xyz.liangwh.headwaters.core.config;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.ByteBuffer;

/**
 * redis {整形} 序列化反序列化器
 * 
 * @author liangwh
 */
public class InteageRedisSerializer implements RedisSerializer<Long> {
    @Override
    public byte[] serialize(Long integer) throws SerializationException {

//        return int2bytes(integer);
        return Long.toString(integer).getBytes();
    }

    @Override
    public Long deserialize(byte[] bytes) throws SerializationException {

        return Long.parseLong(new String(bytes));
    }

    /**
     * 整型转换成字节数组
     */
    public static byte[] int2bytes(long num) {
//        byte[] arr = new byte[8];
//        arr[0] = (byte) i;
//        arr[1] = (byte) (i >> 8);
//        arr[2] = (byte) (i >> 16);
//        arr[3] = (byte) (i >> 24);
//        arr[4] = (byte) (i >> 32);
//        arr[5] = (byte) (i >> 40);
//        arr[6] = (byte) (i >> 48);
//        arr[7] = (byte) (i >> 56);
        long temp = num;
        byte[] b =new byte[8];
        for(int i =0; i < b.length; i++){
            b[i]=new Long(temp &0xff).byteValue();//
                    temp = temp >>8;// 向右移8位
        }
        return b;
    }

    /**
     * 字节数组转成int
     */
    public static long bytes2int(byte[] bytes) {
        long i0 = bytes[0]& 0xFF;
        long i1 = (bytes[1] & 0xFF) << 8;
        long i2 = (bytes[2] & 0xFF) << 16;
        long i3 = (bytes[3] & 0xFF) << 24;
        long i4 = (bytes[4] & 0xFF) << 32;
        long i5 = (bytes[5] & 0xFF) << 40;
        long i6 = (bytes[6] & 0xFF) << 48;
        long i7 = (bytes[7] & 0xFF) << 58;
        return i0 | i1 | i2 | i3 |i4 | i5 | i6 | i7;
    }


    public static void main(String[] args) {
        InteageRedisSerializer inteageRedisSerializer = new InteageRedisSerializer();
        byte[] serialize = inteageRedisSerializer.serialize(8121L);
        Long deserialize = inteageRedisSerializer.deserialize(serialize);
        System.out.println(deserialize);

    }
}
