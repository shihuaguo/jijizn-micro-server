package io.vertx.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class MainVerticle extends AbstractVerticle {
	private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		JsonObject jo = vertx.getOrCreateContext().config();
		if(logger.isInfoEnabled()) {
			logger.info("config = {}", jo);
		}
		vertx.createHttpServer().requestHandler(req -> {
			req.response().putHeader("content-type", "text/plain").end("Hello from Vert.x!");
		}).listen(8080, http -> {
			if (http.succeeded()) {
				startFuture.complete();
				System.out.println("HTTP server started on http://localhost:8080");
			} else {
				startFuture.fail(http.cause());
			}
		});
	}

}
