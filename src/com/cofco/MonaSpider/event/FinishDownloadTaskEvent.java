package com.cofco.MonaSpider.event;

import java.util.Map;

/**
 * 下载任务完成的事件
 * 
 * @author mona
 *
 */
public class FinishDownloadTaskEvent {
	private Map<String, String> downloadResultMap;

	/**
	 * 下载任务完成
	 * 
	 * @param downloadResultMap
	 *            下载内容Map
	 */
	public FinishDownloadTaskEvent(Map<String, String> downloadResultMap) {
		this.downloadResultMap = downloadResultMap;
	}

	public Map<String, String> getDownloadResultMap() {
		return downloadResultMap;
	}
}
