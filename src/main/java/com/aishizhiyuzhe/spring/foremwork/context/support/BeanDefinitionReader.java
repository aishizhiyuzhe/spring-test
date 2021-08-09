package com.aishizhiyuzhe.spring.foremwork.context.support;

import com.aishizhiyuzhe.spring.foremwork.beans.config.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

//对配置文件进行查找、读取、解析
public class BeanDefinitionReader {

    private List<String> registyBeanClasses=new ArrayList<String>();//登记
    private Properties config=new Properties();
    private final String SCAN_PACKAGE="scanPackage";

    public BeanDefinitionReader(String... location){
        InputStream is=this.getClass().getClassLoader().getResourceAsStream(location[0].replace("classpath:",""));
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (null!=is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void doScaner(String scanPackage){
        URL url=this.getClass().getClassLoader().getResource("/"+scanPackage.replaceAll("\\.","/"));
        File classPath=new File(url.getFile());
        for (File file:classPath.listFiles()){
            if (file.isDirectory()){
                doScaner(scanPackage+"."+file.getName());
            }else {
                if (!file.getName().endsWith(".class"))continue;
                String beanName=scanPackage+"."+file.getName().replaceAll(".class","");
                registyBeanClasses.add(beanName);
            }

        }
    }

    public Properties getConfig(){
        return this.config;
    }

    public List<BeanDefinition> loadBeanDefinitionss(){
        List<BeanDefinition> result=new ArrayList<BeanDefinition>();
        try {
            for (String className:registyBeanClasses){
                Class<?> beanClass=Class.forName(className);
                if (beanClass.isInterface())continue;
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()),beanClass.getName()));
                Class<?> []interfaces=beanClass.getInterfaces();
                for (Class<?> i:interfaces){
                    result.add(doCreateBeanDefinition(i.getName(),beanClass.getName()));
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    private BeanDefinition doCreateBeanDefinition(String factoryBeanName,String beanClassName){
        BeanDefinition beanDefinition=new BeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    private String toLowerFirstCase(String simpleName){
        char[] chars=simpleName.toCharArray();
        chars[0]+=32;
        return String.valueOf(chars);
    }
}
