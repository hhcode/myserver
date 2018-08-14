package com.huang.myservice.mq;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huang.myservice.bean.Contants;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import kafka.message.MessageAndMetadata;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class KafkaMQAdapter implements MQAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMQAdapter.class);

	private static KafkaMQAdapter instance = new KafkaMQAdapter();

	private final Map<String, ExecutorService> consumerExecutorService = new ConcurrentHashMap<String, ExecutorService>();;

	private final Map<String, AtomicBoolean> consumerRunning = new ConcurrentHashMap<String, AtomicBoolean>();

	private Producer<byte[], byte[]> producer = null;

	private KafkaMQAdapter() {
		Properties producerProps = new Properties();
		producerProps.put("metadata.broker.list", Contants.METADATA_BROKER_LIST);
		producerProps.put("request.required.acks", "1");
		producerProps.put("producer.type", "sync");
		producerProps.put("serializer.class", "kafka.serializer.DefaultEncoder");
		producer = new Producer<byte[], byte[]>(new ProducerConfig(producerProps));
	}

	public static KafkaMQAdapter getInstance() {
		return instance;
	}

	@Override
	public void subscribe(String topic) {
		if (consumerExecutorService.containsKey(topic)) {
			return;
		}
		ExecutorService executorService = Executors.newFixedThreadPool(Contants.MQ_SUBSCRIBE_CONNECT_NUM);
		consumerExecutorService.put(topic, executorService);
		AtomicBoolean atomic = new AtomicBoolean(true);
		consumerRunning.put(topic, atomic);

		for (int i = 0; i < Contants.MQ_SUBSCRIBE_CONNECT_NUM; i++) {
			executorService.execute(() -> {

				Properties consumerProp = new Properties();
				consumerProp.put("zookeeper.connect", Contants.ZOOKEEPER_CONNECT);
				consumerProp.put("group.id", Contants.GROUP_ID);

				ConsumerConnector consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(consumerProp));
				Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
				topicCountMap.put(topic, 1);
				Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer
						.createMessageStreams(topicCountMap);
				List<KafkaStream<byte[], byte[]>> consumerRecords = consumerMap.get(topic);
				KafkaStream<byte[], byte[]> stream = consumerRecords.get(0);
				ConsumerIterator<byte[], byte[]> consumerIterator = stream.iterator();

				// while判断条件没有直接写为true是为了可以动态取消订阅
				while (consumerRunning.containsKey(topic) && consumerRunning.get(topic).get()) {
					if (consumerIterator.hasNext()) {
						MessageAndMetadata<byte[], byte[]> msgAndMeta = consumerIterator.next();
						byte[] keyByte = msgAndMeta.key();
						byte[] messageByte = msgAndMeta.message();

						String key = null;
						String message = null;
						try {
							key = new String(keyByte, Contants.DEFAULT_CHARSET);
							message = new String(messageByte, Contants.DEFAULT_CHARSET);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						LOGGER.info("receive topic : {} key : {} message : {}", topic, key, message);
						MessageEntry entry = MessageEntry.builder().topic(topic).key(key).message(message).build();
						try {
							ConsumerRegister.queue.put(entry);
						} catch (InterruptedException e) {
							LOGGER.error("falied to put entry to queue,key : {}", key);
							e.printStackTrace();
						}
					}
				}
			});
		}
	}

	@Override
	public void unSubscribe(String topic) {
		AtomicBoolean running = consumerRunning.remove(topic);
		if (running != null) {
			running.set(false);
		}
		ExecutorService executorService = consumerExecutorService.remove(topic);
		if (executorService != null) {
			executorService.shutdownNow();
		}
	}

	@Override
	public void sendMessage(String topic, String key, String message) {
		KeyedMessage<byte[], byte[]> keyMsg;
		try {
			keyMsg = new KeyedMessage<>(topic, key.getBytes(Contants.DEFAULT_CHARSET),
					message.getBytes(Contants.DEFAULT_CHARSET));
			producer.send(keyMsg);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
