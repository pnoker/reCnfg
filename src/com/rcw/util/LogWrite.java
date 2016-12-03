package com.rcw.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

/**
 * @author Pnoker
 * @description 日志工具类
 */
public class LogWrite {
	private BufferedWriter fw = null;
	private String path = "D:/dataCollect/RemoteConfigLog/";
	private DateUtil date;
	private PrintUtil printUtil;

	public LogWrite() {
		this.printUtil = new PrintUtil();
		this.date = new DateUtil();
		if (!(new File(path).isDirectory())) {
			new File(path).mkdirs();
		}
	}

	public void write(String detail) {
		String file = path + "/" + date.getDayTime(new Date()) + ".txt";
		try {
			FileOutputStream fos = new FileOutputStream(file, true);
			fw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
			fw.write(date.getCompleteTime(new Date()) + " " + " --> " + detail);
			printUtil.printDetail(detail);
			fw.newLine();
			fw.flush();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void close() {
		try {
			fw.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		String detail = "01830b00cd450000740010";
		String regex = "(.{2})";
		detail = detail.replaceAll(regex, "$1 ");
		System.out.println(detail);
	}
}
