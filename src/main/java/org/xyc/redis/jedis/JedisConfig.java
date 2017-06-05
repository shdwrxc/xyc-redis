package org.xyc.redis.jedis;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
                shardInfoList.add(new JedisShardInfo(ipAndPort[0], ipAndPort[1]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //        Set<String> sentinels = new HashSet<String>();
        //        sentinels.add("192.168.200.8:26397");
        //        JedisSentinelPool pool = new JedisSentinelPool("mymaster", sentinels);
        return new ShardedJedisPool(new JedisPoolConfig(), shardInfoList);
    }
}
