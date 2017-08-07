package com.cofco.MonaSpider.download;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.cofco.MonaSpider.event.EventBus;
import com.cofco.MonaSpider.event.FinishDownloadPageEvent;
import com.cofco.MonaSpider.event.FinishDownloadTaskEvent;
import com.cofco.MonaSpider.util.ThreadManage;
import com.cofco.MonaSpider.util.TypeEnum;
import com.cofco.MonaSpider.util.UrlManage;

/**
 * 下载管理
 * 
 * @author mona
 *
 */
public class DownloadManage {
	/* 单例 */
	private static DownloadManage downloadManage = null;

	public static synchronized DownloadManage getInstance(String url) {
		if (downloadManage == null) {
			downloadManage = new DownloadManage(url);
		}
		return downloadManage;
	}

	/* 下载管理 */
	private List<String> waitDownloadList = new ArrayList<>();/* 记录等待下载队列 */
	private List<String> finishDownloadList = new ArrayList<>();/* 记录下载完成队列 */
	private List<String> errorDownloadList = new ArrayList<>();/* 记录错误下载队列 */
	private Map<String, String> downloadResultMap = new HashMap<>();/* 记录下载结果 */
	private ThreadManage threadManage = ThreadManage.getInstance();/* 线程管理工具类 */

	/**
	 * 根据起始网页爬所有网页
	 * 
	 * @param url
	 *            起始网页地址
	 */
	private DownloadManage(String url) {
		waitDownloadList.add(url);
		Thread thread = threadManage.creatThread(TypeEnum.DOWNLOAD, new Runnable() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				// TODO
				int time = 1;

				while (waitDownloadList.size() > 0) {
					String downloadUrl = waitDownloadList.get(0);
					String downloadHtml = DownloadUtil.getInstance().downloadHtmlByGET(downloadUrl);
					if (downloadHtml != null) {
						/* 保存内容, 刷新列表 */
						finishDownloadList.add(downloadUrl);
						waitDownloadList.remove(downloadUrl);
						downloadResultMap.put(downloadUrl, downloadHtml);

						/* 下载完一个页面事件, 将下载内容和地址传到事件总线上 */
						EventBus.getInstance().fireEvent(new FinishDownloadPageEvent(downloadUrl, downloadHtml));
						/* 将网页中的链接添加到列表 */
						filterUrl(downloadHtml);
					} else {
						/* 页面找不到, 即404错误 */
						errorDownloadList.add(downloadUrl);
						waitDownloadList.remove(downloadUrl);
						downloadResultMap.put(downloadUrl, "");
					}

					// TODO
					time++;
					if (time > 500) {
						break;
					}

					// TODO 延时
					try {
						Thread.sleep(500);
					} catch (InterruptedException interruptedException) {
						System.err.println("DownloadManage.class InterruptedException : " + interruptedException.getStackTrace());
					}
				}
				/* 所有下载完成事件 */
				EventBus.getInstance().fireEvent(new FinishDownloadTaskEvent(downloadResultMap));
			}
		});

		/* 开始抓取网页 */
		if (thread != null) {
			threadManage.startThread(thread);
		} else {
			System.err.println("DownloadManage.class 线程为空不能启动");
		}
	}

	/**
	 * 过滤当前页面中的地址并用白名单筛选
	 * 
	 * @param html
	 *            网页内容
	 */
	private void filterUrl(String html) {
		List<String> nextUrlList = new ArrayList<>();

		Document doc = Jsoup.parseBodyFragment(html);
		Element body = doc.body();
		Elements links = body.getElementsByTag("a");
		/* 遍历所有a标签筛选 */
		for (Element link : links) {
			List<String> findUrlList = new UrlManage().getFindUrlList();
			for (String findUrl : findUrlList) {
				String linkHref = link.attr("href");
				// String linkText = link.text().trim();
				/* 白名单 和 完全相同的重复过滤 */
				if (linkHref.contains(findUrl) && !nextUrlList.contains(linkHref)) {
					nextUrlList.add(linkHref);
				}
			}
		}

		List<String> removeUrlList = new ArrayList<>();
		/* 定位符外相同地址过滤 */
		for (int i = 0; i < nextUrlList.size(); i++) {
			for (int j = 0; j < nextUrlList.size(); j++) {
				if (nextUrlList.get(i).contains(nextUrlList.get(j) + "#")) {
					removeUrlList.add(nextUrlList.get(i));
				}
			}
		}

		/* 过滤: 排队列表, 成功列表 ,失败列表 */
		for (String nextUrl : nextUrlList) {
			for (String waitUrl : waitDownloadList) {
				if (nextUrl.equals(waitUrl) || nextUrl.equals(waitUrl + "/") || nextUrl.contains(waitUrl + "#")) {
					removeUrlList.add(nextUrl);
				}
			}
			for (String finishUrl : finishDownloadList) {
				if (nextUrl.equals(finishUrl) || nextUrl.equals(finishUrl + "/") || nextUrl.contains(finishUrl + "#")) {
					removeUrlList.add(nextUrl);
				}
			}
			for (String errorUrl : errorDownloadList) {
				if (nextUrl.equals(errorUrl) || nextUrl.equals(errorUrl + "/") || nextUrl.contains(errorUrl + "#")) {
					removeUrlList.add(nextUrl);
				}
			}
		}

		/* 移除不满足的地址 */
		for (String removeUrl : removeUrlList) {
			nextUrlList.remove(removeUrl);
		}

		/* 新链接添加 */
		waitDownloadList.addAll(nextUrlList);

		/* 输出遍历当前网页后新增需要下载的任务 */
		// System.out.println("新增下载任务 " + nextUrlList.size() + " / " +
		// waitDownloadList.size());
	}
}