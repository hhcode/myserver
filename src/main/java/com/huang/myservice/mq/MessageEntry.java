package com.huang.myservice.mq;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息实体
 * @author hWX511382
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageEntry implements Serializable{
	private static final long serialVersionUID = -396771360939644383L;
	
	private String topic;
	private String key;
	private String message;
}
