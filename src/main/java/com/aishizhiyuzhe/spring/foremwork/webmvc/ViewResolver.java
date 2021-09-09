package com.aishizhiyuzhe.spring.foremwork.webmvc;

import java.io.File;
import java.util.Locale;

//��һ����̬�ļ����һ����̬�ļ�
//�����û����Ͳ�ͬ�Ĳ�����������ͬ�Ľ��
public class ViewResolver {
    private final String DEFAULT_TEMPLATE_SUFFIX=".html";
    private File templateRootDir;
    private String viewName;

    public ViewResolver(String templateRoot){
        String templateRootPath=this.getClass().getClassLoader().getResource(templateRoot).getFile();
        this.templateRootDir=new File(templateRootPath);
    }

    public View resolveViewName(String viewName, Locale locale) throws Exception{
        this.viewName=viewName;
        if (null==viewName || "".equals(viewName.trim())){
            return null;
        }
        viewName=viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX)?viewName:(viewName+DEFAULT_TEMPLATE_SUFFIX);
        File templateFile=new File((templateRootDir.getParent()+"/"+viewName).replaceAll("/+","/"));
        return  new View(templateFile);
    }

    public String getViewName(){
        return viewName;
    }
}
