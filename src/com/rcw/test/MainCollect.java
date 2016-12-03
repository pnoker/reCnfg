package com.rcw.test;

import com.rcw.pojo.BaseInfo;

public class MainCollect {
	public static void main(String[] args) {

		System.out.println("<---开启采数线程--->");
		BaseInfo base = new BaseInfo();
		base.setIpaddress("10.112.126.29");
		base.setLocalport(6003);
		base.setPort(6001);

		ReceiverDatagram receiverDatagram = new ReceiverDatagram(base);
		Thread thread = new Thread(receiverDatagram);
		thread.start();
		try {
			Thread.sleep(3000);// 3秒后启动下一个线程
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}
}
