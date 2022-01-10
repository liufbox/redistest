package com.example.demo5.test;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class testmain {


    public static void main(String[] args) throws Exception {

        RedisClient client = RedisClient.create(RedisURI.create("192.168.2.55", 6379));

        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(100);
        genericObjectPoolConfig.setMinIdle(5);
        genericObjectPoolConfig.setMaxTotal(1000);
        genericObjectPoolConfig.setMaxWaitMillis(1000);
        genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(100);


        GenericObjectPool<StatefulRedisConnection<String, String>> pool = ConnectionPoolSupport
                .createGenericObjectPool(() -> client.connect(), genericObjectPoolConfig);

        System.out.println("####createcount:" + pool.getCreatedCount());

        // executing work
        try (StatefulRedisConnection<String, String> connection = pool.borrowObject()) {

            RedisCommands<String, String> commands = connection.sync();
            commands.multi();
            commands.set("key", "value");
            commands.set("key2", "value2");
            commands.exec();
        }

        System.out.println("####createcount:" + pool.getCreatedCount());

// terminating
        pool.close();
        client.shutdown();

    }
}
