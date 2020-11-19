package xyz.liangwh.headwaters.core.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.liangwh.headwaters.core.exception.HedisError;
import xyz.liangwh.headwaters.core.exception.HedisException;
import xyz.liangwh.headwaters.core.model.HeadwatersPo;
import xyz.liangwh.headwaters.core.model.HwMarkSamplePo;
import xyz.liangwh.headwaters.core.utils.RedisUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对redis序列号的操作类
 *
 */
@Component
@Slf4j
public class HwMarkRedisDao {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取gid中所有的序列
     * redis 数据结构 hash
     * gid : {key1:seqNum,key2:seqNum...}
     * @return
     */
    public List<HwMarkSamplePo>  getAllKeyMap(){
        Map<Object, Object> kv = redisUtil.hmget(redisUtil.getKey());
        List<HwMarkSamplePo> list = new ArrayList<>();
        kv.forEach((k,v)->{
            HwMarkSamplePo po = new HwMarkSamplePo();
            po.setGid(redisUtil.getKey());
            po.setKey((String) k);
            list.add(po);
        });
        return list;
    }

    /**
     * 更新并获取序列值
     * @param key 序列名
     * @return
     */
    public HeadwatersPo updateAndGetHeadwaters(long currentvar,String key) throws HedisException {
        //Long hincr = redisUtil.hincr(redisUtil.getKey(), key, redisUtil.getStep());
        Long hincr = update(currentvar,redisUtil.getKey(), key, redisUtil.getStep());
        HeadwatersPo po = new HeadwatersPo();
        po.setGid(redisUtil.getKey());
        po.setKey(key);
        po.setInsideId(hincr.intValue() );
        po.setStep(redisUtil.getStep());
        po.setMaxId((long)(po.getInsideId()+po.getStep()));
        return po;
    }

    /**
     * 根据动态步长 更新并获取序列值
     * @param key 序列名
     * @param autoStep 动态步长
     * @return
     */
    public HeadwatersPo updateAutoAndGetHeadwaters(long currentvar,String key, int autoStep) throws HedisException {
        Long hincr = update(currentvar,redisUtil.getKey(), key, autoStep);//redisUtil.hincr(redisUtil.getKey(), key, autoStep);

        HeadwatersPo po = new HeadwatersPo();
        po.setGid(redisUtil.getKey());
        po.setKey(key);
        po.setInsideId(hincr.intValue() );
        po.setStep(autoStep);
        po.setMaxId((long)(po.getInsideId()+autoStep));
        return po;
    }

    private Long update(long currentvar,String hkey,String key, int step) throws HedisException {
        Long hincr = null;
        try {
            RandomAccessFile accessFile = updateFileMap.get(key);
            if(accessFile!=null){
                hincr = currentvar+step;
                redisUtil.hset(redisUtil.getKey(), key, hincr);
                updateFileMap.remove(key);
                MappedByteBuffer map = accessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 8);
                map.putLong(0);
            }else{
                hincr = redisUtil.hincr(redisUtil.getKey(), key, step);
            }

        }catch (Exception e){
            log.error("redis申请id异常", e);
            try {
                hincr = updateFromFile(currentvar,key, step);
            }catch (Exception e2){
                log.error("file申请id异常", e2);
                throw new HedisException(HedisError.REDIS_APPLY_ID_ERROR, "redis and file apply id failed ", e);
            }

        }
        return hincr;
    }

    Map<String, RandomAccessFile> updateFileMap = new ConcurrentHashMap<>();
    Object lock = new Object();

    private Long updateFromFile(long currentvar,String key,int step) throws IOException {
        RandomAccessFile accessFile = updateFileMap.get(key);
        if(accessFile==null){
            synchronized (lock){
                if(accessFile==null){
                    File file = new File("./dats",key);
                    accessFile = new RandomAccessFile(file, "rw");
                    updateFileMap.put(key, accessFile);
                }
            }
        }
        synchronized (accessFile){
            FileChannel channel = accessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, 8);
            long aLong = map.getLong();
            if(aLong==0){
                map.position(0);
                aLong = currentvar+step;
                map.putLong(aLong);
            }else{
                aLong+=step;
                map.position(0);
                map.putLong(aLong);
                map.position(0);
                aLong = map.getLong();
            }
        return aLong;
        }


    }



}
