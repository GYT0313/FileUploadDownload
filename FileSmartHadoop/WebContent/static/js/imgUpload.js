//1、文件上传HTML5 通过drag把文件拖拽到浏览器的默认事件覆盖
//文件离开
document.ondragleave = function(e) {
	e.preventDefault();
	console.info("文件离开执行了我！！");
};
// 鼠标松开文件
document.ondrop = function(e) {
	e.preventDefault();
	console.info("松开以后执行了我！");
};
// 鼠标移动文件
document.ondragover = function(e) {
	e.preventDefault();
	console.info("文件移动以后执行了我！");
};

function tm_upload() {
	var img1 = "";
	var uploadArea = document.getElementById("upload-area");
	// 2、通过HTML5拖拽事件，ondrop，然后通过拖动区域监听浏览器的drop事件达到文件上传的目的
	uploadArea.addEventListener("drop", function(e) {
		e.preventDefault();
		// 3、从事件event中获取拖拽到浏览器的文件信息
		var fileList = e.dataTransfer.files;
		for (var i = 0; i < fileList.length; i++) {
			// 此处判断只能上传图片
			if (fileList[i].type.indexOf("image") != 0) {
				var upSuccessFlag = document.getElementById("upSuccess");
				if (upSuccessFlag != null) {
					document.getElementById("preview").removeChild(upSuccessFlag);
				}
				var showUploadImg = document.getElementById("showUploadImg");
				if (showUploadImg != null) {
					document.getElementById("preview").removeChild(showUploadImg);
				}
				
				var str = "<h3 id='upSuccess' style='position: absolute; top:130px; left: 80px; color:#FF4500;'>上传失败</h3>";
				document.getElementById("preview").innerHTML += str;
				alert("仅支持图片格式!");
				return;
			}
			// 图片预览 这一步需要判断是什么浏览器 大家自己判断吧
			/** ********************************** */
			img1 = window.URL.createObjectURL(fileList[i]);
			/** ********************************** */
			var upSuccessFlag = document.getElementById("upSuccess");
			if (upSuccessFlag != null) {
				document.getElementById("preview").removeChild(upSuccessFlag);
			}
			var showUploadImg = document.getElementById("showUploadImg");
			if (showUploadImg != null) {
				document.getElementById("preview").removeChild(showUploadImg);
			}

			/*str可以在下方显示上传的图片*/
			var str = "<h4 id='upSuccess' style='position: absolute; top:130px; left: 30px; color:#C0FF3E;'>上传成功</h4>";
			var imgStr = "<div id='showUploadImg' style='position: absolute; top:115px; left: 140px;'><img style='width:120px; height:80px;' src='" + 
					img1 + "'/><p>" + fileList[i].name + "</p></div>";
			document.getElementById("preview").innerHTML += str;
			document.getElementById("preview").innerHTML += imgStr;

			var fileName = fileList[i].name;
			console.info(fileName);
			var fileSize = fileList[i].size;
			console.info(fileSize);
			// 4、通过XMLHttpRequest上传文件到服务器 就是一个ajax请求
			var xhr = new XMLHttpRequest();
			// 5、这里需要先写好一个imgUpload.jsp的上传文件，当然，你写成servlet或者是action都可以
			xhr.open("post", "imgUpload.jsp", true);
			xhr.setRequestHeader("X-Request-Width", "XMLHttpRequest");
			// 6、通过HTML5 FormData动态设置表单元素
			var formData = new FormData();// 动态给表单赋值，传递二进制文件
			// 其实就相当于<input type="file" name="file"/>
			formData.append("doc", fileList[i]);
			xhr.send(formData);
		}
	});
}
// 直接调用
tm_upload();