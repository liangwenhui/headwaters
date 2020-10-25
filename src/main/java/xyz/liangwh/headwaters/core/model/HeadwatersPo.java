package xyz.liangwh.headwaters.core.model;

import lombok.Data;

/**
 * Headwaters 序列对象实体类
 */
@Data
public class HeadwatersPo  {
    //域
    private String gid;
    //序列名
    private String key;
    //当前从redis更新的序列值
    private Integer insideId;
    //当前序列可达最大值，接近改值表示要进行下一次更新内存中序列
    private Long maxId;
    //序列递增步长
    private Integer step;


}
