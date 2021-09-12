package com.aishizhiyuzhe.spring.foremwork.context;

import com.aishizhiyuzhe.spring.foremwork.annotation.Autowired;
import com.aishizhiyuzhe.spring.foremwork.annotation.Controller;
import com.aishizhiyuzhe.spring.foremwork.annotation.Service;
import com.aishizhiyuzhe.spring.foremwork.beans.BeanWrapper;
import com.aishizhiyuzhe.spring.foremwork.beans.config.BeanDefinition;
import com.aishizhiyuzhe.spring.foremwork.beans.config.BeanPostProcessor;
import com.aishizhiyuzhe.spring.foremwork.context.support.BeanDefinitionReader;
import com.aishizhiyuzhe.spring.foremwork.context.support.DefaultListableBeanFactory;
import com.aishizhiyuzhe.spring.foremwork.core.BeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext extends DefaultListableBeanFactory implements BeanFactory {

    private String[] configLocations;
    private BeanDefinitionReader reader;
    //用来确保注册式单例的容器
    private Map<String,Object> factoryBeanObjectCache=new ConcurrentHashMap<String, Object>();
    //用来存储所有的被代理的对象
    private Map<String, BeanWrapper> factoryBeanInstanceCache=new ConcurrentHashMap<String, BeanWrapper>();

    public ApplicationContext(String...configLocations){
        this.configLocations=configLocations;
        try {
            refresh();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void refresh() throws Exception {
        reader=new BeanDefinitionReader(configLocations);

        List<BeanDefinition> beanDefinitions=reader.loadBeanDefinitionss();
        doRegisterBeanDefinition(beanDefinitions);
        doAutowrited();
    }
    private void doAutowrited(){
        for (Map.Entry<String,BeanDefinition> beanDefinitionEntry:super.beanDefinitionMap.entrySet()){
            String beanName=beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()){
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegisterBeanDefinition(List<BeanDefinition> beanDefinitions) throws Exception{
        for (BeanDefinition beanDefinition:beanDefinitions){
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw new Exception("The “"+beanDefinition.getFactoryBeanName()+"” is exists!!" );
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }

    }
    public Object getBean(String beanName) throws Exception {
        BeanDefinition beanDefinition=super.beanDefinitionMap.get(beanName);
        BeanPostProcessor beanPostProcessor=new BeanPostProcessor();
        Object instance=instantiateBean(beanDefinition);
        beanPostProcessor.postProcessBeforeInitiallization(instance,beanName);
        BeanWrapper beanWrapper=new BeanWrapper(instance);
        this.factoryBeanInstanceCache.put(beanName,beanWrapper);
        //这一步是用来给属性自动赋对象
        populateBean(beanName,instance);
        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    private void populateBean(String beanName,Object instance){
        Class clazz=instance.getClass();
        if(!(clazz.isAnnotationPresent(Controller.class))||clazz.isAnnotationPresent(Service.class)){
            return;
        }
        Field[] fields=clazz.getDeclaredFields();

        for (Field field:fields){
            if (!field.isAnnotationPresent(Autowired.class)){
                continue;
            }
            Autowired autowired=field.getAnnotation(Autowired.class);
            String autowiredBeanName=autowired.value().trim();
            if ("".equals(autowiredBeanName)){
                autowiredBeanName=field.getType().getName();
            }
            field.setAccessible(true);

            try {
                field.set(instance,this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }

    private Object instantiateBean(BeanDefinition beanDefinition){
        Object instance=null;
        String className=beanDefinition.getBeanClassName();
        try{
            if (this.factoryBeanObjectCache.containsKey(className)){
                instance=this.factoryBeanObjectCache.get(className);
            }else {
                Class<?> clazz=Class.forName(className);
                instance=clazz.newInstance();
                this.factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(),instance);
            }
            return instance;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;

    }
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    //获取所有bean的名字
    public String[] getBeanDefinitionNames(){
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCout(){
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig(){
        return this.reader.getConfig();
    }
}
