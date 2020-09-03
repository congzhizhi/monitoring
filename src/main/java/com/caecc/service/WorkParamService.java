package com.caecc.service;

import com.caecc.dao.WorkParamDao;
import com.caecc.model.WorkParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class WorkParamService {


    @Autowired
    WorkParamDao workParamDao;


    public List<WorkParam> getAll(){
        List<WorkParam> list =  workParamDao.getAllWorkParams();
        return list;

    }
}
