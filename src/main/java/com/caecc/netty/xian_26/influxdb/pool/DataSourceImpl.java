package com.caecc.netty.xian_26.influxdb.pool;


import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


public class DataSourceImpl implements DataSource {

	private ReentrantLock lock = new ReentrantLock();

	//定义连接池中连接对象的存储容器
	private List<PoolConnection> list = Collections.synchronizedList(new ArrayList<>());



	//定义默认连接池属性配置
	private int initSize = 30;
	private int maxSize = 50;
	private int stepSize = 1;
	private int timeout = 2000;


	public DataSourceImpl() {
		try {
			initPool();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//初始化连接池
	private void initPool() throws SQLException {
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



	@Override
	public PoolConnection getDataSource() {
		PoolConnection poolConnection = null;

		try{
			lock.lock();

			//连接池对象为空时，初始化连接对象
			if(list.size() == 0){
				createConnection(initSize);
			}

			//获取可用连接对象
			poolConnection = getAvailableConnection();

			//没有可用连接对象时，等待连接对象的释放或者创建新的连接对象使用
			while(poolConnection == null){
				createConnection(stepSize);
				poolConnection = getAvailableConnection();

				if(poolConnection == null){
					TimeUnit.MILLISECONDS.sleep(10);
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}finally {
			lock.unlock();
		}
		return poolConnection;
	}


	//创建数据库连接
	private void createConnection(int count) throws SQLException{
		if(list.size() + count <= maxSize){
			for(int i = 0; i < count; i++){
				System.out.println("初始化了"+ (i + 1) +"个连接");
				list.add(new PoolConnection());
			}
		}
	}

	//获取可用连接对象
	private PoolConnection getAvailableConnection() throws SQLException{
		for(PoolConnection pool : list){
			if(pool.isStatus()){
				pool.setStatus(false);
				return pool;
			}
		}
		return null;
	}

}
