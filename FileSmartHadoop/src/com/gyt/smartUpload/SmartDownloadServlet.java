package com.gyt.smartUpload;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

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
		System.out.println("----- Starting Download -----");
		// 获取要下载的文件名
		String fileName = request.getParameter("filename");
		System.out.println("Download filename" + fileName);

		try {			
			// 通过文件名访问数据库，获得路径
			ResultSet resultSet = DAO.query(fileName);
			
			// 由于文件名为UUID+文件名，所以结果集值=1
			if (resultSet.next()) {	// 数据库存在该资源数据
				// 数据库存在，但磁盘可能已经删除
				filePath = resultSet.getString("filepath");
				if (new File(filePath).exists()) {	// 文件存在
					SmartUpload smartUpload = new SmartUpload();
					smartUpload.initialize(getServletConfig(), request, response);
					// 设置响应类型
					smartUpload.setContentDisposition(null);
					smartUpload.downloadFile(filePath);
					smartUpload.downloadFile(filePath);
					System.out.println("成功下载！");
				} else {
					System.out.println("本地文件已被删除：" + filePath);
					request.getRequestDispatcher("/404.jsp").forward(request, response);
				}
			} else {	// 查询时数据库存在，下载时数据库已被删除
				System.out.println("Error--数据库记录已被删除!");
				request.getRequestDispatcher("/404.jsp").forward(request, response);
			}
			System.out.println("----- Over Download -----");
		} catch (SmartUploadException | SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
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
