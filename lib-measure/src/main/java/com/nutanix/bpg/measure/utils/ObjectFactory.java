package com.nutanix.bpg.measure.utils;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;

public class ObjectFactory {
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(String className, Class<T> t) {
		try {
			Class<?> cls = Class.forName(className);
			Constructor<?> c = cls.getConstructor(new Class<?>[0]);
			if (c == null) {
				throw new IllegalArgumentException(cls + " does not have "
						+ " no-arg constrctuor");
			}
			Object obj = c.newInstance(new Object[0]);
			return (T)cls.cast(obj);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Class<? extends T> newClass(String className, Class<T> t) {
		try {
			Class<?> cls = Class.forName(className);
			if (!t.isAssignableFrom(cls)) {
				throw new IllegalArgumentException(t + " is not "
						+ " a superclass or interface of "
						+ className);
			}
			return (Class<? extends T>)cls;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		
	}
	
    public static void printClassloader(String header, ClassLoader cl) {
    	PrintStream out = System.out;
    	out.println(header);
    	if (URLClassLoader.class.isInstance(cl)) {
    		URLClassLoader ucl = URLClassLoader.class.cast(cl);
    		for (URL url : ucl.getURLs()) {
    			out.println(url);
    		}
    	} else {
			out.println(cl);
    	}
    }


}
