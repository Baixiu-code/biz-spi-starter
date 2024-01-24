package com.baixiu.middleware.spi.proxy;

import com.baixiu.middleware.spi.consts.CommonConsts;
import com.baixiu.middleware.spi.context.SPIExtensionBeanContexts;
import com.baixiu.middleware.spi.core.SPIRouter;
import com.baixiu.middleware.spi.core.ThreadLocalSPIRouter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 完成代理工厂
 * step1:获取上下文中router
 * step2:获取spring 上下文中动态代理对象
 * step3:实现动态代理 invoke
 * @author baixiu
 * @date 创建时间 2023/12/7 4:47 PM
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ExtensionBeanInterceptor implements ApplicationContextAware
            , BeanClassLoaderAware, MethodInterceptor, FactoryBean<Object> {

    private static final Logger log = LoggerFactory.getLogger(ExtensionBeanInterceptor.class);

    private ApplicationContext applicationContext;

    /**
     * 类加载器
     */
    private ClassLoader classLoader;

    /**
     * 策略路由
     */
    private SPIRouter spiRouter;

    /**
     * 代理bean
     */
    private Object serviceProxy;

    /**
     * 扩展服务接口
     */
    private Class<?> serviceInterface;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }


    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        //step1:get invocation identity
        String identityStr=getIdentity(methodInvocation);
        //step2:push into thread local
        ThreadLocalSPIRouter.pushIdentity(identityStr);
        //step3:通过identity 获取router
        String routerName=this.spiRouter.route(methodInvocation);
        //step4:通过SPIExtensionBeanContexts 获取 bean
        Object routerObject=SPIExtensionBeanContexts.BEAN_EXTENDS_MAP.get(routerName);
        if(Objects.nonNull(routerObject)){
            //通过声明class强转object
            Object realBean=methodInvocation.getMethod ().getDeclaringClass ().cast (routerObject);
            Method method=realBean.getClass().getMethod(methodInvocation.getMethod().getName(),methodInvocation.getMethod()
                    .getParameterTypes());
            try {
                return method.invoke(realBean,methodInvocation.getArguments());
            } catch (Exception e) {
                throw new RuntimeException (e);
            } finally {
                ThreadLocalSPIRouter.popIdentity();
            }
        }
        //step5:动态代理反射调用
        return null;
    }

    private String getIdentity(MethodInvocation methodInvocation) {
        try {
            Object[] objects=methodInvocation.getArguments();
            if(objects!=null && objects.length>0){
                Field fields=objects[0].getClass().getDeclaredField(CommonConsts.DEFAULT_IDENTITY_FIELD_NAME);
                fields.setAccessible (true);
                String identityStr=fields.get(objects[0]).toString()+"_"+CommonConsts.DEFAULT_SCENARIO;
                log.info("getIdentity.identityStr.{}",identityStr);
                return identityStr;
            }
            return CommonConsts.DEFAULT_IDENTITY;
        } catch(Exception e) {
            return CommonConsts.DEFAULT_IDENTITY;
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader=classLoader;
    }

    @Override
    public Object getObject() throws Exception {
        if (serviceProxy == null) {
            Class<?> ifc = getServiceInterface();
            Assert.notNull(ifc, "Property 'serviceInterface' is required");
            Assert.notNull(getSpiRouter(), "Property 'spiRouter' is required");
            serviceProxy = new ProxyFactory (ifc, this).getProxy(classLoader);
        }
        return serviceProxy;
    }

    @Override
    public Class<?> getObjectType() {
        return getServiceInterface ();
    }


    public SPIRouter getSpiRouter() {
        return spiRouter;
    }

    public void setSpiRouter(SPIRouter spiRouter) {
        this.spiRouter = spiRouter;
    }

    /**
     * 获取服务接口
     *
     * @param serviceInterface
     */
    public void setServiceInterface(Class<?> serviceInterface) {
        Assert.notNull(serviceInterface, "'serviceInterface' must not be null");
        Assert.isTrue(serviceInterface.isInterface(), "'serviceInterface' must be an interface");
        this.serviceInterface = serviceInterface;
    }

    public Object getServiceProxy() {
        return serviceProxy;
    }

    public void setServiceProxy(Object serviceProxy) {
        this.serviceProxy = serviceProxy;
    }

    public Class<?> getServiceInterface() {
        return serviceInterface;
    }
}
