package com.gyt.smartUpload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		if (request.getParameter("kw") != null && !request.getParameter("kw").equals("")) {
			System.out.println("search by key");
			isSearch = true;
			searchKeyWord = request.getParameter("kw");
			// 该判断已经在前端JS中进行了判断， 避免漏网之鱼，这里也进行 了判断
			// 对kw的字符判断，即只能包含中文，字母
			if (!checkKeyWordIsIllegal(searchKeyWord)) {
				System.out.println("关键字包含非汉字、非字母、非数字：" + searchKeyWord);
				request.setAttribute("message", "keyWord");
				request.getRequestDispatcher("/404.jsp").forward(request, response);
				return ;
			}
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
		// 使用Hadoop的倒排索引，根据内容查询，增加这个集合主要想要实现在前台将内容匹配和名称匹配分开展示，想了一下最多花时间处理前台，没有实际意义
//		Map<String, String> fileNameMapHadoop = new HashMap<>();
		
		// 递归遍历uploadFilePath下所有文件
		System.out.println("Exist File:");
		// 执行遍历函数
		if (isSearch) {	// 按照 kw 关键字遍历
			System.out.println("------ list by kw -----");
			listFile(fileNameMap, searchKeyWord.toLowerCase());	// 转化为小写s
			listByContent(fileNameMap, searchKeyWord.toLowerCase(), uploadFilePath);
			
		} else {	// 遍历所有
			System.out.println("------ list all -----");
			listFile(fileNameMap);
		}
//		isFirstQuery = false;
		System.out.println("This listing have files counts: " + fileNameMap.size());
		

		// 根据请求参数page，判断请求是否合理，并作出相应正确的处理
		request.setAttribute("fileNameMap", fileNameMap);
		// 计算以10条为每页，最大为多少页，并设置request参数
		Integer maxPage = (int) (Math.ceil(fileNameMap.size()/10)+1);
		request.setAttribute("maxPage", maxPage);
		System.out.println("\nMax show pages: " + maxPage);
		
		// page合理性判断
		if (request.getParameter("page") == null) {
			request.setAttribute("page", 1);
			request.getRequestDispatcher("/home-smart.jsp").forward(request, response);
		} else {
			// 获得参数page
			Integer page = Integer.parseInt(request.getParameter("page"));
			if (page <= 0) {	// 已经是第一页，无法在上一页
				request.setAttribute("page", 1);
				// 将Map发送到jsp显示，重定向到home页面
				request.getRequestDispatcher("/home-smart.jsp").forward(request, response);
			} else if (page > maxPage) {	// 已经是最后页，无法在上一页
				System.out.println("/home-smart.jsp?page=" + maxPage);
				request.setAttribute("page", maxPage);
				// 将Map发送到jsp显示
				request.getRequestDispatcher("/home-smart.jsp?page=" + 
						maxPage).forward(request, response);
			} else {	// 正常跳转
				request.setAttribute("page", page);
				// 将Map发送到jsp显示
				request.getRequestDispatcher("/home-smart.jsp?page=" + page).forward(request, response);
			}
		}
		System.out.println("----- Over ListFileServlet -----");
	}
	
	
	private Boolean checkKeyWordIsIllegal(String searchKeyWord) {
		// 使用正则校验
		Pattern pattern = Pattern.compile("([^\\u4e00-\\u9fa5a-zA-Z0-9]+)");
		Matcher matcher = pattern.matcher(searchKeyWord);
		
		if (matcher.find()) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @Title：listFile
	 * @Description: 遍历所有文件
	 * @Param: @param file
	 * @Param: @param fileNameMap
	 * @Return: void
	 */
	private void listFile(Map<String, String> fileNameMap) {
		// 访问数据库查询所有记录
		try {
			ResultSet resultSet = DAO.query();
			if (resultSet == null) {
				System.out.println("null");
				
			} else {
				while (resultSet.next()) {
					// 取得本地文件名
					String fileName = resultSet.getString("filename");
					String realFileName = fileName.substring(fileName.lastIndexOf("_") + 1);
					
					// 格式化输出到控制台
					formatPrint2Console(realFileName);
					fileNameMap.put(fileName, realFileName);
				}
				System.out.println(" ");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @Title：listFile
	 * @Description: 按照 kw 关键字使用SQL正则表达式遍历
	 * @Param: @param file
	 * @Param: @param fileNameMap
	 * @Param: @param searchKeyWord
	 * @Return: void
	 * @Date: 2018年12月9日
	 */
	private void listFile(Map<String, String> fileNameMap, String searchKeyWord) {
		// TODO Auto-generated method stub
		// 使用SQL正则查询
		String regexp = "'.*" + searchKeyWord + ".*'";
		ResultSet resultSet = DAO.queryRegexp(regexp);
		
		// 可能存在多个匹配可能
		try {
			while (resultSet.next()) {
				// 本地文件名
				String fileName = resultSet.getString("fileName");
				String realFileName = fileName.substring(fileName.lastIndexOf("_") + 1);
				// 格式化输出到控制台
				formatPrint2Console(realFileName);
				fileNameMap.put(fileName, realFileName);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @throws SQLException 
	 * @Title：listByContent
	 * @Description: 读取索引问价，判断kw是否与索引文件的key值相似
	 * @Param: 
	 * @Return: void
	 * @Date: 2018年12月11日
	 */
	public static void listByContent(Map<String, String> fileNameMap, String kw, String uploadFilePath){
		// 读取倒排索引文件
		System.out.println("\n-----Query by content -----");
		try {
			// 索引文件地址
			// 获取文件目录
			String invertedIndexFilePath = uploadFilePath + "\\fileSmartUpload\\part-r-00000";
			System.out.println("Inverted File: " + invertedIndexFilePath);
//			FileReader fileReader = new FileReader("D:\\soft2\\javaweb\\work_space\\.metadata\\.plugins\\"
//					+ "org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\FileSmartHadoop\\WEB-INF\\file\\"
//					+ "fileSmartUpload\\part-r-00000");
			File invertedIndexFile = new File(invertedIndexFilePath);
			if (!invertedIndexFile.exists()) {
				System.out.println("--- Inverted index file doesn't exist! ---");
				return ;
			}
			FileReader fileReader = new FileReader(invertedIndexFilePath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String strLine;
			// 正则判断
			Pattern pattern = Pattern.compile(".*" + kw + ".*");
			Matcher matcher;
			
			// IO流按行读取倒排索引文件
			while ((strLine = bufferedReader.readLine()) != null) {
//				System.out.println(strLine);
				// 索引文件： key  file1.txt:1;file2.txt:2;
				// 分割
				StringTokenizer sTokenizer = new StringTokenizer(strLine);
				// 匹配key值
				matcher = pattern.matcher(sTokenizer.nextToken().toLowerCase());
				if (matcher.find()) {	// 匹配成功
					// 将value：file1.txt:1; file2.txt:2;进行处理
					getMatchingFile(fileNameMap, sTokenizer.nextToken().toLowerCase());
				}
			}
			
			bufferedReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @throws SQLException 
	 * @Title：getMatchingFile
	 * @Description: 按照  ";"  分割所有文件
	 * @Param: @param allFileName
	 * @Return: void
	 * @Date: 2018年12月11日
	 */
	private static void getMatchingFile(Map<String, String> fileNameMap, String allFileName){
		// 按照 ; 进行分割
		StringTokenizer sTokenizer = new StringTokenizer(allFileName, ";");
		while (sTokenizer.hasMoreElements()) {
			// 格式: file1.txt:1;
			String fileNameAndCounts = sTokenizer.nextToken();
			if (fileNameAndCounts == null || fileNameAndCounts.equals("")) {	// 避免最后一个 ; 的右边为空
				continue;
			}
			// 分割为文件名、频数
			int indexOft = fileNameAndCounts.lastIndexOf(":");
			String fileName = fileNameAndCounts.substring(0, indexOft);
			// 根据文件名获取文件路径
			getFileAddress(fileNameMap, fileName);
		}
	}
	
	/**
	 * 
	 * @throws SQLException 
	 * @Title：getFileAddress
	 * @Description: 访问数据库获得文件的路径
	 * @Param: @param fileName
	 * @Return: void
	 * @Date: 2018年12月11日
	 */
	private static void getFileAddress(Map<String, String> fileNameMap, String fileName) {
//		System.out.println("access mysql");
		// 根据文件名访问数据库获得地址
		ResultSet UUIDfileNames = DAO.query(fileName);
		// 真实文件名称
		String realFileName = fileName.substring(fileName.indexOf("_") + 1);
		
		// 遍历符合条件的ResultSet
		try {
			while (UUIDfileNames.next()) {

				String uuidFilename = UUIDfileNames.getString("filename");

				if (!fileNameMap.containsKey(uuidFilename)) {
//				System.out.println("uuidname"+uuidFilename + ", fiename: "+fileName);
					// 格式化输出到控制台
					formatPrint2Console(realFileName);
					fileNameMap.put(uuidFilename, realFileName);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void formatPrint2Console(String realFileName) {
		// 格式化输出到控制台
		if (num++ < 5) {
			System.out.print(realFileName + ";   ");
		} else {
			System.out.println(" ");
			num = 0;
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
