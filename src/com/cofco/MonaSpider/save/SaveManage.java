package com.cofco.MonaSpider.save;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofco.MonaSpider.event.EventBus;
import com.cofco.MonaSpider.event.FinishDownloadPageEvent;

/**
 * 保存管理
 * 
 * @author mona
 *
 */
public class SaveManage {
	/* 单例 */
	private static SaveManage saveManage = null;

	public static synchronized SaveManage getInstance() {
		if (saveManage == null) {
			saveManage = new SaveManage();
		}
		return saveManage;
	}

	private Connection connection = null; // 定义一个MYSQL链接对象
	private ResultSet resultSet = null;
	private PreparedStatement statement;

	/**
	 * 保存管理
	 */
	private SaveManage() {
		try {
			// TODO 用户名密码
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(
					"jdbc:mysql://127.0.0.1:3306/monaspiderproject?useUnicode=true&characterEncoding=utf-8&autoReconnect=true", "root", "monawolf"); // 链接本地MYSQL
		} catch (SQLException sqlException) {
			System.err.println("SaveManage.class SQLException " + sqlException.getStackTrace());
		} catch (ClassNotFoundException classNotFoundException) {
			System.err.println("SaveManage.class ClassNotFoundException " + classNotFoundException.getStackTrace());
		} catch (Exception exception) {
			System.err.println("SaveManage.class Exception " + exception.getStackTrace());
		}

		/* 下载完成一个页面事件 */
		EventBus.getInstance().addEventListener(FinishDownloadPageEvent.class, eventObject -> {
			saveAPage((FinishDownloadPageEvent) eventObject);
		});
	}

	/**
	 * 保存页面到数据库
	 * 
	 * @param htmlPage
	 *            页面对象
	 */
	private void saveAPage(FinishDownloadPageEvent htmlPage) {
		try {
			String sql = "select * from tbl_page where page_url = ?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, htmlPage.getUrl());
			resultSet = statement.executeQuery();
			/* url为条件是否有记录，没有创建，有则覆盖 */
			if (!resultSet.next()) {
				sql = "insert into tbl_page(page_url,page_code) values (? , ?)";
				statement = connection.prepareStatement(sql);
				statement.setString(1, htmlPage.getUrl());
				statement.setString(2, htmlPage.getHtml().trim());
				statement.executeUpdate();
			} else {
				sql = "update tbl_page set page_code = ? where page_url = ?";
				statement = connection.prepareStatement(sql);
				statement.setString(1, htmlPage.getHtml().trim());
				statement.setString(2, htmlPage.getUrl());
				statement.executeUpdate();
			}
		} catch (SQLException sqlException) {
			System.err.println("SaveManage.class SQLException " + sqlException.getStackTrace());
		}
		System.out.println("save page finish for url is " + htmlPage.getUrl());
	}
}
