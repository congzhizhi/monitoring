package com.caecc.netty.xian_26.accept;


import com.caecc.netty.xian_26.handle.impl.DataUploadFrameProcessor;
import com.caecc.netty.xian_26.handle.impl.LogFrameProcessor;
import com.caecc.netty.xian_26.handle.impl.WorkParamAckFrameProcessor;
import com.caecc.netty.xian_26.handle.inter.IFrameProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:congzhizhi
 * @date:2020-08-13
 * @description:数据帧类型
 */
@Deprecated
public enum FrameTypeAndProcessor {

    /**********************************************载荷帧类型定义*******************************************/
    /*
     * 工作参数下发数据帧
     */
    @Deprecated
    WORK_PARAM_DOWNLOAD((byte) 1, "工作参数下发数据帧", null),

    /*
     * 工作参数接收响应帧
     */
    WORK_PARAM_ACK((byte) 2, "工作参数接收响应帧", new WorkParamAckFrameProcessor()),

    /*
     * 采集数据上报数据帧
     */
    DATA_UPLOAD((byte) 3, "采集数据上报数据帧", new DataUploadFrameProcessor()),

    /*
     * 日志数据帧
     */
    LOGGER((byte) 4, "日志数据帧", new LogFrameProcessor());


    /**********************************************载荷帧的参数及操作方法*************************************/
    /*
     * 帧类型id
     */
    private byte id;
    /*
     * 帧类型说明
     */
    private String value;
    /*
     * 帧类型对应的处理器
     */
    private IFrameProcessor iFrameHandle;

    /*
     * 构造器
     * @param id    帧类型id
     * @param value     帧类型说明
     * @param iFrameHandle  帧类型对应的帧处理器
     */
    private FrameTypeAndProcessor(byte id, String value, IFrameProcessor iFrameHandle) {
        this.id = id;
        this.value = value;
        this.iFrameHandle = iFrameHandle;
    }

    /*
     * 帧类型映射
     * @param value
     * @return
     */
    static Map<Byte, FrameTypeAndProcessor> frameTypeMap = new HashMap<Byte, FrameTypeAndProcessor>();

    /*
     * 初始化帧类型映射关系，方便查找
     */
    static {
        for (FrameTypeAndProcessor framType : FrameTypeAndProcessor.values()) {
            frameTypeMap.put(framType.id, framType);
        }
    }

    /*
     * 查找帧类型
     * @param value
     * @return
     */
    public static FrameTypeAndProcessor type(byte value) {
        return frameTypeMap.get(value);
    }

    /*
     * 获取帧类型id
     * @return id
     */
    public byte getId() {
        return id;
    }

    /*
     * 获取帧类型说明
     * @return value
     */
    public String getValue() {
        return value;
    }

    /*
     * 获取帧类型对应的处理器
     * @return iFrameHandle
     */
    public IFrameProcessor getIFrameHandle() {
        return iFrameHandle;
    }
}
