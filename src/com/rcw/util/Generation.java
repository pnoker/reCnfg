package com.rcw.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.rcw.main.MainFunction;

import sun.io.CharToByteDBCS_ASCII;

/**
 * @author Pnoker
 * @description 将字符转换成Byte数组
 */
public class Generation {

	/**
	 * CRC,转换成两个字节的Byte数组
	 */
	public byte[] getBytesCrc(byte[] code) {
		short shortCrc = getShorCrc(code);
		byte[] crc = shortToBytes(shortCrc);
		return crc;
	}

	/**
	 * CRC,通过Byte数组生成Short型Crc
	 */
	public short getShorCrc(byte[] code) {
		short shortCrc = 0;
		short baseCrc = 0x1021;
		int len = code.length;
		int crc_cur = 0;
		while (crc_cur < len) {
			shortCrc = (short) (shortCrc ^ (code[crc_cur] << 8));
			for (int i = 0; i < 8; i++) {
				if ((shortCrc & 0x8000) != 0) {
					shortCrc = (short) ((shortCrc << 1) ^ baseCrc);
				} else {
					shortCrc = (short) (shortCrc << 1);
				}
			}
			crc_cur++;
		}
		return shortCrc;
	}

	/**
	 * CRC,将Short型Crc转换成两个字节的Byte型数组
	 */
	public byte[] shortToBytes(short num) {
		int temp = num;
		byte[] bytes = new byte[2];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = new Integer(temp & 0xff).byteValue();
			temp = temp >> 8;
		}
		return bytes;
	}

	/**
	 * 将字符串的命令转换成Byte型数组，并添加两个字节的CRC校验码
	 */
	public byte[] StringToBytes(String command) {
		String regex = "(.{2})";
		String[] temp;
		int num = 0;
		command = command.replaceAll(regex, "$1,");
		temp = command.split(",");
		byte[] order = new byte[temp.length];// 不带CRC校验码的命令
		byte[] instruct = new byte[temp.length + 2];// 带CRC校验码的命令
		for (int m = 0; m < temp.length; m++) {
			int code = Integer.valueOf(temp[m], 16);
			order[num] = (byte) code;
			instruct[num] = (byte) code;
			num++;
		}
		byte[] crc = getBytesCrc(order);// 获取CRC校验码
		for (int m = 0; m < crc.length; m++) {
			instruct[num] = crc[m];// 添加两个字节的CRC校验码
			num++;
		}
		return instruct;// 返回带CRC校验码的命令
	}

	/**
	 * 将一个Float型的数转换成四个字节的数组
	 */
	public byte[] FloatToBytes(float f) {
		byte[] bytes = ByteBuffer.allocate(4).putFloat(f).array();
		byte[] result = new byte[4];
		for (int m = 0; m < bytes.length; m++) {
			result[3 - m] = bytes[m];
		}
		return result;
	}

	public byte[] IntToBytes(int i) {
		byte[] result = new byte[4];
		result[3] = (byte) ((i >> 24) & 0xFF);
		result[2] = (byte) ((i >> 16) & 0xFF);
		result[1] = (byte) ((i >> 8) & 0xFF);
		result[0] = (byte) (i & 0xFF);
		return result;
	}

	public byte[] queryCommand(String typeserial, int serial) {
		String command = "02";// 协议标识
		command += "6900";// 远程读取或设置命令号
		command += MainFunction.item.get(typeserial).split("#")[0];// 设备的长地址
		command += MainFunction.code.get("" + serial);// 操作命令号
		byte[] b = StringToBytes(command);
		return b;
	}

	public byte[] configCommand(String typeserial, int serial, float value) {
		String command = "02";// 协议标识
		command += "6900";// 远程读取或设置命令号
		command += MainFunction.item.get(typeserial).split("#")[0];// 设备的长地址
		command += MainFunction.code.get("" + serial);// 操作命令号
		String num = "";
		if (serial == 2) {// 设置水表基数，水表基数为浮点型数据，高字节在后
			byte[] f = FloatToBytes(value);
			num = byteToString(f, 4);
		}
		if (serial == 4) {// 水表脉冲数清零
			num = "";
		}
		if (serial == 5) {// 远程配置水表磁性指针位置
			switch ("" + value) {
			case "1.0":
				num = "CDCCCC3D";
				break;
			case "0.1":
				num = "0000803F";
				break;
			case "0.01":
				num = "00002041";
				break;
			case "0.001":
				num = "0000C842";
				break;
			case "0.0001":
				num = "00007A44";
				break;
			}
		}
		if (serial == 7) {
			byte[] f = IntToBytes((int) value);
			num = byteToString(f, 4);
		}
		command += num;
		byte[] b = StringToBytes(command);
		return b;
	}

	public byte[] configCommand(String typeserial, int serial, String value) {
		String command = "02";// 协议标识
		command += "6900";// 远程读取或设置命令号
		command += MainFunction.item.get(typeserial).split("#")[0];// 设备的长地址
		command += MainFunction.code.get("" + serial);// 操作命令号
		String num = "";
		StringBuffer sbuf = new StringBuffer();
		if (serial == 9) {// 远程配置水表位号，可以设置中文
			byte[] bytes = null;
			try {
				bytes = value.getBytes("GBK");
			} catch (UnsupportedEncodingException e) {
				System.out.println(e.getMessage());
			}
			for (int m = 0; m < bytes.length; m++) {
				String hex = Integer.toHexString(bytes[m] & 0xFF);
				if (hex.length() == 1) {
					hex = "0" + hex;
				}
				sbuf.append(hex.toUpperCase());
			}

			while (sbuf.toString().length() < 50) {
				sbuf.append("0");
			}
			num = sbuf.toString();
		}
		command += num;
		byte[] b = StringToBytes(command);
		return b;
	}

	public static String byteToString(byte[] b, int length) {
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

	public static void main(String[] args) {
		/*
		 * Generation generation = new Generation(); //
		 * generation.StringToBytes("0269003729000000417A0015057EC8"); byte[]
		 * code = generation.StringToBytes("0269003729000000417A001505"); for
		 * (int m = 0; m < code.length; m++) { System.out.println(code[m]); }
		 */
		String s = "完";// 字符串
		// CD EA CA C2 34 34 BA D9 BA D9 00 00 00 00 00 00 00 00 00 00 00 00 00
		// 00 00
		char[] chars = s.toCharArray(); // 把字符中转换为字符数组
		System.out.println(new String(chars));
		byte[] t = { (byte) 0x31, (byte) 0x32, (byte) 0x33 };
		byte[] tt = null;
		try {
			tt = s.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String ss = byteToString(tt, 2);
		System.out.println(new String(t));
		try {
			System.out.println(new String(tt, "GBK"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("\n\n汉字 ASCII\n----------------------");
		for (int i = 0; i < chars.length; i++) {// 输出结果
			System.out.println(Integer.toHexString(chars[i]));
		}

	}

	/**
	 * 与网关建立连接，固定命令
	 */
	public byte[] connect() {
		return StringToBytes("020F000059C1");
	}
}
