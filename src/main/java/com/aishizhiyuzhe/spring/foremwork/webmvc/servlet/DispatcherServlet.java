package com.aishizhiyuzhe.spring.foremwork.webmvc.servlet;

import com.aishizhiyuzhe.spring.foremwork.annotation.Controller;
import com.aishizhiyuzhe.spring.foremwork.annotation.RequestMapping;
import com.aishizhiyuzhe.spring.foremwork.annotation.RequestParam;
import com.aishizhiyuzhe.spring.foremwork.context.ApplicationContext;
import com.aishizhiyuzhe.spring.foremwork.webmvc.*;
import sun.rmi.runtime.Log;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DispatcherServlet extends HttpServlet {

    private final String LOCATION="contextConfigLocation";
    //保存的是类加方法加正则表达式
    private List<HandlerMapping> handlerMappings=new ArrayList<HandlerMapping>();
    private Map<HandlerMapping, HandlerAdapter> handlerAdaptors=new HashMap<HandlerMapping, HandlerAdapter>();
    private List<ViewResolver> viewResolers=new ArrayList<ViewResolver>();
    private ApplicationContext context;
    @Override
    public void init(ServletConfig config) throws ServletException {
        context=new ApplicationContext(config.getInitParameter(LOCATION));
        initStrategies(context);
    }
    
    protected void initStrategies(ApplicationContext context){
        initMultipartResolver(context);//文件上传解析
        initLocaleResolver(context);//本地化解析
        initThemeResolver(context);//主题解析

        initHandlerMappings(context);//通过HandlerMapping将请求映射到处理器
        initHandlerAdapters(context);//通过HandlerAdapter进行多类型的参数动态匹配
        
        initHandlerExceptionResolvers(context);//入个执行过程中遇到异常，进行处理
        initRequestToViewNameTranslator(context);//直接将请求解析到视图名
        
        initViewResolver(context);//通过viewResolver将逻辑视图解析到具体视图实现
        initFlashMapManager(context);//Flash映射管理器
    }

    private void initFlashMapManager(ApplicationContext context) {}
    private void initRequestToViewNameTranslator(ApplicationContext context) {}
    private void initHandlerExceptionResolvers(ApplicationContext context) {}
    private void initThemeResolver(ApplicationContext context) {}
    private void initLocaleResolver(ApplicationContext context) {}
    private void initMultipartResolver(ApplicationContext context) {}

    private void initHandlerMappings(ApplicationContext context) {
        //Map<String, Method> map;

        String[] beanNames=context.getBeanDefinitionNames();
        try {
            for (String beanName:beanNames){
                Object controller=context.getBean(beanName);
                Class<?> clazz=controller.getClass();
                if (!clazz.isAnnotationPresent(Controller.class)){
                    continue;
                }
                String baseUrl="";

                if (clazz.isAnnotationPresent(RequestMapping.class)){
                    RequestMapping requestMapping=clazz.getAnnotation(RequestMapping.class);
                    baseUrl=requestMapping.value();
                }
                Method[] methods=clazz.getMethods();
                for (Method method:methods){
                    if (!method.isAnnotationPresent(RequestMapping.class)){
                        continue;
                    }
                    RequestMapping requestMapping=method.getAnnotation(RequestMapping.class);
                    //不是很明白，什么情况下会出现*和+的情况
                    String regex=("/"+baseUrl+requestMapping.value().replaceAll("\\*",".*")).replaceAll("/+","/");
                    Pattern pattern=Pattern.compile(regex);
                    this.handlerMappings.add(new HandlerMapping(controller,method,pattern));
                    System.out.println("Mapping: "+regex+" , "+ method);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initHandlerAdapters(ApplicationContext context) {
        for (HandlerMapping handlerMapping:this.handlerMappings){
            this.handlerAdaptors.put(handlerMapping,new HandlerAdapter());
        }
    }

    private void initViewResolver(ApplicationContext context) {
        String templateRoot=context.getConfig().getProperty("templateRoot");
        String templateRootPath=this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir=new File(templateRootPath);
        for (File template:templateRootDir.listFiles()){
            this.viewResolers.add(new ViewResolver(template.getName()));
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req,resp);
        }catch (Exception e){
            resp.getWriter().write("<font size='25' color='blue'>500 Exception</font>");
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HandlerMapping handler=getHandler(req);
        if (handler==null){
            processDispatchResult(req,resp,new ModelAndView("404"));
            return;
        }
        HandlerAdapter ha=getHandlerAdapter(handler);
        ModelAndView mv=ha.handle(req,resp,handler);

        processDispatchResult(req,resp,mv);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, ModelAndView mv) throws Exception {
        if (null==mv){
            return;
        }
        if (this.viewResolers.isEmpty()){
            return;
        }
        if (this.viewResolers!=null){
            for (ViewResolver viewResolver:this.viewResolers){
                View view=viewResolver.resolveViewName(mv.getViewName(),null);
                if (view!=null){
                    view.render(mv.getModel(),req,resp);
                    return;
                }
            }
        }
    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping handler) {
        if (this.handlerAdaptors.isEmpty()){
            return null;
        }
        HandlerAdapter ha=this.handlerAdaptors.get(handler);
        if (ha.supports(handler)){
            return ha;
        }
        return null;
    }

    private HandlerMapping getHandler(HttpServletRequest req) {
        if (this.handlerAdaptors.isEmpty()){
            return null;
        }
        String url=req.getRequestURI();
        String contextPath=req.getContextPath();
        url=url.replace(contextPath,"").replaceAll("/+","/");
        for (HandlerMapping handler:this.handlerMappings){
            Matcher matcher=handler.getPattern().matcher(url);
            if (!matcher.matches()){
                continue;
            }
            return handler;
        }
        return null;
    }
}
