package com.huang.myservice.mq;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MethodInvocationForMQ {

	private Method method;
	private String topic;
	private Object target;

	public void invoke(MessageEntry entry) {
		try {
			method.invoke(target, entry.getMessage());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
