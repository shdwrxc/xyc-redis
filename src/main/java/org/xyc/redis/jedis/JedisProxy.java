package org.xyc.redis.jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wks on 2016/1/12.
 */
public class JedisProxy {

    private static Object lock = new Object();

    private static Map<String, JedisProxy> map = new HashMap<String, JedisProxy>();

    private JedisUtil jedisUtil = new JedisUtil();

    public static JedisProxy getRedisCluster(String cluster) {
        JedisProxy jedisProxy = null;
        synchronized (lock) {
            jedisProxy = map.get(cluster);
            if (jedisProxy == null) {
                JedisConfig config = new JedisConfig();
                jedisProxy = new JedisProxy();
                jedisProxy.jedisUtil.setShardedJedisPool(config.load(cluster));
                map.put(cluster, jedisProxy);
            }
        }
        return jedisProxy;
    }

    public boolean set(String key, String value) {
        return jedisUtil.set(key, value);
    }

    public String get(String key) {
        return jedisUtil.get(key);
    }

    public List<String> getBatch(List<String> keyList) {
        return jedisUtil.getBatch(keyList);
    }

    public boolean zadd(String key, double score, String member) {
        return jedisUtil.zadd(key, score, member);
    }

    public Set<String> zrange(String key, long start, long end) {
        return jedisUtil.zrange(key, start, end);
    }
}
