package com.rcw.util;

import java.util.HashMap;
import java.util.Map;

public class Command {
	private Map<String, String> code = new HashMap<String, String>();
	private Map<String, String> item = new HashMap<String, String>();

	public Command() {
		try {
			this.code = ExcutePro.getProperties("command.properties");
			this.item = ExcutePro.getProperties("typeserial.properties");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public String queryCommand(String typeserial, int serial) {
		String command = "02";// 协议标识
		command += "6900";// 远程读取或设置命令号
		command += item.get(typeserial).split("#")[0];// 设备的长地址
		command += code.get("" + serial);// 操作命令号
		return command;
	}
}
