package com.baixiu.middleware.spi.annotation;

import com.baixiu.middleware.spi.consts.CommonConsts;
import java.lang.annotation.*;

/**
 * 扩展点身份标识。
 * 首先识别入驻系统中的系统名称，第一个值
 * 然后识别入驻系统中具体是什么场景来进行扩展能力划分，第二个值
 * 运行时确定
 * @author baixiu
 * @date 2023年12月07日
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Extension {

    /**
     * 系统名称。如：健康
     * @return
     */
    String appName() default CommonConsts.DEFAULT_EXTENSION_APP;

    /**
     * 系统中具体的场景
     * @return the string
     */
    String scenario() default CommonConsts.DEFAULT_SCENARIO;


}
