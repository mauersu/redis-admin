package cn.workcenter.common.exception;

public class ParameterEmptyException extends RuntimeException {
	
	public ParameterEmptyException() {
		super();
	}
	
	public ParameterEmptyException(String msg) {
		super(msg);
	}
}
