package com.caecc.netty.xian_26.handle.impl;

import com.caecc.netty.xian_26.handle.inter.IFrameProcessor;
import com.caecc.netty.xian_26.influxdb.InfluxDBUtil;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 采集数据上报帧处理器
 */
public class DataUploadFrameProcessor implements IFrameProcessor {
    static private final Logger LOGGER = LoggerFactory.getLogger(DataUploadFrameProcessor.class);
    static InfluxDBUtil influxDB6 = new InfluxDBUtil();



    static AtomicInteger count = new AtomicInteger(1);
    /**
     * 包数统计
     */
    volatile  static int packageSize = 0;
    public static AtomicLong framSize = new AtomicLong(0);
    /**
     * 帧个数统计
     */
    public static void main(String[] args) {

    }
    private byte frameType = 3;
    /**
     * 采集数据上报帧处理
     * @param frame
     */

    static {
        new Thread(()->{
            while (true){
                System.out.println("记录数:"+packageSize);
                System.out.println("帧数:"+ framSize.get());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public synchronized  void add(int val){
        packageSize += val;
    }
    @Override
    public void handle(ByteBuf frame)  {

//        LOGGER.info("载荷"+ ByteBufUtil.hexDump(frame));
        ByteBuf lianlu = null;
        ByteBuf data = null;
        int segment = 0;
        try {
            frame.readByte();//读取数据补传标志
            lianlu =  frame.readRetainedSlice(14);

            byte protocol  = lianlu.skipBytes(12).readByte();
           byte liuliang = frame.readByte();
           if (liuliang == 1){
              int pps =  frame.readIntLE();
              int Bps = frame.readIntLE();
              frame.skipBytes(8);
//              LOGGER.info("包流量："+pps+"----字节流量："+Bps);
           }else{
               framSize.getAndIncrement();
           }
            segment= frame.readUnsignedShortLE();
           add(segment);
           int seglen = 0;
            for (int i = 0; i <segment ; i++) {
                frame.skipBytes(8);
                if (protocol == 3){
                    seglen = frame.readUnsignedShortLE();
                }else if(protocol == 1){
                    seglen = 32;
                }else{
                    seglen = 24;
                }
                data = frame.readRetainedSlice(seglen);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }



        //入库IO
        try {
            List<Point> points3 = new ArrayList<>();
            Random random =  new Random();
            for (int i = 0; i < segment; i++) {
                Map<String, String> tag = new HashMap<>();
                Map<String, Object> fiel = new HashMap<String, Object>();
                tag.put("location", getRandomString(5)+ random.nextInt(100));
                tag.put("address", getRandomString(3)+ random.nextInt(60));
                tag.put("age", getRandomString(6)+random.nextInt(100));
                fiel.put("value", 10+i);
                points3.add(influxDB6.pointBuilder("frame", 178850751188689709L + count.incrementAndGet(), tag, fiel));
            }

            influxDB6.insert(influxDB6.pointsBuilder(points3));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        ReferenceCountUtil.release(lianlu);
        ReferenceCountUtil.release(frame);
        ReferenceCountUtil.release(data);

    }

    /**
     * 获取日志帧类型
     */
    @Override
    public byte getFrameType() {
        return frameType;
    }


    /**
     * 模拟数据
     * @param length
     * @return
     */
    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

}
