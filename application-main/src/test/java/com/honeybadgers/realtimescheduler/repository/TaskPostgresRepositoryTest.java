package com.honeybadgers.realtimescheduler.repository;

import com.honeybadgers.models.Group;
import com.honeybadgers.models.ModeEnum;
import com.honeybadgers.models.Task;
import com.honeybadgers.models.TypeFlagEnum;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*@RunWith(SpringRunner.class)
@ActiveProfiles("test")
//@SpringBootTest
//@ExtendWith(SpringExtension.class)
@DataJpaTest
/*@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DATABASE_TO_UPPER=false;MODE=PostgreSQL"
})*
//@PropertySource("classpath:application-test.properties")
//@RunWith(SpringJUnit4ClassRunner.class)
@EntityScan(basePackages = {"com.honeybadgers.models"})
public class TaskPostgresRepositoryTest {
    @Autowired private EntityManager entityManager;

    @Autowired
    private TaskPostgresRepository taskPostgresRepository;

    @Value("${spring.datasource.url}")
    private String url;

    @Test
    public void injectedComponentsAreNotNull(){

        System.out.println("URL: " + url);

        assertThat(entityManager).isNotNull();
        assertThat(taskPostgresRepository).isNotNull();
    }

    /*@ClassRule
    public static PostgreSQLContainer postgreSQLContainer = SingletonPostgreContainer.getInstance();

    @TestConfiguration
    @EnableJpaRepositories(basePackages = {"com.honeybadgers.realtimescheduler.repository"})            // enable all jpa repositories in the given paths
    @PropertySource("classpath:application-dev.properties")                                                     // use this properties file
    @EnableAutoConfiguration(exclude = {RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class})       // disable redis configuration
    public class PostgreTestConfig {


        // Following is initialization of test config or in other words: MAGIC (seriously no idea what exactly happens here)


        @Autowired
        private Environment env;

        @Bean
        public DataSource dataSource() {
            final DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
            dataSource.setUrl(env.getProperty("spring.datasource.url"));
            dataSource.setUsername(env.getProperty("spring.datasource.username"));
            dataSource.setPassword(env.getProperty("spring.datasource.password"));

            return dataSource;
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
            final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
            em.setDataSource(dataSource());
            em.setPackagesToScan(new String[] { "com.honeybadgers.realtimescheduler.model" });
            em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            em.setJpaProperties(additionalProperties());
            return em;
        }

        final Properties additionalProperties() {
            final Properties hibernateProperties = new Properties();

            hibernateProperties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
            hibernateProperties.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
            hibernateProperties.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));

            return hibernateProperties;
        }
    }*


    @Test
    public void testFindById() {
        // given
        Task task = new Task();
        task.setId("TestUUID");
        task.setGroup(new Group());
        //taskPostgresRepository.save(task);

        // when
        Optional<Task> found = taskPostgresRepository.findById(task.getId());

        // then
        assertTrue(found.isPresent());
        assertThat(found.get().getId())
                .isEqualTo(task.getId());
    }
}*/
