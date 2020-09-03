package com.caecc.netty.xian_26.influxdb;


import com.caecc.netty.xian_26.influxdb.pool2.InfluxDBPool;
import okhttp3.OkHttpClient;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.InfluxDBIOException;
import org.influxdb.dto.*;
import org.influxdb.impl.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class InfluxDBUtil {
    static private final Logger LOGGER = LoggerFactory.getLogger(InfluxDBUtil.class);
    /**
     * 数据库IP地址
     */
    private static String host = "192.168.0.224";
    /**
     * 数据库端口号
     */
    private static int port = 8086;
    /**
     * 时序单位
     */
    private static TimeUnit timeUtil = TimeUnit.NANOSECONDS;
    /**
     * 数据库名
     */
    private static String db = "frame";
    /**
     * 表名
     */
    private static String measurement = "frame";
    /**
     * 访问数据库的HTTP协议
     */
    private static String url = "http://" + host + ":" + port;
    /**
     * 数据库连接实例
     */
    public  InfluxDB influxDB;

     {
        connect();
     }

    /**
     * 连接数据库
     *
     * @throws InfluxDBIOException
     */
    public  void connect() throws InfluxDBIOException {
        //如果InfluxDB关闭，发请求时会报InfluxDBIOException异常
        //此时，不需要重连，influxDB启动后，发送的请求会自动链接
        influxDB = InfluxDBFactory.connect(url);
        influxDB.enableBatch(2000,20010,TimeUnit.MILLISECONDS);
        influxDB.setDatabase(db);
        if (!isConnected()) {
            LOGGER.error("启动链接 [influxdb:" + db + "] 失败");
            throw new InfluxDBIOException(new IOException("链接失败"));
        }

    }

    /**
     * 关闭数据库
     *
     * @return true 正常
     */
    public  void close() {
        influxDB.close();
    }

    /**
     * 测试连接是否正常
     *
     * @return true 正常
     */
    public  boolean isConnected() {
        boolean isConnected = false;
        try {
            Pong pong = influxDB.ping();
            if (pong != null && pong.isGood()) {
                isConnected = true;
            }
        } catch (Exception e) {
            isConnected = false;
            e.printStackTrace();
        }
        return isConnected;

    }

    /**
     * 构建Point,一条记录对应一个Point
     *
     * @param measurement
     * @param time
     * @param fields
     * @return
     */
    public  Point pointBuilder(String measurement, long time, Map<String, String> tags, Map<String, Object> fields) {
        Point.Builder point = Point.measurement(measurement);
        if (0 != time) {
            point.time(time, timeUtil);
        }
        point.tag(tags).fields(fields);
        return point.build();
    }


    /**
     * 构建多条Point
     *
     * @param points
     * @return
     */
    public  BatchPoints pointsBuilder(List<Point> points) {
        BatchPoints batchPoints =
                BatchPoints.database(db)
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();

        for (int i = 0; i < points.size(); i++) {
            batchPoints.point(points.get(i));
        }
        return batchPoints;
    }


    /**
     * 单条记录插入
     *
     * @param point
     */
    public  void insert(Point point) {
        influxDB.write(point);
    }

    /**
     * 批量写入记录
     *
     * @param batchPoints
     */
    public  void insert(BatchPoints batchPoints) {
        influxDB.write(batchPoints);
//        List<String> records = new ArrayList<String>();
//        records.add(batchPoints.lineProtocol());
//        influxDB.write(records);


        // influxDB.enableGzip();
        // influxDB.enableBatch(2000,100,TimeUnit.MILLISECONDS);
        // influxDB.disableGzip();
        // influxDB.disableBatch();
    }

    /**
     * 批量写入数据
     *
     * @param database
     *            数据库
     * @param retentionPolicy
     *            保存策略
     * @param consistency
     *            一致性
     * @param records
     *            要保存的数据（调用BatchPoints.lineProtocol()可得到一条record）
     */
//    public void batchInsert(final String database, final String retentionPolicy, final ConsistencyLevel consistency,
//                            final List<String> records) {
//        influxDB.write(database, retentionPolicy, consistency, records);
//    }


    /**
     * 测试用例
     *
     * @param tags
     * @param fields
     * @throws Exception
     */
    public  void writeWithTime(Map<String, String> tags, Map<String, Object> fields) throws Exception {
        BatchPoints batchPoints = BatchPoints.database(db).retentionPolicy("default")
                .consistency(InfluxDB.ConsistencyLevel.ALL).build();
        for (int i = 0; i < 10000; i++) {
            Point.Builder builder = Point.measurement(measurement);
            builder.tag(tags);
            builder.fields(fields);
            builder.time(1893015197740000000L + i, TimeUnit.NANOSECONDS).build();
            batchPoints.point(builder.build());
        }
        insert(batchPoints);
    }


    /**
     * 查询
     *
     * @param command 查询语句
     * @return
     */
    public  void query(String command) throws Exception {
        //默认查询是select * from mydb.autogen.demo_api
        QueryResult rs = influxDB.query(new Query(command));
        if (!rs.hasError() && !rs.getResults().isEmpty()) {
            List<QueryResult.Result> results = rs.getResults();

            rs.getResults().forEach(result -> {
                List<List<Object>> values = result.getSeries().get(0).getValues();
                for (List<Object> value : values) {
                    System.out.println(value.get(1));
                    System.out.println(value.get(2));
                }
            });


        }

    }

    public  void showLineProtocol() throws Exception {
        String line = Point.measurement("demo_api").tag("name", "hello")
                .addField("rt", 3).addField("times", 145).build().lineProtocol();

        System.out.println(line);
    }


    /**
     * 创建数据库
     *
     * @param dbName
     */
    @SuppressWarnings("deprecation")
    public  void createDB(String dbName) {
        if (!influxDB.databaseExists(dbName)) {
            influxDB.createDatabase(dbName);
        }
    }

    /**
     * 删除数据库
     *
     * @param dbName
     */
    @SuppressWarnings("deprecation")
    public  void deleteDB(String dbName) {
        influxDB.deleteDatabase(dbName);
    }

    /**
     * 创建自定义保留策略
     *
     * @param policyName  策略名
     * @param duration    保存天数
     * @param replication 保存副本数量
     * @param isDefault   是否设为默认保留策略
     */
    public  void createRetentionPolicy(String policyName, String duration, int replication, Boolean isDefault, String database) throws Exception {
        String sql = String.format("CREATE RETENTION POLICY \"%s\" ON \"%s\" DURATION %s REPLICATION %s ", policyName,
                database, duration, replication);
        if (isDefault) {
            sql = sql + " DEFAULT";
        }
        query(sql);
    }

    /**
     * 创建默认的保留策略
     *
     */
    public void createDefaultRetentionPolicy(String database) throws Exception {
        String command = String.format("CREATE RETENTION POLICY \"%s\" ON \"%s\" DURATION %s REPLICATION %s DEFAULT",
                "default", database, "30d", 1);
        query(command);
    }


    public static void main(String[] args) throws InterruptedException {
        InfluxDBPool.getConnection();

//        InfluxDBUtil influxDB = new InfluxDBUtil();
//        InfluxDBUtil influxDB1 = new InfluxDBUtil();
//        InfluxDBUtil influxDB2 =new InfluxDBUtil();
//        InfluxDBUtil influxDB3 = new InfluxDBUtil();
//        InfluxDBUtil influxDB4 = new InfluxDBUtil();
//        InfluxDBUtil influxDB5 = new InfluxDBUtil();
        InfluxDBUtil influxDB6 = new InfluxDBUtil();
            Long startTime = System.currentTimeMillis();
        //测试多条记录,时间为纳秒
        // 放在要检测的代码段前，取开始前的时间戳


        try {
//            deleteDB("test");
//            query("select * from frame");

            Map<String, String> tags = new HashMap<>();
            tags.put("location", "yantai");
            Map<String, Object> field = new HashMap<String, Object>();
            field.put("value", 13.5);
            //测试单条记录
//            insert(pointBuilder("frame",0,tags,field));


         /*   Thread thread1 =  new Thread(() -> {
//                InfluxDBUtil influxDB1 = null;
//                try {
//                    influxDB1 = InfluxDBPool.getConnection();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                List<Point> points5 = new ArrayList<>();
                for (int i = 0; i < 10000; i++) {
                    points5.add(influxDB1.pointBuilder("frame", 1198850751188689709L + i, tags, field));
                }
                influxDB1.insert(influxDB1.pointsBuilder(points5));
            });

            Thread thread2 = new Thread(() -> {
//                InfluxDBUtil influxDB2 = null;
//                try {
//                    influxDB2 = InfluxDBPool.getConnection();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                List<Point> points6 = new ArrayList<>();
                for (int i = 0; i < 1000; i++) {
                    points6.add(influxDB2.pointBuilder("frame", 1298850751188689709L + i, tags, field));
                }
                influxDB2.insert(influxDB2.pointsBuilder(points6));
            });
            Thread thread3 =  new Thread(() -> {
//                InfluxDBUtil influxDB3 = null;
//                try {
//                    influxDB3 = InfluxDBPool.getConnection();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                List<Point> points7 = new ArrayList<>();
                for (int i = 0; i < 1000; i++) {
                    points7.add(influxDB3.pointBuilder("frame", 1398850751188689709L + i, tags, field));
                }
                influxDB3.insert(influxDB3.pointsBuilder(points7));
            });

            Thread thread4 = new Thread(() -> {
//                InfluxDBUtil influxDB4 = null;
//                try {
//                    influxDB4 = InfluxDBPool.getConnection();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                List<Point> points1 = new ArrayList<>();
                for (int i = 0; i < 1000; i++) {
                    points1.add(influxDB4.pointBuilder("frame", 1698850751188689709L + i, tags, field));
                }
                influxDB4.insert(influxDB4.pointsBuilder(points1));
            });

            Thread thread5 =new Thread(() -> {
//                InfluxDBUtil influxDB5 = null;
//                try {
//                    influxDB5 = InfluxDBPool.getConnection();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                List<Point> points2 = new ArrayList<>();
                for (int i = 0; i < 1000; i++) {
                    points2.add(influxDB5.pointBuilder("frame", 1798850751188689709L + i, tags, field));
                }
                influxDB5.insert(influxDB5.pointsBuilder(points2));
            });
*/
            Thread thread6 = new Thread(() -> {
//                InfluxDBUtil influxDB6 = null;
//                try {
//                    influxDB6 = InfluxDBPool.getConnection();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                    Map<String, String> tag = new HashMap<>();
                    Map<String, Object> fiel = new HashMap<String, Object>();
                List<Point> points3 = new ArrayList<>();

                for (int i = 0; i < 120000; i++) {
                    tag.put("location", getRandomString(5)+ new Random().nextInt(10000));
                    tag.put("address", getRandomString(13)+ new Random().nextInt(6000));
                    tag.put("age", getRandomString(8)+ new Random().nextInt(10000));
                    fiel.put("value", 13.5+i);

                    points3.add(influxDB6.pointBuilder("frame", 198850751188689709L + i, tag, fiel));

                }
                long start = System.currentTimeMillis();
                influxDB6.insert(influxDB6.pointsBuilder(points3));
                long end = System.currentTimeMillis();
                System.out.println(".......耗时:"+(end-start));
                influxDB6.close();


                long start2 = System.currentTimeMillis();
                StringBuilder sb = new StringBuilder();
                for (Point point : points3) {
                    sb.append(point.lineProtocol(TimeUnit.NANOSECONDS)).append("\n");
                }
                 sb.toString();
                long end2 = System.currentTimeMillis();
                System.out.println(".......拼接耗时:"+(end2-start2));

            });

            //序列化
//            System.out.println(pointsBuilder(points).lineProtocol());
//            thread1.start();
//            thread2.start();
//            thread3.start();
//            thread4.start();
//            thread5.start();
            thread6.start();

//            thread1.join();
//            thread2.join();
//            thread3.join();
//            thread4.join();
//            thread5.join();
            thread6.join();

        } catch (
                InfluxDBIOException e) {
            e.printStackTrace();
            System.out.println("链接异常");
        } catch (
                Exception e) {
            e.printStackTrace();
        } finally {
            // 放在要检测的代码段后，取结束后的时间戳
            Long endTime = System.currentTimeMillis();

// 计算并打印耗时
            Long tempTime = (endTime - startTime);
            System.out.println("花费时间：" +
                    (((tempTime / 86400000) > 0) ? ((tempTime / 86400000) + "d") : "") +
                    ((((tempTime / 86400000) > 0) || ((tempTime % 86400000 / 3600000) > 0)) ? ((tempTime % 86400000 / 3600000) + "h") : ("")) +
                    ((((tempTime / 3600000) > 0) || ((tempTime % 3600000 / 60000) > 0)) ? ((tempTime % 3600000 / 60000) + "m") : ("")) +
                    ((((tempTime / 60000) > 0) || ((tempTime % 60000 / 1000) > 0)) ? ((tempTime % 60000 / 1000) + "s") : ("")) +
                    ((tempTime % 1000) + "ms"));
        }
    }



    public static void testInsert(){
        InfluxDBUtil influxDB6 = new InfluxDBUtil();
        Map<String, String> tag = new HashMap<>();
        Map<String, Object> fiel = new HashMap<String, Object>();
        List<Point> points3 = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            tag.put("location", getRandomString(5)+ new Random().nextInt(10000));
            tag.put("address", getRandomString(13)+ new Random().nextInt(6000));
            tag.put("age", getRandomString(8)+ new Random().nextInt(10000));
            fiel.put("value", 13.5+i);
            points3.add(influxDB6.pointBuilder("frame", 198850751188689709L + i, tag, fiel));
        }

        long start = System.currentTimeMillis();
        influxDB6.insert(influxDB6.pointsBuilder(points3));
        long end = System.currentTimeMillis();
        System.out.println(".......耗时:"+(end-start));
        influxDB6.close();
    }



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
