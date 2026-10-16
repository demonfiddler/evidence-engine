# Evidence Engine Testing

The Evidence Engine tests are exclusively end-to-end integration tests, focussed on simultaneously verifying both the client API and server functionality. The tests live in the client project.

By default, the ```:client:test``` Gradle task automatically launches a server process, configured to use the H2 in-memory database.

## Configuration

The client and server are both Spring Boot projects.

The client is configured by the [client/src/main/resources/application.properties](client/src/main/resources/application.properties) file.

The server is configured by the [server/src/main/resources/config/application.properties](server/src/main/resources/config/application.properties) file. When run in integration test mode (i.e. when the ```integration-test``` Spring profile active), some of these settings are overridden by the [server/src/main/resources/config/application-integration-test.properties](server/src/main/resources/config/application-integration-test.properties) file.

## Gradle Flags

|Flag|Meaning|
|----|-------|
|```--debug-jvm```|Launch gradle task in remote-debuggable mode (process will suspend and wait for a debugger to connect on port 5005)|
|```--Pprofile=PROFILE```|Launch gradle task with a specific Spring profile|
|```-PskipLaunch```|Do not launch a server process, connect instead to a running server|
|```-PcaseInsensitive```|Run tests in case-insensitive mode, necessary when testing against a MariaDB production database, which uses case-insensitive collation by default|
|```-x TASK```|Skip TASK|

## Running tests

N.B. By default, the server runs with a built-in Tomcat instance on port 8080. If, however, a local Tomcat installation is already using that port, it will be necessary to specify an explicit profile with ```-Pprofile=integration-test```, as in the following commands. For example, to use port 8082, the ```client/src/main/resources/application-integration-test.properties``` file should contain an entry like:
```
graphql.endpoint.url=http://localhost:8082/api/graphql
```
and the ```server/src/main/resources/config/application-integration-test.properties``` should contain entries like:
```
server.port=8082
data.server.url=http://localhost:8082
```

|Task|Command Line|Comments|
|----|------------|--------|
|Launch server process|```./gradlew :server:bootRun```|Typically uses MariaDB production database|
|Launch integration test server|```./gradlew :server:bootIntegrationTestServer -Pprofile=integration-test```|Typically uses H2 integration test database|
|Launch integration test server in background|```./gradlew :server:bootIntegrationTestServerBG -Pprofile=integration-test```|Typically uses H2 integration test database|
|Terminate integration test server|```./gradlew :server:terminateIntegrationTestServer -Pprofile=integration-test```|Typically used in conjunction with :server:bootIntegrationTestServerBG|
|Run tests|```./gradlew :client:test -Pprofile=integration-test```|Automatically launches the integration test server and shuts it down afterwards|
|Run tests without launching server|```./gradlew :client:test -Pprofile=integration-test -PskipLaunch```|Server must already be running|
|Run tests in case-insensitive mode|```./gradlew :client:test -Pprofile=integration-test -PskipLaunch -PcaseInsensitive```|When the running server is using the MariaDB database|
|Re-run unchanged tests without launching server|```./gradlew :client:test -Pprofile=integration-test -PskipLaunch --rerun```|Unchanged code/tests that have previously passed don't get run by default|
|Debug a specific test class|```./gradlew :client:test -Pprofile=integration-test -PskipLaunch --tests "*LinkableEntityTests" --debug-jvm```|Wildcard matches package prefix|
|Debug a specific test case|```./gradlew :client:test -Pprofile=integration-test -PskipLaunch --tests "*LinkableEntityTests.readEntitiesFilteredTopic" --debug-jvm```|Specify test class and method name|

Note: use the appropriate platform-specific path separator in the above commands (i.e. \ for Windows, / for Linux, etc).