package org.gyt.fileUploadDownload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DownloadServlet
 */
@WebServlet("/DownloadServlet")
public class DownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static String filePath = "404";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		// 下载文件
		System.out.println("下载文件：");
		// 文件名
		String fileName = request.getParameter("filename"); //文件名_UUID
		fileName = new String(fileName.getBytes("iso8859-1"), "UTF-8");
		// 文件保存在/WEB-INF/file中
		String fileStorageRootPath = this.getServletContext().getRealPath("/WEB-INF/file");
		// 通过文件名找出文件所在目录, 返回绝对路径
		findFileStoragePathByFileName(fileName, new File(fileStorageRootPath));
		System.out.println("Download File: " + filePath);
		// 得到该文件
		if (filePath.equals("404")) {
			request.setAttribute("message", "错误");
			request.getRequestDispatcher("/message.jsp").forward(request, response);
		}
		File file = new File(filePath);
		// 若不存在
		if (file != null && !file.exists()) {
			request.setAttribute("message", "资源已被删除！");
			request.getRequestDispatcher("/message.jsp").forward(request, response);
			return ;
		}
		// 处理文件名
		String realName = fileName.substring(fileName.indexOf("_")+1);
		// 设置相应头，控制浏览器下载文件
		response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(realName, "UTF-8"));
		// 读取要下载的文件，保存到输入流
		
		FileInputStream inputStream = new FileInputStream(filePath);
		// 创建输出流
		OutputStream outputStream = response.getOutputStream();
		// 创建缓冲区
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len=inputStream.read(buffer)) > 0) {
			// 输出缓冲区内容到浏览器，实现下载
			outputStream.write(buffer, 0, len);			
		}
		// 关闭流
		inputStream.close();
		outputStream.close();
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
