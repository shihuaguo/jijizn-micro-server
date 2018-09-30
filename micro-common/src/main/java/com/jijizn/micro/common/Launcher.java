package com.jijizn.micro.common;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jijizn.micro.context.Context;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.launcher.VertxLifecycleHooks;
import io.vertx.core.impl.launcher.commands.BareCommand;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.SLF4JLogDelegateFactory;

/**
 * 
 * @author shihuaguo
 * @email 147402691@qq.com
 * @date 2018年9月28日
 */
public class Launcher extends io.vertx.core.Launcher implements VertxLifecycleHooks{
	
	private static final Logger log = LoggerFactory.getLogger(Launcher.class);
	
	/**
	 * support for multi config files,path prefix can be classpath:/ or file:/
	 * for example:classpath:/config.json,file:/opt/config/vertx.json
	 */
	private static final String VERTX_CONFIG_LOCATION = "--vertx.config.location=";
	private static String[] vertxConfigLocation;
	
	/**
	 * options use to start vertx instance
	 * will be read from config file specified by VERTX_CONFIG_LOCATION afterConfigParsed
	 * option inlcue clustered,clusterHost and clusterPort
	 * these options doesn't ovveride system properties
	 */
	private static final JsonObject vertxOptions = new JsonObject();

	public static void main(String[] args) {
		setLogger();
		processArgs(args);
		new Launcher().dispatch(args);
	}
	
	private static final void setLogger() {
		if (System.getProperty("vertx.logger-delegate-factory-class-name") == null) {
			System.setProperty("vertx.logger-delegate-factory-class-name",
					SLF4JLogDelegateFactory.class.getCanonicalName());
		}
	}
	
	private static final void processArgs(String[] args) {
		for(String arg : args) {
			if(arg.startsWith(VERTX_CONFIG_LOCATION)) {
				log.info("application param contains vertx config location: {}", arg);
				String location = arg.substring(VERTX_CONFIG_LOCATION.length());
				vertxConfigLocation = location.split(",");
			}
		}
	}

	@Override
	public void afterConfigParsed(JsonObject config) {
		JsonObject jo = getJsonConfiguration();
		log.info("afterConfigParsed, getJsonConfiguration={}", jo);
		//如果有vertx options
		if(jo.getJsonObject("vertxOptions") != null) {
			vertxOptions.mergeIn(jo.getJsonObject("vertxOptions"));
		}
		config.mergeIn(jo);
		super.afterConfigParsed(config);
	}

	@Override
	public void beforeStartingVertx(VertxOptions options) {
		Map<Object, Object> vertxOptionsMap = System.getProperties().entrySet().stream().filter(entry->{
			return entry.getKey().toString().startsWith(BareCommand.VERTX_OPTIONS_PROP_PREFIX);
		}).collect(Collectors.toMap(
				e -> e.getKey().toString().substring(BareCommand.VERTX_OPTIONS_PROP_PREFIX.length()), 
				v -> v.getValue())
		);
		log.info("beforeStartingVertx, vertxOptionsMap from system properties={}", vertxOptionsMap);
		VertxOptionsConverter.fromJson(vertxOptions, options, vertxOptionsMap);
		//options.setClustered(true).setClusterHost("127.0.0.1");
	}

	@Override
	public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
		JsonObject config = deploymentOptions.getConfig();
		if(config.getString("packages_scan") != null) {
			log.info("scanning packages {}", config.getString("packages_scan"));
			Context.INSTANCE.init(config.getString("packages_scan").split(","));
			//deploymentOptions.getConfig().put("context", context);
		}
		super.beforeDeployingVerticle(deploymentOptions);
	}

	@Override
	public void afterStartingVertx(Vertx vertx) {
		//Context.INSTANCE.getAnnotationConfigApplicationContext("");
		super.afterStartingVertx(vertx);
	}

	/**
	 * load json config file specified by VERTX_CONFIG_LOCATION
	 * multi config file divide by ",", for example:classpath:/config.json,file:/opt/config/vertx.json
	 * if VERTX_CONFIG_LOCATION isn't exists, load classpath:/config.json 
	 */
	private static final JsonObject getJsonConfiguration() {
		JsonObject config = new JsonObject();
		if(vertxConfigLocation == null) {
			config.mergeIn(getJsonConfigurationFromClasspath("/config.json"));
		}else {
			for(String location : vertxConfigLocation) {
				log.info("config location {}", location);
				if(location.startsWith("classpath:") && location.endsWith(".json")) {
					config.mergeIn(getJsonConfigurationFromClasspath(location.substring("classpath:".length())));
				}else if(location.startsWith("file:")) {
					String path = location.substring("file:".length());
					//如果是json文件,进行加载
					if(path.endsWith(".json")) {
						config.mergeIn(getJsonConfiguration(Optional.of(new File(path)), path));
					}else if(path.endsWith("/")) {
						File dir = new File(path);
						File[] files = dir.listFiles();
						for(File file : files) {
							if(file.getPath().endsWith(".json")) {
								config.mergeIn(getJsonConfiguration(Optional.of(file), file.getPath()));
							}
						}
					}
				}
			}
		}
		return config;
	}
	
	private static final <T> JsonObject getJsonConfiguration(Optional<T> op, String path) {
		JsonObject conf = new JsonObject();
		log.info("Reading json config file: {}", path);
		T t = op.get();
		boolean isFile = t instanceof File;
		try (Scanner scanner = isFile ? new Scanner((File)t).useDelimiter("\\A") 
				: new Scanner((InputStream)t).useDelimiter("\\A")) {
			String sconf = scanner.next();
			try {
				conf = new JsonObject(sconf);
			} catch (DecodeException e) {
				log.error("Configuration file {} does not contain a valid JSON object", path);
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return conf;
	}
	
	private static final JsonObject getJsonConfigurationFromClasspath(String classpath) {
		URL url = Launcher.class.getResource(classpath);
		if(url != null) {
			InputStream is = Launcher.class.getResourceAsStream(classpath);
			return getJsonConfiguration(Optional.of(is), url.getPath());
		}
		return new JsonObject();
	}
}
