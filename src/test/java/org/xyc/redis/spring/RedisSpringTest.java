package org.xyc.redis.spring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xyc.redis.spring.RedisSpring;

/**
 * Created by wks on 2016/5/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-redis.xml")
public class RedisSpringTest {

    @Autowired
    private RedisSpring<String> redisSpring;

    @Before
    public void init() {
//        final String[] paths = {"spring-redis.xml"};
//        final ApplicationContext ctx = new ClassPathXmlApplicationContext(paths);
//        redisSpring = (RedisSpring)ctx.getBean("redisSpring");
    }

    @Test
    public void testString() {
        redisSpring.set("key1", "value2");
        String value = redisSpring.get("key1");
        System.out.println(value);

        Map<String, String> map = new HashMap<String, String>();
        map.put("key2", "value2");
        map.put("key3", "value3");
        redisSpring.mSet(map);

        List<String> list = new ArrayList<String>();
        list.add("key2");
        list.add("key3");
        List<String> result = redisSpring.mGet(list);
        System.out.println(result);

        redisSpring.hSet("hkey1", "key1", "value1");
        String hResult = redisSpring.hGet("hkey1", "key1");
        System.out.println(hResult);

        Map<String, String> hmap = new HashMap<String, String>();
        hmap.put("key1", "value1");
        hmap.put("key2", "value2");
        redisSpring.hSetAll("hkey2", hmap);
        Map<String, String> hResultMap = redisSpring.hGetAll("hkey2");
        System.out.println(hResultMap);

        redisSpring.lPush("lkey1", "value1", "value2", "value3", "value4");
        redisSpring.rPush("lkey1", "value5", "value6");
        List<String> llist1 = redisSpring.lRange("lkey1", 1, 2);
        System.out.println(llist1);
        String lvalue1 = redisSpring.lPop("lkey1");
        System.out.println(lvalue1);
        String lvalue2 = redisSpring.rPop("lkey1");
        System.out.println(lvalue2);
        redisSpring.lRemove("lkey1", -1, "value6");

        redisSpring.sAdd("skey1", "value1", "value2", "value3", "value1");
        Set<String> slist1 = redisSpring.sMembers("skey1");
        System.out.println(slist1);
        long scount = redisSpring.sSize("skey1");
        System.out.println(scount);
        redisSpring.sRemove("skey1", "value1");

        redisSpring.zAdd("zkey1", "value1", 100);
        redisSpring.zAdd("zkey1", "value2", 200);
        redisSpring.zAdd("zkey1", "value3", 300);
        redisSpring.zAdd("zkey1", "value4", 150);
        redisSpring.zAdd("zkey1", "value5", 180);
        Set<String> zset1 = redisSpring.zRange("zkey1", 1, 2);
        System.out.println(zset1);
        Set<String> zset2 = redisSpring.zRangeByScore("zkey1", 100, 200);
        System.out.println(zset2);
        Set<String> zset3 = redisSpring.zRevRange("zkey1", 1, 2);
        System.out.println(zset3);
        Long l = redisSpring.zRemove("zkey1", "value2");
        System.out.println(l);
        Long l2 = redisSpring.zSize("zkey1");
        System.out.println(l2);

        Assert.assertNotNull("1");
    }
}
