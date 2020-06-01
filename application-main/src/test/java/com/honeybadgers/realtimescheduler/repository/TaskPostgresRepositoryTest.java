package com.honeybadgers.realtimescheduler.repository;

/*@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
//@SpringBootTest()
public class TaskPostgresRepositoryTest {

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
    }


    @Autowired
    private TaskPostgresRepository taskPostgresRepository;

    //@Test
    public void testFindById() {
        // given
        Task task = new Task("uuidTest", 100, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), 1, TypeFlagEnum.Batch, ModeEnum.Parallel, 1, 1, new HashMap<String, String>());
        taskPostgresRepository.save(task);

        // when
        Optional<Task> found = taskPostgresRepository.findById(task.getId());

        // then
        assertTrue(found.isPresent());
        assertThat(found.get().getId())
                .isEqualTo(task.getId());
    }
}*/
