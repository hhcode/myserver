package com.huang.myservice.mq;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * kafka处理method注解
 * @author hWX511382
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MQMsgMapping {

	/**
	 * kafka topic
	 * @return
	 */
	public String topic() default "";
}
