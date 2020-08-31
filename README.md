# headwaters
Id生成器-水龙头

寓意：获取id像是去水龙头取水一样，一个水龙头可以喷涌出许多水滴，并且我们要饮水思源，可以根据id找到所在的数据表....

- 双重缓存池
- 线程安全
- 根据热度自动调整号段步长
- id为64位整形，前32位记录数据库key配置的id，后32位记录真实id。整体组成数据id。可根据前32位追踪key（key可使用表名，则可以根据id查到所在的数据库表）

可完善问题：

- 部署集群时，不同headwater获取的号段是不一致的，但获取id请求是会打到不同的headwater上，这样会导致数据失去有序性。可在前32位中选部分位置存储时间戳，但可能会有时间回溯问题，需要再加入集群同步时间的工具。
- 

## 分支介绍

- dev-atomic  [go](https://github.com/liangwenhui/headwaters/tree/dev-atomic)

  - 基于java原子操作类自增获取下一个ID，比使用队列存储ID要节省空间，并且都是CAS，效率相仿。

    

- dev-disruptor [go](https://github.com/liangwenhui/headwaters/tree/dev-disruptor)

  - 基于disruptor储存id，disruptor效率高，但消耗空间，空间换时间。