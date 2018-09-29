package com.jijizn.micro.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Context {
	
	public static final Context INSTANCE = new Context();
	
	public static final Map<String, ApplicationContext> CONTEXT_MAP = new ConcurrentHashMap<>();
	
	public ApplicationContext getAnnotationConfigApplicationContext(String... basePackages) {
		String key = StringUtils.join(basePackages, ",");
		ApplicationContext context = CONTEXT_MAP.get(key);
		if(context == null) {
			context = new AnnotationConfigApplicationContext(basePackages);
			CONTEXT_MAP.put(key, context);
		}
		return context;
	}
}
