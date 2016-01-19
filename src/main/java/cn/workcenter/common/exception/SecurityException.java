package cn.workcenter.common.exception;

public class SecurityException extends RuntimeException {
	
	String errormsg;
	String errorcode;
	
	public SecurityException() {
		super();
	}
	
	public SecurityException(String msg) {
		super(msg);
	}
	
	public SecurityException(String msg, String code) {
		errormsg = msg;
		errorcode = code;
	}
	
}
