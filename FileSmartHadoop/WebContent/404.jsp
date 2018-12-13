<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>404-对不起！您访问的页面不存在</title>
<style type="text/css">
.head404 {
	width: 580px;
	height: 234px;
	margin: 50px auto 0 auto;
	background: url(https://www.daixiaorui.com/Public/images/head404.png)
		no-repeat;
}

.txtbg404 {
	width: 499px;
	height: 199px;
	margin: 10px auto 0 auto;
	background: url(https://www.daixiaorui.com/Public/images/txtbg404.png)
		no-repeat;
}

.txtbg404 .txtbox {
	width: 390px;
	position: relative;
	top: 30px;
	left: 60px;
	color: #eee;
	font-size: 15px;
}

.txtbg404 .txtbox p {
	margin: 5px 0;
	line-height: 20px;
}

.txtbg404 .txtbox .paddingbox {
	padding-top: 15px;
}

.txtbg404 .txtbox p a {
	color: #eee;
	text-decoration: none;
}

.txtbg404 .txtbox p a:hover {
	color: #FC9D1D;
	text-decoration: underline;
}
.message {
	font-size: 25px;
	color: yellow;
}
</style>
</head>
<!-- 原文地址：https://blog.csdn.net/qq_39208536/article/details/80635677 -->
<body bgcolor="#494949">
	<div class="head404"></div>
	<div class="txtbg404">
		<div class="txtbox">
			<c:choose>
				<c:when test="${ requestScope.message == 'keyWord' }">
					<p class="message" style="line-height: 35px;" >输入不合法，输入包含非汉字、非字母、非数字</p>
				</c:when>
				<c:otherwise>
					<p class="meaage" style="line-height: 35px;" >对不起，您请求的资源不存在、或已被删除、或暂时不可用</p>
				</c:otherwise>
			</c:choose>
			<p class="paddingbox">请点击以下链接继续浏览网页</p>
			<p>
				》<a style="cursor: pointer" onclick="history.back()">返回上一页面</a>
			</p>
			<p>
				》<a href="/FileSmartHadoop/home-smart.jsp">返回网站首页</a>
			</p>
		</div>
	</div>
</body>
</html>