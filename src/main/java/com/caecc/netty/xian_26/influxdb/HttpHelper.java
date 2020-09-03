package com.caecc.netty.xian_26.influxdb;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class HttpHelper {

    CloseableHttpClient m_HttpClient;

    public HttpHelper() {
        m_HttpClient = HttpClients.createDefault();
    }


    // send bytes and recv bytes
    public byte[] post(String url, byte[] bytes, String contentType) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new ByteArrayEntity(bytes));
        if (contentType != null) {
            httpPost.setHeader("Content-type", contentType);
        }
        CloseableHttpResponse httpResponse = m_HttpClient.execute(httpPost);
        try {
            HttpEntity entityResponse = httpResponse.getEntity();
            if (entityResponse == null) {
                return null;
            }
            int contentLength = (int) entityResponse.getContentLength();

            if (contentLength <= 0) {
                throw new IOException("No response");
            }

            byte[] respBuffer = new byte[contentLength];

            if (entityResponse.getContent().read(respBuffer) != respBuffer.length) {
                throw new IOException("Read response buffer error");
            }

            return respBuffer;
        } finally {
            httpResponse.close();
        }
    }

    public byte[] post(String url, byte[] bytes) throws IOException {
        return post(url, bytes, null);
    }


    public static void main(String[] args) throws IOException {
        HttpHelper httpHelper = new HttpHelper();
        InfluxDBUtil influxDB6 = new InfluxDBUtil();
        List<Point> points3 = new ArrayList<>();
        Map<String, String> tag = new HashMap<>();
        Map<String, Object> fiel = new HashMap<String, Object>();
        for (int i = 0; i < 10000; i++) {
            tag.put("location", getRandomString(5) + new Random().nextInt(10000));
            tag.put("address", getRandomString(13) + new Random().nextInt(6000));
            tag.put("age", getRandomString(8) + new Random().nextInt(10000));
            fiel.put("value", 13.5 + i);

            points3.add(influxDB6.pointBuilder("frame", 198850751188689709L + i, tag, fiel));

        }



        BatchPoints batchPoints = influxDB6.pointsBuilder(points3);
        byte[] data = batchPoints.lineProtocol().getBytes();
        long start = System.currentTimeMillis();
        httpHelper.post("http://192.168.0.224:8086/write?db=frame", data);
        long end = System.currentTimeMillis();
        System.out.println(".......耗时:"+(end-start));


    }


    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
