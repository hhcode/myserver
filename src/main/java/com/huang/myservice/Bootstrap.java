package com.huang.myservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ImportResource;

import com.huang.myservice.bean.Contants;

@SpringBootApplication
@ImportResource({ "classpath:spring/application-context.xml" })
public class Bootstrap {
	private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrap.class);

	public static void main(String[] args) {
		LOGGER.debug("springboot logback...");
		LOGGER.info("start myservice...");
		Contants.initConfig();

		new SpringApplicationBuilder().sources(Bootstrap.class).run(args);
		LOGGER.info("myservice start success...");
	}

}
