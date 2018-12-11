<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport" content="width=device-width,initial-scale=1" />
<title>文件上传 - 文件及时分享</title>
<!-- Theme -->
<link rel="stylesheet" href="./static/css/bootstrap.min.css" />
<link rel="stylesheet" href="./static/css/animate.min.css" />
<link rel="stylesheet" href="./static/css/vegas.min.css" />
<link rel="stylesheet" href="./static/css/style.css" />
<link rel="stylesheet" href="./static/css/style2.css" />
<link rel="stylesheet" href="./static/css/home-smart.css" />
<link rel="shortcut icon" href="./static/img/tz.ico" type="image/x-icon" />
<!-- Fonts -->
<link rel="stylesheet"
	href="https://at.alicdn.com/t/font_xtmtjsdjlmc84cxr.css">
<link rel="stylesheet"
	href="https://at.alicdn.com/t/font_k8psy17rqdoyldi.css">

</head>
<body
	style="background: url('./static/img/bannerq11.jpg'); position: absolute; width: 100%;">
	<!-- search file -->
	<div class="search-div" style="width: 300px; float: left; z-index: 9999; top: 10px;">
		<p>&nbsp;</p>
		<p>&nbsp;</p>
		&nbsp;&nbsp;&nbsp;
		<form id="search" action="${ pageContext.request.contextPath }/ListFileServlet" method="get">
			<div class="input-box">
				<input id="files-info" name="kw" type="text"
					onkeydown="if(event.keyCode==13){return false;}" autocomplete="off"
					placeholder="Search File" />
					<span	class="iconfont search-text-icon">&#xe714;</span>
			</div>
		</form>
	</div>

	<div class="list">
		<c:choose>
			<%-- 是否第一次访问  --%>
			<c:when test="${ null == fileNameMap }">
				<h4>
					<%
						request.getRequestDispatcher("/ListFileServlet").forward(request, response);
					%>
				</h4>
			</c:when>
			<%-- 不是第一次访问判断访问参数page的合理性 --%>
			<c:otherwise>
				<%-- page<0，重定向到page=1 --%>
				<c:if test="${ param.page < 0 }">
					<%
						request.getRequestDispatcher("/FileSmartUpload/home-smart.jsp?page=1");
					%>
				</c:if>
				<%-- page>maxPage，重定向到最后一页 --%>
				<c:if test="${ param.page > requestScope.maxPage}">
					<%
						request.getRequestDispatcher(
											"/FileSmartUpload/home-smart.jsp?page=" + request.getAttribute("maxpage"));
					%>
				</c:if>
			</c:otherwise>

		</c:choose>

		<br> <br>

		<div class="imgUpload">
			<div class="demo">
				<div class="drag-area" id="upload-area" style="position: absolute; top: 0px;">将图片拖拽到这里</div>
				<!-- 图片预览 -->
				<div id="preview" class="preview"></div>
			</div>
			<script src="./static/js/imgUpload.js"></script>
		</div>


		<!-- 遍历Map集合 -->
		<div class="file-list">
			<div class="file-foreach">
				<c:if test="${ (requestScope.page-1)*10  < 0 }">
					<% request.setAttribute("page", 2); %>
				</c:if>
				<c:choose>
					<c:when test="${ fn:length(fileNameMap) == 0 }">
						<h2>
							<c:out value="无\"  ${ param.kw } \"及相关文件"></c:out>
						</h2>
					</c:when>
					<c:otherwise>
						<c:forEach var="me" begin="${ (requestScope.page-1)*10 }"
							varStatus="count"
							end="${ requestScope.page*9+(requestScope.page-1)*1 }"
							items="${ fileNameMap }">
							<table class="list-table">
								<tr>
									<c:url value="/SmartDownloadServlet" var="downurl">
										<c:param name="filename" value="${ me.key }"></c:param>
									</c:url>
									<td class="td1"><c:out value="${ count.index+1 }"></c:out></td>
									<td class="td2">${ me.value }</td>
									<td class="td3"><a href="${ downurl }">下载</a></td>
								</tr>
							</table>
							<br>
							<br>
						</c:forEach>
					</c:otherwise>
				</c:choose>
			</div>

			<div class="pages">
				<font> 共&nbsp;<fmt:formatNumber type="number"
						value="${ ((fn:length(fileNameMap))-(fn:length(fileNameMap))%10)/10 +1}"
						maxFractionDigits="0" />&nbsp;页
				</font> &nbsp;&nbsp; <font>第&nbsp;${ requestScope.page }&nbsp;页</font>
				&nbsp; <a
					href="http://localhost:8080/FileSmartUpload/home-smart.jsp?page=1&kw="
					id="firstPage">首页</a> &nbsp; <a
					href="http://localhost:8080/FileSmartUpload/home-smart.jsp?page=${ requestScope.page-1 }&kw=${ param.kw }"
					id="lastPage">上一页</a> &nbsp; <a
					href="http://localhost:8080/FileSmartUpload/home-smart.jsp?page=${ requestScope.page+1 }&kw=${ param.kw }"
					id="nextPage">下一页</a> &nbsp; <a
					href="http://localhost:8080/FileSmartUpload/home-smart.jsp?page=${ requestScope.maxPage }&kw=${ param.kw }"
					id="endPage">尾页</a>
			</div>
		</div>

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
	<span id="h-animate" class="wow zoomInLeft" data-wow-delay="5s">&#10084;</span>
	<!--开场 心 效果-->
	<!--===== Begin Header =====-->

	<!--===== END Advertisement =====-->


	<!--===== Begin Update File =====-->
	<section id="content">
		<div class="container">

			<div class="col-sj-12">

				<!-- update-file-container -->
				<div class="up-container wow bounceIn" data-wow-delay="0.9s">


					<!-- END search file -->

					<!-- upload files -->
					<form id="form"
						action="${ pageContext.request.contextPath }/SmartUploadServlet"
						method="post" enctype="multipart/form-data">
						<input id="files" name="file" multiple="multiple"
							style="display: none;" class="file-addr" type="file" /> <input
							type='hidden' name='csrfmiddlewaretoken'
							value='KkH5QKGddlcrnylQOY3KwwNre5uxIzHFUIKKMkEJVshVWuQZeNHakFNsoyaEgWfU' />
						<!-- {% csrf_token %} -->
						<div class="uploadfile">
							<input id="bt" type="button" value="Upload File"
								style="font-size: 22px;" />
						</div>
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
						Upload the file size is not more than 10M</p>
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

	<!-- Page -->
	<script src="./static/js/index.js"></script>
</body>
</html>