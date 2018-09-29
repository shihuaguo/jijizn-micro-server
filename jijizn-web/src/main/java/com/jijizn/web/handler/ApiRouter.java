package com.jijizn.web.handler;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public interface ApiRouter {

	void route(Vertx vertx, Router parent);
}
