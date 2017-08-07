package com.cofco.MonaSpider;

import com.cofco.MonaSpider.download.DownloadManage;
import com.cofco.MonaSpider.save.SaveManage;

public class Main {
	public static void main(String[] args) {
		SaveManage.getInstance();
//		DownloadManage.getInstance("http://www.meishij.net");
		DownloadManage.getInstance("http://www.meishij.net/梨");
	}
}
