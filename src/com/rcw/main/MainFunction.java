package com.rcw.main;

import java.util.HashMap;
import java.util.Map;

import com.rcw.pojo.BaseInfo;
import com.rcw.test.QueryPara;
import com.rcw.util.ExcutePro;
import com.rcw.util.Generation;

public class MainFunction {
	public static Map<String, String> code = new HashMap<String, String>();
	public static Map<String, String> item = new HashMap<String, String>();

	public static void initParameter() {
		try {
			code = ExcutePro.getProperties("command.properties");
			item = ExcutePro.getProperties("typeserial.properties");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void query(String typeserial, int serial) {
		QueryPara queryPara = new QueryPara();
		Generation generation = new Generation();
		byte[] connectCode = generation.connect();// 连接网关命令，固定写法
		byte[] sendCode = generation.queryCommand(typeserial, serial);
		BaseInfo base = new BaseInfo();
		base.setIpaddress(MainFunction.item.get(typeserial).split("#")[1]);
		base.setLocalport(Integer.parseInt(MainFunction.item.get(typeserial).split("#")[2]));
		base.setPort(6001);
		queryPara.query(base, connectCode, typeserial, serial);
		queryPara.query(base, sendCode, typeserial, serial);
	}

	public static void config(String typeserial, int serial, float value) {
		QueryPara queryPara = new QueryPara();
		Generation generation = new Generation();
		byte[] connectCode = generation.connect();// 连接网关命令，固定写法
		byte[] sendCode = generation.configCommand(typeserial, serial, value);
		BaseInfo base = new BaseInfo();
		base.setIpaddress(MainFunction.item.get(typeserial).split("#")[1]);
		base.setLocalport(Integer.parseInt(MainFunction.item.get(typeserial).split("#")[2]));
		base.setPort(6001);
		queryPara.query(base, connectCode, typeserial, serial);
		queryPara.query(base, sendCode, typeserial, serial);
	}

	public static void config(String typeserial, int serial, String value) {
		QueryPara queryPara = new QueryPara();
		Generation generation = new Generation();
		byte[] connectCode = generation.connect();// 连接网关命令，固定写法
		byte[] sendCode = generation.configCommand(typeserial, serial, value);
		BaseInfo base = new BaseInfo();
		base.setIpaddress(MainFunction.item.get(typeserial).split("#")[1]);
		base.setLocalport(Integer.parseInt(MainFunction.item.get(typeserial).split("#")[2]));
		base.setPort(6001);
		queryPara.query(base, connectCode, typeserial, serial);
		queryPara.query(base, sendCode, typeserial, serial);
	}

	public static void main(String[] args) {
		System.out.println("<---初始化操作--->");
		System.out.println("<---启动远程配置线程--->");
		initParameter();
		// query("shui", 10);
		// config("shui", 9,"1_Aa-您好%*！@#￥%……");
		// query("shui", 10);
		query("IMTAG.JL-390002", 15);
	}
}
