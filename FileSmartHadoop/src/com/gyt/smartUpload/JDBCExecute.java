package com.gyt.smartUpload;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @FileName: JDBCExecute.java
 * @Package: com.gyt.smartUpload
 * @Author: Gu Yongtao
 * @Description: 简单封装JDBC连接
 *
 * @Date: 2018年12月12日 下午7:04:02
 */

public class JDBCExecute {
	// 属性
	private static final String url = "jdbc:mysql://localhost:3306/smart_uploadDB";
	private static final String user = "root";
	private static final String password = "123456";
	private static Connection connection;
	{
		
	}
	
	public static Statement getStatement() throws ClassNotFoundException, SQLException {
		// 1. 加载驱动
		Class.forName("com.mysql.jdbc.Driver");
		// 2. 创建连接
		connection = DriverManager.getConnection(url, user, password);
		// 创建声明
		return connection.createStatement();
	}
	
	public static PreparedStatement getConnection(String sql) throws ClassNotFoundException, SQLException {
		// 1. 加载驱动
		Class.forName("com.mysql.jdbc.Driver");
		// 2. 创建连接
		connection = DriverManager.getConnection(url, user, password);
		// 创建声明
		return connection.prepareStatement(sql);
	}
	
}

