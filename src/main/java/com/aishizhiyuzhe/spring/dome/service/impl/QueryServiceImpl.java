package com.aishizhiyuzhe.spring.dome.service.impl;


import com.aishizhiyuzhe.spring.dome.service.IQueryService;
import com.aishizhiyuzhe.spring.foremwork.annotation.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class QueryServiceImpl implements IQueryService {
    public String query(String name) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=sdf.format(new Date());
        String json="{name:\""+name+"\" , time:\""+time+"\"}";
        return json;
    }
    
}
