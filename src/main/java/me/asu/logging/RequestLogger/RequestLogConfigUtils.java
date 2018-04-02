package me.asu.logging.RequestLogger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Properties;

/**
 * 公用工具类
 *
 * @author Suk
 */
public class RequestLogConfigUtils {

	/**
	 * 配置文件名
	 */
	private static final String CONFIG_FILE_NAME = "application.properties";

	/**
	 * 配置map
	 */
	private static HashMap<String, Object[]> propsMap = new HashMap<>();

	/**
	 * 从配置文件中取得 String 值，若无则返回默认值
	 *
	 * @param keyName 属性名
	 * @param defaultValue 默认值
	 * @return 属性值
	 */
	public static String getConfigByString(String keyName, String defaultValue) {
		String value = getConfig(keyName);
		if (value != null && value.length() > 0) {
			return value.trim();
		} else {
			return defaultValue;
		}
	}

	/**
	 * 从配置文件中取得 int 值，若无（或解析异常）则返回默认值
	 *
	 * @param keyName 属性名
	 * @param defaultValue 默认值
	 * @return 属性值
	 */
	public static int getConfigByInt(String keyName, int defaultValue) {
		String value = getConfig(keyName);
		if (value != null && value.length() > 0) {
			try {
				int parseValue = Integer.parseInt(value.trim());
				return parseValue;
			} catch (Exception e) {
				return defaultValue;
			}
		} else {
			return defaultValue;
		}
	}

	/**
	 * 从配置文件中取得 long 值，若无（或解析异常）则返回默认值
	 *
	 * @param keyName 属性名
	 * @param defaultValue 默认值
	 * @return 属性值
	 */
	public static long getConfigByLong(String keyName, long defaultValue) {
		String value = getConfig(keyName);
		if (value != null && value.length() > 0) {
			try {
				long parseValue = Long.parseLong(value.trim());
				return parseValue;
			} catch (Exception e) {
				return defaultValue;
			}
		} else {
			return defaultValue;
		}
	}

	/**
	 * 从配置文件中取得 boolean 值，若无则返回默认值
	 *
	 * @param keyName 属性名
	 * @param defaultValue 默认值
	 * @return 属性值
	 */
	public static boolean getConfigByBoolean(String keyName, boolean defaultValue) {
		String value = getConfig(keyName);
		if (value != null && value.length() > 0) {
			return Boolean.parseBoolean(value.trim());
		} else {
			return defaultValue;
		}
	}

	/**
	 * 从配置文件中读取字符串的值
	 * 配置文件查找顺序：
	 * 1-项目根路径
	 * 2-src/main/resources
	 *
	 * @param keyName 属性名
	 * @return 属性值
	 */
	private static String getConfig(String keyName) {
		Properties props = null;
		boolean bIsNeedLoadCfg = false;
		boolean maybeInClasspath = false;
		String filePath = CONFIG_FILE_NAME;
		File cfgFile = new File(filePath);
		if (!cfgFile.exists()) {
			try {
				filePath = RequestLogConfigUtils.class.getClassLoader()
						.getResource(CONFIG_FILE_NAME).getPath();
			} catch (Exception e) {
				return null;
			}
			cfgFile = new File(filePath);
			if (!cfgFile.exists()) {
				maybeInClasspath = true;
			}
		}

		Object[] arrs = propsMap.get(filePath);
		if (arrs == null) {
			bIsNeedLoadCfg = true;
		} else if (maybeInClasspath) {
			bIsNeedLoadCfg = false;
			props = (Properties) arrs[1];
		} else {
			Long lastModify = (Long) arrs[0];
			if (lastModify.longValue() != cfgFile.lastModified()) {
				bIsNeedLoadCfg = true;
			} else {
				props = (Properties) arrs[1];
			}
		}

		if (bIsNeedLoadCfg == true) {
			if (maybeInClasspath) {
				try(InputStream fis = inputStreamFromClasspath()) {
					props = new Properties();
					props.load(fis);
					propsMap.put(filePath, new Object[]{System.currentTimeMillis(), props});
				} catch (Exception e) {
					return "";
				}
			} else {
				try(InputStream fis = inputStreamFromFile(cfgFile)) {
					props = new Properties();
					props.load(fis);
					propsMap.put(filePath, new Object[]{cfgFile.lastModified(), props});
				} catch (Exception e) {
					return "";
				}
			}

		}
		return props.getProperty(keyName, "");
	}

	private static FileInputStream inputStreamFromFile(File cfgFile) throws FileNotFoundException {
		return new FileInputStream(cfgFile);
	}

	private static InputStream inputStreamFromClasspath() {
		return RequestLogConfigUtils.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
	}

	/**
	 * 将字符串转为字节数组
	 *
	 * @param str 源字符串
	 * @return 字节数组
	 */
	public static byte[] StringToBytes(String str) {
		try {
			if (str == null || str.length() <= 0) {
				return new byte[0];
			} else {
				return str.getBytes(getConfigByString("gateway.request.logging.charset_name", "UTF-8"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将异常的堆栈信息转为字符串
	 *
	 * @param e 异常
	 * @return 异常的字符串描述
	 */
	public static String getExpStack(Exception e) {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(bo);
		e.printStackTrace(pw);
		pw.flush();
		pw.close();
		return bo.toString();
	}

}
