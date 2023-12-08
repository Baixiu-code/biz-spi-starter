package com.baixiu.middleware.spi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 用以定义需要进行 扩展点 SPI 的接口。
 * 如要定义扩展点，需要首先定义此注解到业务接口上
 * @author baixiu
 * @date 2023年12月07日
 */
@Retention (RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SPIDefine {

    /**
     * 扩展点路由的路由方案 默认使用第一个
     * @return
     */
    String router() default "defaultSPIRouter";

}
