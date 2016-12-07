package com.rcw.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

import com.rcw.main.MainFunction;
import com.rcw.pojo.BaseInfo;
import com.rcw.pojo.Result;
import com.rcw.util.LogWrite;
import com.rcw.util.PackageProcessor;

public class QueryPara {
	private DatagramPacket datagramReceive;
	private byte[] buf = new byte[1024];
	private PackageProcessor p;
	private LogWrite logWrite;
	private int num;

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
	public String success(PackageProcessor p) {
		String result = "";
		if (p.bytesToString(11, 12).equals("1605")) {
			int value = p.bytesToIntSmall(13, 16);
			result += value;
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
			result += num;
			logWrite.write("水表磁性指针位置为:" + num);
		}
		if (p.bytesToString(11, 12).equals("1005")) {
			float value = p.bytesToFloatSmall(13, 16);
			result += value;
			logWrite.write("水表基数为:" + value + "M³");
		}
		if (p.bytesToString(11, 12).equals("1505")) {
			float value = p.bytesToFloatSmall(13, 16);
			result += value;
			logWrite.write("水表流量值为:" + value + "M³");
		}
		if (p.bytesToString(11, 12).equals("be00")) {
			String value = p.bytesToString(13, 37);
			if (value.equals("ffffffffffffffffffffffffffffffffffffffffffffffff00")) {
				logWrite.write("未配置水表位号");
				result += "未配置水表位号";
			} else {
				value = p.bytesToChar(13, 37);// 字节流转换成字符转
				result += value;
				logWrite.write("水表位号为:" + value);
			}
		}
		if (p.bytesToString(11, 12).equals("1205")) {
			float value = p.bytesToIntSmall(13, 16);
			result += value;
			logWrite.write("水表脉冲个数为:" + value + "个");
		}
		return result;
	}

	/**
	 * 负响应解析
	 */
	public String fail(PackageProcessor p) {
		String result = "";
		if (p.bytesToString(3, 3).equals("01")) {
			result += "未知错误";
			logWrite.write("未知错误");
		}
		if (p.bytesToString(3, 3).equals("02")) {
			result += "输入长度有问题";
			logWrite.write("输入长度有问题");
		}
		if (p.bytesToString(3, 3).equals("03")) {
			result += "不支持的命令号";
			logWrite.write("不支持的命令号");
		}
		if (p.bytesToString(3, 3).equals("04")) {
			result += "设备不在网";
			logWrite.write("设备不在网");
		}
		if (p.bytesToString(3, 3).equals("05")) {
			result += "串口号错误";
			logWrite.write("串口号错误");
		}
		if (p.bytesToString(3, 3).equals("06")) {
			result += "数据不合理";
			logWrite.write("数据不合理");
		}
		if (p.bytesToString(3, 3).equals("07")) {
			result += "地址不成对";
			logWrite.write("地址不成对");
		}
		if (p.bytesToString(3, 3).equals("08")) {
			result += "写入Modbus映射表地址索引不连续";
			logWrite.write("写入Modbus映射表地址索引不连续");
		}
		if (p.bytesToString(3, 3).equals("09")) {
			result += "写入Modbus地址溢出";
			logWrite.write("写入Modbus地址溢出");
		}
		if (p.bytesToString(3, 3).equals("10")) {
			result += "UDP端口重复";
			logWrite.write("UDP端口重复");
		}
		if (p.bytesToString(3, 3).equals("11")) {
			result += "命令号不存在";
			logWrite.write("命令号不存在");
		}
		return result;
	}

