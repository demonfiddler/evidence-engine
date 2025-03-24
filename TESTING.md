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
|```-PskipLaunch```|Do not launch a server process, connect instead to a running server|
|```-PcaseInsensitive```|Run tests in case-insensitive mode, necessary when testing against a MariaDB production database, which uses case-insensitive collation by default|

## Running tests

|Task|Command Line|Comments|
|----|------------|--------|
|Launch server process|```./gradlew :server:bootRun```|Typically uses MariaDB production database|
|Launch integration test server|```./gradlew :server:bootIntegrationTestServer```|Typically uses H2 integration test database|
|Launch integration test server in background|```./gradlew :server:bootIntegrationTestServerBG```|Typically uses H2 integration test database|
|Run tests|```./gradlew :client:test```|Automatically launches the integration test server and shuts it down afterwards|
|Run tests without launching server|```./gradlew :client:test -PskipLaunch```|Server must already be running|
|Run tests in case-insensitive mode|```./gradlew :client:test -PskipLaunch -PcaseInsensitive```|When the running server is using the MariaDB database|
|Re-run unchanged tests without launching server|```./gradlew :client:test -PskipLaunch --rerun```|Unchanged code/tests that have previously passed don't get run by default|
|Debug a specific test class|```./gradlew :client:test -PskipLaunch --tests "*LinkableEntityTests" --debug-jvm```|Wildcard matches package prefix|
|Debug a specific test case|```./gradlew :client:test -PskipLaunch --tests "*LinkableEntityTests.readEntitiesFilteredTopic" --debug-jvm```|Specify test class and method name|

Note: use the appropriate platform-specific path separator in the above commands (i.e. \ for Windows, / for Linux, etc).