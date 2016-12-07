package com.rcw.main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.rcw.pojo.BaseInfo;
import com.rcw.test.QueryPara;
import com.rcw.util.ExcutePro;
import com.rcw.util.Generation;
import com.rcw.util.Sqlserver;

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
		Sqlserver dBtool = new Sqlserver();
		boolean isnew = true;
		byte[] connectCode = generation.connect();// 连接网关命令，固定写法
		byte[] sendCode = generation.queryCommand(typeserial, serial);
		BaseInfo base = new BaseInfo();
		base.setIpaddress(MainFunction.item.get(typeserial).split("#")[1]);
		base.setLocalport(Integer.parseInt(MainFunction.item.get(typeserial).split("#")[2]));
		base.setPort(6001);
		queryPara.query(base, connectCode, typeserial, serial);
		String result = queryPara.query(base, sendCode, typeserial, serial);
		String sql = "delete from Command where typeserial = '" + typeserial + "' and type = '" + serial + "'";
		try {
			dBtool.executeUpdate(sql);
		} catch (SQLException e1) {
			System.out.println(e1.getMessage());
		}
		sql = "select * from Parameter where typeserial = '" + typeserial + "' and type = '" + serial + "'";
		try {
			ResultSet rs = dBtool.executeQuery(sql);
			while (rs.next()) {
				isnew = false;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		if (isnew) {
			sql = "insert into Parameter (typeserial,type,value,time) values ('" + typeserial + "','" + serial + "','"
					+ result + "',getdate())";
			try {
				dBtool.executeUpdate(sql);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		} else {
			sql = "update Parameter set value = '" + result + "' ,time = getdate() where typeserial = '" + typeserial
					+ "' and type = '" + serial + "'";
			try {
				dBtool.executeUpdate(sql);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		try {
			dBtool.free();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void config(String typeserial, int serial, float value) {
		QueryPara queryPara = new QueryPara();
		Generation generation = new Generation();
		Sqlserver dBtool = new Sqlserver();
		byte[] connectCode = generation.connect();// 连接网关命令，固定写法
		byte[] sendCode = generation.configCommand(typeserial, serial, value);
		BaseInfo base = new BaseInfo();
		base.setIpaddress(MainFunction.item.get(typeserial).split("#")[1]);
		base.setLocalport(Integer.parseInt(MainFunction.item.get(typeserial).split("#")[2]));
		base.setPort(6001);
		queryPara.query(base, connectCode, typeserial, serial);
		queryPara.query(base, sendCode, typeserial, serial);
		String sql = "delete from Command where typeserial = '" + typeserial + "' and type = '" + serial + "'";
		try {
			dBtool.executeUpdate(sql);
			dBtool.free();
		} catch (SQLException e1) {
			System.out.println(e1.getMessage());
		}
	}

	public static void config(String typeserial, int serial, String value) {
		QueryPara queryPara = new QueryPara();
		Generation generation = new Generation();
		Sqlserver dBtool = new Sqlserver();
		byte[] connectCode = generation.connect();// 连接网关命令，固定写法
		byte[] sendCode = generation.configCommand(typeserial, serial, value);
		BaseInfo base = new BaseInfo();
		base.setIpaddress(MainFunction.item.get(typeserial).split("#")[1]);
		base.setLocalport(Integer.parseInt(MainFunction.item.get(typeserial).split("#")[2]));
		base.setPort(6001);
		queryPara.query(base, connectCode, typeserial, serial);
		queryPara.query(base, sendCode, typeserial, serial);
		String sql = "delete from Command where typeserial = '" + typeserial + "' and type = '" + serial + "'";
		try {
			dBtool.executeUpdate(sql);
			dBtool.free();
		} catch (SQLException e1) {
			System.out.println(e1.getMessage());
		}
	}

	public static boolean read(int serial) {
		boolean result = false;
		if (serial == 1) {
			System.out.println("远程查询水表流量值命令号");
			result = true;
		}
		if (serial == 3) {
			System.out.println("远程读取水表流量基数");
			result = true;
		}
		if (serial == 6) {
			System.out.println("远程读取水表磁性指针位置");
			result = true;
		}
		if (serial == 8) {
			System.out.println("远程读取水表上传周期");
			result = true;
		}
		if (serial == 10) {
			System.out.println("远程读取水表位号");
			result = true;
		}
		if (serial == 11) {
			System.out.println("远程重启水表");
			result = true;
		}
		if (serial == 12) {
			System.out.println("水表脉冲查询");
			result = true;
		}
		if (serial == 13) {
			System.out.println("远程读取IO");
			result = true;
		}
		if (serial == 14) {
			System.out.println("远程读取开封开润IO瞬时值");
			result = true;
		}
		if (serial == 15) {
			System.out.println("远程读取开封开润IO累计值");
			result = true;
		}
		return result;
	}

	// sql = "update devicefaultmanage set networkStatus = '1',dataStatus =
	// '1',batteryStatus = '1',reachtime = getdate() where typeserial = '"+
	// typeserial + "";
	public String status() {
		String result = "";
		Sqlserver connect1 = new Sqlserver();
		Sqlserver connect2 = new Sqlserver();
		String sql = "select * from fs_equipmentmanage";
		ResultSet rs;
		try {
			rs = connect1.executeQuery(sql);
			while (rs.next()) {
				String typeserial = rs.getString("othername");
				String status = rs.getString("status");
				if (status.equals("工作中")) {
					sql = "update devicefaultmanage set dataStatus = '1',reachtime = getdate() where typeserial = '"
							+ typeserial + "";
				}
				if (status.equals("非工作中")) {
					sql = "update devicefaultmanage set dataStatus = '0',reachtime = getdate() where typeserial = '"
							+ typeserial + "";
				}
				connect2.executeUpdate(sql);
			}
			sql = "select * from fs_equipmentmanage";
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		try {
			connect1.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return result;
	}

	public static void main(String[] args) {
		System.out.println("<---初始化操作--->");
		System.out.println("<---启动远程配置线程--->");
		initParameter();
		// query("shui", 10);
		// config("shui", 9,"1_Aa-您好%*！@#￥%……");
		// query("shui", 10);
		// query("IMTAG.JL-390002", 15);
		Timer timer = new Timer();
		// 远程配置任务
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				Sqlserver connect = new Sqlserver();
				String sql = "select * from Command where enable = 0 order by time asc";
				try {
					ResultSet rs = connect.executeQuery(sql);
					while (rs.next()) {
						String typeserial = rs.getString("typeserial");// 位号
						int serial = Integer.parseInt(rs.getString("type"));// 操作命令
						String operation = rs.getString("operation");// 操作
						String value = rs.getString("value");
						if (operation.contains("R")) {
							if (read(serial)) {
								query(typeserial, serial);
							}
						}
						if (operation.contains("W")) {
							if (serial == 2) {
								System.out.println("远程配置水表流量基数");
								config(typeserial, serial, Float.parseFloat(value));
							}
							if (serial == 4) {
								System.out.println("远程配置水表脉冲数清零");
								config(typeserial, serial, value);
							}
							if (serial == 5) {
								System.out.println("远程配置水表磁性指针位置");
								config(typeserial, serial, (1 / Float.parseFloat(value)));
							}
							if (serial == 7) {
								System.out.println("远程配置水表上传周期");
								config(typeserial, serial, Float.parseFloat(value));
							}
							if (serial == 9) {
								System.out.println("远程配置水表位号");
								config(typeserial, serial, value);
							}
						}
					}
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}
				try {
					connect.free();
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}
			}
		}, 1000, 1000);
		// 设备故障管理
		timer.schedule(new TimerTask() {

			@Override
			public void run() {

			}
		}, 1000 * 20, 1000 * 60 * 5);
	}
}
