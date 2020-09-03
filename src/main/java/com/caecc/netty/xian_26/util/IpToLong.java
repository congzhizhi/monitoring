package com.caecc.netty.xian_26.util;

public class IpToLong {

    /**
     * 将IP转换成int类型
     *
     * @param strIp
     * @return
     */
    public static int ipToLong(String strIp) {
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
        return (int) ((ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3]);
    }


    public static void main(String[] args) {
        System.out.println(ipToLong("192.168.0.224"));
        System.out.println(ipToLong("224.0.0.1"));
    }
}
