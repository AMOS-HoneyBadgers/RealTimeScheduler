# Technology Decision


This Document is supposed to archive and document the technology and architecture decision that where made throughout the AMOS 2020 Project.

### Cloud Foundry

- We will use Pivotal Web Services, since it is very easy to deploy and add various services like databases,mq etc. Also the DEV-Team got some previous experience with the platform.

### Communication

- CRUD Operations for Tasks, Groups etc. will be exposed through an open REST API
- For internal communication and handing off tasks to dispatcher, we decided to build a generic interface of event driven patterns, technologies like kafka or mq can be used and swaped within the interface


### Code Quality
 - In order to have a good Code quality, the devs agreed on a 95% test coverage
 - The coverage will be tested with jacoco and checked before every push via a githook
 
### Data Storage
 - We will use a combination of PostgreSQL and REDIS, because both data-bases have a pre-installed service on the Pivotal Cloud Foundry platform. We will use PostGreSQl as our main data storage database, which will store groups and tasks persistent. Redis will be used to share status among our service instances.

