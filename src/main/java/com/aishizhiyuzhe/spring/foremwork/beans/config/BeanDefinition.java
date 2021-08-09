package com.aishizhiyuzhe.spring.foremwork.beans.config;

//用于存储配置文件中的信息
//相当于保存在内存中的配置
//相当于是将每一个类的创建属性封装到一个类中
public class BeanDefinition {
    private String beanClassName;
    private boolean lazyInit=false;//是否延迟加载
    private String factoryBeanName;

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
}
