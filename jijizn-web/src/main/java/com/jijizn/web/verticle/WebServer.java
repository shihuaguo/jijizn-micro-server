package com.jijizn.web.verticle;

import java.util.Optional;

import com.jijizn.web.handler.RouterHelper;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class WebServer extends AbstractVerticle {
	private static final Logger log = LoggerFactory.getLogger(WebServer.class);
	
	private HttpServer server;
	
	private String contextPath;

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		HttpServerOptions options = createOptions();
		server = vertx.createHttpServer(options);
		server.requestHandler(createRouter(options)::accept);
		server.listen(http -> {
			if (http.succeeded()) {
				startFuture.complete();
				log.info("HTTP server started on {}:{}", options.getHost(), options.getPort());
			} else {
				startFuture.fail(http.cause());
			}
		});
	}
	
	private HttpServerOptions createOptions() {
		JsonObject httpServerOptions = Optional.ofNullable(config().getJsonObject("HttpServerOptions")).orElse(new JsonObject());
		contextPath = "/" + Optional.ofNullable(httpServerOptions.getString("contextPath")).orElse("");
		//contextPath = "/".equals(contextPath) ? "" : contextPath;
		return new HttpServerOptions(httpServerOptions);
	}
	
	@Override
	public void stop(Future<Void> future) {
		if (server == null) {
			future.complete();
			return;
		}
		server.close(future.completer());
	}
	
	private Router createRouter(HttpServerOptions options) {
		Router router = Router.router(vertx);
		router.route().failureHandler(ErrorHandler.create(true));
		
		staticHandler(router);
		
		RouterHelper.mountSubRouter(vertx, router, contextPath);
		
		return router;
	}
	
	private void staticHandler(Router router) {
		StaticHandler staticHandler = StaticHandler.create();
		staticHandler.setCachingEnabled(false);
		String staticPath = contextPath.endsWith("/") ? contextPath + "static/*" : contextPath + "/static/*";
		router.route(staticPath).handler(staticHandler);
	}

}
