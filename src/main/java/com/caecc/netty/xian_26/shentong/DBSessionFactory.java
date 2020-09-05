package com.caecc.netty.xian_26.shentong;


import com.caecc.dao.WorkParamDao;
import com.caecc.model.WorkParam;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import com.caecc.model.WorkParam;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * @description 数据库连接工厂
 * @author Cong ZhiZzhi
 * @date 2020-09-04 10:07
 */
public final class DBSessionFactory {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(DBSessionFactory.class);

    /**
     * MyBatis Sql 会话工厂
     */

    static private SqlSessionFactory _sqlSessionFactory;

    /**
     * 私有化类默认构造器
     */
    private DBSessionFactory() {
    }




    public static void main(String[] args) {

        init();
        SqlSession session = openSession();


        List<WorkParam> getAll= openSession().getMapper(WorkParamDao.class).getAllWorkParams();
        System.out.println();
    }

    /**
     * 初始化
     */
    static public void init() {
        try {
            LOGGER.info("初始化数据库连接");
            _sqlSessionFactory = (new SqlSessionFactoryBuilder()).build(
                Resources.getResourceAsStream("MyBatisConfig.xml")
            );

            // 测试数据库连接
            SqlSession tempSession = openSession();

            tempSession.getConnection()
                .createStatement()
                .execute("SELECT -1");

            tempSession.close();
            LOGGER.info("连接测试成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }


    static public SqlSession openSession() {
        if (null == _sqlSessionFactory) {
            throw new RuntimeException("_sqlSessionFactory 尚未初始化");
        }

        return _sqlSessionFactory.openSession(true);
    }

    /**
     * @description 返回一个mapper，注意要释放连接
     * @author Cong ZhiZzhi
     * @date 2020-09-04 10:12
     */
    public static <T> T getMapper(Class<T>  tClass){
        return _sqlSessionFactory.openSession().getMapper(tClass);

    }
}
