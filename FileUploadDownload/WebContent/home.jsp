<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="description" content="最好用的文件及时分享系统">
<meta name="Keywords" content="文件及时分享,网盘,云盘">
<title>我上传的文件 - 文件及时分享</title>
<!-- Theme -->
<link rel="stylesheet" href="./static/css/bootstrap.min.css" />
<link rel="stylesheet" href="./static/css/animate.min.css" />
<link rel="stylesheet" href="./static/css/vegas.min.css" />
<link rel="stylesheet" href="./static/css/style.css" />
<link rel="stylesheet" href="./static/css/style2.css" />
<link rel="shortcut icon" href="./static/img/tz.ico" type="image/x-icon" />
<!-- Fonts -->
<link rel="stylesheet"
	href="https://at.alicdn.com/t/font_xtmtjsdjlmc84cxr.css">
<link rel="stylesheet"
	href="https://at.alicdn.com/t/font_k8psy17rqdoyldi.css">
<style type="text/css">
.uploadfile {
	position: absolute;
	left: 200px;
	top: 0px;
}
.list {
	position: absolute;
	width: 500px;
	left: 300px;
	text-align: center;
}
.list-table {
	font-size: 22px;
	position: absolute;
	left: 150px;
	width: 300px;
}
.list-table  tr {
	position: absolute;
	top: 10px;
}
.list-table  tr td{
	text-align: left;
}
.td2 a{
color: rgb(238, 119, 17);
}
.td2 a:active {
	color: rgb(242, 242, 242);
}
.td2 a:hover {
	color: rgb(242, 242, 242);
}
</style>
</head>
<body style="background: url('./static/img/bannerq6.jpg');">
	<div class="list">
		<h2>文件下载</h2>
		<br><br>
		<%-- 遍历Map集合 --%>

		<c:forEach var="me" items="${ fileNameMap }">
			<table class="list-table">
				<tr>
					<td class="td1">
						<c:url value="/DownloadServlet" var="downurl">
							<c:param name="filename" value="${ me.key }"></c:param>
						</c:url>
					</td>
					<td class="td2">
						${ me.value }&nbsp;&nbsp;&nbsp;&nbsp;<a href="${ downurl }">下载</a>
					</td>
				</tr>
				<br>
			</table>
			<br>
		</c:forEach>
		<!-- <h4><a href="http://localhost:8080/project/upload.jsp">上传</a></h4> -->
	</div>
	
	<div class="popup-window">
		<div id="shade">
			<div class="shade-container">
				<div class="stick stick-1"></div>
				<div class="stick stick-2"></div>
				<div class="stick stick-3"></div>
			</div>
			<div class="col-xs-12">
				<p class="text-info">testing</p>
			</div>
			<span id="ok">确定</span>
		</div>
	</div>
	<!-- 遮罩层 -->
	<div class="page-loader"></div>
	<!--循环进度条读取网页-->
	<span id="h-animate" class="wow zoomInLeft" data-wow-delay="2s">&#10084;</span>
	<!--开场 心 效果-->
	<!--===== Begin Header =====-->

	<!--===== END Advertisement =====-->


	<!--===== END Header =====-->


	<!--===== Begin Update File =====-->
	<section id="content">
		<div class="container">

			<div class="col-sj-12">

				<!-- update-file-container -->
				<div class="up-container wow bounceIn" data-wow-delay="0.9s">

					<!-- upload files -->
					<form id="form" action="${ pageContext.request.contextPath }/UploadServlet" method="post"
						enctype="multipart/form-data">
						<input id="files" name="file" multiple="multiple"
							style="display: none;" class="file-addr" type="file" /> 
							<input type='hidden' name='csrfmiddlewaretoken'
							value='KkH5QKGddlcrnylQOY3KwwNre5uxIzHFUIKKMkEJVshVWuQZeNHakFNsoyaEgWfU' />
						<!-- {% csrf_token %} -->
						<div class="uploadfile"><input id="bt" type="button" value="Upload File" /></div>
					</form>
					<!-- END Upload file -->
				</div>

			</div>

		</div>
		<!-- /.container -->
	</section>

	<section>
		<div class="container">
			<div class="row">

				<div class="col-md-12 col-xs-12"></div>

			</div>
			<!-- /.row -->
		</div>
	</section>

	<section>
		<div class="container">
			<div class="row">

				<div class="col-xs-12">
					<h1 class="wow fadeInUp" data-wow-delay="0.4s">
						<span>About</span> Attention...
					</h1>
					<p class="about-text wow slideInLeft" style="font-size: 22px">1.
						Upload the file size is not more than 50M</p>
					<p class="about-text wow slideInLeft" style="font-size: 22px">2.
						Please properly keep network backup link</p>
				</div>

			</div>
			<!-- /.row -->
		</div>
	</section>



	<!-- ThemePlug -->
	<script src="./static/js/jquery.min.js"></script>
	<script src="./static/js/vegas.min.js"></script>
	<script src="./static/js/wow.min.js"></script>
	<script src="./static/js/dist/clipboard.min.js"></script>

	<!-- Page -->
	<script src="./static/js/index.js"></script>


</body>
</html>