package com.gyt.smartUpload;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * @FileName: JDBClib5.java
 * @Package: com.gyt.lib
 * @Author: Gu Yongtao
 * @Description: 访问数据库
 * 
 * SQL语句：
	CREATE DATABASE IF NOT EXISTS smart_uploadDB DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
	
	USE smart_uploadDB;
	
	CREATE TABLE `smart_upload` (
	`id`  int NOT NULL AUTO_INCREMENT ,
	`filename`  varchar(255) NULL COMMENT 'name' ,
	`filepath`  tinytext NULL COMMENT 'path' ,
	PRIMARY KEY (`id`)
	)ENGINE=InnoDB DEFAULT CHARACTER SET=utf8;
	
 * @Date: 2018年12月6日 下午12:46:17
 */

public class JDBC {
	// 属性
    private static Boolean isFirstQuery = true;
    
    
	public static ResultSet query() throws SQLException, ClassNotFoundException {
		System.out.println("-----Query all-----");
		// 第一次访问数据库设置时区
		if (isFirstQuery) {
			setTimeZone();
			isFirstQuery = false;
		}
		
		if (isFirstQuery) {
			setTimeZone();
		}
		
		String sql = "SELECT * FROM smart_upload";
		ResultSet resultSet = JDBCExecute.getStatement().executeQuery(sql);
		
		System.out.println("-----Query over-----");
		
		return resultSet;
	}
	
	private static void setTimeZone() throws ClassNotFoundException, SQLException {
		System.out.println("--- First access MySQL, set time zone ---");
		Statement statement = JDBCExecute.getStatement();
		String sql1 = "set global time_zone = '+8:00'";
		String sql2 = "set time_zone = '+8:00'";
		String sql3 = "flush privileges";
		String sql4 = "show variables like 'time_zone'";
		statement.execute(sql1);
		statement.execute(sql2);
		statement.execute(sql3);
		ResultSet resultSet = statement.executeQuery(sql4);
		while (resultSet.next()) {
			String value = resultSet.getString("Value");
			System.out.println("--- Set time zone success: " + value.equals("+08:00")+ " ---");
		}
	}

	public static void add(Map<String, String> fileNameMap) throws SQLException, ClassNotFoundException {
		System.out.println("-----Add to MySQL-----");
		
		String sql = "INSERT INTO smart_upload(filename, filepath) VALUES(?, ?)";
		for (String key : fileNameMap.keySet()) {
			// 创建预处理
			PreparedStatement preparedStatement = JDBCExecute.getConnection(sql);
			preparedStatement.setString(1, key);
			preparedStatement.setString(2, fileNameMap.get(key));
			preparedStatement.executeUpdate();
		}
		System.out.println("----- Add to MySQL over -----");
	}
	
	
	public static void deleteAll() throws SQLException, ClassNotFoundException {
//		System.out.println("-----DELETE ALL-----");
		
		// 清空表
		String sql = "TRUNCATE smart_upload";
		// 重置自增长
		String sql2 = "ALTER TABLE smart_upload AUTO_INCREMENT=1";
		
		// 创建预处理
		Statement statement = JDBCExecute.getStatement();
		statement.execute(sql);
		statement.execute(sql2);
	}

	public static ResultSet query(String fileName) throws ClassNotFoundException {
		
		String sql = "SELECT * FROM smart_upload WHERE filename=?";
		// 创建预处理
		PreparedStatement preparedStatement;
		try {
			preparedStatement = JDBCExecute.getConnection(sql);
			preparedStatement.setString(1, fileName);
			return preparedStatement.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
	}
	
	public static ResultSet queryRegexp(String regexp) throws ClassNotFoundException {
		
		Statement statement;
		try {
			statement = JDBCExecute.getStatement();
			String sql = "SELECT * FROM smart_upload WHERE filename REGEXP " + regexp;
//			System.out.println(sql);
			ResultSet resultSet = statement.executeQuery(sql);

			return resultSet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;		
	}

}

