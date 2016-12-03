package com.rcw.util;

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

	public static void main(String[] args) {
		Generation generation = new Generation();
		// generation.StringToBytes("0269003729000000417A0015057EC8");
		byte[] code = generation.StringToBytes("0269003729000000417A001505");
		for (int m = 0; m < code.length; m++) {
			System.out.println(code[m]);
		}
	}
}
