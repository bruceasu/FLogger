package me.asu.logging.RequestLogger;

import com.cyfonly.flogger.strategy.LogFileItem;
import com.cyfonly.flogger.strategy.LogManager;
import com.cyfonly.flogger.utils.TimeUtil;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.asu.logging.util.StringUtils;

/**
 * 日志管理线程
 *
 * @author yunfeng.cheng
 * @version 2015/10/31
 */
public class RequestLogManager extends LogManager {

	/**
	 * 单例
	 */
	private static RequestLogManager instance = null;

	/**
	 * 日志文件列表
	 */
	private Map<String, LogFileItem> logFileMap = new ConcurrentHashMap<>();



	/**
	 * 日志文件根目录
	 */
	public final static File ROOT_DIR = new File(
			RequestLogConfigUtils.getConfigByString("gateway.request.logging.path", "./log"));

	public final static String REQ_LOG_KEY = "req-log";

	/**
	 * 是否运行
	 */
	private boolean bIsRun = true;

	static {
		//判断日志root路径是否存在，不存在则先创建
		if (!ROOT_DIR.exists() || !ROOT_DIR.isDirectory()) {
			ROOT_DIR.mkdirs();
		}
		/* 日志写入的间隔时间 */
		WRITE_LOG_INV_TIME = RequestLogConfigUtils
				.getConfigByLong("gateway.request.logging.write_log_inv_time", 1000);

		/* 单个日志文件的大小(默认为100M) */
		SINGLE_LOG_FILE_SIZE = RequestLogConfigUtils
				.getConfigByLong("gateway.request.logging.single_log_file_size", 1024 * 1024 * 100);

		/* 缓存大小(默认10KB) */
		SINGLE_LOG_CACHE_SIZE = RequestLogConfigUtils
				.getConfigByLong("gateway.request.logging.single_log_cache_size", 1024 * 10);
	}

	public RequestLogManager() {

	}

	/**
	 * 获得日志管理类单例
	 */
	public synchronized static RequestLogManager getInstance() {
		if (instance == null) {
			instance = new RequestLogManager();
			instance.setName("RequestLogManager");
			instance.start();
		}
		return instance;
	}

	/**
	 * 添加日志
	 *
	 * @param logMsg 日志内容
	 */
	public void addLog(StringBuffer logMsg) {
		//获得单个日志文件的信息
		super.addLog(REQ_LOG_KEY, logMsg);
	}

	/**
	 * 线程方法
	 */
	@Override
	public void run() {
		while (bIsRun) {
			try {
				//输出到文件
				flush(false);
			} catch (Exception e) {
				System.out.println("开启日志服务错误...");
				e.printStackTrace();
			}
		}
	}



	/**
	 * 创建日志文件
	 */
	@Override
	protected void createLogFile(LogFileItem lfi) {
		//当前系统日期
		String currPCDate = TimeUtil.getPCDateISO();
		boolean tooLarge = false;
		//如果超过单个文件大小，则拆分文件
		if (!StringUtils.isEmpty(lfi.fullLogFileName)
				&& lfi.currLogSize >= SINGLE_LOG_FILE_SIZE) {
			// create new file
			tooLarge = true;
		}

		//创建文件
		if (tooLarge || StringUtils.isEmpty(lfi.fullLogFileName) || !lfi.lastPCDate.equals(currPCDate)) {
			if (!ROOT_DIR.exists()) {
				ROOT_DIR.mkdir();
			}
			long timestamp = timeGen();
			//如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
			if (timestamp < lfi.lastTimestamp) {
				throw new RuntimeException(
						String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lfi.lastTimestamp - timestamp));
			}

			//如果是同一时间生成的，则进行毫秒内序列
			if (lfi.lastTimestamp == timestamp) {
				//阻塞到下一个毫秒,获得新的时间戳
				timestamp = tilNextMillis(lfi.lastTimestamp);
			}
			//上次生成ID的时间截
			lfi.lastTimestamp = timestamp;

			String fileName = lfi.logFileName + "_" + currPCDate + "_" + TimeUtil.getCurrTimeMills(timestamp) + ".log";
			File logFile = new File(ROOT_DIR,  fileName);
			lfi.fullLogFileName = logFile.getAbsolutePath();
			lfi.lastPCDate = currPCDate;
			if (logFile.exists()) {
				lfi.currLogSize = logFile.length();
			} else {
				lfi.currLogSize = 0;
			}

		} else {
			// using current file.
		}
	}

	/**
	 * 阻塞到下一个毫秒，直到获得新的时间戳
	 *：www.xttblog.com
	 * @param lastTimestamp 上次生成ID的时间截
	 * @return 当前时间戳
	 */
	protected long tilNextMillis(long lastTimestamp) {
		long timestamp = timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = timeGen();
		}
		return timestamp;
	}

	/**
	 * 返回以毫秒为单位的当前时间
	 * @return 当前时间(毫秒)
	 */
	protected long timeGen() {
		return System.currentTimeMillis();
	}
}
