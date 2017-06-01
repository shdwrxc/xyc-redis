package org.xyc.redis.spring;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Redis base.
 */
public class RedisSpring<V extends Serializable> {

    protected RedisTemplate<String, V> redisTemplate;

    public RedisSpring() {
    }

    public RedisSpring(RedisTemplate<String, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean hasKey(String key) {
        Boolean b = redisTemplate.hasKey(key);
        if (b == null)
            return false;
        return b.booleanValue();
    }

    public void set(String key, V value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, V value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    public void mSet(Map<String, ? extends V> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    public V get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void setNative(final String key, final String value, final long timeout) {
        if (isEmpty(key) || isEmpty(value))
            return;
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> redisSerializer = redisTemplate.getStringSerializer();
                connection.setEx(redisSerializer.serialize(key), timeout, redisSerializer.serialize(value));
                return null;
            }
        });
    }

    /**
     * 用于获取未序列化的value，比如incr方法产生的value
     * @param key
     * @return
     */
    public String getNative(final String key) {
        if (isEmpty(key))
            return "";
        return redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> redisSerializer = redisTemplate.getStringSerializer();
                return redisSerializer.deserialize(connection.get(redisSerializer.serialize(key)));
            }
        });
    }

    public List<V> mGet(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    public Long fuzzyDelete(final String fuzzyKey) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return del(connection, redisTemplate.keys(fuzzyKey).toArray(new String[0]));
            }
        });
    }

    public Long delete(final String... keys) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return del(connection, keys);
            }
        });
    }

    private Long del(RedisConnection connection, String... keys) {
        if (keys.length == 0) {
            return 0l;
        }
        RedisSerializer<String> redisSerializer = redisTemplate.getStringSerializer();
        byte[][] bytes = new byte[keys.length][];
        int i = 0;
        for (String key : keys) {
            bytes[i++] = redisSerializer.serialize(key);
        }
        return connection.del(bytes);
    }

    public Long incrBy(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    public Long incr(String key) {
        return redisTemplate.opsForValue().increment(key, 1);
    }

    public Long decrBy(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    public Long decr(String key) {
        return redisTemplate.opsForValue().increment(key, -1);
    }

    public void hSet(String key, String field, V value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    public V hGet(String key, String field) {
        return (V) redisTemplate.opsForHash().get(key, field);
    }

    public void hSetAll(String key, Map<String, V> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    public Map<String, V> hGetAll(String key) {
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
        Map<String, V> result = new HashMap<String, V>();
        if (map == null || map.size() == 0)
            return result;
        for (Object hkey : map.keySet()) {
            result.put((String) hkey, (V) map.get(hkey));
        }
        return result;
    }

    public long lPush(String key, V... values) {
        if (values == null)
            return 0;
        return redisTemplate.opsForList().leftPushAll(key, values);
    }

    public long rPush(String key, V... values) {
        if (values == null)
            return 0;
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    public List<V> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    public V lPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    public V rPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    public Long lRemove(String key, int count, V value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    public Long sAdd(String key, V... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    public Set<V> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public long sSize(String key) {
        Long l = redisTemplate.opsForSet().size(key);
        if (l == null)
            return 0;
        return l;
    }

    public Long sRemove(String key, V... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    public Boolean zAdd(String key, V value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    public Long zRemove(String key, V... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    public Set<V> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    public Long zSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    public Set<V> zRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    public Set<V> zRangeByScore(String key, double min, double max, long offset, long count) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
    }

    public Set<V> zRevRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    public Set<V> zRevRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }

    public Set<V> zRevRangeByScore(String key, double min, double max, long offset, long count) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max, offset, count);
    }

    private boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
