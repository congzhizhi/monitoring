package com.caecc.netty.xian_26.redis;


import com.caecc.dao.WorkParamDao;
import com.caecc.model.WorkParam;
import com.caecc.netty.xian_26.shentong.DBSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 工作参数缓存
 */
public class WorkParamMemoryCache {

    static private final Logger LOGGER = LoggerFactory.getLogger(WorkParamMemoryCache.class);
    /**
     * 工作参数缓存对象
     */
    private static ConcurrentHashMap<String ,List<WorkParam>> workParamMap = new ConcurrentHashMap<>();


    /**
     * 初始化工作参数缓存
     */
    public static  void init(){

        LOGGER.info("开始加载工作参数缓存");
        /**
         * 从数据库加载工作参数信息，以下拿到所有的工作参数
         */
        List<WorkParam>  workParam = DBSessionFactory.openSession().getMapper(WorkParamDao.class).getAllWorkParams();

        //现根据srcIp进行分组
        workParam.stream().collect(Collectors.groupingBy(WorkParam::getSrcIP)).forEach((srcIp,subWorkParam)->{
            System.out.println(srcIp);

            //再根据srcPort进行分组
            subWorkParam.stream().collect(Collectors.groupingBy(WorkParam::getSrcPort)).forEach((srcPort,treeWorkParam)->{

                List<WorkParam> ipPort  = new ArrayList();
                treeWorkParam.forEach(param->{
                    ipPort.add(param);
                });
                //一个ip+port对应一个下发参数
                workParamMap.put(srcIp+":"+srcPort,ipPort);
            });
        });

        LOGGER.info("工作参数缓存加载完毕");
    }

    /**
     * 根据ip和端口取缓存拿数据
     * @param ip
     * @param port
     * @return
     */
    public static List<WorkParam> getWorkParamByIpPort(int ip,int port){
        return  workParamMap.get(ip+":"+"port");
    }

    /**
     * 刷新缓存
     */
    public static void refresh(){
        workParamMap.clear(); //first , clear caching map
        init();// second , init caching  map
    }

    /**
     * 根据ip和端口删除缓存
     * @param ip
     * @param port
     */
    public static void delete(int ip,int port){
        workParamMap.remove(ip+":"+port);
    }

    /**
     * 根据ip和端口添加缓存
     * @param ip
     * @param port
     */
    public static void add(int ip,int port,WorkParam workParam){
//        workParamMap.put(ip+":"+port,ipPort);
    }

}
