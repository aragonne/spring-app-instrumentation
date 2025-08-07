# Hands On - Install metrics

## Objectif

Understand how to install metrics in a Spring Boot application.
There are multiple ways to do it :

- Actuator
- Micrometer
- Prometheus
- OpenTelemetry

## Prerequisite

- Java 17
- Maven

## 0 - Install java and maven

1 Download and install java 17

```bash
  sudo apt update
  sudo apt install --yes openjdk-17-jdk
```

2 Download and install maven

```bash
  sudo apt update
  sudo apt install --yes maven
```

3 Check java and maven version

```bash
  java -version
  mvn -version
```

## 1 - Build a Spring Boot application

First, lets create a simple Spring Boot application.
Nothing fancy but lets create an app with a "hello world" endpoint.

1 Create an empty Spring Boot application using maven

```bash
  mvn archetype:generate -DgroupId=com.exemple.demo -DartifactId=spring-demo -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
```

> This will create a new directory called "spring-demo" with the basic structure of a Maven project.
> The project will have a "src/main/java" directory for the source code and a "src/test/java" directory for the tests and a "pom.xml" file for the project configuration.

2 Add a "hello world" endpoint

```java
package com.exemple.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BonjourEQLController {

    @GetMapping("/bonjour-eql")
    public String direBonjourEQL() {
        return "Bonjour EQL, Voici Spring!";
    }
}
```

3 Add the Application class

```java
package com.exemple.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

4 Add the pom.xml file

```Xml
<!-- Maven metadata and things -->
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">

    
    <modelVersion>4.0.0</modelVersion>
    
    <!-- Parent to inherit properties and dependencies from (outside of the project) -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.2</version>
        <relativePath/>
    </parent>
    
    <!-- Project metadata -->
    <groupId>com.exemple</groupId>
    <artifactId>spring-demo</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    <name>spring-demo</name>
    <description>Demo project for Spring Boot observability</description>

    <!-- Properties -->
    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>

    <!-- Dependencies to build our app -->
    <dependencies>
        <!-- Spring Boot Starter Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

    <!-- Maven pipeline configuration -->
    <build>
        <plugins>
            <!-- Plugin Spring Boot -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <!-- Rq: there is no version specified, it will download the latest version available -->
            </plugin>
            
            <!-- Maven Compiler Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
```

4 Build the application

```bash
  mvn clean install
```

5 Run the application

```bash
  java -jar target/spring-demo-1.0.0.jar
```

6 Check the endpoint

```bash
  curl http://localhost:8080/bonjour-eql
```

It should return "Bonjour EQL, Voici Spring!"

7 Check the metrics

```bash
  curl http://localhost:8080/metrics
```

It should return nothing.
Do we have data on the application?

### To Do

- [ ] Try to use `ps aux | grep java` to see if the application is running.
- [ ] Try to use `jps` to see if the application is running.
- [ ] Try to use `jstat -gcutil <pid> 1000` to see it consume memory.
- [ ] Try to use `jmap -histo <pid>` to see the histogram of the application.
- [ ] Try to use `jmap -clstats <pid>` to see the class loader statistics.

There is a lot of metrics available and infos are available from the JVM. However the java cuttles are tedious and not really user friendly. And what happen when the application is running in a container?
What if we want to export the metrics to a monitoring system?
What if we want to see the metrics in a dashboard?

## 2 - Build a Spring Boot application with Actuator

### Install Actuator

Actuator is a Spring Boot module that provides production-ready features to your application.
It provides a set of endpoints that you can use to monitor and manage your application.

0 Move to the `02_actuator_app` directory

```bash
  cd 02_actuator_app
```

1 Add the Actuator dependency

```xml
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
  </dependency>
```

2 Add the Actuator endpoint in the BonjourEQLController
Header

```Java
package com.exemple.demo;

import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
```

Body

```java

    @Autowired
    private MetricsService metricsService;
```

3 Add an empty metrics service

```Java
package com.exemple.demo;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

}
```

4 Run the application

```bash
  java -jar target/actuator-metrics-1.0.0.jar
```

5 Check the metrics

```bash
  curl http://localhost:8080/actuator/metrics
```

### To Do

Try the following:

- [ ] Check disk.free : `curl http://localhost:8080/actuator/metrics/disk.free`
- [ ] Check process.cpu.usage : `curl http://localhost:8080/actuator/metrics/process.cpu.usage`
- [ ] Check the jvm info : `curl http://localhost:8080/actuator/info`

Ok now we have metrics, but we want to add a custom metric !

### Add a custom metric

1 Add a metrics service to the MetricsService class

