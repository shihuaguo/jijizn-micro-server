package com.jijizn.web.verticle;

import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.jijizn.web.handler.ApiRouter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.ErrorHandler;

public class MainVerticle extends AbstractVerticle {
	private static final Logger log = LoggerFactory.getLogger(MainVerticle.class);
	
	private HttpServer server;

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		HttpServerOptions options = createOptions();
		server = vertx.createHttpServer(options);
		server.requestHandler(createRouter()::accept);
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
		JsonObject httpServerOptions = config().getJsonObject("HttpServerOptions");
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
	
	private Router createRouter() {
		Router router = Router.router(vertx);
		router.route().failureHandler(ErrorHandler.create(true));
		router.get("/hello").handler(rc -> {
			rc.response().end("hello,world!");
		});
		ApplicationContext context = (ApplicationContext) config().getValue("context");
		vertx.executeBlocking(f -> {
			Map<String, ApiRouter> map = context.getBeansOfType(ApiRouter.class);
			map.entrySet().stream().forEach(ar -> {
				ar.getValue().route(vertx, router);
			});
		}, res -> {
			if(res.succeeded()) {
				log.info("init spring context success");
			}else {
				log.info("init spring context failed", res.cause());
			}
		});
		return router;
	}

}
