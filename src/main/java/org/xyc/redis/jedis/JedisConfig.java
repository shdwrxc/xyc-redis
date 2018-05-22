package org.xyc.redis.jedis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by wks on 2016/1/12.
 */
public class JedisConfig {

    public ShardedJedisPool load(String cluster) {
        List<JedisShardInfo> shardInfoList = new ArrayList<JedisShardInfo>();
        try {
            InputStream in = JedisConfig.class.getClassLoader().getResourceAsStream("jedis.properties");
            Properties p = new Properties();
            p.load(in);

            String shards = p.getProperty(JedisConstants.REDIS_SHARD + JedisConstants.DOT + cluster);
            String[] shardArray = shards.split(",");
            for (String shard : shardArray) {
                String[] ipAndPort = shard.split(":");
                shardInfoList.add(new JedisShardInfo(ipAndPort[0], Integer.parseInt(ipAndPort[1])));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //        Set<String> sentinels = new HashSet<String>();
        //        sentinels.add("192.168.200.8:26397");
        //        JedisSentinelPool pool = new JedisSentinelPool("mymaster", sentinels);
        return new ShardedJedisPool(new JedisPoolConfig(), shardInfoList);
    }

    public JedisSentinelPool createSentinelPool(String hosts) {
        Set<String> sentinels = new HashSet<String>();
        try {
            InputStream in = JedisConfig.class.getClassLoader().getResourceAsStream("jedis.properties");
            Properties p = new Properties();
            p.load(in);

            String sentinelStr = p.getProperty(JedisConstants.REDIS_SENTINEL + JedisConstants.DOT + hosts);
            String[] sentinelArray = sentinelStr.split(",");
            for (String sentinel : sentinelArray) {
                sentinels.add(sentinel);    //sentinel地址
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(32);
        JedisSentinelPool pool = new JedisSentinelPool("mymaster", sentinels, config);
        return pool;
    }
}
