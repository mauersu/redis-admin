package cn.workcenter.common;

import cn.workcenter.common.constant.WebConstant;

public class WorkcenterResult implements WebConstant{
	
	private String resultCode; //200 成功    500服务器错误   600执行业务异常
	
	private String resultMsg;  //说明
	
	private Object data;       //附加信息
	
	private WorkcenterCodeEnum codeEnum;//错误码及具体错误说明

	private WorkcenterResult() {
		
	}
	
	private WorkcenterResult(String resultCode, String resultMsg, Object data, WorkcenterCodeEnum codeEnum) {
		this.resultCode = resultCode;
		this.resultMsg = resultMsg;
		this.data = data;
		this.codeEnum = codeEnum;
	}
	
	public String getResultCode() {
		return resultCode;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public Object getData() {
		return data;
	}

	public WorkcenterCodeEnum getCodeEnum() {
		return codeEnum;
	}

	public void setCodeEnum(WorkcenterCodeEnum codeEnum) {
		this.codeEnum = codeEnum;
	}
	
	public static Builder custom() {
		return new Builder();
	}
	
	public static class Builder {
		
		private String resultCode; //200 成功  SUCCESS200    500服务器错误  ERROR500  600执行业务异常  BUSINESSEXCEPTION600
		
		private String resultMsg;  //说明
		
		private Object data;       //附加信息
		
		private WorkcenterCodeEnum codeEnum;//错误码及具体错误说明
		
		Builder() {
			resultCode = SUCCESS200;
			resultMsg = "请求成功";
			data = "无";
			codeEnum = WorkcenterCodeEnum.valueOf(OK_SUCCESS);
		}
		
		public Builder setOK(WorkcenterCodeEnum codeEnum, Object data) {
			resultCode = SUCCESS200;
			resultMsg = "请求成功";
			this.data=data;
			this.codeEnum = codeEnum;
			return this;
		}
		
		public Builder setOK(WorkcenterCodeEnum codeEnum) {
			resultCode = SUCCESS200;
			resultMsg = "请求成功";
			this.data=null;
			this.codeEnum = codeEnum;
			return this;
		}
		
		public Builder setNO(WorkcenterCodeEnum codeEnum) {
			resultCode = EXCEPTION600;
			resultMsg = "执行业务异常";
			this.data=null;
			this.codeEnum = codeEnum;
			return this;
		}
		
		public WorkcenterResult build() {
			return new WorkcenterResult(resultCode, resultMsg, data, codeEnum);
		}
		
	}
}
