package com.caecc.dao;


import com.caecc.model.WorkParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkParamDao {

     /**
      * @description 查询所有
      * @author Cong ZhiZzhi
      * @date 2020-09-05 19:23
      */
     List<WorkParam> getAllWorkParams();
     /**
      * @description 根据id查找
      * @author Cong ZhiZzhi
      * @date 2020-09-05 19:23
      */
     WorkParam getWorkParamById(int id);
     /**
      * @description 多条件并行查询
      * @author Cong ZhiZzhi
      * @date 2020-09-05 19:24
      */
     List<WorkParam>  getByConditions(WorkParam workParam);
     /**
      * @description 根据ID数组查找
      * @author Cong ZhiZzhi
      * @date 2020-09-05 19:24
      */
     List<WorkParam>  getByIdList(@Param("ids") List<Integer> ids);
     /**
      * @description 插入
      * @author Cong ZhiZzhi
      * @date 2020-09-05 19:24
      */
     public int insert(WorkParam workParam);
     /**
      * @description 跟新一条记录
      * @author Cong ZhiZzhi
      * @date 2020-09-05 19:24
      */
     public int updateById(WorkParam workParam);
     /**
      * @description 删除一条数据
      * @author Cong ZhiZzhi
      * @date 2020-09-05 19:24
      */
     public int deleteById(int id);

}
