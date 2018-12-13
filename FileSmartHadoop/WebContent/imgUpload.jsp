<%@page import="com.gyt.smartUpload.DAO"%>
<%@page import="java.net.FileNameMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.UUID,
org.apache.commons.fileupload.FileItem,
java.util.Iterator,
java.util.List,
org.apache.commons.fileupload.servlet.ServletFileUpload,
org.apache.commons.fileupload.disk.DiskFileItemFactory,
org.apache.commons.fileupload.FileItemFactory,
java.io.File,
java.util.Date" %>

<%

	/*
	 *使用commons组件进行图片上传
	 */

	System.out.println("----- Starting imgUpload.jsp -----");

	//获取文件路径
	String storagePath = request.getRealPath("/WEB-INF/file");
	File file = new File(storagePath);
	if (!file.exists())
		file.mkdirs();
	
	// 构建工厂
	FileItemFactory factory = new DiskFileItemFactory();
	ServletFileUpload upload = new ServletFileUpload(factory);
	// 存储文件名，路径
	Map<String, String> fileNameMap = new HashMap<>();
	
	//从请求对象中获取文件信息
	List<FileItem> items = upload.parseRequest(request);
	if (items != null) {
		for (int i = 0; i < items.size(); i++) {
			// 构造器
			Iterator iterator = items.iterator();
			
			// 遍历
			while (iterator.hasNext()) {
				FileItem item = (FileItem) iterator.next();
				if (item.isFormField()) {	// 文本域
					continue;
				} else {	// 文件域
					// 文件名
					String fileName = item.getName();
					int index = fileName.lastIndexOf("\\");
					if (index >= 0) {
						fileName = fileName.substring(index+1);
					}
					// 文件大小
					Long fileSize = item.getSize();
					System.out.println("Size: " + fileSize);
					
					// 使用UUID拼接唯一文件名
					fileName = UUID.randomUUID().toString() + "_" + fileName;
					System.out.println("Filename: " + fileName);
					
					// 使用Hash打散存储
					int hashcode = fileName.hashCode();
					int dir1 = hashcode & 0xf; // 0-15
					int dir2 = (hashcode & 0xf0) >> 4; // 0-15
					// 构造保存目录
					String dir = storagePath + "\\" + dir1 + "\\" + dir2; //如: \\file\2\3
					
					System.out.println("dir: " + dir);
					// File 即可代表文件也可是目录，不存在则创建
					File file2 = new File(dir);
					if (!file2.exists()) {
						file2.mkdirs();
					}
					
					// 写入本地磁盘
					File saveFile = new File(dir, fileName);
					item.write(saveFile);
					System.out.println("savefile");
					
					fileNameMap.put(fileName, dir + "\\" + fileName);
				}
			}
		}
	}
	
	DAO.add(fileNameMap);
	
	System.out.println("----- Over imgUpload.jsp -----");
%>