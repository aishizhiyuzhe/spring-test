package com.aishizhiyuzhe.spring.foremwork.beans.config;

public class BeanPostProcessor {

    //Ϊ��Bean�ĳ�ʼ��֮ǰ�ṩ�ص����
    public Object postProcessBeforeInitiallization(Object bean,String beanName) throws Exception{
        return bean;
    }

    //Ϊ��bean�ĳ�ʼ��֮���ṩ�ص����
    public Object postProcessAfterInitialization(Object bean,String beanName) throws Exception{
        return bean;
    }
}
