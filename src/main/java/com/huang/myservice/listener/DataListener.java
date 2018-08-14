package com.huang.myservice.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 事件监听器
 * 
 * @author hWX511382
 *
 */
@Component
public class DataListener implements ApplicationListener<DataEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataListener.class);

	@Override
	public void onApplicationEvent(DataEvent event) {
		LOGGER.info("实现具体监听业务，收到事件源发送的事件内容为 ： {}", event.getList());
	}
}
