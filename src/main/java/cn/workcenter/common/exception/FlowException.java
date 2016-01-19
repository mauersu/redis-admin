package cn.workcenter.common.exception;

public class FlowException extends RuntimeException {
	
	String errormsg;
	String errorcode;
	
	public FlowException() {
		super();
	}
	
	public FlowException(String msg) {
		super(msg);
	}
	
	public FlowException(String msg, String code) {
		errormsg = msg;
		errorcode = code;
	}
}
