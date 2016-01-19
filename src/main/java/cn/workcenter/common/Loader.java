package cn.workcenter.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Loader {
	
	public static Class getClass(String clazz) throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		try {
			return getTCL().loadClass(clazz);
		} catch (ClassNotFoundException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return Class.forName(clazz);
	}
	
	protected static ClassLoader getTCL() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Method method = null;
		method = Thread.class.getMethod("getContextClassLoader", null);
		return (ClassLoader)method.invoke(Thread.currentThread(), null);
	}
	
}
