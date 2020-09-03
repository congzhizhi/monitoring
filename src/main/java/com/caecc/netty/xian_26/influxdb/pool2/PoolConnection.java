package com.caecc.netty.xian_26.influxdb.pool2;


import com.caecc.netty.xian_26.influxdb.InfluxDBUtil;

public class PoolConnection {
	
	private InfluxDBUtil connect;

	//false--繁忙，true--空闲
	private boolean status =true;
	

	public PoolConnection() {
		connect = new InfluxDBUtil();
		
	}
	
	public PoolConnection(InfluxDBUtil connect, boolean status) {
		this.connect = connect;
		this.status = status;
	}

	public InfluxDBUtil getConnect() {
		return connect;
	}
	public void setConnect(InfluxDBUtil connect) {
		this.connect = connect;
	}
	public boolean  isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	//释放连接池中的连接对象
	public void releaseConnect(){
		System.out.println("-----------释放连接-----------");
		this.status = true;
	}
	

}
