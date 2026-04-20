# Building & Deploying

## Production Build
$ cd BASEDIR/web-client
$ rm -rf out
$ pnpm run build
$ cd ..
-THEN-
$ ./gradlew assemble -Pprofile=production
-OR-
$ ./gradlew :common:jar
$ ./gradlew :client:bootJar
$ ./gradlew :server:bootWar -Pprofile=production
$ ./gradlew :web-client:war

## Deploying
$ cd BASEDIR
$ cp server/build/lib/ee-server.war DEPLOYMENT_DIR
$ cp web-client/build/lib/ee-web-client.war DEPLOYMENT_DIR
