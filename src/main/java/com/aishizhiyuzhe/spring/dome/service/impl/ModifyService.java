package com.aishizhiyuzhe.spring.dome.service.impl;

import com.aishizhiyuzhe.spring.dome.service.IModifyService;
import com.aishizhiyuzhe.spring.foremwork.annotation.Service;

@Service
public class ModifyService implements IModifyService {

    /**
     * ����
     */
    public String add(String name,String addr) throws Exception {
        throw new Exception("����Tom��ʦ�����׵��쳣����");
        //return "modifyService add,name=" + name + ",addr=" + addr;
    }

    /**
     * �޸�
     */
    public String edit(Integer id,String name) {
        return "modifyService edit,id=" + id + ",name=" + name;
    }

    /**
     * ɾ��
     */
    public String remove(Integer id) {
        return "modifyService id=" + id;
    }

}
