# Log Persister application

This is an implementation of a Coding Assignment

## Getting Started

It's implemented using the following tech:

* Java 8
* Spring Boot
* Spring Integration
* JPA / Hibernate / H2
* Lombok

### Implementation details

You can break it down into the following steps:

* Scan / Read
* Split 
* Transform
* Aggregate
* Persist

This is a typical EIP (Enterprise Integration Pattern) and the right tool for the job is an EIP implementation.
I chose Spring Integration since I currently use it but there are other great implementation such as 

* [Apache Camel](http://camel.apache.org)
* [RedHat Fuse](https://www.redhat.com/en/technologies/jboss-middleware/fuse)
* [Mule](http://www.mulesoft.com)

### Running

In order to run and test it use the following command

```
./gradlew bootRun -Pargs=--file=src/test/resources/log.json
```

and go to H2 console to validate the results

```
http://localhost:8080/console
```

Use the following connection string 

````
jdbc:h2:mem:example-app
````

validate results by 
````
SELECT * FROM SERVER_EVENT 
````

validate log output, there is a mix of INFO and DEBUG log statements:
````
2018-11-09 08:13:38.822  INFO 93088 --- [ask-scheduler-1] ication$$EnhancerBySpringCGLIB$$1f527f8d : /Users/roman/Projects/log-persister/src/test/resources/log.json
2018-11-09 08:13:38.894 DEBUG 93088 --- [ask-scheduler-1] c.c.c.s.r.l.a.LogEntryAggregator         : Aggregated new ServerEvent: ServerEvent(id=scsmbstgra, duration=5, alert=true, type=APPLICATION_LOG, host=12345)
2018-11-09 08:13:38.895  INFO 93088 --- [ask-scheduler-1] ication$$EnhancerBySpringCGLIB$$1f527f8d : ServerEvent(id=scsmbstgra, duration=5, alert=true, type=APPLICATION_LOG, host=12345)
2018-11-09 08:13:39.011 DEBUG 93088 --- [ask-scheduler-1] c.c.c.s.r.l.a.LogEntryAggregator         : Aggregated new ServerEvent: ServerEvent(id=scsmbstgrc, duration=8, alert=true, type=null, host=null)
2018-11-09 08:13:39.012  INFO 93088 --- [ask-scheduler-1] ication$$EnhancerBySpringCGLIB$$1f527f8d : ServerEvent(id=scsmbstgrc, duration=8, alert=true, type=null, host=null)
2018-11-09 08:13:39.015 DEBUG 93088 --- [ask-scheduler-1] c.c.c.s.r.l.a.LogEntryAggregator         : Aggregated new ServerEvent: ServerEvent(id=scsmbstgrb, duration=3, alert=false, type=null, host=null)
2018-11-09 08:13:39.015  INFO 93088 --- [ask-scheduler-1] ication$$EnhancerBySpringCGLIB$$1f527f8d : ServerEvent(id=scsmbstgrb, duration=3, alert=false, type=null, host=null)
````
## Running the tests / test coverage

To build and run the tests use

```
./gradlew build
```

There are tests for most critical code in LogEntryAggregator.
[TODO]: Add integration tests

### Other requirements / extra points

##### Large files
I am using Spring Integration FileReadingMessageSource with iterator mode one so it shoud be able to handle large files.

##### Multithreading
Log Persister uses one single thread to process one file. But it can be easily modified to:

* Scan a directory using file filter to process more than one file
* Use thread pool to process multiple files in parallel
* Use native tail command or java implementation of it in order to process live files  

For more details read: [Spring Integration File Support](https://docs.spring.io/spring-integration/reference/html/files.html)

### Things to improve

* More verbose logs
* More test coverage
* Run static code analysys / code coverage tools 
* Implement error handling using errorChannel

