package com.caecc.netty.xian_26.handle.distribute;

import com.caecc.model.WorkParam;
import com.caecc.netty.xian_26.model.ProxyClientGroup;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

/**
 * 下发工作参数给代理程序
 */
public class DistributeWorkParamToProxy {


    public static void  distribute(Channel channel , WorkParam workParam){

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
        work_param_frame.writeIntLE((int)workParam.getEthIP());
        /**
         * 源IP
         */
        work_param_frame.writeIntLE((int) workParam.getSrcIP());
        /**
         * 源端口
         */
        work_param_frame.writeShortLE(workParam.getSrcPort());
        /**
         * 目的IP
         */
        work_param_frame.writeIntLE(workParam.getDesIP());
        /**
         * 目的端口
         */
        work_param_frame.writeShortLE(workParam.getDesPort());
        /**
         * 通信协议
         */
        work_param_frame.writeByte(workParam.getProtocol());
        /**
         * 工作模式
         */
        work_param_frame.writeByte(workParam.getMode());

        /*****************************************帧尾*******************************************/
        /**
         * 帧尾
         */
        work_param_frame.writeShortLE(0x7100);


        /**
         * 发送给客户端
         */
        ProxyClientGroup.send(work_param_frame, channel);

    }


}
