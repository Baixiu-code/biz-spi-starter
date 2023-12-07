package com.baixiu.middleware.spi.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 扫描 extension 注解后的bean 上下文容器
 * @author baixiu
 * @date 2023年12月07日
 */
public class SPIExtensionBeanContexts {

    public static Map<String,Object> BEAN_EXTENDS_MAP=new ConcurrentHashMap<> ();

}
