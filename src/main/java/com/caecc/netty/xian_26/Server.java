package com.caecc.netty.xian_26;

import com.caecc.netty.xian_26.accept.FrameAcceptHandler;
import com.caecc.netty.xian_26.accept.FrameAnalyseHandler;
import com.caecc.netty.xian_26.accept.HeartBeatHandler;
import com.caecc.netty.xian_26.redis.WorkParamMemoryCache;
import com.caecc.netty.xian_26.shentong.DBSessionFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Server {
	static private final Logger LOGGER = LoggerFactory.getLogger(Server.class);


	/**
	 * 初始化系统
	 */
	static void init(){

		/**
		 * 初始化数据库连接
		 */
		DBSessionFactory.init();

		/**
		 * 初始化数据库对应的内存缓存
		 */
		WorkParamMemoryCache.init();
	}
	public void bind(int port) throws Exception {
		init();

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try{
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup)
		.channel(NioServerSocketChannel.class)
		.option(ChannelOption.SO_BACKLOG, 100)
		.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				/**
				 * 设置接收缓冲区大小，320M
				 */
		.childOption(ChannelOption.SO_RCVBUF, 83886080<<2)
				/**
				 * 设置接收ByteBuf缓冲池大小,默认8M，通过参数14设置为268M
				 * pagesize页大小默认8K，这里设置65K，
				 * Page= 268435456/2048=131072，subpage=131072/2=65536
				 * 所有缓存池里有4096个subpage，也就是说缓存池一下能拿出4096个帧
				 * 如果想提高一倍，则可以将65546这个参数乘以2
				 */
//		.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
		.childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true,
																	PooledByteBufAllocator.defaultNumHeapArena(),
																	PooledByteBufAllocator.defaultNumDirectArena(),
																	65536,
																	12))
		/**
		 * 从缓冲池一次拿65KB的内存块
		 */
		.childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65536))
		/**
		 * 从缓冲池一次拿的快数自动扩容收缩
		 */
//		.childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator())

		.handler(new LoggingHandler(LogLevel.INFO))
		.childHandler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				// TODO Auto-generated method stub
				//针对客户端，如果1分钟之内没有向服务器发送读写心跳，则主动断开
				ch.pipeline().addLast(new IdleStateHandler(35,35,35));
				ch.pipeline().addLast(new HeartBeatHandler());
				ch.pipeline().addLast(new FrameAcceptHandler());
				ch.pipeline().addLast(new FrameAnalyseHandler());
			}


		});
		
		//bind port
		ChannelFuture f = b.bind(port).sync();
		if (f.isSuccess()){
			LOGGER.info("服务器启动成功");
		}
		//wait
		f.channel().closeFuture().sync();
		}finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int port = 8080;
		try {
			if(args != null && args.length > 0){
				port = Integer.parseInt(args[0]);
			}
			new Server().bind(port);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
