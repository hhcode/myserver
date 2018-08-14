package com.huang.myservice.mq;

/**
 * kafka 适配器接口
 * 
 * @author hWX511382
 *
 */
public interface MQAdapter {
	/**
	 * 订阅topic
	 * 
	 * @param topic
	 * @param listener
	 */
	public void subscribe(String topic);

	/**
	 * 取消订阅topic
	 * 
	 * @param topic
	 */
	public void unSubscribe(String topic);

	/**
	 * 发送topic
	 * 
	 * @param topic
	 * @param key
	 * @param message
	 */
	public void sendMessage(String topic, String key, String message);
}
