package org.xyc.redis.jedis;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by wks on 2016/1/13.
 */
public class JedisTest {

    @Test
    public void get() {
        JedisProxy jedisProxy = JedisProxy.getRedisCluster("test");
        String str = jedisProxy.get("ab");
        //        Assert.assertNull(str);
        jedisProxy.set("ab", "123");
        str = jedisProxy.get("ab");
        Assert.assertNotNull(str);
    }
}