```Java
@Service
public class MetricsService {

    private final Counter bonjourCounter;
    private final Timer responseTimer;
    private final MeterRegistry meterRegistry;

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.bonjourCounter = Counter.builder("bonjour.requests")
                .description("Nombre de requêtes bonjour")
                .register(meterRegistry);
        
        this.responseTimer = Timer.builder("bonjour.response.time")
                .description("Temps de réponse des requêtes bonjour")
                .register(meterRegistry);
    }

    public String processBonjour() {
        // Incrémenter le compteur
        bonjourCounter.increment();
        
        // Simuler un traitement avec mesure du temps
        return responseTimer.recordCallable(() -> {
            // Simuler un délai de traitement variable
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(10, 100));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Bonjour, Spring avec métriques!";
        });
    }

    public void recordCustomMetric(String operation, double value) {
        meterRegistry.gauge("custom.operation.value", value);
        meterRegistry.counter("custom.operation.count", "operation", operation).increment();
    }
}
```

2 Add the metrics service to the BonjourEQLController

```Java
@RestController
public class BonjourEQLController {

    @Autowired
    private MetricsService metricsService;

    @GetMapping("/bonjour-eql")
    public String direBonjourEQL() {
        return "Bonjour EQL, Voici Spring!";
    }

    @PostMapping("/metrics/custom")
    public String recordCustomMetric(@RequestParam String operation, @RequestParam double value) {
        metricsService.recordCustomMetric(operation, value);
        return "Métrique personnalisée enregistrée: " + operation + " = " + value;
    }
}
```

3 Build and run the application

```bash
mvn clean install
java -jar target/actuator-metrics-1.0.0.jar
```

4 Check the metrics

```bash
  curl http://localhost:8080/actuator/metrics
```

There is a new metric called `bonjour.requests` and `bonjour.response.time`.

- [ ] Check the custom metrics `curl http://localhost:8080/actuator/metrics/bonjour.requests`
- [ ] Try to call the endpoint `/bonjour-eql` twice and check the metrics again.

## 3 - Lets use a Java agent

A Java agent is a piece of code that is loaded by the JVM at startup. It can be used to instrument the application without modifying the code.
The bonus is that it can be used to instrument any application, not only Spring Boot applications and this is not at compile time.
This can there be used to instrument a production application without having to recompile it. You just need to restart the application with the Java agent.

We will use the Java agent from JMX. It is easy to use and it is a standard Java agent.

0 Move to the `03_java_agent_app` directory

```bash
  cd 03_java_agent_app
```

1 Download the Java agent

```bash
  wget -O jmx_prometheus_javaagent.jar https://github.com/prometheus/jmx_exporter/releases/download/1.3.0/jmx_prometheus_javaagent-1.3.0.jar
```

2 Run the application with the Java agent

```bash
java -javaagent:jmx_prometheus_javaagent.jar=8081:jmx_config.yml -jar target/spring-demo-1.0.0.jar
```

3 Check the metrics

```bash
  curl http://localhost:8081/metrics
```

> Rq : Datas are weird, they are in the prometheus format. We can use it with any monitoring system.

- [ ] Check the metrics `curl http://localhost:8081/metrics`
- [ ] Check the metrics `curl http://localhost:8081/metrics | grep jvm_threads`
- [ ] Check the metrics `curl http://localhost:8081/metrics | grep jvm_memory_bytes_committed`

The pom.xml and code is untouched, but the application can be instrumented !

## 4 - A first implementation of tracing

Tracing is a way to track the execution of an application through its various components and services. Unlike metrics (which give you numbers) and logs (which give you events), tracing gives you the complete journey of a request through your system.

Tracing helps you understand:

- Where time is spent in your application
- Which components are involved in processing a request
- How services interact with each other
- Where errors occur in the request flow
- Performance bottlenecks across your system

Each trace consists of spans (individual operations that make up the complete request journey). Spans can have parent-child relationships, creating a tree structure that shows the execution flow.

In the `04_tracing_app` folder, we've implemented a basic tracing system from scratch to understand the core concepts before using complex tools like OpenTelemetry or Jaeger.

### Key Files and Their Purpose

**Core Tracing Components:**

- `Trace.java` - Represents a single trace/span with timing, status, and metadata
- `SimpleTraceCollector.java` - Collects and manages traces in memory, exports to file and console
- `TracingService.java` - Provides easy-to-use methods for instrumenting code with tracing

**Application Components:**

- `BonjourEQLController.java` - REST endpoints instrumented with tracing (manual and automatic)
- `TraceViewerController.java` - Web interface to visualize collected traces

**Web Interface:**

- `templates/traces.html` - Simple web UI to view traces, with auto-refresh and filtering

