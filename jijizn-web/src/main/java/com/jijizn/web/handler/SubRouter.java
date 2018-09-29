package com.jijizn.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

@Component
public class SubRouter implements ApiRouter {
	
	private static final Logger log = LoggerFactory.getLogger(SubRouter.class);
	
	private static final String mountPoint = "/sub1";

	@Override
	public void route(Vertx vertx, Router parent) {
		Router router = Router.router(vertx);
		router.route("/test1").handler(this::test1);
		log.info("mount {}{} to SubRouter's test1 method", mountPoint, "/test1");
		
		parent.mountSubRouter(mountPoint, router);
	}
	
	public void test1(RoutingContext rc) {
		rc.response().end("from /sub1/test1");
	}

}
