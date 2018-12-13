package com.gyt.smartUpload;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * @FileName: DAO.java
 * @Package: com.gyt.smartUpload
 * @Author: Gu Yongtao
 * @Description: [类的描述]
 *
 * @Date: 2018年12月10日 下午9:46:10
 */

public class DAO {
	
	public static void add(Map<String, String> fileNameMap) throws SQLException {
		try {
			JDBC.add(fileNameMap);
		} catch (SQLException | ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	public static ResultSet query() {
		try {
			return JDBC.query();
		} catch (SQLException | ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public static ResultSet query(String fileName) {
		try {
			return JDBC.query(fileName);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	public static ResultSet queryRegexp(String regexp) {
		try {
			return JDBC.queryRegexp(regexp);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}

