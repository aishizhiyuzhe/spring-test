package com.aishizhiyuzhe.spring.foremwork.beans.config;

//���ڴ洢�����ļ��е���Ϣ
//�൱�ڱ������ڴ��е�����
//�൱���ǽ�ÿһ����Ĵ������Է�װ��һ������
public class BeanDefinition {
    private String beanClassName;
    private boolean lazyInit=false;//�Ƿ��ӳټ���
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
