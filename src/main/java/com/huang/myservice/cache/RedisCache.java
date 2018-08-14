package com.huang.myservice.cache;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.huang.myservice.bean.Contants;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisCache {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisCache.class);

	private static RedisCache cache;

	private RedisCache() {

	}

	/**
	 * jedis cluster instance
	 */
	public static JedisPool jedisPool = null;

	public static RedisCache getInstance() {
		return cache;
	}

	static {
		LOGGER.info("init redis...");
		String redisURL = Contants.REDIS_URL;
		URI uri = URI.create(redisURL);
		String password = Contants.REDIS_PASSWORD;
		String host = uri.getHost();
		int port = uri.getPort();
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(Contants.REDIS_MAXCONNECT);
		jedisPoolConfig.setMaxWaitMillis(Contants.REDIS_MAXWAIT);
		jedisPool = new JedisPool(jedisPoolConfig, host, port, Contants.REDIS_MAXWAIT, password);
		cache = new RedisCache();
	}

	/**
	 * 根据key值获取对应的结构体
	 *
	 * @param key
	 *            key
	 * @return 对应的结构体
	 */
	public Object getObject(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			String redisCache = jedis.get(key);
			if (redisCache != null && redisCache.trim().length() > 0) {
				return JSON.parse(redisCache);
			} else {
				return null;
			}
		} finally {
			jedis.close();
		}
	}

	/**
	 * 根据key值删除对应的缓存值
	 *
	 * @param key
	 *            key
	 */
	public void removeObject(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.del(key);
		} finally {
			jedis.close();
		}
	}

	/**
	 * 将obj缓存起来
	 *
	 * @param key
	 *            缓存用的key值
	 * @param obj
	 *            对应的结构体
	 */
	public void putObject(String key, Object obj) {
		String cacheValue = JSON.toJSONString(obj, SerializerFeature.WriteClassName,
				SerializerFeature.NotWriteDefaultValue);
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.set(key, cacheValue);
		} finally {
			jedis.close();
		}
	}

	/**
	 * 将obj缓存起来
	 *
	 * @param key
	 *            缓存用的key值
	 * @param obj
	 *            对应的结构体
	 * @param expireTime
	 *            缓存过期的时间 , seconds
	 */
	public void putObject(String key, Object obj, int expireTime) {
		String cacheValue = JSON.toJSONString(obj, SerializerFeature.WriteClassName,
				SerializerFeature.NotWriteDefaultValue);
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.setex(key, expireTime, cacheValue);
		} finally {
			jedis.close();
		}
	}

	/**
	 * 将key的值设为value,当且仅当key不存在。
	 *
	 * @param key
	 *            缓存用的key值
	 * @param value
	 *            缓存用的value值
	 * @return 若给定的 key 存在返回0，不存在则返回1
	 */
	public Long setnx(String key, String value) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.setnx(key, value);
		} finally {
			jedis.close();
		}
	}

	/**
	 * 将value 放到key的缓存队列中，并将values放到队列中末尾
	 *
	 * @param key
	 *            key
	 * @param values
	 *            values
	 * @return channel current size
	 */
	public long offer(String key, String... values) {
		Long ret;
		Jedis jedis = jedisPool.getResource();
		try {
			ret = jedis.rpush(key, values);
			if (ret == null) {
				ret = -1l;
			}
		} finally {
			jedis.close();
		}
		return ret;
	}

	/**
	 * 获取key对应的缓存队列的第一个元素，并将该元素从队列中删除
	 *
	 * @param key
	 *            key
	 * @return value
	 */
	public String poll(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.lpop(key);
		} finally {
			jedis.close();
		}
	}

	/**
	 * 对指定的key值增加1
	 *
	 * @param key
	 *            key
	 * @return 增1 之后的值
	 */
	public long incr(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			Long ret = jedis.incr(key);
			if (ret == null) {
				throw new RuntimeException("failed to incr");
			}
			return ret;
		} finally {
			jedis.close();
		}
	}

	/**
	 * llen
	 *
	 * @param key
	 *            缓存key
	 * @return 缓存key对应的list的长度
	 */
	public long llen(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			Long ret = jedis.llen(key);
			if (ret == null) {
				throw new RuntimeException("failed to llen");
			}
			return ret;
		} finally {
			jedis.close();
		}
	}

	/**
	 * lrange
	 *
	 * @param key
	 *            缓存key
	 * @param start
	 *            start num
	 * @param stop
	 *            stop num
	 * @return sublist
	 */
	public List<String> lrange(String key, long start, long stop) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.lrange(key, start, stop);
		} finally {
			jedis.close();
		}
	}

	/**
	 * 重新设置key对应的过期时间
	 *
	 * @param key
	 *            缓存key
	 * @param expireTime
	 *            过期时间，单位秒
	 * @return 设置成功则返回1， 如果key已经过期或者key不存在则返回0
	 */
	public Long expire(String key, int expireTime) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.expire(key, expireTime);
		} finally {
			jedis.close();
		}
	}

}
