package com.aishizhiyuzhe.spring.foremwork.context;

import com.aishizhiyuzhe.spring.foremwork.beans.BeanWrapper;
import com.aishizhiyuzhe.spring.foremwork.beans.config.BeanDefinition;
import com.aishizhiyuzhe.spring.foremwork.context.support.BeanDefinitionReader;
import com.aishizhiyuzhe.spring.foremwork.context.support.DefaultListableBeanFactory;
import com.aishizhiyuzhe.spring.foremwork.core.BeanFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext extends DefaultListableBeanFactory implements BeanFactory {

    private String[] configLocations;
    private BeanDefinitionReader reader;
    private Map<String,Object> factoryBeanObjectCache=new ConcurrentHashMap<String, Object>();
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
        reader=new BeanDefinitionReader();

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
                throw new Exception("The ¡°"+beanDefinition.getFactoryBeanName()+"¡± is exists!!" );
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }

    }
    public Object getBean(String beanName) throws Exception {
        return null;
    }

    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

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
