package com.baixiu.middleware.spi.annotation;

import com.baixiu.middleware.spi.consts.CommonConsts;
import java.lang.annotation.*;

/**
 * ��չ����ݱ�ʶ��
 * ����ʶ����פϵͳ�е�ϵͳ���ƣ���һ��ֵ
 * Ȼ��ʶ����פϵͳ�о�����ʲô������������չ�������֣��ڶ���ֵ
 * ����ʱȷ��
 * @author baixiu
 * @date 2023��12��07��
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Extension {

    /**
     * ϵͳ���ơ��磺����
     * @return
     */
    String appName() default CommonConsts.DEFAULT_EXTENSION_APP;

    /**
     * ϵͳ�о���ĳ���
     * @return the string
     */
    String scenario() default CommonConsts.DEFAULT_SCENARIO;


}
