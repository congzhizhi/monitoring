package com.caecc.netty.xian_26.handle.threadpool;

import com.caecc.netty.xian_26.accept.FrameAcceptHandler;
import com.caecc.netty.xian_26.handle.factory.FrameTypeProcessorFactory;
import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * 数据帧通过线程池入库
 */
public class MainProcessorThreadPool {
    static private final Logger LOGGER = LoggerFactory.getLogger(FrameAcceptHandler.class);

    /**
     * CPU核心数
     */
    private static volatile int CPU_NUM = Runtime.getRuntime().availableProcessors();

    /**
     * 线程池核心线程数，等于CPU核数
     */
    private static volatile int CORE_POOL_SIZE = CPU_NUM;

    /**
     * 线程池最大线程数，等于CPU核数*2
     */
    private static volatile int MAXIMUM_POOL_SIZE = CPU_NUM * 2;

    /**
     * 线程池任务队列容量
     */
    private static volatile int QUEUE_SIZE = 10000;

    /**
     * 线程池任务队列类型
     */
    private static volatile BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);

    /**
     * 线程池空闲线程空闲存活时间
     */
    private static volatile long keepAliveTime = 60L;

    /**
     * 帧拒绝策略
     */
    private static volatile RejectedExecutionHandler frameDiscardPolicy = new FrameDiscardPolicy();

    /**
     * 自定义线程池
     */
    private static volatile ThreadPoolExecutor pool = null;

    static {
        pool = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                keepAliveTime,
                TimeUnit.SECONDS,
                workQueue, frameDiscardPolicy);

        /**
         * 预加载核心线程数
         */
        pool.prestartCoreThread();

        /**
         * 监控线程池,每5秒输出一次参数值
         */
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                statics();

            }
        }).start();
    }

    /**
     * 帧处理，交由线程池进行IO操作，处理入库
     */
    public static void processFrame(ByteBuf frame) {
        Objects.requireNonNull(frame);
        pool.execute(() -> {

            /**
             * 获取帧类型
             */
            byte frameType = frame.readByte();

            /**
             * 根据帧类型选取帧处理策略，并进行帧解析处理
             */
            FrameTypeProcessorFactory.create(frameType).handle(frame);

        });
    }


    /**
     * 线程池性能检测
     */
    public static void statics() {
        LOGGER.info("【当前活跃线程数:" + pool.getActiveCount() + "】");
        LOGGER.info("【当前活跃线程数:" + pool.getPoolSize() + "】");
        LOGGER.info("【正在执行的任务数量:" + pool.getActiveCount() + "】");
        LOGGER.info("【已经执行的任务数:" + pool.getCompletedTaskCount() + "】");
        LOGGER.info("【任务总数:" + pool.getTaskCount() + "】");
        LOGGER.info("【队列积压任务数:" + pool.getQueue().size() + "】");

    }


}


/**
 * 帧拒绝策略
 * 当线程池线程已满且工作队列已满时的帧拒绝策略
 * 主线程阻塞添加任务
 */
class FrameDiscardPolicy implements RejectedExecutionHandler {

    @SneakyThrows
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        if (!e.isShutdown()) {
            System.out.println("___________________________________警告：拒决绝策略生效！____________________________________________");
            e.getQueue().put(r);
        }
    }
}