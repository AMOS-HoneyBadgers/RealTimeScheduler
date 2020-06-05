package com.honeybadgers.realtimescheduler.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties("vcap.services")
public class CloudFoundryProperties {

    RedisProperties priority_store;

    RedisProperties lock_store;


    @Override
    public String toString() {
        return "CloudFoundryProperties{" +
                "priority_store=" + priority_store +
                ", lock_store=" + lock_store +
                '}';
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class RedisProperties {

        String label;

        String provider;

        String plan;

        List<String> tags;

        String instance_name;

        String binding_name;

        LinkedHashMap<String, String> credentials;

        String syslog_drain_url;

        List<?> volume_mounts;


        @Override
        public String toString() {
            return "RedisProperties{" +
                    "label='" + label + '\'' +
                    ", provider='" + provider + '\'' +
                    ", plan='" + plan + '\'' +
                    ", tags=" + tags +
                    ", instance_name='" + instance_name + '\'' +
                    ", binding_name='" + binding_name + '\'' +
                    ", credentials=" + credentials +
                    ", syslog_drain_url='" + syslog_drain_url + '\'' +
                    ", volume_mounts=" + volume_mounts +
                    '}';
        }
    }
}
