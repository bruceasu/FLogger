package com.cyfonly.flogger.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 * @author yunfeng.cheng
 * @version 2015/10/31
 */
public class TimeUtil {
	
	/**
	 * 获取完全格式的的日期格式
	 * @return 格式如 2015-10-31 10:33:25.012
	 */
	public static String getFullDateTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return sdf.format(new Date());
	}
	
	/**
	 * 获取机器时间
	 * @return 年月日，格式如2015-10-31
	 */
	public static String getPCDateISO() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date());
	}
	
	/**
	 * 获取机器时间
	 * @return 年月日，格式如20151031
	 */
	public static String getPCDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}
	
	/**
	 * 获取当前时间
	 * @return 时分秒，格式如122415，12时24分15秒
	 */
	public static String getCurrTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		return sdf.format(new Date());
	}
	/**
	 * 获取当前时间
	 * @return 时分秒，格式如122415012，12时24分15秒012毫秒
	 */
	public static String getCurrTimeMills() {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmssSSS");
		return sdf.format(new Date());
	}
	/**
	 * 获取当前时间
	 * @return 时分秒，格式如122415012，12时24分15秒012毫秒
	 */
	public static String getCurrTimeMills(long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmssSSS");
		return sdf.format(new Date(timestamp));
	}
}
