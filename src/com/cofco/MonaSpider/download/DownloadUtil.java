package com.cofco.MonaSpider.download;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 * 下载方法
 * 
 * @author mona
 *
 */
@SuppressWarnings("unused")
public class DownloadUtil {
	/* 单例 */
	private static DownloadUtil downloadUtil = null;

	public static synchronized DownloadUtil getInstance() {
		if (downloadUtil == null) {
			downloadUtil = new DownloadUtil();
		}
		return downloadUtil;
	}

	/**
	 * 通过HttpURLConnection获取网页
	 * 
	 * @param urlString
	 *            网页地址
	 * @return 页面HTML内容
	 */
	public synchronized static String getHtml(String urlString) {
		try {
			StringBuffer html = new StringBuffer();
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStreamReader isr = new InputStreamReader(conn.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String temp;
			while ((temp = br.readLine()) != null) {
				html.append(temp).append("\n");
			}
			br.close();
			isr.close();
			return html.toString();
		} catch (Exception exception) {
			System.err.println("DownloadUtil.class 网页找不到, 失效地址 : " + urlString);
			return null;
		}
	}

	/**
	 * 通过HttpClient的GET方式获取网页
	 * 参考地址:https://www.ibm.com/developerworks/cn/opensource/os-cn-crawler/
	 * 
	 * @param urlString
	 *            网页地址
	 * @return 页面HTML内容
	 */
	public synchronized static String downloadHtmlByGET(String urlString) {
		HttpClient httpClient = new HttpClient();
		/* 代理设定, 参考: http://llying.iteye.com/blog/333644 */
		// TODO 测试抓取数据用
		httpClient.getHostConfiguration().setProxy("127.0.0.1", 9000);
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);/* 设置http连接超时为5秒 */
		urlString = convertUrl(urlString);/* 中文地址转码 */
		GetMethod getMethod = new GetMethod(urlString);
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);/* 设置get请求超时为5秒 */
		/* 设置请求重试处理, 用的是默认的重试处理: 请求三次 */
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		/* 设置标记 */
		getMethod.setRequestHeader("User-Agent", "TestEduSpider");
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			/* 判断访问的状态码 */
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("DownloadUtil.class 获取页面失败 : " + getMethod.getStatusLine());
			}
			/// * 处理http响应内容 */
			// Header[] headers = getMethod.getResponseHeaders();
			// for (Header h : headers) {
			// System.out.println(h.getName() + " " + h.getValue());
			// }
			/* 读取网页内容 */
			StringBuffer html = new StringBuffer();
			InputStream is = getMethod.getResponseBodyAsStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String temp;
			while ((temp = br.readLine()) != null) {
				html.append(temp).append("\n");
			}
			br.close();
			is.close();
			return html.toString();
		} catch (HttpException httpException) {
			System.err.println("DownloadUtil.class 无效访问地址 : "
					+ urlString);/* 发生致命的异常, 可能是协议不对或者返回的内容有问题 */
			return null;
		} catch (IOException ioException) {
			System.err.println("DownloadUtil.class 发生网络异常 : " + urlString);
			return null;
		} finally {
			getMethod.releaseConnection();/* 释放连接 */
		}
	}

	private static final String zhPattern = "[\u4e00-\u9fa5]+";/* 中文正则表达式 */

	/**
	 * 转换地址中的中文
	 * 
	 * @param url
	 *            源地址
	 * @return 转换后地址
	 */
	private static String convertUrl(String url) {
		Pattern pattern = Pattern.compile(zhPattern);
		Matcher matcher = pattern.matcher(url);
		StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			try {
				matcher.appendReplacement(buffer, URLEncoder.encode(matcher.group(0), "utf-8"));
			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				System.err.println("DownloadUtil.class 地址转换失败 : " + url);
			}
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}
}