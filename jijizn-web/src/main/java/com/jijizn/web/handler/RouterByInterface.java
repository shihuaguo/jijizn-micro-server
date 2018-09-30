package com.jijizn.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

@Component
public class RouterByInterface implements ApiRouter {
	
	private static final Logger log = LoggerFactory.getLogger(RouterByInterface.class);
	
	@Override
	public void route(Vertx vertx, Router parent, String contextPath) {
		Router router = Router.router(vertx);
		router.route("/test1").handler(this::test1);
		log.info("mount {}{} to RouterByInterface test1 method", contextPath, "/test1");
		
		parent.mountSubRouter(contextPath, router);
	}
	
	public void test1(RoutingContext rc) {
		rc.response().end("from /sub1/test1");
	}

}
