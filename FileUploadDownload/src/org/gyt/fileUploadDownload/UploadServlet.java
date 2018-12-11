package org.gyt.fileUploadDownload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class Upload
 */
@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String storagePath;
	private static String tmpPath;
	private static String message;
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadServlet() {
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
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		// 文件保存路径和临时目录
		storagePath = this.getServletContext().getRealPath("/WEB-INF/file");
		tmpPath = this.getServletContext().getRealPath("/WEB-INF/tmp");
		// 不存在则创建临时目录
		File tmpFile = new File(tmpPath);
		if (!tmpFile.exists()) {
			tmpFile.mkdir();
		}
		
		// 1.创建工厂
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 工厂缓冲区
		factory.setSizeThreshold(1024*100);
		// 临时目录
		factory.setRepository(tmpFile);
		
		// 2.创建解析器
		ServletFileUpload upload = new ServletFileUpload(factory);
		// 中文乱码
		upload.setHeaderEncoding("UTF-8");
		// 监听上传进度
		upload.setProgressListener(new ProgressListener() {
			
			@Override
			public void update(long pBytesRead, long pContentLength, int arg2) {
				// TODO Auto-generated method stub
				System.out.println("文件大小：" + pContentLength + ", 已上传: " + pBytesRead);
			}
		});
		
		// 3. 判断上传的数据是否是上传表单的数据
		if (!ServletFileUpload.isMultipartContent(request)) {
			// 传统方式获取
			System.out.println("表单数据，返回!");
			return ;
		}
		
		// 上传限制
		upload.setFileSizeMax(1024*1024*5); // 单个文件最大5M
		upload.setSizeMax(1024*1024*10); // 总文件最大10M
		

		// 4. 解析器解析上传数据，每一个FileItem对应一个Form表单的输入项
		List<FileItem> items = null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 5. 处理
		System.out.println("上传的文件数: " + items.size());
		if (items != null) {
			for (FileItem item : items) {
				if (item.isFormField()) { // 处理普通域
					System.out.println("-----普通域处理-----");
					processFormField(item);
				} else { // 处理文件域
					System.out.println("-----文件域处理-----");
					processUploadField(item);
				}
			}
		}
//		out.println("上传成功！");
		
		// 转发
//		request.setAttribute("message", message);
		response.setHeader("refresh", "0.9;url=http://localhost:8080/FileUploadDownload/ListFileServlet");
		request.getRequestDispatcher("/ListFileServlet").forward(request, response);
	}
	
	
	/**
	 * 
	 * @Title：processUploadField
	 * @Description: 处理文件域
	 * @Param: @param item
	 * @Param: @throws IOException
	 * @Return: void
	 */
	@SuppressWarnings("null")
	private void processUploadField(FileItem item) throws IOException {
		// TODO Auto-generated method stub
		// 获得文件名
		String filename = item.getName();
		// 空文件
		if (filename == null && filename.trim().equals("")) {
			return ;
		}
		
		// 处理不同浏览器提交的文件，如带路径，不带路径
		// 保留文件名
		filename = filename.substring(filename.lastIndexOf("\\")+1);
		System.out.println("文件名：" + filename);
		// 得到后缀名
		String fileSuffixName = filename.substring(filename.lastIndexOf(".")+1);
		System.out.println("后缀名：" + fileSuffixName);
		
		// 获取上传文件的输入流
		InputStream inputStream = item.getInputStream();
		// 获得文件保存唯一名称
		String storageFileName = makeFileName(filename);
		// 文件保存路径
		String realStoragePath = makePath(storageFileName);
		
		// 创建文件输出流
		FileOutputStream outputStream = new FileOutputStream(realStoragePath + "\\" + storageFileName);
		
		// 创建缓冲区
		byte buffer[] = new byte[1024];
		
		// 输入流是否读取完标识
		int len = 0;
		// 循环将输入流读入到缓冲区
		while ((len=inputStream.read(buffer)) > 0) { // 还会有数据
			// 将读取到buffer中的内容输出到指定文件
			outputStream.write(buffer, 0, len);
		}
		// 关闭流
		inputStream.close();
		outputStream.close();
		
		message = "上传成功！";
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
	
	/**
	 * 
	 * @throws UnsupportedEncodingException 
	 * @Title：processFormField
	 * @Description: 处理普通域
	 * @Param: @param item
	 * @Return: void
	 */
	private void processFormField(FileItem item) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		System.out.println("普通域处理：");
		String name = item.getFieldName();
		// 解决普通输入项的数据乱码
		String value = item.getString("UTF-8");
		System.out.println(name + "=" + value);
	}

}
