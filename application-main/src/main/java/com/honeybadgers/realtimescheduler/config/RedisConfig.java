package com.honeybadgers.realtimescheduler.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(basePackages = "com.honeybadgers.realtimescheduler")
public class RedisConfig {

    static final Logger logger = LogManager.getLogger(RedisConfig.class);

    private String priohost;

    private int prioport;

    private String priopassword;

    private String lockhost;

    private int lockport;

    private String lockpassword;

    @Autowired
    public RedisConfig(CloudFoundryProperties cloudConfig) {
        logger.info("################################## START");
        logger.warn("+++++++++++++++++++++ CLOUDCONFIG IS NULL: " + (cloudConfig == null));
        logger.warn("++++++++++++++++++++ TEST TO STRING: " + cloudConfig.toString());
        logger.warn("++++++++++++++++++++ prio_Store IS NULL: " + (cloudConfig.priority_store == null));
        logger.warn("++++++++++++++++++++ lock_Store IS NULL: " + (cloudConfig.lock_store == null));
        logger.warn("++++++++++++++++++++ prioStore creds: " + cloudConfig.priority_store.credentials.toString());
        logger.warn("++++++++++++++++++++ lockStore creds: " + cloudConfig.lock_store.credentials.toString());
        priohost = cloudConfig.priority_store.credentials.get("hostname");
        logger.info("################################## host: " + priohost);
        priopassword = cloudConfig.priority_store.credentials.get("password");
        logger.info("################################## pw: " + priopassword);
        prioport = Integer.parseInt(cloudConfig.priority_store.credentials.get("port"));
        logger.info("################################## port: " + prioport);
        lockhost = cloudConfig.lock_store.credentials.get("hostname");
        logger.info("################################## host: " + lockhost);
        lockpassword = cloudConfig.lock_store.credentials.get("password");
        logger.info("################################## pw: " + lockpassword);
        lockport = Integer.parseInt(cloudConfig.lock_store.credentials.get("port"));
        logger.info("################################## port: " + lockport);
        logger.info("################################## END");
    }

    @Bean(name="prioConnectionFactory")
    JedisConnectionFactory jedisConnectionFactoryForPrioDatabase() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(priohost);
        redisStandaloneConfiguration.setPassword(priopassword);
        redisStandaloneConfiguration.setPort(prioport);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }


    @Bean(name="lockConnectionFactory")
    JedisConnectionFactory jedisConnectionFactoryForLockDatabase() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(lockhost);
        redisStandaloneConfiguration.setPassword(lockpassword);
        redisStandaloneConfiguration.setPort(lockport);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean(name="prioRedisTemplate")
    @Primary
    public RedisTemplate<String, Object> prioRedisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactoryForPrioDatabase());
        return template;
    }

    @Bean(name="lockRedisTemplate")
    public RedisTemplate<String, Object> lockRedisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactoryForLockDatabase());
        return template;
    }

}
