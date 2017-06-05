package org.xyc.redis.jedis;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wks on 2016/1/12.
 */
public class JedisUtil {

    private ShardedJedisPool shardedJedisPool;
    //    private JedisSentinelPool sentinelPool;     sentinel support

    public ShardedJedisPool getShardedJedisPool() {
        return shardedJedisPool;
    }

    public void setShardedJedisPool(ShardedJedisPool shardedJedisPool) {
        this.shardedJedisPool = shardedJedisPool;
    }

    public ShardedJedis getResource() {
        ShardedJedis jedis = null;
        try {
            jedis = shardedJedisPool.getResource();
        } catch (Exception e) {
            //log info
            e.printStackTrace();
        }
        return jedis;
    }

    public void returnResource(ShardedJedis jedis) {
        if (jedis != null) {
            try {
                shardedJedisPool.returnResourceObject(jedis);
            } catch (Exception e) {
                //log info
                e.printStackTrace();
            }
        }
    }

    public boolean set(String key, String value) {
        ShardedJedis jedis = getResource();
        try {
            jedis.set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return false;
    }

    public String get(String key) {
        ShardedJedis jedis = getResource();
        try {
            return jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public List<String> getBatch(List<String> keyList) {
        ShardedJedis jedis = getResource();
        List<String> resultList = new ArrayList<String>();
        try {
            ShardedJedisPipeline jedisPipeline = jedis.pipelined();
            for (String key : keyList) {
                jedisPipeline.get(key);
            }
            List<Object> valueList = jedisPipeline.syncAndReturnAll();
            for (Object obj : valueList) {
                resultList.add(deserialize((byte[]) obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return resultList;
    }

    public String deserialize(byte[] bytes) throws Exception {
        return (bytes == null ? null : new String(bytes, "UTF-8"));
    }

    public boolean zadd(String key, double score, String member) {
        ShardedJedis jedis = getResource();
        try {
            Long l = jedis.zadd(key, score, member);
            if (l != null && l > 0)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return false;
    }

    public Set<String> zrange(String key, long start, long end) {
        ShardedJedis jedis = getResource();
        Set<String> set = null;
        try {
            set = jedis.zrange(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        if (set == null)
            set = new LinkedHashSet<String>();
        return set;
    }
}
