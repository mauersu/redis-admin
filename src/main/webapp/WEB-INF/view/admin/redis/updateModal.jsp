<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String basePath = request.getContextPath();
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="modal fade bs-example-modal-lg" id="updateModal" tabindex="-1" role="dialog" aria-labelledby="updateModalLabel">
	<div class="modal-dialog modal-lg" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="back_btn close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title" id="updateModalLabel">view/update redisKV</h4>
			</div>
			<div class="modal-body">
			
				<!--<div class="row">
					<br>
				</div>  空行 -->
				
				<div class="input-group">
				  <span class="input-group-addon" >serverName</span>
				  <input id="updateModal_serverName" name="serverName" class="form-control" placeholder="serverName" readonly>
				</div>
				
				<div class="row">
					<br>
				</div>
				
				<div class="input-group">
				  <span class="input-group-addon" >dbIndex</span>
				  <input id="updateModal_dbIndex" name="dbIndex" class="form-control" placeholder="dbIndex" readonly> 
				</div>
				
				<div class="row">
					<br>
				</div>
				
				<div class="input-group">
				  <span class="input-group-addon" >key</span>
				  <input id="updateModal_key" name="key" class="form-control" placeholder="key" readonly>
				</div>
				
				<div class="row">
					<br>
				</div>
				
				<div class="input-group">
				  <span class="input-group-addon" >dataType</span>
				  <input id="updateModal_dataType" name="dataType" class="form-control" placeholder="dataType" readonly>
				</div>
				
				<div class="row">
					<br>
				</div>
				
				<form id="STRINGForm" style="display: none;" value1="STRINGForm">
					<table id="STRINGFormTable" class="table table-striped table-bordered" >
						<thead>
							<tr>
								<th>value</th>
							</tr>
						</thead>
						<tbody>
							<tr style="display: none;">
								<td title="value"><input name="value" class="form-control" ></td>
							</tr>
						</tbody>
					</table>
				</form>
				
				<form id="LISTForm" style="display: none;">
					<table id="LISTFormTable" class="table table-striped table-bordered" >
						<thead>
							<tr>
								<th>value</th>
							</tr>
						</thead>
						<tbody>
							<tr style="display: none;">
								<td title="value"><input name="value" class="form-control" ></td>
							</tr>
						</tbody>
					</table>
				</form>
				
				<form id="SETForm" style="display: none;">
					<table id="SETFormTable" class="table table-striped table-bordered" >
						<thead>
							<tr>
								<th>value</th>
							</tr>
						</thead>
						<tbody>
							<tr style="display: none;">
								<td title="value"><input name="value" class="form-control" ></td>
							</tr>
						</tbody>
					</table>
				</form>
				
				<form id="ZSETForm" style="display: none;">
					<table id="ZSETFormTable" class="table table-striped table-bordered" >
						<thead>
							<tr>
								<th>score</th>
								<th>member</th>
							</tr>
						</thead>
						<tbody>
							<tr style="display: none;">
								<td title="score"><input name="score" class="form-control" ></td>
								<td title="member"><input name="member" class="form-control" ></td>
							</tr>
						</tbody>
					</table>
				</form>
				
				<form id="HASHForm" style="display: none;">
					<table id="HASHFormTable" class="table table-striped table-bordered" >
						<thead>
							<tr>
								<th>field</th>
								<th>value</th>
							</tr>
						</thead>
						<tbody>
							<tr style="display: none;">
								<td title="field"><input name="field" class="form-control"></td>
								<td title="value"><input name="value" class="form-control"></td>
							</tr>
						</tbody>
					</table>
				</form>
				
			</div>
			
			<div class="modal-footer">
				<button type="button" class="close_btn btn btn-default" data-dismiss="modal">close</button>
				<button type="button" class="update_btn btn btn-primary">update</button>
			</div>
		</div>
	</div>
</div>