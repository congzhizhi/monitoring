package com.caecc.netty.xian_26.handle.factory;

import com.caecc.netty.xian_26.handle.inter.IFrameProcessor;
import com.caecc.netty.xian_26.util.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class FrameTypeProcessorFactory {
    static private final Logger LOGGER = LoggerFactory.getLogger(FrameTypeProcessorFactory.class);

    /**
     * 帧类型与帧处理器映射集合
     */
    static public final Map<Byte, IFrameProcessor> _processorMap = new HashMap<>();

    /**
     * 帧类型集合
     */
    static public final List<Byte>  _frameList = new ArrayList<Byte>();

    /*
     * 本类测试
     */
    public static void main(String[] args) {
        FrameTypeProcessorFactory frameTypeProcessorFactory = new FrameTypeProcessorFactory();
        FrameTypeProcessorFactory._processorMap.get(2);
        System.out.println();
    }
    static {
        /*
         *获取包名称
         */
        final String packageName = FrameTypeProcessorFactory.class.getPackage().getName();

        /*
         *获取 IFrameProcessor 所有的实现类
         */
        Set<Class<?>> clazzSet = PackageUtil.listSubClazz(
                packageName,
                true,
                IFrameProcessor.class
        );
        /*
         *遍历IFrameProcessor 所有的实现类，存到帧处理器集合中
         */
        for (Class<?> processorClazz : clazzSet) {
            if (null == processorClazz ||
                    0 != (processorClazz.getModifiers() & Modifier.ABSTRACT)) {
                continue;
            }
            // 获取方法数组
            Method[] methodArray = processorClazz.getDeclaredMethods();
            // 消息类型
            Class<?> cmdClazz = null;

            /*
             *循环镇处理器方法，找到获取帧处理器编号的方法；
             *将帧处理器类型编号和帧类型处理器实例进行映射
             */
            for (Method currMethod : methodArray) {
                if (null == currMethod ||
                        !currMethod.getName().equals("getFrameType")) {
                    continue;
                }
                try {

                    /**
                     * 创建帧处理器实例
                     */
                    IFrameProcessor newProcessor= (IFrameProcessor) processorClazz.newInstance();

                    /**
                     * 返回帧处理器对应的帧类型编号
                     */
                    Object retu = currMethod.invoke(newProcessor,null);
                    if (retu == null){break;}


                    /**
                     * 判断帧类型ID是否重复
                     */
                    if (_frameList.contains(Byte.valueOf(retu.toString()))){
                        LOGGER.error("帧类型ID重复",new IllegalArgumentException("帧类型ID重复"));
                    }

                    /**
                     * 将帧类型添加到集合中
                     */
                    _frameList.add(Byte.valueOf(retu.toString()));

                    /**
                     * 将帧处理器类型编号和帧类型处理器实例进行映射
                     */
                    _processorMap.put(Byte.valueOf(retu.toString()), newProcessor);

                } catch (Exception ex) {
                    // 记录错误日志
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }
    }

    /**
     * 创建帧处理器
     *
     * @param frameType 帧类型
     * @return
     */
    static public IFrameProcessor create(byte frameType)  {
        /**
         * 帧类型校验
         */
        validate(frameType);

        return _processorMap.get(frameType);
    }

    /**
     * 帧类型数据校验
     */
    static private void validate(byte frameType)  {
        if (!_frameList.contains(frameType)) {
            LOGGER.error("帧类型传递错误:" + frameType);
        }
    }

}
