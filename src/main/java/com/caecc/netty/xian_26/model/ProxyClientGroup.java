package com.caecc.netty.xian_26.model;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Optional;
import java.util.Random;

public final class ProxyClientGroup {

    /**
     * 链接组，保存所有客户端链接
     */
    static private final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private ProxyClientGroup() {
    }

    /**
     * 添加代理客户端通道
     *
     * @param ch
     */
    static public void addChannel(Channel ch) {
        Optional.ofNullable(ch).ifPresent(channel -> {
            _channelGroup.add(ch);
        });
    }

    /**
     * 移除代理客户端通道
     *
     * @param ch
     */
    static public void removeChannel(Channel ch) {
        Optional.ofNullable(ch).ifPresent(channel -> {
            _channelGroup.remove(ch);

        });
    }

    /**
     * 向所有代理客户端广播消息
     *
     * @param message 广播的消息
     */
    static public void broadcast(ByteBuf message) {
        Optional.ofNullable(message).ifPresent(msg -> {
            _channelGroup.writeAndFlush(msg);

        });
    }

    /**
     * 向某一个代理客户端发送消息
     *
     * @param message 消息
     * @param ch      代理客户端对应的channel通道
     */
    static public void send(ByteBuf message, Channel ch) {
        //数据发送前，先判断链路是否处于链接状态
        Optional.ofNullable(message)
                .map(c->ch)
                .ifPresent(channel -> channel.writeAndFlush(message));

    }
    /**
     * 根据IP和端口找到channel，并发送消息
     *
     * @param message 消息
     * @param ip 代理程序ip地址
     * @param port 代理程序端口
     */
    static public void send(ByteBuf message, String ip ,int port) {
        StringBuilder remoteAddress = new StringBuilder();
        remoteAddress.append("/").append(ip).append(":").append(port);
        _channelGroup.forEach(channel -> {
            String clientIP_Port = (String) channel.attr(AttributeKey.valueOf("clientIP_Port")).get();
            if (remoteAddress.equals(clientIP_Port)){
                channel.writeAndFlush(message);
                return;
            }
            // 获取攻击用户 Id

        });
    }
}
