package com.caecc.netty.xian_26.accept;

import com.caecc.netty.xian_26.util.CONSTANT;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author:congzhizhi
 * @date:2020-08-13
 * @description:数据帧接收程序，按帧头、帧长、帧尾判别有效数据帧
 */
public class FrameAcceptHandler extends ByteToMessageDecoder {
    /*
     * 帧头标识，默认0x0710
     */
     byte[] headBytes = CONSTANT.headBytes;
    /*
     * 帧尾标识，默认0x7100
     */
     byte[] tailBytes = CONSTANT.headBytes;
     int tailValue ;
    /*
     * 构造netty的帧头缓冲区，用于帧搜索
     */
     ByteBuf head;
    /*
     * 构造netty的帧尾缓冲区，用于帧搜索
     */
     ByteBuf tail;
    /*
     * 日志记录
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(FrameAcceptHandler.class);


    public FrameAcceptHandler(){
        this.head = Unpooled.copiedBuffer(this.headBytes);
        this.tail=Unpooled.copiedBuffer(this.tailBytes);
        tailValue = this.tail.readUnsignedShortLE();
    }

    /**
     * 缓冲区解码
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ByteBuf childBuf = decode(in);
        /*
         * 解析出有效帧后，交由后续帧处理程序
         */
        Optional.ofNullable(childBuf).ifPresent((buf)->{
            out.add(buf);
        });
    }
    /**
     *
     * @description
     * 首先根据帧头帧尾获取数据帧，获取后根据帧长字段的值判断载荷帧的长度是否等于帧长，若不相等则丢掉
     *
     * +-----------------+--------------+----------------+----------+------------+
     * |       帧结构     |  帧头（2字节） | 帧长字段（2字节） | 数据帧+   |帧尾（2字节） |                       |
     * +-----------------+--------------+----------------+----------+------------+
     *
     * @param  buf:接收缓冲区
     * @return ByteBuf:有效数据帧（去掉帧头帧长帧尾）
     *
     **/
    protected ByteBuf decode(ByteBuf buf) {
        /*
         *  帧头起始位置
         */
        int headPosition = 0;
        /*
         *  载荷长度
         */
        int lengthFieldValue = 0;
        /*
         *  有效载荷帧
         */
        ByteBuf frame = null;
        /*
         *  获取帧头位置
         */
        //LOGGER.info("载荷"+ ByteBufUtil.hexDump(buf));
        headPosition = ByteBufUtil.indexOf(head, buf);
        /*
         *  headPosition>-1表示找到了帧头
         */
        if (headPosition > -1){
            /*
             *  防止有无效数据，将读指针移动到帧头位置
             */
            buf.readerIndex(headPosition);
            /*
             *  缓冲区必须至少有帧头和帧长4个字节
             */
            if (buf.readableBytes()<4){
                return null;
            }
            /*
             *  读取帧长字段的值
             */
             lengthFieldValue = buf.skipBytes(headBytes.length).readUnsignedShortLE();
            /*
             *  此时，如果缓冲区可读字节数<载荷+帧尾，不做处理，继续等待缓冲区接收新的数据，再做判断
             */
            if (buf.readableBytes()<lengthFieldValue+tailBytes.length){
                /*
                 * 重置读指针到帧头位置，以便下次检索
                 */
                buf.readerIndex(headPosition);
                return null;
            }else{
                /*
                 * 判断载荷后面两个字节是否是帧尾，获取这两个字节的值
                 */
                int tail= buf.skipBytes(lengthFieldValue).readUnsignedShortLE();
                /*
                 * 判断是否是帧尾，true表示是帧尾，否则不是
                 */
                if (tail == this.tailValue){
                       /*
                        * 定位到载荷位置
                        */
                        buf.readerIndex(headPosition+headBytes.length+2);
                        /*
                         * 截取载荷数据
                         */
                        frame = buf.readRetainedSlice(lengthFieldValue);
                        /*
                         * 有效帧解析完毕后，跳过帧尾，进行下一次的帧解析
                         */
                        buf.skipBytes(tailBytes.length);
                        /*
                         * 返回有效帧，交由帧解析程序
                         */

                        return frame;
                }else{
                        /*
                         * 没有找到帧尾，则丢弃帧头两个字节，从后面继续寻找有效帧头
                         */
                        buf.readerIndex(headPosition+2);
                        return null;
                }
            }
        }else{
            /*
             * 没有找到帧头，丢弃帧，但是保留一个字节，因为帧头由两个字节组成
             * 由于网络不稳定，缓冲区有可能按两次接收到完整的帧头
             */
            buf.skipBytes(buf.readableBytes()-1);
            return null;
        }
    }
}
