package com.huang.myservice.bean;

import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class Contants {
	private static final Logger LOGGER = LoggerFactory.getLogger(Contants.class);

	public static Map<String, Object> yampValue;

	public static String DEFAULT_CHARSET;

	public static int MQ_SUBSCRIBE_CONNECT_NUM;

	public static int CONSUMER_THREAD_NUM;

	public static int QUEUE_SIZE;

	public static String ZOOKEEPER_CONNECT;

	public static Object GROUP_ID;

	public static String METADATA_BROKER_LIST;

	public static String REDIS_URL;

	public static String REDIS_PASSWORD;

	public static int REDIS_MAXCONNECT;

	public static int REDIS_MAXWAIT;

	@SuppressWarnings("unchecked")
	public static void initConfig() {
		LOGGER.info("init config start...");
		InputStream yamlIn = Contants.class.getResourceAsStream("/application.yaml");
		Yaml yaml = new Yaml();
		Map<String, Object> yamlValue = (Map<String, Object>) yaml.load(yamlIn);
		Contants.yampValue = yamlValue;

		Map<String, Object> commonConfig = (Map<String, Object>) yamlValue.get("common");
		if (commonConfig != null) {
			DEFAULT_CHARSET = (String) commonConfig.get("default_charset");
			MQ_SUBSCRIBE_CONNECT_NUM = (int) commonConfig.get("mq_subscribe_connect_num");
			CONSUMER_THREAD_NUM = (int) commonConfig.get("consumer_thread_num");
			QUEUE_SIZE = (int) commonConfig.get("queue_size");
		}

		Map<String, Object> kafkaConfig = (Map<String, Object>) yamlValue.get("kafka");
		if (kafkaConfig != null) {
			ZOOKEEPER_CONNECT = (String) kafkaConfig.get("zookeeper_connect");
			GROUP_ID = (String) kafkaConfig.get("group_id");
			METADATA_BROKER_LIST = (String) kafkaConfig.get("metadata_broker_list");
		}

		Map<String, Object> redisConfig = (Map<String, Object>) yamlValue.get("redis");
		if (redisConfig != null) {
			REDIS_URL = (String) redisConfig.get("url");
			REDIS_PASSWORD = (String) redisConfig.get("password");
			REDIS_MAXCONNECT = (int) redisConfig.get("maxConnectRedis");
			REDIS_MAXWAIT = (int) redisConfig.get("maxWaitRedis");
		}
		LOGGER.info("init config end...");
	}
}
