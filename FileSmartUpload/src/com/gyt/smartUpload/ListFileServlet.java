package com.gyt.smartUpload;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.print.attribute.standard.MediaSize.Other;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jasper.tagplugins.jstl.core.Out;

import com.alibaba.fastjson.JSON;
import com.sun.org.apache.xpath.internal.operations.Bool;


/**
 * Servlet implementation class ListFileServlet
 */
@WebServlet("/ListFileServlet")
public class ListFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static String uploadFilePath;
    private static int num = 0;
	
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
//		PrintWriter out = response.getWriter();
		// 搜索文件
		Boolean isSearch = false;
		String searchKeyWord = "";
		if (request.getParameter("kw") != null) {
			isSearch = true;
			searchKeyWord = request.getParameter("kw");
		}
		
		
		// 列出所有文件
		System.out.println("----- Starting ListFileServlet -----");
		// 获取文件目录
		uploadFilePath = this.getServletContext().getRealPath("/WEB-INF/file");
		// 不存在文件目录则创建
		File uploadFile = new File(uploadFilePath);
		if (!uploadFile.exists()) {
			uploadFile.mkdir();
		}
		
		// 存储文件名
		Map<String, String> fileNameMap = new HashMap<>();
		// 递归遍历uploadFilePath下所有文件
		System.out.println("Exist File:");
		// 执行遍历函数
		if (isSearch) {	// 按照 kw 关键字遍历
			System.out.println("------ list by kw -----");
			listFile(new File(uploadFilePath), fileNameMap, searchKeyWord.toLowerCase());	// 转化为小写s
		} else {	// 遍历所有
			System.out.println("------ list all -----");
			listFile(new File(uploadFilePath), fileNameMap);
		}

		// 根据请求参数page，判断请求是否合理，并作出相应正确的处理
		request.setAttribute("fileNameMap", fileNameMap);
		// 计算以10条为每页，最大为多少页，并设置request参数
		Integer maxPage = (int) (Math.ceil(fileNameMap.size()/10)+1);
		request.setAttribute("maxPage", maxPage);
		System.out.println("Max pages: " + maxPage);
		
		// page合理性判断
		if (request.getParameter("page") == null) {
			request.setAttribute("page", "1");
			request.getRequestDispatcher("/home-smart.jsp").forward(request, response);
		} else {
			if (Integer.parseInt(request.getParameter("page")) <= 0) {	// 已经是第一页，无法在上一页
				request.setAttribute("page", "1");
				// 将Map发送到jsp显示，重定向到home页面
				request.getRequestDispatcher("/home-smart.jsp").forward(request, response);
			} else if (Integer.parseInt(request.getParameter("page")) > maxPage) {	// 已经是最后页，无法在上一页
				System.out.println("/home-smart.jsp?page=" + maxPage);
				request.setAttribute("page", maxPage);
				// 将Map发送到jsp显示
				request.getRequestDispatcher("/home-smart.jsp?page=" + 
						maxPage).forward(request, response);
			} else {	// 正常跳转
				request.setAttribute("page", request.getParameter("page"));
				// 将Map发送到jsp显示
				request.getRequestDispatcher("/home-smart.jsp?page=" + 
						request.getParameter("page")).forward(request, response);
			}
		
		}
		System.out.println("----- Over ListFileServlet -----");
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
			
			 /* 由于上传保存的文件含有文件名_UUID
			  则取出来时，将其分割，取文件名*/
			
			String realFileName = file.getName().substring(file.getName().indexOf("_")+1);
			// 根据file.getName()唯一作为key，realFileName为真实文件名作为value
			// 调整控制台输出格式
			if (num++ < 5) {
				System.out.print(realFileName + ";    ");
			} else {
				System.out.println(" ");
				num = 0;
			}			
			fileNameMap.put(file.getName(), realFileName);
		}
	}
	
	/**
	 * 
	 * @Title：listFile
	 * @Description: 按照 kw 关键字使用正则表达式遍历
	 * @Param: @param file
	 * @Param: @param fileNameMap
	 * @Param: @param searchKeyWord
	 * @Return: void
	 * @Date: 2018年12月9日
	 */
	private void listFile(File file, Map<String, String> fileNameMap, String searchKeyWord) {
		// TODO Auto-generated method stub
		// 正则表达式
		String regex = ".*" + searchKeyWord + ".*";
//		System.out.println(regex);
		Matcher matcher = null;
		
		// 分为目录和文件
		if (!file.isFile()) { // 目录则列举所有目录和文件
			File[] files = file.listFiles();
			// 遍历
			for (File f : files) {
				// 递归遍历
				listFile(f, fileNameMap, searchKeyWord);
			}
		} else { // 文件
			/*
			 * 由于上传保存的文件含有文件名_UUID
			 * 则取出来时，将其分割，取文件名
			 */
			String realFileName = file.getName().substring(file.getName().indexOf("_")+1).toLowerCase();
			// 使用正则匹配
			matcher = Pattern.compile(regex).matcher(realFileName);
			if (matcher.find()) {	// 匹配成功
				// 根据file.getName()唯一作为key，realFileName为真实文件名作为value
				if (num++ < 5) {
					System.out.print(realFileName + ";   ");
				} else {
					System.out.println(" ");
					num = 0;
				}
				fileNameMap.put(file.getName(), realFileName);
			}
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
