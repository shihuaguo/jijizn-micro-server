package com.jijizn.micro.common;

import java.util.Map;

import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

class VertxOptionsConverter {
	private static final Logger log = LoggerFactory.getLogger(Launcher.class);
	
	/**
	 * config VertxOptions from JsonObject, if option isn't exist in VertxOptions
	 * this allow config VertxOptions from config file and don't ovveride option
	 * from system properties
	 * 
	 * @param json            vertx options from config file
	 * @param obj
	 * @param vertxOptionsMap vertx options from system properties
	 */
	static void fromJson(JsonObject json, VertxOptions obj, Map<Object, Object> vertxOptionsMap) {
		if (json.getValue("addressResolverOptions") instanceof JsonObject) {
			obj.setAddressResolverOptions(
					new io.vertx.core.dns.AddressResolverOptions((JsonObject) json.getValue("addressResolverOptions")));
		}
		if (json.getValue("blockedThreadCheckInterval") instanceof Number) {
			obj.setBlockedThreadCheckInterval(((Number) json.getValue("blockedThreadCheckInterval")).longValue());
		}
		if (json.getValue("clusterHost") instanceof String) {
			if(vertxOptionsMap.get("clusterHost") != null) {
				log.warn(String.join("", "vertx option clusterHost is configured by system properties=", 
						vertxOptionsMap.get("clusterHost").toString(), ", ignore config file's value=", json.getValue("clusterHost").toString()));
			}else {
				obj.setClusterHost((String) json.getValue("clusterHost"));
			}
		}
		if (json.getValue("clusterPingInterval") instanceof Number) {
			obj.setClusterPingInterval(((Number) json.getValue("clusterPingInterval")).longValue());
		}
		if (json.getValue("clusterPingReplyInterval") instanceof Number) {
			obj.setClusterPingReplyInterval(((Number) json.getValue("clusterPingReplyInterval")).longValue());
		}
		if (json.getValue("clusterPort") instanceof Number) {
			if(vertxOptionsMap.get("clusterPort") != null) {
				log.warn(String.join("", "vertx option clusterPort is configured by system properties=", 
						vertxOptionsMap.get("clusterPort").toString(), ", ignore config file's value=", json.getValue("clusterPort").toString()));
			}else {
				obj.setClusterPort(((Number) json.getValue("clusterPort")).intValue());
			}
		}
		if (json.getValue("clusterPublicHost") instanceof String) {
			obj.setClusterPublicHost((String) json.getValue("clusterPublicHost"));
		}
		if (json.getValue("clusterPublicPort") instanceof Number) {
			obj.setClusterPublicPort(((Number) json.getValue("clusterPublicPort")).intValue());
		}
		if (json.getValue("clustered") instanceof Boolean) {
			if(vertxOptionsMap.get("clustered") != null) {
				log.warn(String.join("", "vertx option clustered is configured by system properties=", 
						vertxOptionsMap.get("clustered").toString(), ", ignore config file's value=", json.getValue("clustered").toString()));
			}else {
				obj.setClustered((Boolean) json.getValue("clustered"));
			}
		}
		if (json.getValue("eventBusOptions") instanceof JsonObject) {
			obj.setEventBusOptions(
					new io.vertx.core.eventbus.EventBusOptions((JsonObject) json.getValue("eventBusOptions")));
		}
		if (json.getValue("eventLoopPoolSize") instanceof Number) {
			obj.setEventLoopPoolSize(((Number) json.getValue("eventLoopPoolSize")).intValue());
		}
		if (json.getValue("fileResolverCachingEnabled") instanceof Boolean) {
			obj.setFileResolverCachingEnabled((Boolean) json.getValue("fileResolverCachingEnabled"));
		}
		if (json.getValue("haEnabled") instanceof Boolean) {
			obj.setHAEnabled((Boolean) json.getValue("haEnabled"));
		}
		if (json.getValue("haGroup") instanceof String) {
			obj.setHAGroup((String) json.getValue("haGroup"));
		}
		if (json.getValue("internalBlockingPoolSize") instanceof Number) {
			obj.setInternalBlockingPoolSize(((Number) json.getValue("internalBlockingPoolSize")).intValue());
		}
		if (json.getValue("maxEventLoopExecuteTime") instanceof Number) {
			obj.setMaxEventLoopExecuteTime(((Number) json.getValue("maxEventLoopExecuteTime")).longValue());
		}
		if (json.getValue("maxWorkerExecuteTime") instanceof Number) {
			obj.setMaxWorkerExecuteTime(((Number) json.getValue("maxWorkerExecuteTime")).longValue());
		}
		if (json.getValue("metricsOptions") instanceof JsonObject) {
			obj.setMetricsOptions(
					new io.vertx.core.metrics.MetricsOptions((JsonObject) json.getValue("metricsOptions")));
		}
		if (json.getValue("preferNativeTransport") instanceof Boolean) {
			obj.setPreferNativeTransport((Boolean) json.getValue("preferNativeTransport"));
		}
		if (json.getValue("quorumSize") instanceof Number) {
			obj.setQuorumSize(((Number) json.getValue("quorumSize")).intValue());
		}
		if (json.getValue("warningExceptionTime") instanceof Number) {
			obj.setWarningExceptionTime(((Number) json.getValue("warningExceptionTime")).longValue());
		}
		if (json.getValue("workerPoolSize") instanceof Number) {
			obj.setWorkerPoolSize(((Number) json.getValue("workerPoolSize")).intValue());
		}
	}

	static void toJson(VertxOptions obj, JsonObject json) {
		if (obj.getAddressResolverOptions() != null) {
			json.put("addressResolverOptions", obj.getAddressResolverOptions().toJson());
		}
		json.put("blockedThreadCheckInterval", obj.getBlockedThreadCheckInterval());
		if (obj.getClusterHost() != null) {
			json.put("clusterHost", obj.getClusterHost());
		}
		json.put("clusterPingInterval", obj.getClusterPingInterval());
		json.put("clusterPingReplyInterval", obj.getClusterPingReplyInterval());
		json.put("clusterPort", obj.getClusterPort());
		if (obj.getClusterPublicHost() != null) {
			json.put("clusterPublicHost", obj.getClusterPublicHost());
		}
		json.put("clusterPublicPort", obj.getClusterPublicPort());
		json.put("clustered", obj.isClustered());
		if (obj.getEventBusOptions() != null) {
			json.put("eventBusOptions", obj.getEventBusOptions().toJson());
		}
		json.put("eventLoopPoolSize", obj.getEventLoopPoolSize());
		json.put("fileResolverCachingEnabled", obj.isFileResolverCachingEnabled());
		json.put("haEnabled", obj.isHAEnabled());
		if (obj.getHAGroup() != null) {
			json.put("haGroup", obj.getHAGroup());
		}
		json.put("internalBlockingPoolSize", obj.getInternalBlockingPoolSize());
		json.put("maxEventLoopExecuteTime", obj.getMaxEventLoopExecuteTime());
		json.put("maxWorkerExecuteTime", obj.getMaxWorkerExecuteTime());
		if (obj.getMetricsOptions() != null) {
			json.put("metricsOptions", obj.getMetricsOptions().toJson());
		}
		json.put("preferNativeTransport", obj.getPreferNativeTransport());
		json.put("quorumSize", obj.getQuorumSize());
		json.put("warningExceptionTime", obj.getWarningExceptionTime());
		json.put("workerPoolSize", obj.getWorkerPoolSize());
	}
}