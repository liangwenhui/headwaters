package xyz.liangwh.headwaters.core.interfaces;

import java.util.Map;

/**
 * 资源监控者，将监控的信息通过getInfo方法暴露出去
 */
public interface Monitor {

    Map getInfo();
}
