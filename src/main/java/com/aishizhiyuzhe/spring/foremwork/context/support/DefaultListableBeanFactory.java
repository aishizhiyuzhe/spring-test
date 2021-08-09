package com.aishizhiyuzhe.spring.foremwork.context.support;

import com.aishizhiyuzhe.spring.foremwork.beans.config.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultListableBeanFactory extends AbstractApplicationContext {

    protected final Map<String, BeanDefinition> beanDefinitionMap=new ConcurrentHashMap<String, BeanDefinition>();
}
