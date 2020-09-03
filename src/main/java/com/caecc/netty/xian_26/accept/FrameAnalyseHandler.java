package com.caecc.netty.xian_26.accept;


import com.caecc.netty.xian_26.handle.threadpool.MainProcessorThreadPool;
import com.caecc.netty.xian_26.model.ProxyClientGroup;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameAnalyseHandler extends SimpleChannelInboundHandler<ByteBuf> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(FrameAnalyseHandler.class);

    /**
     * 读取完整的帧
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf frame) throws Exception {
        try {
            //logger.info("载荷"+ ByteBufUtil.hexDump(frame));
            /**
             *  交由线程池处理载荷帧
             */
            MainProcessorThreadPool.processFrame(frame.retain());

        } catch (Exception e) {
            LOGGER.error("帧处理出错：" + e.getMessage(), e);
        }
    }


    /**
     * 客户端成功接入后，将链接添加到组中
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (null == ctx) {
            return;
        }
        try {

            LOGGER.debug("客户端接入:" + ctx.channel().remoteAddress());

            /**
             * 将链接保存到组里
             */
            ctx.channel().attr(AttributeKey.valueOf("clientIP_Port")).set(ctx.channel().remoteAddress());
            ProxyClientGroup.addChannel(ctx.channel());

            /**
             * 下发工作参数
             */
            ByteBuf work_param_frame = Unpooled.buffer();
            /*****************************************帧头*******************************************/

            /**
             * 帧头
             */
            work_param_frame.writeShortLE(0x0710);
            /**
             * 帧长
             */
            work_param_frame.writeShortLE(19);
            /**
             * 帧类型
             */
            work_param_frame.writeByte(1);
            /*****************************************帧载荷*******************************************/
            /**
             * 网卡IP
             */
            work_param_frame.writeIntLE((int) ipToLong("192.168.0.224"));
            /**
             * 源IP
             */
            work_param_frame.writeIntLE((int) ipToLong("192.168.0.224"));
            /**
             * 源端口
             */
            work_param_frame.writeShortLE(12345);
            /**
             * 目的IP
             */
            work_param_frame.writeIntLE((int) ipToLong("224.0.0.1"));
            /**
             * 目的端口
             */
            work_param_frame.writeShortLE(7100);
            /**
             * 通信协议
             */
            work_param_frame.writeByte(2);
            /**
             * 工作模式
             */
            work_param_frame.writeByte(1);

            /*****************************************帧尾*******************************************/
            /**
             * 帧尾
             */
            work_param_frame.writeShortLE(0x7100);



            /**
             * 下发工作参数
             */
            ByteBuf work_param_frame2 = Unpooled.buffer();
            /*****************************************帧头*******************************************/

            /**
             * 帧头
             */
            work_param_frame2.writeShortLE(0x0710);
            /**
             * 帧长
             */
            work_param_frame2.writeShortLE(19);
            /**
             * 帧类型
             */
            work_param_frame2.writeByte(1);
            /*****************************************帧载荷*******************************************/
            /**
             * 网卡IP
             */
            work_param_frame2.writeIntLE((int) ipToLong("192.168.0.224"));
            /**
             * 源IP
             */
            work_param_frame2.writeIntLE((int) ipToLong("192.168.0.224"));
            /**
             * 源端口
             */
            work_param_frame2.writeShortLE(12346);
            /**
             * 目的IP
             */
            work_param_frame2.writeIntLE((int) ipToLong("224.0.0.1"));
            /**
             * 目的端口
             */
            work_param_frame2.writeShortLE(7100);
            /**
             * 通信协议
             */
            work_param_frame2.writeByte(2);
            /**
             * 工作模式
             */
            work_param_frame2.writeByte(1);

            /*****************************************帧尾*******************************************/
            /**
             * 帧尾
             */
            work_param_frame2.writeShortLE(0x7100);

            ProxyClientGroup.send(work_param_frame, ctx.channel());
            ProxyClientGroup.send(work_param_frame2, ctx.channel());


        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("客户端退出:" + ctx.channel().remoteAddress());
        ctx.fireChannelUnregistered();
    }


    /**
     * 客户端断开链接，将链接从组中移除
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        LOGGER.info("客户端退出:" + ctx.channel().remoteAddress());
        super.channelInactive(ctx);
        ProxyClientGroup.removeChannel(ctx.channel());
    }

    /**
     * 客户端链接异常，关闭连接
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.info("客户端链接退出:" + ctx.channel().remoteAddress());
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 将IP转换成int类型
     *
     * @param strIp
     * @return
     */
    public static long ipToLong(String strIp) {
        long[] ip = new long[4];
        //先找到IP地址字符串中.的位置
        int position1 = strIp.indexOf(".");
        int position2 = strIp.indexOf(".", position1 + 1);
        int position3 = strIp.indexOf(".", position2 + 1);
        //将每个.之间的字符串转换成整型
        ip[3] = Long.parseLong(strIp.substring(0, position1));
        ip[2] = Long.parseLong(strIp.substring(position1 + 1, position2));
        ip[1] = Long.parseLong(strIp.substring(position2 + 1, position3));
        ip[0] = Long.parseLong(strIp.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

}
