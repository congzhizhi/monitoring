package com.caecc.netty.xian_26.handle.impl;

import com.caecc.netty.xian_26.handle.inter.IFrameProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志帧处理器
 */
public class LogFrameProcessor implements IFrameProcessor {
    static private final Logger logger = LoggerFactory.getLogger(LogFrameProcessor.class);
    /**
     * 日志帧类型
     */
    private byte frameType = 4;

    /**
     * 日志帧处理类
     */
    @Override
    public void handle(ByteBuf frame) {

        logger.info("日志帧处理类");
        resolveFrame(frame);
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
