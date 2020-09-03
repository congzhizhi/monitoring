package com.caecc.netty.xian_26.influxdb.pool2;

import com.caecc.netty.xian_26.influxdb.InfluxDBUtil;
import lombok.SneakyThrows;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class InfluxDBPool {

    private static List<InfluxDBUtil> list = new ArrayList<>();
    private static BlockingQueue<InfluxDBUtil> blockingQueue = new LinkedBlockingQueue<InfluxDBUtil>();
    //定义默认连接池属性配置
    private static int initSize = 50;


    static {
        try {
            initPool();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //初始化连接池
    private static void initPool() throws SQLException {
//		String init = PropertiesHolder.getInstance().getProperty("initSize");
//		String step = PropertiesHolder.getInstance().getProperty("stepSize");
//		String max = PropertiesHolder.getInstance().getProperty("maxSize");
//		String time = PropertiesHolder.getInstance().getProperty("timeout");
//
//		initSize = init==null? initSize : Integer.parseInt(init);
//		maxSize = max==null? maxSize : Integer.parseInt(max);
//		stepSize = step==null? stepSize : Integer.parseInt(step);
//		timeout = time==null? timeout : Integer.parseInt(time);
        createConnection(initSize);

    }

    @SneakyThrows
    private static void createConnection(int initSize) {

        for (int i = 0; i <initSize ; i++) {
            InfluxDBUtil poolConnection = new InfluxDBUtil();
            list.add(poolConnection);
            blockingQueue.put(poolConnection);
        }
    }

    public static InfluxDBUtil getConnection() throws InterruptedException {
        InfluxDBUtil connection = blockingQueue.take();
        return connection;
    }

    public static void realease(InfluxDBUtil connection) throws InterruptedException {
        blockingQueue.put(connection);
    }



    public static void main(String[] args) throws Exception {
        for (int i = 0; i <50 ; i++) {
            new Thread(()->{
                InfluxDBUtil conn = null;
                try {
                     conn= InfluxDBPool.getConnection();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(5000);
                    InfluxDBPool.realease(conn);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        Thread.sleep(2000);
        System.out.println("-----------------");
        for (int i = 0; i <10 ; i++) {
            new Thread(()->{
                try {
                    InfluxDBUtil conn= InfluxDBPool.getConnection();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }).start();
        }


    }


}
