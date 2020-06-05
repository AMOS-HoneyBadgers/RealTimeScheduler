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

    @Autowired
    RedisApplicationProperties redisApplicationProperties;

    public RedisConfig() {
        /*logger.info("################################## START");
        logger.warn("+++++++++++++++++++++ CLOUDCONFIG IS NULL: " + (cloudConfig == null));
        logger.warn("++++++++++++++++++++ TEST TO STRING: " + cloudConfig.toString());
        logger.warn("++++++++++++++++++++ prio_Store IS NULL: " + (cloudConfig.priority_store == null));
        logger.warn("++++++++++++++++++++ lock_Store IS NULL: " + (cloudConfig.priority_store == null));
        logger.warn("++++++++++++++++++++ prioStore creds: " + cloudConfig.priority_store.credentials.toString());
        logger.warn("++++++++++++++++++++ lockStore creds: " + cloudConfig.priority_store.credentials.toString());
        priohost = cloudConfig.priority_store.credentials.get("hostname");
        logger.info("################################## host: " + priohost);
        priopassword = cloudConfig.priority_store.credentials.get("password");
        logger.info("################################## pw: " + priopassword);
        prioport = Integer.parseInt(cloudConfig.priority_store.credentials.get("port"));
        logger.info("################################## port: " + prioport);
        lockhost = cloudConfig.priority_store.credentials.get("hostname");
        logger.info("################################## host: " + lockhost);
        lockpassword = cloudConfig.priority_store.credentials.get("password");
        logger.info("################################## pw: " + lockpassword);
        lockport = 15915;//Integer.parseInt(cloudConfig.priority_store.credentials.get("port"));
        logger.info("################################## port: " + lockport);
        logger.info("################################## END");*/
    }

    @Bean(name="prioConnectionFactory")
    @Primary
    JedisConnectionFactory jedisConnectionFactoryForPrioDatabase() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        logger.warn("#################### PRIO DB HOST: " + redisApplicationProperties.prio.host);
        logger.warn("#################### PRIO DB PW: " + redisApplicationProperties.prio.password);
        logger.warn("#################### PRIO DB PORT: " + redisApplicationProperties.prio.port);
        redisStandaloneConfiguration.setHostName(redisApplicationProperties.prio.host);
        redisStandaloneConfiguration.setPassword(redisApplicationProperties.prio.password);
        redisStandaloneConfiguration.setPort(Integer.parseInt(redisApplicationProperties.prio.port));
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }


    @Bean(name="lockConnectionFactory")
    JedisConnectionFactory jedisConnectionFactoryForLockDatabase() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        logger.warn("#################### LOCK DB HOST: " + redisApplicationProperties.lock.host);
        logger.warn("#################### LOCK DB PW: " + redisApplicationProperties.lock.password);
        logger.warn("#################### LOCK DB PORT: " + redisApplicationProperties.lock.port);
        redisStandaloneConfiguration.setHostName(redisApplicationProperties.lock.host);
        redisStandaloneConfiguration.setPassword(redisApplicationProperties.lock.password);
        redisStandaloneConfiguration.setPort(Integer.parseInt(redisApplicationProperties.lock.port));
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
