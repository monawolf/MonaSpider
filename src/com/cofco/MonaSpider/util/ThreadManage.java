package com.cofco.MonaSpider.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 线程管理
 * 
 * @author mona
 *
 */
public class ThreadManage extends Thread {
	/* 单例 */
	private static ThreadManage threadManage = null;

	public static synchronized ThreadManage getInstance() {
		if (threadManage == null) {
			threadManage = new ThreadManage();
		}
		return threadManage;
	}

	/* 线程管理 */
	private List<Thread> downloadThreadList = new ArrayList<>();/* 下载线程列表 */

	/**
	 * 创建线程
	 * 
	 * @param type
	 *            当前线程状态
	 * @param runnable
	 *            运行接口对象
	 * @return 当前线程对象
	 */
	public Thread creatThread(TypeEnum type, Runnable runnable) {
		Thread thread = new Thread(runnable);
		if (type == TypeEnum.DOWNLOAD) {
			downloadThreadList.add(thread);
			return thread;
		}
		return null;
	}

	/**
	 * 启动线程
	 * 
	 * @param thread
	 *            线程对象
	 */
	public void startThread(Thread thread) {
		if (thread != null) {
			thread.start();
		}
	}
}