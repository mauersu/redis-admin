package cn.workcenter.common.constant;

import java.util.concurrent.CopyOnWriteArrayList;

public interface SecurityConstant extends Constant {
	
	String ANONYMOUS_ROLE = "ANONYMOUS_ROLE";
	CopyOnWriteArrayList<String> auth_escapepage = new CopyOnWriteArrayList<String>();
	CopyOnWriteArrayList<String> static_page = new CopyOnWriteArrayList<String>();
}
