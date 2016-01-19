package cn.workcenter.common;

import cn.workcenter.common.constant.WebConstant;

public abstract class WorkcenterApplication implements WebConstant {
	
	protected static ThreadLocal<String> sidThreadLocal = new ThreadLocal<String>(){
		protected String initialValue() {
			return "";
		}
	};
}
