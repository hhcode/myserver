package com.huang.myservice.controller;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.huang.myservice.cache.RedisCache;
import com.huang.myservice.listener.DataEvent;

@RestController
@RequestMapping("/data")
public class DataController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataController.class);

	@Autowired
	private ApplicationEventPublisher eventBus;

	@RequestMapping(value = "/data", method = RequestMethod.GET)
	public String getData() {
		return "data";
	}

	@RequestMapping(value = "/aop/{str}", method = RequestMethod.GET)
	public String testAop(@PathVariable("str") String str) {
		LOGGER.info("controller aop : str ： {}", str);
		return "data";
	}

	@RequestMapping(value = "/cache/{cache}", method = RequestMethod.GET)
	public String testCache(@PathVariable("cache") String str) {
		LOGGER.info("--- cache put : {}", str);
		String key = "testcache";
		RedisCache.getInstance().putObject(key, str);
		String cacheStr = (String) RedisCache.getInstance().getObject(key);
		LOGGER.info("--- cache get : {}", cacheStr);
		return "success";
	}

	@RequestMapping(value = "/listener/{data}", method = RequestMethod.GET)
	public String listener(@PathVariable(value = "data") String data) {
		LOGGER.info("事件源... {}", data);
		String[] datas = data.split(",");
		List<String> list = Arrays.asList(datas);
		DataEvent event = new DataEvent(list);

		eventBus.publishEvent(event);

		return "success";
	}

}
