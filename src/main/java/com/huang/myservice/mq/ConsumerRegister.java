package com.huang.myservice.mq;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.huang.myservice.bean.Contants;

//@Component
public class ConsumerRegister implements ApplicationContextAware {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerRegister.class);
	/**
	 * topic的处理method 即被@MQMsgMapping注解的方法
	 */
	private final Map<String, MethodInvocationForMQ> topicHandler = new HashMap<String, MethodInvocationForMQ>();
	/**
	 * 阻塞队列 -> 接收到的topic消息
	 */
	public static BlockingQueue<MessageEntry> queue;

	/**
	 * 线程池 -> 处理接收到的topic消息
	 */
	private ExecutorService executorService;

	ApplicationContext context;

	public void init() {
		LOGGER.info("init consumer register for kafka.");
		takeConsumerQueue();
		// topic handler
		Map<String, Object> beanMap = context.getBeansWithAnnotation(MQMsgReceiver.class);
		Method[] methods = null;
		MQMsgMapping mqMsgMapping = null;
		for (Object bean : beanMap.values()) {
			methods = bean.getClass().getDeclaredMethods();
			String topic = null;
			for (Method method : methods) {
				mqMsgMapping = method.getAnnotation(MQMsgMapping.class);
				if (null != mqMsgMapping) {
					topic = mqMsgMapping.topic();
					if (org.springframework.util.StringUtils.isEmpty(topic)) {
						LOGGER.error("topic is empty,add topic handler falied");
						continue;
					}
					if (topicHandler.containsKey(topic)) {
						LOGGER.error("only one method can subscript to the same topic");
					}
					topicHandler.put(topic,
							MethodInvocationForMQ.builder().method(method).topic(topic).target(bean).build());
					KafkaMQAdapter.getInstance().subscribe(topic);
				}
			}
		}
	}

	/**
	 * 启动线程处理收到的topic
	 */
	private void takeConsumerQueue() {
		// 初始化队列
		queue = new LinkedBlockingQueue<>(Contants.QUEUE_SIZE);
		// 初始化线程池
		executorService = Executors.newFixedThreadPool(Contants.CONSUMER_THREAD_NUM);

		for (int i = 0; i < Contants.CONSUMER_THREAD_NUM; i++) {
			executorService.execute(() -> {

				try {
					while (true) {
						MessageEntry entry = queue.take();
						topicHandler.get(entry.getTopic()).invoke(entry);
					}
				} catch (InterruptedException e) {
					LOGGER.error("failed to take from queue.");
				}

			});
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
		init();
	}

}
