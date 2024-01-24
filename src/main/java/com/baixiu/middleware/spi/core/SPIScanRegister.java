package com.baixiu.middleware.spi.core;

import com.baixiu.middleware.spi.annotation.SPIDefine;
import com.baixiu.middleware.spi.annotation.SPIScan;
import com.baixiu.middleware.spi.consts.CommonConsts;
import com.baixiu.middleware.spi.proxy.ExtensionBeanInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Proxy;
import java.util.*;

/**
 *
 * 注册Extension注解标识的实现类
 * @author baixiu
 * @date 创建时间 2023/12/7 5:02 PM
 */
public class SPIScanRegister implements ImportBeanDefinitionRegistrar , BeanFactoryAware, ResourceLoaderAware,
        BeanClassLoaderAware, EnvironmentAware {

    /**
     * 环境配置
     */
    private Environment environment;

    /**
     * class loader
     */
    private ClassLoader classLoader;

    /**
     * resource loader
     */
    private ResourceLoader resourceLoader;


    private BeanFactory beanFactory;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        //定义scanner
        ClassPathScanningCandidateComponentProvider spiScanner=getSPIScanner();
        //根据 SPIscan 获取需要扫描的basePackage 路径
        Set<String> scanPackages=getSPIScanPackages(annotationMetadata);
        //遍历需要扫描的包路径
        for (String scanPackage : scanPackages) {
            //获取某一个路径下的候选bean定义
            Set<BeanDefinition> beanDefinitions= spiScanner.findCandidateComponents(scanPackage);
            //遍历每个 bean 注册定义，生产动态代理对象，通过 ImportBeanDefinitionRegistrar 开放的接口registerBeanDefinitions 进行
            //动态代理对象的注册到 spring 容器
            try {
                for (BeanDefinition beanDefinition : beanDefinitions) {

                    //当注册对象实例为注解bean定义时 获取注解的元数据信息
                    AnnotationMetadata annotationMetadataItem=null;
                    if(beanDefinition instanceof AnnotatedBeanDefinition){
                        annotationMetadataItem=((AnnotatedBeanDefinition) beanDefinition).getMetadata();
                    }

                    //通过注解定义的元数据信息获取注解对应的注解属性值
                    Map<String,Object> spiDefineAttrs=annotationMetadataItem.getAnnotationAttributes (SPIDefine.class.getName ());
                    if(spiDefineAttrs ==null || spiDefineAttrs.isEmpty ()){
                        continue;
                    }
                    String routerBeanName=null;
                    if(spiDefineAttrs.get(CommonConsts.SPI_DEFINE_ATTR_NAME) instanceof String){
                        routerBeanName = (String) spiDefineAttrs.get(CommonConsts.SPI_DEFINE_ATTR_NAME);
                    }

                    //get router
                    SPIRouter spiRouter=beanFactory.getBean(routerBeanName,SPIRouter.class);

                    //创建beanFactory
                    ExtensionBeanInterceptor interceptor=this.beanFactory.getBean(ExtensionBeanInterceptor.class);

                    //创建动态代理
                    Class<?> clazz=Class.forName(beanDefinition.getBeanClassName());
                    interceptor.setServiceInterface(clazz);
                    interceptor.setSpiRouter(spiRouter);
                    Object spiProxyObject=interceptor.getObject();
                    BeanDefinitionBuilder beanDefinitionBuilder=BeanDefinitionBuilder.genericBeanDefinition(spiProxyObject.getClass());
                    beanDefinitionBuilder.addConstructorArgValue (Proxy.getInvocationHandler(spiProxyObject));
                    
                    AbstractBeanDefinition realBeanDefinition = beanDefinitionBuilder.getBeanDefinition();
                    //非常重要。通过设置动态代理的bean为主对象，保证其能够在进行具体bean注入的时候注入成功。否则在具体注入的时候将出现
                    //注入失败，因为在进行字段注入时不能出现多个对象的实现。
                    realBeanDefinition.setPrimary(true);

                    //注册definition到spring容器
                    StringBuilder sb = new StringBuilder()
                            .append(clazz.getSimpleName())
                            .append("#Proxy");
                    beanDefinitionRegistry.registerBeanDefinition(sb.toString(), realBeanDefinition);

                }
            } catch (Exception e) {
                throw new RuntimeException (e);
            }
        }

    }

    private Set<String> getSPIScanPackages(AnnotationMetadata annotationMetadata) {

        Map<String, Object> annotationAttrs= annotationMetadata.getAnnotationAttributes(SPIScan.class.getCanonicalName ());
        Set<String> spiScanPackages=null;
        if(Objects.nonNull(annotationAttrs) && !annotationAttrs.isEmpty()){
            for (Map.Entry<String, Object> stringObjectEntry : annotationAttrs.entrySet()) {
                if(CommonConsts.BASE_PACKAGES_STR.equals(stringObjectEntry.getKey()) && stringObjectEntry.getValue() instanceof  String[]){
                    String[] basePackages= (String[]) stringObjectEntry.getValue();
                    spiScanPackages =new HashSet<>(basePackages.length);
                    spiScanPackages.addAll(Arrays.asList(basePackages));
                }
            }
        }

        //当spi scan package 没有配置 则直接使用 spi scan 所在类的路径
        if(Objects.isNull(spiScanPackages) || spiScanPackages.isEmpty()){
            spiScanPackages.add(ClassUtils.getPackageName(annotationMetadata.getClassName()));
        }
        return spiScanPackages;
    }


    private ClassPathScanningCandidateComponentProvider getSPIScanner() {
        ClassPathScanningCandidateComponentProvider scanner= new ClassPathScanningCandidateComponentProvider (false,this.environment){
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition annotatedBeanDefinition){
                //获取注解的元数据
                // 1.判断是否是独立的注解 不依赖其他注解或者类
                // 2.注解是否是一个接口
                if(annotatedBeanDefinition.getMetadata().isIndependent() && annotatedBeanDefinition.getMetadata().isInterface()){
                    //获取类 判断是否存在SPI defines
                    try {
                        Class<?> target=ClassUtils.forName(annotatedBeanDefinition.getMetadata().getClassName (),
                                SPIScanRegister.this.classLoader);
                       SPIDefine[] spiDefines= target.getAnnotationsByType (SPIDefine.class);
                       return spiDefines.length>0;
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException (e);
                    }
                }
                return false;
            }
        };
        scanner.setResourceLoader(this.resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(SPIDefine.class));
        return scanner;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader=classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
