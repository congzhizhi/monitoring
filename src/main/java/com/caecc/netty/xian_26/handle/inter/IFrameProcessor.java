package com.caecc.netty.xian_26.handle.inter;

import io.netty.buffer.ByteBuf;

/**
 * 帧处理接口
 */
public interface IFrameProcessor {

    /**
     * 帧处理接口
     */
    public void handle(ByteBuf frame) ;

    /**
     * 获取帧类型
     */
    public byte getFrameType();

    /**
     * 帧解析统一实现
     */
    default Object resolveFrame(ByteBuf frame){
        System.out.println("解析数据帧");
        return null;
    }
}
