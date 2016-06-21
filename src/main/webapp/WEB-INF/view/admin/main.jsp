<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<title>redis-admin</title>
<link href="${pageContext.request.contextPath}/bower_components/bootstrap/dist/css/bootstrap.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/bower_components/metisMenu/dist/metisMenu.min.css" rel="stylesheet"><!-- MetisMenu CSS -->
<link href="${pageContext.request.contextPath}/bower_components/font-awesome/css/font-awesome.min.css" rel="stylesheet"><!-- Custom Fonts -->
<link href="${pageContext.request.contextPath}/sb-admin/css/timeline.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/sb-admin/css/sb-admin-2.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/ztree/zTreeStyle.css" rel="stylesheet">

<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery.form.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery.table.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery.pagination.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery.ztree.all-3.5.min.js"></script>
<script src="${pageContext.request.contextPath}/sb-admin/js/sb-admin-2.js"></script>
</head>

<body>
	
	<jsp:include page="common/modelDialog.jsp"></jsp:include>
	
	<div id="wrapper">
	
		<!-- Navigation -->
		<nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">

			<jsp:include page="header.jsp"></jsp:include>
			<jsp:include page="menu.jsp"></jsp:include>

		</nav>

		<div id="page-wrapper">
			<br>
			<jsp:include page="${viewPage }"></jsp:include>
		</div>
		<!-- /#page-wrapper -->

		
	</div>
	<!-- /#wrapper -->
	
	<script>
		$(document).ready(function() {
			$(".refresh_a").on("click", function() {
				var value1 = $(this).attr("value1");
				var url = "${pageContext.request.contextPath}/redis/refreshMode";
				$.ajax({
					type: "post",
					url: url,
					dataType: "json",
					data: {
						mode: value1,
					},
					success: function(data) {
						modelAlert(data);
					}
				})
			});
			$("#side-menu").find("a").each(function() {

				var value2 = $(this).attr("value2");
				if (value2 != "#") {
					$(this).on("click", function() {
						var url = $(this).attr("value2");
						window.location.href = url;
					})
				}
			});
		})
		
	</script>
</body>
</html>