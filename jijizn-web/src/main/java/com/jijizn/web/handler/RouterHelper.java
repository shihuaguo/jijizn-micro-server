package com.jijizn.web.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.jijizn.micro.context.Context;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

public class RouterHelper {
	private static final Logger log = LoggerFactory.getLogger(RouterHelper.class);

	public static void mountSubRouter(Vertx vertx, Router router, String contextPath) {
		ApplicationContext context = Context.INSTANCE.getContext();
		vertx.executeBlocking(f -> {
			//mount sub router bean implements ApiRouter
			Map<String, ApiRouter> map = context.getBeansOfType(ApiRouter.class);
			map.entrySet().stream().forEach(ar -> {
				ar.getValue().route(vertx, router, contextPath);
			});
			
			//mount sub router that bean annotation with Path
			context.getBeansWithAnnotation(Path.class).entrySet().stream().forEach(m -> {
				final Class<?> clazz = m.getValue().getClass();
				Path api = clazz.getAnnotation(Path.class);
				String basePath = api.value();
				
				Set<Method> methods = getMethodsAnnotatedWith(clazz, Path.class);
				methods.forEach(method -> {
					//the path request mapping with
					String path = basePath;
					io.vertx.core.http.HttpMethod httpMethod = null;
					Annotation[] annotations = method.getAnnotations();
					for(Annotation anno : annotations) {
						Class<?> type = anno.annotationType();
						if(type == Path.class) {
							path = path + ((Path)anno).value();
						}else {
							HttpMethod hm = anno.annotationType().getAnnotation(HttpMethod.class);
							if(hm != null) {
								httpMethod = io.vertx.core.http.HttpMethod.valueOf(hm.value());
							}
						}
					}
					Router sub = Router.router(vertx);
					Route route = httpMethod == null ? sub.route(path) : sub.route(httpMethod, path);
					route.handler(rc -> {
						try {
							method.invoke(m.getValue(), rc);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							log.error("", e);
						}
					});
					router.mountSubRouter(contextPath, sub);
					log.info("mounting {}{} to {}, httpMethod={}", contextPath, path, method.getDeclaringClass().getName() + "." + method.getName(), httpMethod);
				});
			});;
		}, res -> {
			if(res.succeeded()) {
				log.info("mount sub router success");
			}else {
				log.error("mount sub router failed", res.cause());
			}
		});
	}

	private static Set<Method> getMethodsAnnotatedWith(final Class<?> type,
			final Class<? extends Annotation> annotation) {
		final List<String> methodNames = new ArrayList<>();
		final Set<Method> methods = new HashSet<Method>();
		Class<?> klass = type;
		while (klass != Object.class) {
			final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(klass.getDeclaredMethods()));
			for (final Method method : allMethods) {
				if (method.isAnnotationPresent(annotation)) {
					if (!methodNames.contains(method.getName())) {
						methods.add(method);
						methodNames.add(method.getName());
					}
				}
			}
			klass = klass.getSuperclass();
		}
		return methods;
	}
	
	public static void main(String[] args) {
		System.out.println(GET.class.isAssignableFrom(HttpMethod.class));
		System.out.println(HttpMethod.class.isAssignableFrom(GET.class));
		System.out.println(GET.class.getAnnotation(HttpMethod.class).value());
		Pattern pattern = Pattern.compile("j");
		Matcher matcher = pattern.matcher("/jijizn/login.html");
		System.out.println(matcher.matches());
		System.out.println(matcher.groupCount());
		while(matcher.find()) {
			System.out.println(matcher.group());
		}
	}
}
