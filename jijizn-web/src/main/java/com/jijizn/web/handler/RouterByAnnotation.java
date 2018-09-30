package com.jijizn.web.handler;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.springframework.stereotype.Component;

import io.vertx.ext.web.RoutingContext;

@Component
@Path("/sub2")
public class RouterByAnnotation {
	
//	private static final Logger log = LoggerFactory.getLogger(RouterByAnnotation.class);
	

	@Path("/get1")
	@GET
	public void get1(RoutingContext rc) {
		rc.response().end("from /sub2/get1");
	}
	
	@Path("/post1")
	@POST
	public void post1(RoutingContext rc) {
		rc.response().end("from /sub2/post1");
	}

}
