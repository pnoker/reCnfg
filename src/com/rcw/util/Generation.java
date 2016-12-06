package com.rcw.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.rcw.main.MainFunction;

/**
 * @author Pnoker
 * @description 将字符转换成Byte数组
 */
public class Generation {
	private short[] auchCRCHi = { 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81,
			0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
			0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01,
			0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
			0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80,
			0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
			0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00,
			0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
			0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40 };
	private short[] auchCRCLo = { 0x00, 0xC0, 0xC1, 0x01, 0xC3, 0x03, 0x02, 0xC2, 0xC6, 0x06, 0x07, 0xC7, 0x05, 0xC5, 0xC4, 0x04, 0xCC, 0x0C, 0x0D, 0xCD, 0x0F, 0xCF, 0xCE, 0x0E, 0x0A, 0xCA, 0xCB,
			0x0B, 0xC9, 0x09, 0x08, 0xC8, 0xD8, 0x18, 0x19, 0xD9, 0x1B, 0xDB, 0xDA, 0x1A, 0x1E, 0xDE, 0xDF, 0x1F, 0xDD, 0x1D, 0x1C, 0xDC, 0x14, 0xD4, 0xD5, 0x15, 0xD7, 0x17, 0x16, 0xD6, 0xD2, 0x12,
			0x13, 0xD3, 0x11, 0xD1, 0xD0, 0x10, 0xF0, 0x30, 0x31, 0xF1, 0x33, 0xF3, 0xF2, 0x32, 0x36, 0xF6, 0xF7, 0x37, 0xF5, 0x35, 0x34, 0xF4, 0x3C, 0xFC, 0xFD, 0x3D, 0xFF, 0x3F, 0x3E, 0xFE, 0xFA,
			0x3A, 0x3B, 0xFB, 0x39, 0xF9, 0xF8, 0x38, 0x28, 0xE8, 0xE9, 0x29, 0xEB, 0x2B, 0x2A, 0xEA, 0xEE, 0x2E, 0x2F, 0xEF, 0x2D, 0xED, 0xEC, 0x2C, 0xE4, 0x24, 0x25, 0xE5, 0x27, 0xE7, 0xE6, 0x26,
			0x22, 0xE2, 0xE3, 0x23, 0xE1, 0x21, 0x20, 0xE0, 0xA0, 0x60, 0x61, 0xA1, 0x63, 0xA3, 0xA2, 0x62, 0x66, 0xA6, 0xA7, 0x67, 0xA5, 0x65, 0x64, 0xA4, 0x6C, 0xAC, 0xAD, 0x6D, 0xAF, 0x6F, 0x6E,
			0xAE, 0xAA, 0x6A, 0x6B, 0xAB, 0x69, 0xA9, 0xA8, 0x68, 0x78, 0xB8, 0xB9, 0x79, 0xBB, 0x7B, 0x7A, 0xBA, 0xBE, 0x7E, 0x7F, 0xBF, 0x7D, 0xBD, 0xBC, 0x7C, 0xB4, 0x74, 0x75, 0xB5, 0x77, 0xB7,
			0xB6, 0x76, 0x72, 0xB2, 0xB3, 0x73, 0xB1, 0x71, 0x70, 0xB0, 0x50, 0x90, 0x91, 0x51, 0x93, 0x53, 0x52, 0x92, 0x96, 0x56, 0x57, 0x97, 0x55, 0x95, 0x94, 0x54, 0x9C, 0x5C, 0x5D, 0x9D, 0x5F,
			0x9F, 0x9E, 0x5E, 0x5A, 0x9A, 0x9B, 0x5B, 0x99, 0x59, 0x58, 0x98, 0x88, 0x48, 0x49, 0x89, 0x4B, 0x8B, 0x8A, 0x4A, 0x4E, 0x8E, 0x8F, 0x4F, 0x8D, 0x4D, 0x4C, 0x8C, 0x44, 0x84, 0x85, 0x45,
			0x87, 0x47, 0x46, 0x86, 0x82, 0x42, 0x43, 0x83, 0x41, 0x81, 0x80, 0x40 };

	/**
	 * CRC,转换成两个字节的Byte数组
	 */
	public byte[] getBytesWiaCrc(byte[] code) {
		short shortCrc = getShorWiaCrc(code);
		byte[] crc = shortToBytes(shortCrc);
		return crc;
	}

	public byte[] getBytesModCrc(byte[] code) {
		short shortCrc = getShorModCrc(code);
		byte[] crc = shortToBytes(shortCrc);
		return crc;
	}

