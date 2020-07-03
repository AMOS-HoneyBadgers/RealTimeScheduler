package locksdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;


@Configuration
//@EnableDiscoveryClient
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class LocksApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocksApplication.class, args);
	}
	
	/*@ConditionalOnClass(RedisConnectionFactory.class)
	@ConditionalOnBean(RedisConnectionFactory.class)
	@Configuration
	protected static class RedisLockServiceConfiguration {
		@Bean
		@ConditionalOnMissingBean(LockService.class)
		public RedisLockService lockService(RedisConnectionFactory connectionFactory) {
			return new RedisLockService(connectionFactory);
		}
	}*/

	/*@ConditionalOnClass(RedisConnectionFactory.class)
	@ConditionalOnMissingBean(RedisConnectionFactory.class)
	@Configuration
	protected static class FallbackSimpleLockServiceConfiguration {
		@Bean
		@ConditionalOnMissingBean(LockService.class)
		public SimpleLockService lockService() {
			return new SimpleLockService();
		}
	}

	@ConditionalOnMissingClass(value = "org.springframework.data.redis.connection.RedisConnectionFactory")
	@Configuration
	protected static class SimpleLockServiceConfiguration {
		@Bean
		@ConditionalOnMissingBean(LockService.class)
		public SimpleLockService lockService() {
			return new SimpleLockService();
		}
	}*/

}
