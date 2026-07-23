# Building, Deploying & Launching

Example directories:
```bash
EE_BASE_DIR=~/dev/src/evidence-engine
EE_DEPLOYMENT_DIR=/var/lib/evidence-engine/webapps
EE_DEPLOYMENT_URI=scp://user@server.tld$EE_DEPLOYMENT_DIR
```

## Development
To launch the server as a Java process

From your IDE, from the ``server`` project:
```
java io.github.demonfiddler.ee.server.EvidenceEngineServer --spring.profiles.active=development
```
To launch the web client, from the ``web-client`` project:
```
cd $EE_BASE_DIR/web-client
pnpm dev
```

## Production
The deployable production artefacts are WAR files:

Server: ``server/build/libs/ee-server.war``

Web Client: ``web-client/build/libs/ee-web-client.war``

### Local Deployment
#### Build
For local testing of a production build (e.g., to verify that the WARs run correctly on a local Tomcat instance before full production deployment):
- edit your custom [.env.production.local](web-client/.env.production.local) file to enable the required configuration before building the web-client project.
- prepare a suitable [application-local.properties](server/src/main/resources/config/application-local.properties) file based on [application-example.properties](server/src/main/resources/config/application-example.properties)
```bash
cd $EE_BASE_DIR/web-client
rm -rf out
pnpm run build
```
-THEN-
```bash
cd ..
./gradlew assemble -Pprofile=local
```
-OR-
```bash
./gradlew :server:bootWar -Pprofile=local
./gradlew :web-client:war
```
#### Deploy
```bash
cd $EE_BASE_DIR
cp server/build/libs/ee-server.war $EE_DEPLOYMENT_DIR
cp web-client/build/libs/ee-web-client.war $EE_DEPLOYMENT_DIR
```

### Production Deployment
#### Build
For a full production build for deployment to the production server:
- edit your custom [.env.production.local](web-client/.env.production.local) file to enable the required configuration before building the web-client project.
- prepare a suitable [application-production.properties](server/src/main/resources/config/application-production.properties) file based on [application-example.properties](server/src/main/resources/config/application-example.properties)
```bash
cd $EE_BASE_DIR/web-client
rm -rf out
pnpm run build
```
-THEN-
```bash
cd ..
./gradlew assemble -Pprofile=production
```
-OR-
```bash
./gradlew :server:bootWar -Pprofile=production
./gradlew :web-client:war
```

#### Deploy
```bash
cd $EE_BASE_DIR
scp server/build/libs/ee-server.war $EE_DEPLOYMENT_URI
scp web-client/build/libs/ee-web-client.war $EE_DEPLOYMENT_URI
```