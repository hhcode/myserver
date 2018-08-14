package com.huang.myservice.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huang.myservice.mq.KafkaMQAdapter;
import com.huang.myservice.mq.MQMsgMapping;
import com.huang.myservice.mq.MQMsgReceiver;

/**
 * kafka消息处理类
 * 
 * @author hWX511382
 *
 */
@MQMsgReceiver
public class KafkaController {

	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaController.class);

	@MQMsgMapping(topic = "kafka.test.0")
	public void connect(String message) {
		LOGGER.info("kafka.test.0 : " + message);
		String key = UUID.randomUUID().toString();
		String sendMessage = "testtesttest";
		KafkaMQAdapter.getInstance().sendMessage("kafka.test.1", key, sendMessage);
	}

	@MQMsgMapping(topic = "kafka.test.1")
	public void disconnect(String message) {
		LOGGER.info("kafka.test.1 : " + message);
	}
}
