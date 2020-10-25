package xyz.liangwh.headwaters.core.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.liangwh.headwaters.core.model.HeadwatersPo;
import xyz.liangwh.headwaters.core.model.HwMarkSamplePo;
import xyz.liangwh.headwaters.core.utils.RedisUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 对redis序列号的操作类
 *
 */
@Component
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
    public HeadwatersPo updateAndGetHeadwaters(String key) {
        Long hincr = redisUtil.hincr(redisUtil.getKey(), key, redisUtil.getStep());
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
    public HeadwatersPo updateAutoAndGetHeadwaters(String key, int autoStep) {
        Long hincr = redisUtil.hincr(redisUtil.getKey(), key, autoStep);
        HeadwatersPo po = new HeadwatersPo();
        po.setGid(redisUtil.getKey());
        po.setKey(key);
        po.setInsideId(hincr.intValue() );
        po.setStep(autoStep);
        po.setMaxId((long)(po.getInsideId()+autoStep));
        return po;
    }
}