	public Result data(PackageProcessor p, String typeserial, int serial) {
		Result result = new Result();
		result.setSuccess(false);
		String longAddress = MainFunction.item.get(typeserial).split("#")[0];
		if (longAddress.equals("E119000000417A00")) {
			String cz = MainFunction.item.get(typeserial).split("#")[3];// 设备的从站地址
			String md = MainFunction.item.get(typeserial).split("#")[4];// Modbus命令号
			if (p.bytesToString(3, 13).toUpperCase().equals(longAddress + "01" + cz + md)) {
				float value1 = p.bytesToInt(17, 20);
				int value2 = (int) (p.bytesToInt(21, 24) * 0.1);
				logWrite.write("IO的瞬时值为:" + value1);
				logWrite.write("IO的累计值为:" + value2);
				result.setSuccess(true);
				result.setResult(value1 + "," + value2);
			}
		}
		if (longAddress.equals("F119000000417A00") || longAddress.equals("F31A000000417A00")) {
			String cz = MainFunction.item.get(typeserial).split("#")[3];// 设备的从站地址
			String md = "";
			if (serial == 14) {
				md = "00";
				if (p.bytesToString(3, 13).toUpperCase().equals(longAddress + "07" + cz + md)) {
					int d0 = p.bytesToTen(14, 14);
					int d1 = p.bytesToTen(15, 15);
					int d2 = p.bytesToTen(16, 16);
					int d3 = p.bytesToTen(17, 17);
					float value = d2 * 10000 + d1 * 100 + d0;
					for (int i = 0; i < d3 - 5; i++) {
						value = (float) (value * 0.1);
					}
					logWrite.write("开封开润表瞬时值为:" + value);
					result.setSuccess(true);
					result.setResult("" + value);
				}
			}
			if (serial == 15) {
				md = "04";
				if (p.bytesToString(3, 13).toUpperCase().equals(longAddress + "07" + cz + md)) {
					int d0 = p.bytesToTen(14, 14);
					int d1 = p.bytesToTen(15, 15);
					int d2 = p.bytesToTen(16, 16);
					int d3 = p.bytesToTen(17, 17);
					int d4 = p.bytesToTen(18, 18);
					float value = d4 * 1000000 + d3 * 1000000 + d2 * 10000 + d1 * 100 + d0;
					logWrite.write("开封开润表瞬时值为:" + value);
					result.setSuccess(true);
					result.setResult("" + value);
				}
			}
		}
		return result;
	}

	public String query(BaseInfo base, byte[] send, String typeserial, int serial) {
		String result = "";
		boolean timeOut = false;
		boolean reConnect = true;
		logWrite.write("<---当前网关:" + base.getIpaddress() + "--->");
		DatagramSocket datagramSocket = null;
		long start = 0, end = 0;
		DatagramPacket datagramSend = null;
		try {
			datagramSocket = new DatagramSocket(base.getLocalport());
			datagramSend = new DatagramPacket(send, send.length, InetAddress.getByName(base.getIpaddress()),
					base.getPort());
			num = 0;// 计数清零
			start = (new Date()).getTime();
		} catch (IOException e) {
			logWrite.write(e.getMessage());
		}
		while (reConnect) {
			if (num >= 3) {// 3次重连机会，超过3次就退出重连机制
				reConnect = false;
				logWrite.write("操作失败，请稍后重试！");
			} else {
				try {
					datagramSocket.send(datagramSend);
					logWrite.write("发送:" + getHexDatagram(send, send.length));
					num++;
					timeOut = false;
				} catch (IOException e) {
					logWrite.write(e.getMessage());
				}
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
						result = success(p);
						timeOut = true;
						reConnect = false;
						break;
					/* 负响应 一 */
					case "026980":
						logWrite.write("错误:" + hexDatagram);
						result = fail(p);
						timeOut = true;
						reConnect = false;
						break;
					/* 网关连接成功 */
					case "020f80":
						logWrite.write("网关连接成功:" + hexDatagram);
						timeOut = true;
						reConnect = false;
						break;
					case "025500":
						logWrite.write("应用数据:" + hexDatagram);
						Result dataResult = data(p, typeserial, serial);
						if (dataResult.isSuccess()) {
							result = dataResult.getResult();
							timeOut = true;
							reConnect = false;
						}
						end = (new Date()).getTime();
						if ((end - start) > 8000) {
							timeOut = true;
							reConnect = false;
						}
						break;
					default:
						logWrite.write("其他:" + hexDatagram);
						end = (new Date()).getTime();
						if ((end - start) > 10000) {
							timeOut = true;
							reConnect = false;
						}
					}
				} catch (Exception e) {
					logWrite.write(e.getMessage());
					timeOut = true;
				}
			}
		}
		datagramReceive.setLength(1024);
		datagramSocket.close();
		end = (new Date()).getTime();
		System.out.println("本次操作耗时:" + (end - start) + "ms");
		return result;
	}
}
