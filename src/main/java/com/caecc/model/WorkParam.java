package com.caecc.model;

import lombok.Data;

/**
 * 工作参数下发实体
 *
 *  CREATE TABLE MONITOR.WORK_PARAM (
 * 	ETH_IP INT4,
 * 	ID _INT4 NOT NULL,
 * 	SRC_IP INT4,
 * 	SRC_PORT INT4,
 * 	DES_IP INT4,
 * 	DES_PORT INT4,
 * 	PROTOCOL INT1,
 * 	MODE INT1
 * )
 * TABLESPACE SYSTEM INIT 64K NEXT 64K MAXSIZE UNLIMITED  PCTFREE 10 PCTUSED 40;
 */
@Data
public class WorkParam {

    /**
     * ID
     */
    private Integer id;

    /**
     * 网卡IP,4字节
     */
    private  Integer ethIP;

    /**
     * 源IP,4字节
     */
    private  Integer srcIP;

    /**
     * 源端口,2字节
     */
    private   Integer  srcPort;

    /**
     * 目的IP,4字节
     */
    private  Integer desIP;

    /**
     * 目的端口,2字节
     */
    private   Integer  desPort;

    /**
     * 通信协议,1字节
     */
    private   Byte  protocol;

    /**
     * 工作模式,1字节
     */
    private   Byte  mode;




}
