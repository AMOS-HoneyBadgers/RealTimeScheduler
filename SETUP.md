# Setup

## Cloud Foundry Setup

**Disclaimer: Service names have to be the same as used in this guide, otherwise rename the services in following files:**
* ./manifest.yml
* ./application-apis/groupapi/src/main/resources/application.properties
* ./application-apis/taskapi/src/main/resources/application.properties
* ./application-apis/managementapi/src/main/resources/application.properties
* ./application-apis/monitoringapi/src/main/resources/application.properties
* ./application-cleaner/src/main/resources/application.properties
* ./application-main/src/main/resources/application.properties
* **TODO: Lock Service** 
* ./application-clienttests/src/main/resources/application.properties
* ./application-main-integrationtests/src/test/resources/application.properties

### Services 
Required:
* Redis (Redis Cloud)
* Postgres (ElephantSQL)
* User Provided Service
* Event Queue (In the following used rabbit (CloudAMQP))

#### Redis Cloud
**Name of Service**: _redis_

#### ElephantSQL
**Name of Service**: _task_storage_<br/>
**Database Setup**:
* Run ./docs/DatabaseCreate.sql in DB Admin Tool of your choice. **(Pay attention to change owner of tables in script, see top comment of sql script)**

#### User Provided Service Postgres
**Name of Service**: _postgres_credentials_ <br/>
**Configuration as JSON**: <br/>
{<br/>
  "host":" postgres://**_USERNAME_**:**_PASSWORD_**@**_DBURL_**:**_DBPORT_**/**_DATABASE_**", <br/>
  "username":"**_USERNAME_**", <br/>
  "password":"**_PASSWORD_**" <br/>
}

#### User Provided Service RabbitMQ
**Name of Service**: _rabbit_credentials_ <br/>
**Configuration as JSON**: <br/>
{<br/>
"rabbitmq_host":"**_URL_**",<br/>
"rabbitmq_username":"**_USERNAME_**",<br/>
"rabbitmq_password":"**_PASSWORD_**"<br/>
}

#### CloudAMQP
**Name of Service**: _rabbit_ <br/>
**Create Exchanges** (Renaming required changes in files mentioned in [Cloud Foundry Setup](#cloud-foundry-setup)): <br/>
**All Exchanges have to be declared as durable**
* task.exchange
* priority.exchange
* feedback.exchange
* dispatch.exchange 

**Create Queues** (Renaming required changes in files mentioned in [Cloud Foundry Setup](#cloud-foundry-setup)): <br/>
**All Queues have to be declared as durable**
* tasks
* priority
* dispatch.feedback
* dispatch.queue 

**Bindings**: 

Queue | Exchange | Routing Key
------------ | ------------- | -------------
tasks | task.exchange | tasks.routingkey
priority | priority.exchange | priority.routingkey
dispatch.feedback | feedback.exchange | feedback.routingkey
dispatch.queue | dispatch.exchange | dispatch.routingkey


## Scheduler properties Configuration

### All available property files 
1. ./application-apis/groupapi/src/main/resources/application.properties
2. ./application-apis/taskapi/src/main/resources/application.properties
3. ./application-apis/managementapi/src/main/resources/application.properties
4. ./application-apis/monitoringapi/src/main/resources/application.properties
5. ./application-cleaner/src/main/resources/application.properties
6. ./application-main/src/main/resources/application.properties

### Configurable properties

* Transaction failure max sleep before retry (in ms)
    * `com.honeybadgers.transaction.max-retry-sleep=500`
    * Found in:
        * application-apis/managementapi
        * application-apis/taskapi
        * application-apis/groupapi
        * application-main
* Transaction failure max retry count before exception is thrown
    * `com.honeybadgers.transaction.max-retry-count=500`
    * Found in:
        * application-apis/managementapi
        * application-apis/taskapi
        * application-apis/groupapi
* Interval (in ms) in which the cleaner checks for resumable tasks, groups and scheduler.
    * `cleaner.paused.fixed-rate=60000`
    * Found in:
        * application-cleaner
* Delay (in ms) for which the cleaner waits before searching for resumable tasks for the first time.
    * `cleaner.paused.initial-delay=20000`
    * Found in:
        * application-cleaner
* Enables automatic DB cleanup for finished Tasks. 
    * `cleaner.finished-cleanup.enabled=true`
    * Found in:
        * application-cleaner
* Interval (in ms) in which the cleaner checks for disposable tasks.
    * `cleaner.finished-cleanup.fixed-rate=86400000`
    * Found in:
        * application-cleaner
* Delay (in ms) for which the cleaner waits before searching for disposable tasks for the first time.
    * `cleaner.finished-cleanup.initial-delay=20000`
    * Found in:
        * application-cleaner
* Number of days, the task has to be finished before deletion.
    * `cleaner.finished-cleanup.expiration-days=10`
    * Found in:
        * application-cleaner
* Url of the endpoint for the LockService application
    * `com.honeybadgers.lockservice.url=https://lockservice-amos.cfapps.io/`
    * Found in:
        * application-main
* Minimal Priority + 1 
    * `com.realtimescheduler.scheduler.priority.const=1000`
    * **Changes required changs in OpenAPI to allow creation of task**
    * Found in:
        * application-main
* Modifier for weighting of user provided base priority.
    * `com.realtimescheduler.scheduler.priority.prio-modifier=1`
    * Found in:
        * application-main
* Modifier for weighting of user provided deadline.
    * `com.realtimescheduler.scheduler.priority.deadline-modifier=20`
    * Found in:
        * application-main
* Modifier for weighting of task priority based on retries.
    * `com.realtimescheduler.scheduler.priority.retries-modifier=500`
    * Found in:
        * application-main
* Modifier for weighting based on task type.
    * `com.realtimescheduler.scheduler.priority.realtime-modifier=1`
    * Found in:
        * application-main