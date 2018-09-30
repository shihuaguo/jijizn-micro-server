package com.jijizn.micro.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Context {
	
	public static final Context INSTANCE = new Context();
	
	public ApplicationContext CONTEXT;
	
	public void init(String... basePackages) {
		CONTEXT = new AnnotationConfigApplicationContext(basePackages);
	}
	
	public ApplicationContext getContext() {
		return CONTEXT;
	}
}
