package com.baixiu.middleware.spi.core;

import com.baixiu.middleware.spi.annotation.Extension;
import com.baixiu.middleware.spi.context.SPIExtensionBeanContexts;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 第一步：扫描系统中所有extension注解的实例
 * 第二步：将所有已经标识extension注解实例放置于路由context
 * @author baixiu
 * @date 2023年12月07日
 */
@Component
public class ExtensionScanHandler implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String,Object> allExtensions=applicationContext.getBeansWithAnnotation (Extension.class);
        if(allExtensions!=null && allExtensions.size()>0){
            allExtensions.forEach ((key,value)->{
                Extension extensionItem= AnnotationUtils.findAnnotation(value.getClass (),Extension.class);
                String realKey=extensionItem.appName()+"_"+extensionItem.scenario();
                SPIExtensionBeanContexts.BEAN_EXTENDS_MAP.put(realKey,value);
            });
        }
    }
}
