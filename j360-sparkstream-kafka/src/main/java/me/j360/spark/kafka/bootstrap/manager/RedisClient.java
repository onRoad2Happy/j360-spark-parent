package me.j360.spark.kafka.bootstrap.manager;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;



@Slf4j
public class RedisClient {

    public static JedisPool jedisPool;
    public String host;

    public RedisClient(){
        Runtime.getRuntime().addShutdownHook(new CleanWorkThread());
    }

    private RedisClient(String host){
        this.host = host;
        Runtime.getRuntime().addShutdownHook(new CleanWorkThread());
        jedisPool = new JedisPool(new GenericObjectPoolConfig(), host);
    }

    private static RedisClient singleton;

    public static RedisClient getInstance(String host) {
        if (singleton == null) {
            synchronized (RedisClient.class) {
                if (singleton == null) {
                    singleton = new RedisClient(host);
                }
            }
        }
        return singleton;
    }


    static class CleanWorkThread extends Thread{
        @Override
        public void run() {
            if (null != jedisPool){
                jedisPool.destroy();
                jedisPool = null;
            }
        }
    }

    public Jedis getResource(){
        return jedisPool.getResource();
    }

    public void returnResource(Jedis jedis){
        jedis.close();
    }
}