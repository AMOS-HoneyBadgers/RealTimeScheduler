# Technology Decision

This Document is supposed to archive and document the technology and architecture decision that where made throughout the AMOS 2020 Project.

### Cloud Foundry

- We will use Pivotal Web Services, since it is very easy to deploy and add various services like databases,mq etc. Also the DEV-Team got some previous experience with the platform.
- We did not use the spring libraries for cloud-foundry-service-discovery due to the time frame of the project.


### Communication

- CRUD Operations for Tasks, Groups etc. will be exposed through an open REST API.
- For communication between RestServices and SchedulerService, as well as handing off tasks to dispatcher, we decided to build a generic interface of event driven patterns, technologies like Kafka or MQ can be used and swapped using separate implementations of the interfaces.
- The Raceconditions between Schedulers will be coordinated by the LockService, where you can request locks via a REST Api.


### Code Quality

 - In order to have a good Code quality, the devs agreed on a 95% test coverage.
 - The coverage will be tested with jacoco and checked before every push via a githook.
 - Additionally, we have set up SonarQube for code analysis.


### Data Storage

 - We will use a combination of Redis and PostgreSQL, because both data-bases have a pre-installed service on the Pivotal Cloud Foundry platform.
 - PostGreSQl will be used as our main data storage database, which will store groups and tasks persistent.
 - Redis will be used for the LockService, due to its faster access and write time.
 - We originally intended to use Redis also for currently Scheduled tasks, but changed this, due to the need for transaction, which redis does not support (It is mainly advised to not use redis, if transactions are needed).
