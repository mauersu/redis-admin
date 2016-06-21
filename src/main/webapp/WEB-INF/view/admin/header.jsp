<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="navbar-header">
	<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
		<span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
	</button>
	<a class="navbar-brand" href="${pageContext.request.contextPath}/redis">redis-admin</a>
</div>
<!-- /.navbar-header -->

<ul class="nav navbar-top-links navbar-right">
	<span>&nbsp;</span>
	<li class="dropdown">
		<a class="dropdown-toggle" data-toggle="dropdown" href="#"> 
		<!-- refresh-mode -->
		<i class="fa fa-refresh"></i> <i class="fa fa-caret-down"></i>
		</a>
		<ul class="dropdown-menu dropdown-user">
			<li><a class="refresh_a" href="javascript:void(0);" value1="auto"><i class="fa fa-cogs fa-fw"></i> Auto refresh</a></li>
			<li><a class="refresh_a" href="javascript:void(0);" value1="auto"><i class="fa fa-hand-o-right fa-fw"></i> Manually refresh</a></li>
		</ul> <!-- /.dropdown-user -->
	</li>
	<!-- /.dropdown -->
</ul>
<!-- /.navbar-top-links -->