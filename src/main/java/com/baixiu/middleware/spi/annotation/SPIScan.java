package com.baixiu.middleware.spi.annotation;


import com.baixiu.middleware.spi.core.SPIScanRegister;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;


/**
 * SPI scan 用以定义 SPI 需要扫描的包路径
 * 减少不必要的扫描和加载
 * @author baixiu
 * @date 2023年12月07日
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(SPIScanRegister.class)
public @interface SPIScan {


    /**
     * 扫描@SPI注解接口的路径配置
     * @return
     */
    String[] basePackages() default {};
}
