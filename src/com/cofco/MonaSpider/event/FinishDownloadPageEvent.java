package com.cofco.MonaSpider.event;

/**
 * 下载完成一个页面的事件
 * 
 * @author mona
 *
 */
public class FinishDownloadPageEvent {
	private String url;
	private String html;

	/**
	 * 下载页面完成事件
	 * 
	 * @param url
	 *            页面地址
	 * @param html
	 *            页面内容
	 */
	public FinishDownloadPageEvent(String url, String html) {
		this.url = url;
		this.html = html;
	}

	public String getUrl() {
		return url;
	}

	public String getHtml() {
		return html;
	}
}