	/**
	 * CRC,通过Byte数组生成Short型WiaPaCrc
	 */
	public short getShorWiaCrc(byte[] code) {
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
	 * CRC,通过Byte数组生成Short型ModBusCrc
	 */
	public short getShorModCrc(byte[] code) {
		short shortCrc = 0;
		short uch8CRCHi = 0xFF;
		short uch8CRCLo = 0xFF;
		short u8Index;
		int len = code.length;
		int crc_cur = 0;
		while (crc_cur < len) {
			u8Index = (short) (uch8CRCHi ^ (code[crc_cur]));
			uch8CRCHi = (short) (uch8CRCLo ^ auchCRCHi[u8Index]);
			uch8CRCLo = auchCRCLo[u8Index];
			crc_cur++;
		}
		shortCrc = (short) (uch8CRCHi | (uch8CRCLo << 8));
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
	 * 将字符串的命令转换成Byte型数组，并添加两个字节的WiaPaCRC校验码
	 */
	public byte[] StringToBytesWia(String command) {
		String regex = "(.{2})";
		String[] temp;
		int num = 0;
		command = command.replaceAll(regex, "$1,");
		temp = command.split(",");
		byte[] order = new byte[temp.length];// 不带CRC校验码的命令
		byte[] instruct = new byte[temp.length + 2];// 带WiaCRC校验码的命令
		for (int m = 0; m < temp.length; m++) {
			int code = Integer.valueOf(temp[m], 16);
			order[num] = (byte) code;
			instruct[num] = (byte) code;
			num++;
		}
		byte[] crc = getBytesWiaCrc(order);// 获取WiaCRC校验码
		for (int m = 0; m < crc.length; m++) {
			instruct[num] = crc[m];// 添加两个字节的WiaCRC校验码
			num++;
		}
		return instruct;// 返回带WiaCRC校验码的命令
	}

	public byte[] StringToBytesMod(String command) {
		String regex = "(.{2})";
		String[] temp;
		int num = 0;
		command = command.replaceAll(regex, "$1,");
		temp = command.split(",");
		byte[] order = new byte[temp.length];// 不带CRC校验码的命令
		byte[] instruct = new byte[temp.length + 2];// 带ModCRC校验码的命令
		for (int m = 0; m < temp.length; m++) {
			int code = Integer.valueOf(temp[m], 16);
			order[num] = (byte) code;
			instruct[num] = (byte) code;
			num++;
		}
		byte[] crc = getBytesModCrc(order);// 获取ModCRC校验码
		for (int m = 0; m < crc.length; m++) {
			instruct[num] = crc[m];// 添加两个字节的ModCRC校验码
			num++;
		}
		return instruct;// 返回带ModCRC校验码的命令
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

		if (serial == 13) {
			String cz = MainFunction.item.get(typeserial).split("#")[3];// 设备的从站地址
			String md = MainFunction.item.get(typeserial).split("#")[4];// Modbus命令号
			String jcq = "0000000A";
			command += byteToString(StringToBytesMod(cz + md + jcq), 8);// ModBus查询命令
		}
		if (serial == 14) {// 瞬时值
			command += "2A";// 开始命令
			command += MainFunction.item.get(typeserial).split("#")[3];// 设备的从站地址
			command += "00";// 仪表功能码，01瞬时流量，02瞬时流速，03流量百分比，04流体电阻值，05正向总量，06反向总量，07报警状态，08管道直径
			command += "2E";// 接收命令
		}
		if (serial == 15) {// 累计值
			command += "2A";// 开始命令
			command += MainFunction.item.get(typeserial).split("#")[3];// 设备的从站地址
			command += "04";// 仪表功能码，01瞬时流量，02瞬时流速，03流量百分比，04流体电阻值，05正向总量，06反向总量，07报警状态，08管道直径
			command += "2E";// 接收命令
		}
		byte[] b = StringToBytesWia(command);
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
		if (serial == 7) {// 配置水表上传周期
			byte[] f = IntToBytes((int) value);
			num = byteToString(f, 4);
		}
		command += num;
		byte[] b = StringToBytesWia(command);
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
		byte[] b = StringToBytesWia(command);
		return b;
	}

	public String byteToString(byte[] b, int length) {
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
		/*
		 * String s = "完";// 字符串 // CD EA CA C2 34 34 BA D9 BA D9 00 00 00 00 00
		 * 00 00 00 00 00 00 00 00 // 00 00 char[] chars = s.toCharArray(); //
		 * 把字符中转换为字符数组 System.out.println(new String(chars)); byte[] t = {
		 * (byte) 0x31, (byte) 0x32, (byte) 0x33 }; byte[] tt = null; try { tt =
		 * s.getBytes("GBK"); } catch (UnsupportedEncodingException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } String ss =
		 * byteToString(tt, 2); System.out.println(new String(t)); try {
		 * System.out.println(new String(tt, "GBK")); } catch
		 * (UnsupportedEncodingException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } System.out.println(
		 * "\n\n汉字 ASCII\n----------------------"); for (int i = 0; i <
		 * chars.length; i++) {// 输出结果
		 * System.out.println(Integer.toHexString(chars[i])); }
		 */
		Generation generation = new Generation();
		byte[] code = { 0x01, 0x03, 0x00, 0x00, 0x00, 0x02 };
		short s = generation.getShorModCrc(code);

		byte[] b = generation.getBytesModCrc(code);

		System.out.println(generation.byteToString(b, 2));
		System.out.println(generation.byteToString(generation.StringToBytesMod("040300000002"), 8));// 040300000002C45E
		String one = "026900E119000000417A00E500040300000002C45E";
		System.out.println(generation.byteToString(generation.StringToBytesWia(one), 23));// 026900E119000000417A00E500040300000002C45EA7FB
	}

	/**
	 * 与网关建立连接，固定命令
	 */
	public byte[] connect() {
		return StringToBytesWia("020F000059C1");
	}
}
