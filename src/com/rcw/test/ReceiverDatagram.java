package com.rcw.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import com.rcw.pojo.BaseInfo;
import com.rcw.util.LogWrite;
import com.rcw.util.PackageProcessor;

public class ReceiverDatagram implements Runnable {
	private DatagramSocket datagramSocket;
	private DatagramPacket datagramSend;
	private DatagramPacket datagramReceive;
	private byte[] buf = new byte[1024];
	private byte[] sendCode = { (byte) 0x01, (byte) 0x0B, (byte) 0xFF, (byte) 0xFF, (byte) 0x4A, (byte) 0x9B };
	private PackageProcessor p;
	private BaseInfo base;
	private LogWrite logWrite;
	volatile boolean stop = false;
	volatile boolean restart = false;

	public ReceiverDatagram(BaseInfo base) {
		try {
			this.base = base;
			this.datagramSocket = new DatagramSocket(base.getLocalport());
			this.datagramSend = new DatagramPacket(sendCode, sendCode.length, InetAddress.getByName(base.getIpaddress()), base.getPort());
			this.datagramReceive = new DatagramPacket(buf, 1024);
			this.logWrite = new LogWrite(base.getIpaddress());
		} catch (SocketException e) {
			logWrite.write("【 Error!】ReceiverDatagram。1" + e.getMessage());
		} catch (UnknownHostException e) {
			logWrite.write("【 Error!】ReceiverDatagram.2" + e.getMessage());
		}
	}



	/**
	 * 打印十六进制的报文，不足两位，前面补零
	 */
	public String getHexDatagram(byte[] b, int length) {
		StringBuffer sbuf = new StringBuffer();
		for (int i = 0; i < length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			/* 不足两位前面补零处理 */
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sbuf.append(hex.toUpperCase());
		}
		return sbuf.toString();
	}

	public void run() {
		logWrite.write("<----------当前网关:" + base.getIpaddress() + ",启动线程---------->");
		try {
			datagramSocket.send(datagramSend);
		} catch (IOException e) {
			logWrite.write("【 Error!】ReceiverDatagram.run.1：" + e.getMessage());
		}

		/*
		 * 接收完每条报文就进行判断健康报文时间间隔,这是一个线程，每分钟检测一次当前各个节点的时间间隔，如果有某个节点的时间间隔大于10分钟，
		 * 就向网关重新发送采数命令
		 */
		while (!restart) {
			while (!stop) {
				try {
					datagramSocket.setSoTimeout(1000 * 60 * 3);
					datagramSocket.receive(datagramReceive);
					byte[] receive = datagramReceive.getData();
					p = new PackageProcessor(receive);
					String hexDatagram = getHexDatagram(datagramReceive.getData(), datagramReceive.getLength());
					String datastart = p.bytesToString(0, 1);
					switch (datastart) {
					/* 0101节点加入信息 */
					case "0101":
						logWrite.write("网络报文:" + hexDatagram);
						break;
					/* 节点测试信息 */
					case "010f":
						logWrite.write("健康报文:" + hexDatagram);
						break;
					/* 节点数据信息 */
					case "0183":
						logWrite.write("数据报文:" + hexDatagram);
						break;
					default:
						logWrite.write("其他报文:" + hexDatagram);
					}
				} catch (Exception e) {
					logWrite.write("datagramSocket.receive 堵塞/连接超时：" + e.getMessage());
					try {
						logWrite.write("<-'-'-'-重新发送采数命令:010BFFFF4A9B-'-'-'->");
						datagramSocket.send(datagramSend);
					} catch (IOException ex) {
						logWrite.write("【 Error!】ReceiverDatagram.run.1：" + ex.getMessage());
					}
				}
				datagramReceive.setLength(1024);
			}
			logWrite.write("<-'-'-'-网关（" + base.getIpaddress() + "）下某节点健康报文超时，重新发送采数命令:010BFFFF4A9B-'-'-'->");
			try {
				datagramSocket.send(datagramSend);
			} catch (IOException e) {
				logWrite.write("【 Error!】ReceiverDatagram.run.3：" + e.getMessage());
			}
			stop = false;
			logWrite.write("<-'-'-'-重新设置本次超时时间间隔为10分钟-'-'-'->");
		}
		datagramSocket.close();
		logWrite.close();
		logWrite.write("<----------当前网关:" + base.getIpaddress() + ",关闭Socket---------->");
		logWrite.write("<----------当前网关:" + base.getIpaddress() + ",关闭LogWrite---------->");
	}
}
