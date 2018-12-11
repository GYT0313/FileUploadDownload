package org.gyt.fileUploadDownload;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class ListFileServlet
 */
@WebServlet("/ListFileServlet")
public class ListFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static String uploadFilePath;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ListFileServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		// 列出所有文件
		System.out.println("罗列文件：");
		// 获取文件目录
		uploadFilePath = this.getServletContext().getRealPath("/WEB-INF/file");
		// 不存在则创建
		File uploadFile = new File(uploadFilePath);
		if (!uploadFile.exists()) {
			uploadFile.mkdir();
		}
		// 存储文件名
		Map<String, String> fileNameMap = new HashMap<>();
		// 递归遍历uploadFilePath下所有文件
		System.out.println("Exist File:");
		listFile(new File(uploadFilePath), fileNameMap);
		// 将Map发送到jsp显示
		request.setAttribute("fileNameMap", fileNameMap);
		request.getRequestDispatcher("/home.jsp").forward(request, response);
		
	}
	
	/**
	 * 
	 * @Title：listFile
	 * @Description: 遍历所有文件
	 * @Param: @param file
	 * @Param: @param fileNameMap
	 * @Return: void
	 */
	private void listFile(File file, Map<String, String> fileNameMap) {
		// TODO Auto-generated method stub
		// 分为目录和文件
		if (!file.isFile()) { // 目录则列举所有目录和文件
			File[] files = file.listFiles();
			// 遍历
			for (File f : files) {
				// 递归遍历
				listFile(f, fileNameMap);
			}
		} else { // 文件
			/*
			 * 由于上传保存的文件含有文件名_UUID
			 * 则取出来时，将其分割，取文件名
			 */
			String realFileName = file.getName().substring(file.getName().indexOf("_")+1);
			// 根据file.getName()唯一作为key，realFileName为真实文件名作为value
			System.out.println(realFileName);
			fileNameMap.put(file.getName(), realFileName);
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
