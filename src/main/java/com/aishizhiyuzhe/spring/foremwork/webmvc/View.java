package com.aishizhiyuzhe.spring.foremwork.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class View {
    public static final String DEFAULT_CONTENT_TYPE="text/html;charset=utf-8";
    private File viewFile;

    public View(File viewFile){
        this.viewFile=viewFile;
    }

    public String  getContentType(){
        return DEFAULT_CONTENT_TYPE;
    }

    public void render(Map<String,?> model, HttpServletRequest request, HttpServletResponse response) throws Exception{
        StringBuffer sb=new StringBuffer();
        RandomAccessFile ra=new RandomAccessFile(this.viewFile,"r");

        try {
            String line=null;
            while (null !=(line=ra.readLine())){
                line=new String(line.getBytes("ISO-8859-1"),"utf-8");
                Pattern pattern=Pattern.compile("гд\\{[^\\}]+\\}",Pattern.CASE_INSENSITIVE);
                Matcher matcher=pattern.matcher(line);

                while (matcher.find()){
                    String paramName=matcher.group();
                    paramName=paramName.replaceAll("гд\\{|\\}","");
                    Object paramValue=model.get(paramName);
                    if (null==paramValue){continue;}

                    line=matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                    matcher=pattern.matcher(line);
                }
                sb.append(line);
            }
        }finally {
            ra.close();
        }
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(sb.toString());
    }

    public static String makeStringForRegExp(String str){
        return str.replace("\\","\\\\").replace("*","\\*")
                .replace("+","\\+").replace("|","\\|")
                .replace("{","\\{").replace("}","\\}")
                .replace("(","\\(").replace(")","\\)")
                .replace("^","\\^").replace("$","\\$")
                .replace("[","\\[").replace("]","\\]")
                .replace("?","\\?").replace(",","\\,")
                .replace(".","\\.").replace("&","\\&");
    }
}