### Try it out

0 Move to the `04_tracing_app` directory

```bash
  cd 04_tracing_app
```

1 Compile and run the application

```bash
  mvn clean package
  java -jar target/spring-demo-1.0.0.jar
```

2 Generate some traces

```bash
BASE_URL="http://localhost:8080"

echo "1. endpoint /bonjour-eql test"
curl -s "$BASE_URL/bonjour-eql" && echo

echo -e "\n2. endpoint /user/{id} test"
curl -s "$BASE_URL/user/123" && echo

echo -e "\n3. endpoint POST /data test"
curl -s -X POST -H "Content-Type: text/plain" -d "Test data for simple tracing" "$BASE_URL/data" && echo

echo -e "\n4. endpoint /error test"
curl -s "$BASE_URL/error" && echo

echo -e "\n5. multiple requests"
for i in {1..3}; do
    echo "Série de requêtes $i..."
    curl -s "$BASE_URL/bonjour-eql" > /dev/null
    curl -s "$BASE_URL/user/user$i" > /dev/null
    curl -s "$BASE_URL/error" > /dev/null 2>/dev/null || true
    sleep 1
done

echo -e "\n6. Check stats"
curl -s "$BASE_URL/traces/stats" && echo

```

> Available endpoints
>
> Application API
>
> - `GET /bonjour-eql` : Simple endpoint with tracing
> - `GET /user/{id}` : Endpoint with nested traces (validation + DB)
> - `POST /data` : POST endpoint with data processing
> - `GET /error` : Endpoint that can generate errors
>
> Traces API
>
> - `GET /traces` : Web visualization interface
> - `GET /traces/api` : JSON API for traces
> - `GET /traces/stats` : Trace statistics
> - `GET /traces/clear` : Clear all traces

3 View traces in the web interface

Open your browser to: `http://localhost:8080/traces`

4 Look out the traces.log file

```bash
  cat traces.log
```

> Rq: If you check the metrics
>
> ```bash
>   curl http://localhost:8080/actuator/metrics
>   or
>   curl http://localhost:8081/metrics
> ```
>
> You get nothing. Traces are not metrics.

In the traces.log file, you can see the following format:

`[trace_id] span_id | start_time -> end_time | operation_name (duration) | status | tags`

For example:

```Text
[1e822370] 8005e2d1 | 15:28:25.047 -> 15:28:25.069 | validate-user (22ms) | FINISHED | validation=success
[1e822370] 00e7586b | 15:28:25.070 -> 15:28:25.132 | database-query (62ms) | FINISHED | query=SELECT * FROM users WHERE id=user3
[1e822370] d2099d15 | 15:28:25.047 -> 15:28:25.133 | get-user-request (86ms) | FINISHED | user.id=user3
```

Where:

- **[1e822370]** = Trace ID (same for all related operations)
- **8005e2d1** = Span ID (unique for each operation)
- **15:28:25.047 -> 15:28:25.069** = Start and end timestamps
- **validate-user** = Operation name
- **(22ms)** = Operation duration
- **FINISHED** = Operation status
- **validation=success** = Custom tags with contextual data

Notice how all three operations share the same trace ID `[1e822370]`, showing they're part of the same request flow.

## 5 - Lets dive into logs

Logging is a fundamental pillar of observability that provides detailed information about what's happening inside your application at runtime. Unlike metrics (which give you numbers) and tracing (which gives you request flows), logging gives you detailed events and contextual information about your application's behavior.

Logging helps you understand:

- What operations are being performed in your application
- When errors occur and their context
- Performance characteristics of individual operations
- Business events and user interactions
- System state changes and configuration issues
- Debugging information for troubleshooting

Effective logging requires structured approaches, appropriate log levels, contextual information, and proper configuration for different environments (development vs production).

In the `05_logs_spring_app` folder, we've implemented a comprehensive logging demonstration that showcases best practices for observability-focused logging in a Spring Boot application.

### Key Files and Their Purpose

**Core Application Components:**

- `DemoApplication.java` - Main application demonstrating different log levels, structured logging, error handling, and contextual information
- `UserService.java` - Business service showcasing MDC (Mapped Diagnostic Context), performance logging, audit trails, and error management
- `User.java` - Simple data model for demonstration purposes

**Configuration:**

- `application.yml` - Advanced logging configuration with different profiles, file rotation, custom patterns, and environment-specific settings
- `README.md` - Comprehensive documentation explaining logging concepts, best practices, and usage examples

### Try it out

0 Move to the `05_logs_spring_app/my-app` directory

```bash
  cd 05_logs_spring_app/my-app
```

1 Build the application

