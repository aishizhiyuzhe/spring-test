package com.aishizhiyuzhe.spring.foremwork.core;

/**
 * @Author: Laijh
 * @Description:
 * @Date: Created in 2021/8/7/14:22
 * @Modified By:
 */
public interface BeanFactory {

    Object getBean(String beanName) throws Exception;

    Object getBean(Class<?> beanClass) throws Exception;
}
