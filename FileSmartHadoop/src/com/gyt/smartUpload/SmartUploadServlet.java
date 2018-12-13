package com.gyt.smartUpload;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jspsmart.upload.SmartUpload;

/**
 * Servlet implementation class Upload
 */
@WebServlet("/SmartUploadServlet")
public class SmartUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String storagePath;
	private static String tmpPath;


	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SmartUploadServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}
	


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		doGet(request, response);
		// 上传文件处理
		System.out.println("----- Starting SmartUploadServlet -----");
		// 编码
//		request.setCharacterEncoding("UTF-8");
//		response.setContentType("text/html;charset=UTF-8");
		

		// 存储文件名，路径
		Map<String, String> fileNameMap = new HashMap<>();

		// 文件保存路径和临时目录
		storagePath = this.getServletContext().getRealPath("/WEB-INF/file");
		tmpPath = this.getServletContext().getRealPath("/WEB-INF/tmp");
		// 不存在则文件目录
		File storageFile = new File(storagePath);
		if (!storageFile.exists()) {
			storageFile.mkdir();
		}
		// 不存在则创建临时目录
		File tmpFile = new File(tmpPath);
		if (!tmpFile.exists()) {
			tmpFile.mkdir();
		}
		
		// 实例化上传组件
		SmartUpload smartUpload = new SmartUpload();
		// 初始化SmartUpload
		smartUpload.initialize(getServletConfig(), request, response);
		// 设置上传文件对象10M
		smartUpload.setMaxFileSize(1024*1024*10);
		// 设置所有文件大小100M
		smartUpload.setTotalMaxFileSize(1024*1024*100);
		// 设置允许上传文件类型
//		smartUpload.setAllowedFilesList("txt,jpg,gif,png");
		
		try {
			// 设置禁止上传文件类型
			smartUpload.setDeniedFilesList("rar,jsp,js");
			// 上传文件
			smartUpload.upload();
			// 上传总大小
			System.out.println("\nTotal Size: " + smartUpload.getSize());
			// 遍历上传的文件，并保存
			for (int i=0; i<smartUpload.getFiles().getCount(); i++) {
				// 获取文件
				com.jspsmart.upload.File file = smartUpload.getFiles().getFile(i);
				
				// 提示信息
				System.out.println("---------- Processing " + file.getFileName() + " ----------");
				
				// 使用UUDI拼接新文件名
				String storageFileName = makeFileName(file.getFileName());
				
				// 使用Hash算法获得文件保存路径
				String realStoragePath = makePath(storageFileName);
				
				// 保存文件
				String realFile = realStoragePath + "\\" + storageFileName;
				file.saveAs(realFile);
				
				System.out.println("Size: " + file.getSize() + " bytes");
				fileNameMap.put(storageFileName, realFile);
				// 保存完文件后上传到HDSF
//				System.out.println("realFile: " + realFile);
//				GlobalParameter.fileNameListHadoop.add(realFile);
				
				// 提示信息
				System.out.println("---------- Upload Success ----------\n");
			}
			// 是否上传成功
			request.setAttribute("isSuccess", "yes");
		} catch (Exception e) {
			// TODO: handle exception
			request.setAttribute("isSuccess", "no");
			e.printStackTrace();
		} finally {
		}
//		fileNameListHadoop.clear();
//		System.out.println("fileCounts: " + GlobalParameter.fileNameListHadoop.get(0));
		try {
			System.out.println("to mysql");
			DAO.add(fileNameMap);
		} catch (SQLException | IllegalArgumentException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		response.setHeader("refresh", "0;url=/FileSmartHadoop/ListFileServlet");
		System.out.println("----- Over SmartUploadServlet -----");
	}
	
	
	/**
	 * 
	 * @Title：makePath
	 * @Description: 生成文件路径，使用hash算法打散存储
	 * @Param: @param storageFileName
	 * @Param: @return
	 * @Return: String
	 */
	private String makePath(String storageFileName) {
		// TODO Auto-generated method stub
		int hashcode = storageFileName.hashCode();
		int dir1 = hashcode&0xf; // 0-15
		int dir2 = (hashcode&0xf0) >> 4; // 0-15
		// 构造保存目录
		String dir = storagePath + "\\" + dir1 + "\\" + dir2; //如: \\file\2\3
		// File 即可代表文件也可是目录
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		System.out.println("MakePath：" + dir);
		return dir;
	}
	
	/**
	 * 
	 * @Title：makeFileName
	 * @Description: 生成文件名
	 * @Param: @param filename
	 * @Param: @return
	 * @Return: String
	 */
	private String makeFileName(String filename) {
		// TODO Auto-generated method stub
		String ff = UUID.randomUUID().toString() + "_" + filename;
		System.out.println("makeFileName：" + ff);
		return ff;
	}

}

