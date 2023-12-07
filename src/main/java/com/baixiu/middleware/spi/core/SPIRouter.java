package com.baixiu.middleware.spi.core;


import org.aopalliance.intercept.MethodInvocation;

/**
 * 开放SPI router 接口用以实现方实现自定义router能力
 * @author baixiu
 * @date 2023年12月07日
 */
public interface SPIRouter {


    String route(MethodInvocation methodInvocation);
}
