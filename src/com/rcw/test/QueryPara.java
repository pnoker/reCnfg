package com.rcw.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

import com.rcw.pojo.BaseInfo;
import com.rcw.util.LogWrite;
import com.rcw.util.PackageProcessor;

public class QueryPara {
	private DatagramPacket datagramReceive;
	private byte[] buf = new byte[1024];
	private PackageProcessor p;
	private LogWrite logWrite;
	volatile boolean stop = false;
	volatile boolean restart = false;

	public QueryPara() {
		try {
			this.datagramReceive = new DatagramPacket(buf, 1024);
			this.logWrite = new LogWrite();
		} catch (Exception e) {
			logWrite.write(e.getMessage());
		}
	}

	/**
	 * 打印十六进制的报文，不足两位，前面补零，使用正则表达式格式化报文
	 */
	public String getHexDatagram(byte[] b, int length) {
		StringBuffer sbuf = new StringBuffer();
		for (int i = 0; i < length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			/* 不足两位前面补零处理 */
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			String regex = "(.{2})";
			hex = hex.replaceAll(regex, "$1 ");
			sbuf.append(hex.toUpperCase());
		}
		return sbuf.toString();
	}

	public void query(BaseInfo base, byte[] send) {
		logWrite.write("<---当前网关:" + base.getIpaddress() + "--->");
		DatagramSocket datagramSocket = null;
		long start = 0, end = 0;
		Date date = new Date();
		try {
			datagramSocket = new DatagramSocket(base.getLocalport());
			DatagramPacket datagramSend = new DatagramPacket(send, send.length, InetAddress.getByName(base.getIpaddress()), base.getPort());
			datagramSocket.send(datagramSend);
			logWrite.write("发送:" + getHexDatagram(send, send.length));
			start = (new Date()).getTime();
		} catch (IOException e) {
			logWrite.write(e.getMessage());
		}
		try {
			datagramSocket.setSoTimeout(1000 * 10);
			datagramSocket.receive(datagramReceive);
			byte[] receive = datagramReceive.getData();
			p = new PackageProcessor(receive);
			String hexDatagram = getHexDatagram(datagramReceive.getData(), datagramReceive.getLength());
			String datastart = p.bytesToString(0, 2);
			switch (datastart) {
			/* 正响应 或者是负响应二 */
			case "026a00":
				logWrite.write("成功:" + hexDatagram);
				float value = p.bytesToFloatSmall(13, 16);
				logWrite.write("查询结果为:" + value);
				break;
			/* 负响应 一 */
			case "026a01":
				logWrite.write("失败:" + hexDatagram);
				break;
			default:
				logWrite.write("其他:" + hexDatagram);
			}
		} catch (Exception e) {
			logWrite.write(e.getMessage());
		}
		datagramReceive.setLength(1024);
		datagramSocket.close();
		end = (new Date()).getTime();
		System.out.println("本次查询耗时:" + (end - start) + "ms");
	}
}
