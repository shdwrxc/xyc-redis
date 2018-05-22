package org.xyc.redis.jedis;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by wks on 2016/1/13.
 */
public class JedisTest {

    @Test
    public void setValue() {
        JedisProxy jedisProxy = JedisProxy.getRedisCluster("test");
        jedisProxy.set("s1", "1");
        jedisProxy.set("s2", "2");
        jedisProxy.set("s3", "3");
        jedisProxy.set("s4", "4");
        jedisProxy.set("s5", "5");
        jedisProxy.set("s6", "6");
        jedisProxy.set("s7", "7");
        jedisProxy.set("s8", "8");
        System.out.println("the end.");
    }

    @Test
    public void getValue() {
        JedisProxy jedisProxy = JedisProxy.getRedisCluster("test");
        System.out.println(jedisProxy.get("s1"));
        System.out.println(jedisProxy.get("s2"));
        System.out.println(jedisProxy.get("s3"));
        System.out.println(jedisProxy.get("s4"));
        System.out.println(jedisProxy.get("s5"));
        System.out.println(jedisProxy.get("s6"));
        System.out.println(jedisProxy.get("s7"));
        System.out.println(jedisProxy.get("s8"));
    }
}
