package com.caecc.dao;


import com.caecc.model.WorkParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkParamDao {

     List<WorkParam> getAllWorkParams();

}
