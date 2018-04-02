package me.asu.logging.RequestLogger;

/**
 * 只输出，删除换行符
 * @author Victor
 * @version 2015/10/31
 */
public class RequestLogger {

	private static RequestLogger instance;
	private static RequestLogManager logManager;

	static {
		logManager = RequestLogManager.getInstance();
	}

	public RequestLogger(){
		Runtime.getRuntime().addShutdownHook(new Thread(() -> close()));
	}
	
	public static synchronized RequestLogger getInstance(){
		if(instance == null){
			instance = new RequestLogger();
		}
		return instance;
	}

	/**
	 * 写日志
	 * @param logMsg 日志内容
	 */
	public void writeLog(String logMsg){
		if(logMsg != null){
			StringBuffer sb = new StringBuffer(logMsg.length());
//			sb.append(TimeUtil.getFullDateTime());
//			sb.append(" ");
			sb.append(logMsg.replaceAll("[\\r\\n]", ""));
			sb.append("\n");

			logManager.addLog(sb);
		}
	}
	
	/**
	 * 优雅关闭
	 */
	public void close(){
		logManager.close();
	}
	
}
