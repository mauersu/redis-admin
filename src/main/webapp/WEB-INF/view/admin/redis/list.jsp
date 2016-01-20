<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String basePath = request.getContextPath();
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!-- 右侧列表  -->

<div class="col-sm-11 col-sm-offset-1 col-md-11 col-md-offset-1 main">
	
	<div class="col-sm-4 col-md-4">
		<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#addServerModal" data-whatever="addServer">addServer</button>
		<button type="button" class="refresh_btn btn btn-primary" >refresh</button>
	</div>
	
	<!-- <div class="row col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2"> -->
	<div class="row col-sm-offset-4 col-md-offset-4">
		<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#addModal" data-whatever="add">add</button>
		<button type="button" class="edit_btn btn btn-primary" >view/update</button>
		<button value1="delete" type="button" class="delete_btn btn btn-primary" >delete</button>
		<button type="button" class="refresh_btn btn btn-primary" >refresh</button>

		<div class="col-sm-6 col-md-6">
			<div class="input-group">
				<div class="input-group-btn">
					<button id="queryLabel_btn" type="button" class="btn btn-default">${queryLabel_ch }</button>
					<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
						<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
					</button>
					<ul class="dropdown-menu">
						<li><a class="query_a" href="javascript:void(0);" value1="middle">like*</a></li>
						<li><a class="query_a" href="javascript:void(0);" value1="head">head^</a></li>
						<li><a class="query_a" href="javascript:void(0);" value1="tail">tail$</a></li>
					</ul>
				</div>

				<input id="query_input" type="text" name="${queryLabel_en }" value="${queryValue }" class="form-control" aria-label="Text input with segmented button dropdown"> <span class="input-group-btn">
					<button id="query_btn" class="btn btn-default" type="button">Query</button>
				</span>
			</div>
		</div>
	</div>

	<div class="col-sm-4 col-md-4">
		<div class="zTreeDemoBackground left">
			<ul id="treeDemo" class="ztree"></ul>
		</div>
	</div>
	
	<div class="table-responsive col-sm-offset-4 col-md-offset-4">
		<table id="listTable" class="table table-striped" >
			<thead>
				<tr>
					<th>#</th>
					<th>index</th>
					<th>key</th>
					<th>type</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${redisKeys }" var="key" varStatus="status">
					<tr class="redisKey">
						<td><input type="checkbox" name="redisKey" value1="${key.key }" value2="${key.type }" ></td>
						<td>${status.index + 1}</td>
						<td>${key.key }</td>
						<td>${key.type }</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>

<jsp:include page="./addModal.jsp"></jsp:include>
<jsp:include page="./addServerModal.jsp"></jsp:include>
<jsp:include page="./updateModal.jsp"></jsp:include>

<script src="<%=basePath%>/js/admin/redis/dataTypeLineHtml.js"></script>
<script src="<%=basePath%>/js/admin/redis/addRedisKV.js"></script>

<script>
	var serverName = '${serverName}';
	var dbIndex = '${dbIndex}';
	var basePath = '${basePath}';

	$(document).ready(function() {
		
		$(".query_a").on("click", function() {
			var query_a_ch = $(this).text();
			var query_a_en = $(this).attr("value1");
			$("#queryLabel_btn").text(query_a_ch);
			$("#query_input").attr("name", query_a_en);
		});
		
		$("#query_btn").on("click", function() {
			var queryKey = $("#query_input").attr("name");
			var queryValue = $("#query_input").val();
			var url = basePath + '/redis/stringList/' + serverName + '/' + dbIndex + '?queryKey='+queryKey + '&queryValue=' + queryValue;
			var encodeUrl = encodeURI(url);
			window.location.href = encodeUrl;
		});
		
		$(".back_btn").on("click", function() {
			window.location.href = basePath + '/redis/stringList/' + serverName + '/' + dbIndex;
		});
		
		
		$(".edit_btn").on("click", function() {
			
			var checkedNum = $("#listTable").find("input:checkbox[name='redisKey']:checked").length;
			if(checkedNum > 1||checkedNum<=0) {
				$("#model_title").text("warning");
				$("#model_content").text("please choose one to edit");
				$('#myModal').modal();
				return ;
			}
			
			var key = $("#listTable").find("input:checkbox[name='redisKey']:checked").attr("value1");
			var dataType = $("#listTable").find("input:checkbox[name='redisKey']:checked").attr("value2");
			
			$("#updateModal_serverName").val(serverName);
			$("#updateModal_dbIndex").val(dbIndex);
			$("#updateModal_key").val(key);
			$("#updateModal_dataType").val(dataType);
			
			$.ajax({
				type: "get",
				url: '<%=basePath%>/redis/KV',
				dataType: "json",
				data: {
					serverName: serverName,
					dbIndex: dbIndex,
					key: key,
					dataType: dataType,
				},
				success : function(data) {
					$('#'+ dataType + 'FormTable').tableData(data.data); 
					$("#updateModal").modal('show');
				}
			});
		});
		
		$(".redisKey").on("dblclick", function() {
			
			var key = $(this).find("input:checkbox[name='redisKey']").attr("value1");
			var dataType = $(this).find("input:checkbox[name='redisKey']").attr("value2");
			
			$("#updateModal_serverName").val(serverName);
			$("#updateModal_dbIndex").val(dbIndex);
			$("#updateModal_key").val(key);
			$("#updateModal_dataType").val(dataType);
			
			$.ajax({
				type: "get",
				url: '<%=basePath%>/redis/KV',
				dataType: "json",
				data: {
					serverName: serverName,
					dbIndex: dbIndex,
					key: key,
					dataType: dataType,
				},
				success : function(data) {
					$('#'+ dataType + 'FormTable').tableData(data.data); 
					$("#updateModal").modal('show');
				}
			});
		});
		
		$(".update_btn").on("click", function() {
			
			var serverName = $("#updateModal_serverName").val();
			var dbIndex = $("#updateModal_dbIndex").val();
			var key = $("#updateModal_key").val();
			var dataType = $("#updateModal_dataType").val();
			
			var updateForm = $("#" + dataType + "Form");
			alert($(updateForm).attr("value1"));
			var updateFormParam = $(updateForm).formSerialize();
			updateFormParam = updateFormParam.substring(updateFormParam.indexOf("&")+1);
			var url = basePath + '/redis/KV';
			$.ajax({
				type: "post",
				url: url,
				dataType: "json",
				data: updateFormParam + "&serverName=" + serverName
				+ "&dbIndex=" + dbIndex+ "&key=" + key
				+ "&dataType=" + dataType,
				success: function(data) {
					modelAlert(data);
				}
			});
		});

		$(".delete_btn").on("click", function() {
			var operator = $(this).attr("value1");
			var url = "<%=basePath%>/redis/delKV";
			
			var deleteKeys = '';
			
			$("#listTable").find("input:checkbox[name='redisKey']:checked").each(function(){
				var key = $(this).attr("value1");
				deleteKeys = deleteKeys + "," +key;
			})
			
			deleteKeys = deleteKeys.substring(1);
			
			if(deleteKeys == '') {
				$("#model_title").text("warning");
				$("#model_content").text("please choose one to delete");
				$('#myModal').modal();
				return;
			}
			
			$.ajax({
				type: "post",
				url: url,
				dataType: "json",
				data: {
					serverName: serverName,
					dbIndex: dbIndex,
					deleteKeys: deleteKeys,
				},
				success: function(data) {
					modelAlert(data);
				}
			});
		});
	});
</script>

<script type="text/javascript" src="<%=basePath%>/js/admin/redis/ztree.js" ></script>
