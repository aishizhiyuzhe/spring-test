package com.aishizhiyuzhe.spring.foremwork.webmvc;

import com.aishizhiyuzhe.spring.foremwork.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Laijh
 * @Description:
 * @Date: Created in 2021/9/6/22:45
 * @Modified By:����ת��
 */
public class HandlerAdapter {

    public boolean supports(Object handler){
        return (handler instanceof  HandlerMapping);
    }

    public ModelAndView handle(HttpServletRequest req, HttpServletResponse resp,Object handler) throws Exception {
        HandlerMapping handlerMapping=(HandlerMapping)handler;
        //ÿ��������һ�������б����ﱣ������β��б�
        Map<String,Integer> paramMapping=new HashMap<String, Integer>();
        //����ֻ�Ǹ�����������
        //ֻ��עȡ�˱������β�
        Annotation[][] pa=handlerMapping.getMethod().getParameterAnnotations();
        for (int i=0;i<pa.length;i++){
            for (Annotation an:pa[i]){
                if (an instanceof RequestParam){
                    String paramName=((RequestParam)an).value();
                    if (!"".equals(paramName.trim())){
                        //Ϊʲô��λ����Ϊvalue�������ǽ�������Ϊvalue
                        //��Ϊ���������������name�������ǵڼ�����ͨ��name�ҵ��ڵڼ���λ����
                        paramMapping.put(paramName,i);
                    }
                }
            }
        }
        //ֻ��עrequest��response
        Class<?>[] paramTypes=handlerMapping.getMethod().getParameterTypes();
        for (int i=0;i<paramTypes.length;i++){
            Class<?> type=paramTypes[i];
            if (type==HttpServletRequest.class||type==HttpServletResponse.class){
                paramMapping.put(type.getName(),i);
            }
        }

        Map<String,String[]> reqParameterMap=req.getParameterMap();

        Object[] paramValues=new Object[paramTypes.length];

        for (Map.Entry<String,String[]> param:reqParameterMap.entrySet()){
            //�����ΪʲôҪ�滻���
            String value= Arrays.toString(param.getValue()).replaceAll("\\[|\\]","").replaceAll("\\s","");
            if (!paramMapping.containsKey(param.getKey())){
                continue;
            }
            int index=paramMapping.get(param.getKey());
            paramValues[index]=caseStringValue(value,paramTypes[index]);
        }
        if (paramMapping.containsKey(HttpServletRequest.class.getName())){
            int reqIndex=paramMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex]=req;
        }
        if (paramMapping.containsKey(HttpServletResponse.class.getName())){
            int respIndex=paramMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex]=resp;
        }

        Object result=handlerMapping.getMethod().invoke(handlerMapping.getController(),paramValues);

        if (result==null){
            return null;
        }
        boolean isModelAndView=handlerMapping.getMethod().getReturnType()==ModelAndView.class;
        if (isModelAndView){
            return (ModelAndView)result;
        }else {
            return null;
        }
    }

    private Object caseStringValue(String value,Class<?> clazz){
        if (clazz==String.class){
            return value;
        }else if (clazz==Integer.class){
            return Integer.valueOf(value);
        }else if (clazz==int.class){
            return Integer.valueOf(value);
        }else {
            return null;
        }
    }
}
