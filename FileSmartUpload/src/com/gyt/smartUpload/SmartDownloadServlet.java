package com.gyt.smartUpload;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jspsmart.upload.SmartUpload;
import com.jspsmart.upload.SmartUploadException;

/**
 * Servlet implementation class SmartDownloadServlet
 */
@WebServlet("/SmartDownloadServlet")
public class SmartDownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static String filePath = "404";
    
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SmartDownloadServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		// 获取要下载的文件名
		String fileName = request.getParameter("filename");

		// 
		SmartUpload smartUpload = new SmartUpload();
		smartUpload.initialize(getServletConfig(), request, response);
		// 设置响应类型
		smartUpload.setContentDisposition(null);
		try {
			// 文件保存在/WEB-INF/file中
			String fileStorageRootPath = this.getServletContext().getRealPath("/WEB-INF/file");
			// 通过文件名找出文件所在目录, 返回绝对路径
			findFileStoragePathByFileName(fileName, new File(fileStorageRootPath));
			System.out.println("Download File: " + filePath);
			// 得到该文件
			if (filePath.equals("404")) {
				System.out.println("已被删除");
				request.setAttribute("message", "错误");
				request.getRequestDispatcher("/message.jsp").forward(request, response);
			}
			smartUpload.downloadFile(filePath);
		} catch (SmartUploadException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @throws IOException 
	 * @Title：findFileStoragePathByFileName
	 * @Description: 根据文件名找到所在路径, 返回绝对路径
	 * @Param: @param fileName
	 * @Param: @param fileStorageRootPath
	 * @Param: @return 返回绝对路径
	 * @Return: String
	 */
	private void findFileStoragePathByFileName(String fileName, File file) throws IOException {
		// TODO Auto-generated method stub
		
		// 递归遍历，返回路径
		// 分为目录和文件
		if (!file.isFile()) { // 目录则列举所有目录和文件
			File[] files = file.listFiles();
			// 遍历
			for (File f : files) {
				// 递归遍历
				findFileStoragePathByFileName(fileName, f);
			}
		} else { // 文件
			/*
			 * 如果是文件则判断是否为下载文件
			 */
			if (file.getName().equals(fileName)) { // 同一文件
//				System.out.println("find：" + file.getPath());
				filePath = file.getPath();
			}
//			String realFileName = file.getName().substring(file.getName().indexOf("_")+1);
//			// 根据file.getName()唯一作为key，realFileName为真实文件名作为value
//			fileNameMap.put(file.getName(), realFileName);
		}
		
	}
	
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