```bash
  mvn clean install
```

2 Run the application

```bash
  java -jar target/spring-demo-1.0.0.jar
```

3 Observe the logs

The application will automatically demonstrate various logging scenarios:

- Different log levels in action
- Contextual information with user operations
- Error handling and recovery
- Performance measurements
- Audit trails for critical operations

4 Check the log files

Logs are written to both console and file (`logs/demo-app.log`):

```bash
  cat logs/demo-app.log
```

5 Try different profiles

Run with development profile for more verbose logging:

```bash
  java -jar target/spring-demo-1.0.0.jar --spring.profiles.active=development
```

## 6 - The collector to rule them all, OpenTelemetry

OpenTelemetry is an open-source project that helps developers understand what’s happening inside their applications. It collect three key types of data: traces, metrics, and logs with the same agent.

OpenTelemetry can be installed as a java agent or as a library. For this example, we will use the java agent, which is the most simple way to instrument an application and the most likely to be used in a legacy production environment.

0 Move to the `06_opentelemetry_app` directory

```bash
  cd 06_opentelemetry_app
```

1 Download the OpenTelemetry java agent

```bash
  wget -O opentelemetry-javaagent.jar https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar
```

2 Add a configuration file for the OpenTelemetry java agent named `otel.properties`

> Rq: This file is optional, OpenTelemetry can be configured in multiple ways, but the most common is to use a configuration file. Since OpenTelemetry replace all the metrics, logs and traces, we need to configure it to set the OTLP pipelines of collection.

3 Run the application with the OpenTelemetry java agent

```bash
  java -javaagent:opentelemetry-javaagent.jar -jar target/spring-demo-1.0.0.jar
```

:warning: Now we are stuck. OpenTelemetry doesn't open a metric, logs or trace endpoint.
Instead, we will use something to collect its data and resend them.

4 Run the OpenTelemetry collector

```bash
  docker compose up -d
```

### To Do

- [ ] See the metrics on prometheus : localhost:9090
- [ ] See the traces on jaeger : localhost:16686
- [ ] See the logs on the console

Although the example is too simple, it shows the power of OpenTelemetry and the benefit to have every metrics, logs and traces in the same place.
We will see more complexe examples in the next hands on. But we need a more comprehensive infrastructure : Kubernetes.

## 7 - Instrumentation of a docker container

This time we will see how to instrument a docker container.
In fact this is the same as the java agent example, but we will use docker to install it.

0 Move to the `07_docker_app` directory

```bash
  cd 07_docker_app
```

1 Build the application

```bash
  mvn clean install
```

2 Build the docker image

```bash
  docker build -t spring-demo-otel .
```

3 Run the docker container

```bash
  docker run -d --name spring-demo-otel -p 8080:8080 spring-demo-otel
```

As you can see, the application is running. And we can access it on the browser : <http://localhost:8080/bonjour-eql>

Now we will use the JMX java agent to instrument the application.

4 Download the JMX java agent

```bash
  wget -O jmx_prometheus_javaagent.jar https://github.com/prometheus/jmx_exporter/releases/download/1.3.0/jmx_prometheus_javaagent-1.3.0.jar
```

5 Run the docker container with the JMX java agent and override the entrypoint

```bash
  docker run -d --name spring-demo-otel -p 8080:8080 -p 8081:8081 -v ./jmx_prometheus_javaagent.jar:/jmx_prometheus_javaagent.jar --entrypoint ["/bin/sh", "-c", "java -javaagent:jmx_prometheus_javaagent.jar -jar /app/spring-demo.jar"]
```

> Rq:
>
> - The JMX java agent is a java agent, so it needs to be in the classpath
> - The JMX java agent default port is 8081. So we need to open it.
> - By default on app start with the entrypoint. We need to override it to add the java agent.

6 Check the metrics

```bash
  curl http://localhost:8081/metrics
```

> Rq : Datas are weird, they are in the prometheus format. We can use it with any monitoring system.

- [ ] Check the metrics `curl http://localhost:8081/metrics`
- [ ] Check the metrics `curl http://localhost:8081/metrics | grep jvm_threads`
- [ ] Check the metrics `curl http://localhost:8081/metrics | grep jvm_memory_bytes_committed`

The code and the docker image are untouched, but the application can be instrumented !

7 Now we will use a docker compose and a sidecar pattern

```bash
  docker compose up
```

- [ ] Check the metrics `curl http://localhost:8081/metrics`
- [ ] Check the metrics `curl http://localhost:8081/metrics | grep jvm_threads`
- [ ] Check the metrics `curl http://localhost:8081/metrics | grep jvm_memory_bytes_committed`
