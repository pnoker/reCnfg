package com.rcw.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

import com.rcw.main.MainFunction;
import com.rcw.pojo.BaseInfo;
import com.rcw.util.Generation;
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

	/**
	 * 正响应解析
	 */
	public void success(PackageProcessor p) {
		if (p.bytesToString(11, 12).equals("1605")) {
			int value = p.bytesToIntSmall(13, 16);
			logWrite.write("水表上传周期为:" + value + "S");
		}
		if (p.bytesToString(11, 12).equals("1305")) {
			String value = p.bytesToString(13, 16);
			String num = "";
			switch (value) {
			case "cdcccc3d":
				num = "1";
				break;
			case "0000803f":
				num = "0.1";
				break;
			case "00002041":
				num = "0.01";
				break;
			case "0000c842":
				num = "0.001";
				break;
			case "00007a44":
				num = "0.0001";
				break;
			}
			logWrite.write("水表磁性指针位置为:" + num);
		}
		if (p.bytesToString(11, 12).equals("1005")) {
			float value = p.bytesToFloatSmall(13, 16);
			logWrite.write("水表基数为:" + value + "M³");
		}
		if (p.bytesToString(11, 12).equals("1505")) {
			float value = p.bytesToFloatSmall(13, 16);
			logWrite.write("水表流量值为:" + value + "M³");
		}
		if (p.bytesToString(11, 12).equals("be00")) {
			String value = p.bytesToString(13, 37);
			if (value.equals("ffffffffffffffffffffffffffffffffffffffffffffffff00")) {
				logWrite.write("未配置水表位号");
			} else {
				value = p.bytesToChar(13, 37);// 字节流转换成字符转
				logWrite.write("水表位号为:" + value);
			}
		}
		if (p.bytesToString(11, 12).equals("1205")) {
			float value = p.bytesToIntSmall(13, 16);
			logWrite.write("水表脉冲个数为:" + value + "个");
		}
	}

	/**
	 * 负响应解析
	 */
	public void fail(PackageProcessor p) {
		if (p.bytesToString(3, 3).equals("01")) {
			logWrite.write("未知错误");
		}
		if (p.bytesToString(3, 3).equals("02")) {
			logWrite.write("输入长度有问题");
		}
		if (p.bytesToString(3, 3).equals("03")) {
			logWrite.write("不支持的命令号");
		}
		if (p.bytesToString(3, 3).equals("04")) {
			logWrite.write("设备不在网");
		}
		if (p.bytesToString(3, 3).equals("05")) {
			logWrite.write("串口号错误");
		}
		if (p.bytesToString(3, 3).equals("06")) {
			logWrite.write("数据不合理");
		}
		if (p.bytesToString(3, 3).equals("07")) {
			logWrite.write("地址不成对");
		}
		if (p.bytesToString(3, 3).equals("08")) {
			logWrite.write("写入Modbus映射表地址索引不连续");
		}
		if (p.bytesToString(3, 3).equals("09")) {
			logWrite.write("写入Modbus地址溢出");
		}
		if (p.bytesToString(3, 3).equals("10")) {
			logWrite.write("UDP端口重复");
		}
		if (p.bytesToString(3, 3).equals("11")) {
			logWrite.write("命令号不存在");
		}
	}

	public boolean data(PackageProcessor p, String typeserial) {
		boolean timeout = false;
		String longAddress = MainFunction.item.get(typeserial).split("#")[0];
		if (longAddress.equals("E119000000417A00")) {
			String cz = MainFunction.item.get(typeserial).split("#")[3];// 设备的从站地址
			String md = MainFunction.item.get(typeserial).split("#")[4];// Modbus命令号
			if (p.bytesToString(3, 13).toUpperCase().equals(longAddress + "01" + cz + md)) {
				float value = p.bytesToFloatSmall(21, 24);
				logWrite.write("IO值为:" + value);
				timeout = true;
			}
		}
		return timeout;
	}

	public void query(BaseInfo base, byte[] send, String typeserial) {
		boolean timeOut = false;
		logWrite.write("<---当前网关:" + base.getIpaddress() + "--->");
		DatagramSocket datagramSocket = null;
		long start = 0, end = 0;
		Date date = new Date();
		try {
			datagramSocket = new DatagramSocket(base.getLocalport());
			DatagramPacket datagramSend = new DatagramPacket(send, send.length,
					InetAddress.getByName(base.getIpaddress()), base.getPort());
			datagramSocket.send(datagramSend);
			logWrite.write("发送:" + getHexDatagram(send, send.length));
			start = (new Date()).getTime();
		} catch (IOException e) {
			logWrite.write(e.getMessage());
		}
		while (!timeOut) {
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
					success(p);
					timeOut = true;
					break;
				/* 负响应 一 */
				case "026980":
					logWrite.write("错误:" + hexDatagram);
					fail(p);
					timeOut = true;
					break;
				/* 网关连接成功 */
				case "020f80":
					logWrite.write("网关连接成功:" + hexDatagram);
					timeOut = true;
					break;
				case "025500":
					logWrite.write("仪表应用数据:" + hexDatagram);
					if (data(p, typeserial)) {
						timeOut = true;
					}
					end = (new Date()).getTime();
					if ((end - start) > 10000) {
						timeOut = true;
					}
					break;
				default:
					logWrite.write("其他:" + hexDatagram);
					end = (new Date()).getTime();
					if ((end - start) > 10000) {
						timeOut = true;
					}
				}
			} catch (Exception e) {
				logWrite.write(e.getMessage());
				timeOut = true;
			}
		}
		datagramReceive.setLength(1024);
		datagramSocket.close();
		end = (new Date()).getTime();
		System.out.println("本次操作耗时:" + (end - start) + "ms");
	}
}
