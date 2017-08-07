package com.cofco.MonaSpider.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 针对a标签的href属性过滤
 * 
 * @author mona
 *
 */
public class UrlManage {
	private List<String> findUrlList = new ArrayList<>();/* 查询域名 */
//	private List<String> skipUrlList = new ArrayList<>();/* 跳过域名 */

	public UrlManage() {
		findUrlList.add("http://www.meishij.net");
		findUrlList.add("http://images.meishij.net");
		findUrlList.add("http://i.meishi.cc");

//		skipUrlList.add("#");
	}

	public List<String> getFindUrlList() {
		return findUrlList;
	}

//	public List<String> getSkipUrlList() {
//		return skipUrlList;
//	}
}
