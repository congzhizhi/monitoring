package com.caecc.dao;


import com.caecc.model.WorkParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkParamDao {

     List<WorkParam> getAllWorkParams();
     public int insert(WorkParam workParam);
     public int updateById(WorkParam workParam);
     public int deleteById(int id);

}
