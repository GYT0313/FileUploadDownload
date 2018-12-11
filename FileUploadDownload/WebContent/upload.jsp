<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>File Upload</title>
<style type="text/css">
.file {
    position: relative;
    display: inline-block;
    background: #D0EEFF;
    border: 1px solid #99D3F5;
    border-radius: 4px;
    padding: 4px 12px;
    overflow: hidden;
    color: #1E88C7;
    text-decoration: none;
    text-indent: 0;
    line-height: 20px;
}
.file input {
    position: absolute;
    font-size: 100px;
    right: 0;
    top: 0;
    opacity: 0;
}
.file:hover {
    background: #AADFFD;
    border-color: #78C3F3;
    color: #004974;
    text-decoration: none;
}
</style>
</head>
<body style="text-align: center;">
	<h2>文件上传</h2>
	<hr>
	<form action="${ pageContext.request.contextPath }/UploadServlet" method="post" enctype="multipart/form-data">
		<a href="javascript:;" class="file">选择文件
			<input type="file" name="2" id="up">
		</a>
		<br>
		<!-- <input type="text" name="1" hidden="yes">  -->
		<br>
		<input type="submit" value="上传"> <br>
		<a href="http://localhost:8080/project/ListFileServlet">下载</a>
	</form>
</body>
</html>