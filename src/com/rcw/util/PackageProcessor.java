package com.rcw.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PackageProcessor {
	private byte[] inpackage;

	public PackageProcessor(byte[] ipackage) {
		this.inpackage = ipackage;
	}

	public int bytesToInt(byte bytes0, byte bytes1) {
		int num = ((bytes0 << 8) & 0xFF00);
		num |= (bytes1 & 0xFF);
		return num;
	}

	public int bytesToInt(int start, int end) {
		int value = 0;
		int length = end - start;
		for (int i = length; i >= 0; i--) {
			int num = ((inpackage[start + length - i] & 0xff) << (8 * i));
			value |= num;
		}
		return value;
	}

	/* 将长度为两个字节的报文，转换成整型 */
	public int doublebytesToInt(int start, int end) {
		int num = ((inpackage[end] << 8) & 0xFF00);
		num |= (inpackage[start] & 0xFF);
		return num;
	}

	/* 将一个字节（十六进制），转换成十进制的long型 */
	public Long bytesToLong(int startbit, int endbit) {
		long value = 0;
		String hex = bytesToString(startbit, endbit);
		value = Long.valueOf(hex, 16);
		return value;
	}

	public int bytesToIntSmall(int start, int end) {
		int value = 0;
		int length = end - start;
		;
		for (int i = length; i >= 0; i--) {
			int num = ((inpackage[end + i - length] & 0xff) << (8 * i));
			value |= num;
		}
		return value;
	}

	public int bytesToIntMiddle(int start, int end) {
		int value = 0;
		int length = end - start;
		byte tmp1 = 0;
		tmp1 = inpackage[end];
		inpackage[end] = inpackage[end - 2];
		inpackage[end - 2] = tmp1;
		byte tmp2 = 0;
		tmp2 = inpackage[end - 1];
		inpackage[end - 1] = inpackage[end - 3];
		inpackage[end - 3] = tmp2;
		for (int i = length; i >= 0; i--) {
			int num = ((inpackage[start + length - i] & 0xff) << (8 * i));
			value |= num;
		}
		return value;
	}

	public float bytesToFloat(int startbit, int endbit) {
		String hex = bytesToString(startbit, endbit);
		System.out.println(hex);
		float value = Float.intBitsToFloat(Integer.valueOf(hex, 16));
		return value;
	}

	public int bytesToTen(int start, int end) {
		int value = 0;
		int length = end - start;
		for (int i = length; i >= 0; i--) {
			int num = (inpackage[start + length - i] & 0x00FF) >> 4;
			int num2 = (inpackage[start + length - i] & 0x0f);
			value |= num * 10 + num2;
		}
		return value;
	}

	public float bytesToFloatSmall(int startbit, int endbit) {
		float value = 0;
		try {
			byte[] s = { inpackage[startbit + 3], inpackage[startbit + 2], inpackage[startbit + 1], inpackage[startbit] };
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(s));
			value = dis.readFloat();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}

	public float bytesToFloat3(int startbit, int endbit) {
		float value = 0;
		try {
			byte[] s = { inpackage[startbit], inpackage[startbit + 1], inpackage[startbit + 2], inpackage[startbit + 3] };
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(s));
			value = dis.readFloat();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (Double.isNaN(value)) {
			value = -1;
		}
		return value;
	}

	public float bytesToFloatMiddle(int startbit, int endbit) {
		float value = 0;
		try {
			byte[] s = { inpackage[startbit + 2], inpackage[startbit + 3], inpackage[startbit], inpackage[startbit + 1] };
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(s));
			value = dis.readFloat();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (Double.isNaN(value)) {
			value = -1;
		}
		return value;
	}

	/* 将报文转换成十六进制的字符串，低于两位的前面补零，大于两位的取最后面的两位 */
	public String bytesToString(int startbit, int endbit) {
		String result = "";
		for (int i = startbit; i <= endbit; i++) {
			String s = Integer.toHexString(inpackage[i]);
			if (s.length() < 2) {
				s = "0" + s;
			} else {
				s = s.substring(s.length() - 2, s.length());
			}
			result = result + s;
		}
		return result;
	}

	public static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}

	public static byte[] intToTwoBytes(int i) {
		byte[] result = new byte[2];
		result[0] = (byte) ((i & 0xFF00) >> 8);
		result[1] = (byte) (i & 0xFF);
		return result;
	}

	public static int byteArrayToInt(byte[] bytes) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (bytes[i] & 0x000000FF) << shift;
		}
		return value;
	}

	public boolean hartCRC(int start, int end) {
		boolean resule = false;
		int valueOne = 0;
		int length = end - start;
		for (int i = length; i >= 0; i--) {
			int vaule = ((inpackage[start + length - i] & 0xff));
			valueOne ^= vaule;
		}
		int valueTwo = ((inpackage[end + 1] & 0xff));
		if (valueOne == valueTwo) {
			resule = true;
		}
		return resule;
	}

	/**
	 * Wia-Pa,CRC校验算法输入报文的长度
	 */
	public boolean wiaPaCRC(int length) {
		System.out.println(length);
		boolean result = false;
		short crc = 0;
		short crc_16 = 0x1021;
		int len = length - 2;
		int crc_cur = 0;
		while (crc_cur < len) {
			crc = (short) (crc ^ (inpackage[crc_cur] << 8));
			for (int i = 0; i < 8; i++) {
				if ((crc & 0x8000) != 0) {
					crc = (short) ((crc << 1) ^ crc_16);
				} else {
					crc = (short) (crc << 1);
				}
			}
			crc_cur++;
		}
		int crcOri = bytesToInt(length - 2, length - 1);
		byte[] b = shortToByte(crc);
		int crcNow = twobytesToInt(b);
		System.out.println("原来crc：" + crcOri);
		System.out.println("现来crc：" + crcNow);
		if (crcOri == crcNow) {
			result = true;
		}
		return result;
	}

	public byte[] genWiaPaCRC(int i) {
		byte[] result = new byte[2];
		result[0] = (byte) ((i & 0xFF00) >> 8);
		result[1] = (byte) (i & 0xFF);
		return result;
	}

	public byte[] shortToByte(short number) {
		int temp = number;
		byte[] b = new byte[2];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Integer(temp & 0xff).byteValue();
			temp = temp >> 8;
		}
		return b;
	}

	public int twobytesToInt(byte[] bytes) {
		int num = ((bytes[0] << 8) & 0xFF00);
		num |= (bytes[1] & 0xFF);
		return num;
	}

	public static void main(String[] args) {

		byte[] test1 = { (byte) 0x01, (byte) 0x83, (byte) 0x0B, (byte) 0x00, (byte) 0x37, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0x86, (byte) 0x26, (byte) 0x06, (byte) 0xA2, (byte) 0x86, (byte) 0x36, (byte) 0x01, (byte) 0x07, (byte) 0x00, (byte) 0x48, (byte) 0x0C, (byte) 0x3F, (byte) 0xB5, (byte) 0x6A,
				(byte) 0xD2, (byte) 0xC4, (byte) 0x1A, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xB3, (byte) 0xA7, (byte) 0x00, (byte) 0x00, (byte) 0x78, (byte) 0xDD };
		byte[] test2 = { (byte) 0x02, (byte) 0x69, (byte) 0x00, (byte) 0x37, (byte) 0x29, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x41, (byte) 0x7A, (byte) 0x00, (byte) 0x15, (byte) 0x05,
				(byte) 0x7E, (byte) 0xC8 };
		PackageProcessor p1 = new PackageProcessor(test2);
		System.out.println(p1.wiaPaCRC(test2.length));
		System.out.println(p1.intToTwoBytes(23495));
		byte[] g = p1.genWiaPaCRC(32456);
		for(int m=0;m<g.length;m++){
			System.out.println(g[m]);
		}

	}
}
