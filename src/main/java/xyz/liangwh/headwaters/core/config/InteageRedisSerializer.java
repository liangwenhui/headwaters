package xyz.liangwh.headwaters.core.config;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * redis {整形} 序列化反序列化器
 * @author liangwh
 */
public class InteageRedisSerializer implements RedisSerializer<Integer> {
    @Override
    public byte[] serialize(Integer integer) throws SerializationException {

        return int2bytes(integer.intValue());
    }

    @Override
    public Integer deserialize(byte[] bytes) throws SerializationException {

        return Integer.parseInt( new String(bytes));
    }


    /**
     * 整型转换成字节数组
     */
    public static byte[] int2bytes(int i){
        byte[] arr = new byte[4] ;
        arr[0] = (byte)i ;
        arr[1] = (byte)(i >> 8) ;
        arr[2] = (byte)(i >> 16) ;
        arr[3] = (byte)(i >> 24) ;
        return arr ;
    }

    /**
     * 字节数组转成int
     */
    public static int bytes2int(byte[] bytes){
        int i0= bytes[0];
        int i1 = (bytes[1] & 0xFF) << 8 ;
        int i2 = (bytes[2] & 0xFF) << 16 ;
        int i3 = (bytes[3] & 0xFF) << 24 ;
        return i0 | i1 | i2 | i3 ;
    }

}
