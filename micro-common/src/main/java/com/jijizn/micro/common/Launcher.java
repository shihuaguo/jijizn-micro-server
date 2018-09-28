package com.jijizn.micro.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * 
 * @author shihuaguo
 * @email 147402691@qq.com
 * @date 2018年9月28日
 */
public class Launcher extends io.vertx.core.Launcher {
	
	private static final Logger log = LoggerFactory.getLogger(Launcher.class);

	public static void main(String[] args) {
		new Launcher().dispatch(args);
	}
	
	private final void processArgs(String[] args) {
		
	}

	@Override
	public void beforeStartingVertx(VertxOptions options) {
		options.setClustered(true).setClusterHost("127.0.0.1");
	}

	@Override
	public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
		super.beforeDeployingVerticle(deploymentOptions);

		if (deploymentOptions.getConfig() == null) {
			deploymentOptions.setConfig(new JsonObject());
		}

		// if vertx-config-path is set
		String configPath = System.getProperty("vertx-config-path");
		if (StringUtils.isBlank(configPath)) {
			String path = Launcher.class.getResource("/config.json").toString();
			InputStream is = Launcher.class.getResourceAsStream("/config.json");
			deploymentOptions.getConfig().mergeIn(getConfiguration(is, path));
		}else {
			File file = new File(configPath);
			if(file.exists()) {
				List<File> list;
				if(file.isDirectory()) {
					list = Arrays.asList(file.listFiles());
				}else {
					list = new ArrayList<>();
					list.add(file);
				}
				list.forEach(f -> {
					deploymentOptions.getConfig().mergeIn(getConfiguration(f, f.getPath()));
				});
			}else {
				log.error("config file path {} isn't exists", configPath);
			}
		}
	}
	
	private JsonObject getConfiguration(File f, String path) {
		try(InputStream is = new FileInputStream(f);){
			return getConfiguration(is, path);
		} catch (IOException e) {
			//ignore it
		}
		return new JsonObject();
	}

	private JsonObject getConfiguration(InputStream is, String path) {
		JsonObject conf = new JsonObject();
		log.info("Reading config file: " + path);
		try (Scanner scanner = new Scanner(is).useDelimiter("\\A")) {
			String sconf = scanner.next();
			try {
				conf = new JsonObject(sconf);
			} catch (DecodeException e) {
				log.error("Configuration file {} does not contain a valid JSON object", path);
			}
		} catch (Exception e) {
			// Ignore it.
			}
		return conf;
	}
}
