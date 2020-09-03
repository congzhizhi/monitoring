package com.caecc.netty.xian_26.handle.impl;

import com.caecc.netty.xian_26.handle.inter.IFrameProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 工作参数接收帧处理器
 */
public class WorkParamAckFrameProcessor implements IFrameProcessor {
    static private final Logger logger = LoggerFactory.getLogger(WorkParamAckFrameProcessor.class);
    private byte frameType = 2;

    /**
     * 工作参数接收帧处理
     */
    @Override
    public void handle(ByteBuf frame) {
        logger.info("作参数接收帧处理");
        logger.info("载荷:"+ ByteBufUtil.hexDump(frame));
        ReferenceCountUtil.release(frame);

    }

    /**
     * 获取日志帧类型
     */
    @Override
    public byte getFrameType() {
        return frameType;
    }
}
