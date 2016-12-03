package com.rcw.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import com.rcw.pojo.BaseInfo;
import com.rcw.util.Generation;
import com.rcw.util.LogWrite;
import com.rcw.util.PackageProcessor;

public class QueryPara implements Runnable {
	private DatagramSocket datagramSocket;
	private DatagramPacket datagramConnect;
	private DatagramPacket datagramSend;
	private DatagramPacket datagramReceive;
	private byte[] buf = new byte[1024];
	private byte[] connectCode = { (byte) 0x02, (byte) 0x0F, (byte) 0x00, (byte) 0x00, (byte) 0x59, (byte) 0xC1 };
	private byte[] sendCode = { (byte) 0x02, (byte) 0x69, (byte) 0x00, (byte) 0x37, (byte) 0x29, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x41, (byte) 0x7A, (byte) 0x00, (byte) 0x15, (byte) 0x05,
			(byte) 0x7E, (byte) 0xC8 };
	private PackageProcessor p;
	private BaseInfo base;
	private LogWrite logWrite;
	volatile boolean stop = false;
	volatile boolean restart = false;

	public QueryPara(BaseInfo base) {
		try {
			this.base = base;
			this.datagramSocket = new DatagramSocket(base.getLocalport());
			this.datagramConnect = new DatagramPacket(connectCode, connectCode.length, InetAddress.getByName(base.getIpaddress()), base.getPort());
			this.datagramReceive = new DatagramPacket(buf, 1024);
			this.logWrite = new LogWrite(base.getIpaddress());
		} catch (SocketException e) {
			logWrite.write("【 Error!】ReceiverDatagram。1" + e.getMessage());
		} catch (UnknownHostException e) {
			logWrite.write("【 Error!】ReceiverDatagram.2" + e.getMessage());
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

	public void chat(byte[] send) {
		logWrite.write("<---当前网关:" + base.getIpaddress() + "--->");
		try {
			this.datagramSend = new DatagramPacket(send, send.length, InetAddress.getByName(base.getIpaddress()), base.getPort());
			datagramSocket.send(datagramSend);
			logWrite.write("发送:" + getHexDatagram(send, send.length));
		} catch (IOException e) {
			logWrite.write("【 Error!】ReceiverDatagram.run.1：" + e.getMessage());
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
				logWrite.write("正响应或者负响应二报文:" + hexDatagram);
				break;
			/* 负响应 一 */
			case "026a01":
				logWrite.write("正响应或者负响应二报文:" + hexDatagram);
				break;
			default:
				logWrite.write("其他报文:" + hexDatagram);
			}
		} catch (Exception e) {
			logWrite.write( e.getMessage());
		}
		datagramReceive.setLength(1024);
	}

	public void run() {
		logWrite.write("<---当前网关:" + base.getIpaddress() + ",启动线程--->");
		try {
			datagramSocket.send(datagramConnect);
			datagramSocket.send(datagramSend);
		} catch (IOException e) {
			logWrite.write("【 Error!】ReceiverDatagram.run.1：" + e.getMessage());
		}
		while (!restart) {
			while (!stop) {
				try {
					datagramSocket.setSoTimeout(1000 * 10);
					datagramSocket.receive(datagramReceive);
					byte[] receive = datagramReceive.getData();
					p = new PackageProcessor(receive);
					String hexDatagram = getHexDatagram(datagramReceive.getData(), datagramReceive.getLength());
					String datastart = p.bytesToString(0, 1);
					switch (datastart) {
					/* 正响应 或者是负响应二 */
					case "026a00":
						logWrite.write("正响应或者负响应二报文:" + hexDatagram);
						break;
					/* 负响应 一 */
					case "026a01":
						logWrite.write("正响应或者负响应二报文:" + hexDatagram);
						break;
					default:
						logWrite.write("其他报文:" + hexDatagram);
					}
				} catch (Exception e) {
					logWrite.write(e.getMessage());
					try {
						datagramSocket.send(datagramSend);
					} catch (IOException e1) {
						logWrite.write(e1.getMessage());
					}
				}
				datagramReceive.setLength(1024);
			}
		}
		datagramSocket.close();
		logWrite.close();
	}

	public static void main(String[] args) {
		System.out.println("<---初始化操作--->");
		System.out.println("<---启动远程配置线程--->");
		BaseInfo base = new BaseInfo();
		base.setIpaddress("10.112.126.29");
		base.setLocalport(6003);
		base.setPort(6001);
		QueryPara queryPara = new QueryPara(base);
		Generation generation = new Generation();
		byte[] connectCode = generation.StringToBytes("020F000059C1");// 连接网关命令，固定写法
		byte[] sendCode = generation.StringToBytes("0269003729000000417A001505");//查询水表流量值命令
		queryPara.chat(connectCode);
		queryPara.chat(sendCode);
	}
}
