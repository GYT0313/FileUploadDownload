package com.gyt.smartUpload;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;


/**
 * @FileName: ClearAll.java
 * @Package: com.gyt.smartUpload
 * @Author: Gu Yongtao
 * @Description: 清理整个项目，包括：
 * 				磁盘文件
 * 				数据库记录
 * 				HDFS文件
 *
 * @Date: 2018年12月12日 下午7:25:51
 */

public class ClearAll {
	
	/**
	 * 
	 * @Title：clearLocalDisk
	 * @Description: 清理本地磁盘
	 * @Param: 
	 * @Return: void
	 * @Date: 2018年12月12日
	 */
	public static void clearLocalDisk() {
		// 本地磁盘路径，在不同机器运行需要更改路径
		String storagePath = "D:\\soft2\\javaweb\\work_space\\.metadata\\.plugins\\"
				+ "org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\FileSmartHadoop\\WEB-INF\\file";
		File file = new File(storagePath);
		if (file.exists()) {
			recursiveDeletion(file);
			file.delete();
		}
	}
	
	/**
	 * 
	 * @Title：recursiveDeletion
	 * @Description: 递归删除
	 * @Param: 
	 * @Return: void
	 * @Date: 2018年12月12日
	 */
	public static void recursiveDeletion(File file) {
		// 递归遍历，返回路径
		// 分为目录和文件
		if (!file.isFile()) { // 目录则列举所有目录和文件
			File[] files = file.listFiles();
			// 遍历
			for (File f : files) {
				// 递归遍历
				recursiveDeletion(f);
			}
			// 删除该目录
			file.delete();
		} else { // 文件
			/*
			 * 如果是文件则删除
			 */
			file.delete();
		}
	}
	
	/**
	 * 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @Title：chearMySQL
	 * @Description: 清理MySQL
	 * @Param: 
	 * @Return: void
	 * @Date: 2018年12月12日
	 */
	public static void clearMySQL() throws ClassNotFoundException, SQLException {
		JDBC.deleteAll();
	}
	
	/**
	 * 
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @Title：clearHDFS
	 * @Description: 清理HDFS
	 * @Param: 
	 * @Return: void
	 * @Date: 2018年12月12日
	 */
	public static void clearHDFS() throws IOException, URISyntaxException {
		Configuration configuration = new Configuration();
		FileSystem fileSystem = FileSystem.get(new URI("hdfs://localhost:9000"), configuration);
		
		Path dataPath = new Path("/data/fileSmartUpload");
		Path outputPath = new Path("/output/fileSmartUpload");
		
		if (fileSystem.exists(dataPath)) {
			fileSystem.delete(dataPath, true);
			// 成功删除后创建
			fileSystem.mkdirs(dataPath);
		}
		if (fileSystem.exists(outputPath)) {
			fileSystem.delete(outputPath, true);
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException, URISyntaxException {
		// 删除本地磁盘文件
		clearLocalDisk();
		System.out.println("Success clear disk!");
		
		// 删除MySQL
		clearMySQL();
		System.out.println("Success clear MySQL!");
		
		// 删除HDFS文件
		clearHDFS();
		System.out.println("Success clear HDFS!");
	}

}

