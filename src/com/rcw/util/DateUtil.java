package com.rcw.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Pnoker
 * @description 时间工具类
 */
public class DateUtil {

	/**
	 * @return 2016-10-21 08:33:53
	 */
	public String getCompleteTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	/**
	 * @return 2016-10-21 08:36
	 */
	public String getMinuteTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(date);
	}

	/**
	 * @return 2016-10-21
	 */
	public String getDayTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	/**
	 * @return 1477010427811
	 */
	public long getTimeNum(Date date) {
		return date.getTime();
	}

	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		System.out.println(sdf.format(date));
		System.out.println(date.getTime());
	}
}
