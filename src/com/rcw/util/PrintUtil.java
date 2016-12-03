package com.rcw.util;

import java.util.Date;

/**
 * @author Pnoker
 * @description 打印工具类
 */
public class PrintUtil {
	private DateUtil date;

	public PrintUtil() {
		this.date = new DateUtil();
	}

	public void printDetail(String ipaddress, String detail) {
		while (ipaddress.length() < 15) {
			ipaddress += " ";
		}
		System.out.println(date.getCompleteTime(new Date()) + " " + ipaddress + " --> " + detail);
	}

	public void printMessage(String message) {
		System.out.println("<---- " + date.getCompleteTime(new Date()) + " ----> " + message);
	}

	public static void main(String[] args) {
		String i = "110.112.126.19";
		String n = "10.112.141.212";
		while (i.length() < 15) {
			i += " ";
		}
		if (n.length() == 15)
			System.out.println("mm");
		System.out.println(i.length());
		System.out.println(n.length());
	}
}
