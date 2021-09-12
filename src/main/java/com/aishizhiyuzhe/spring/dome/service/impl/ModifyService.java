package com.aishizhiyuzhe.spring.dome.service.impl;

import com.aishizhiyuzhe.spring.dome.service.IModifyService;
import com.aishizhiyuzhe.spring.foremwork.annotation.Service;

@Service
public class ModifyService implements IModifyService {

    /**
     * 增加
     */
    public String add(String name,String addr) throws Exception {
        throw new Exception("这是Tom老师故意抛的异常！！");
        //return "modifyService add,name=" + name + ",addr=" + addr;
    }

    /**
     * 修改
     */
    public String edit(Integer id,String name) {
        return "modifyService edit,id=" + id + ",name=" + name;
    }

    /**
     * 删除
     */
    public String remove(Integer id) {
        return "modifyService id=" + id;
    }

}
