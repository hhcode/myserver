package com.huang.myservice.listener;

import java.util.List;

import org.springframework.context.ApplicationEvent;

/**
 * 事件对象
 * 
 * @author hWX511382
 *
 */
public class DataEvent extends ApplicationEvent {

	private static final long serialVersionUID = -5171405195060366019L;

	/**
	 * 事件对象默认构造器
	 * 获取事件内容时可以直接调用getSource
	 * 
	 * @param source
	 */
	public DataEvent(Object source) {
		super(source);
	}

	/**
	 * 可以实现自己的构造器，传入事件需要发送的数据
	 * 
	 * @param source
	 */
	public DataEvent(List<String> source) {
		super(source);
	}

	/**
	 * 调用getSource获取传入的事件数据，把Object强转为构造器传入的数据类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> getList() {
		return (List<String>) getSource();
	}
}
