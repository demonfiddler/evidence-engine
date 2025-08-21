# Integrated Development Environment

The authors use Visual Studio Code with a selection of appropriate extensions:
- [Apollo GraphQL](https://marketplace.visualstudio.com/items?itemName=apollographql.vscode-apollo) for working with the Apollo GraphQL Client and GraphQL queries
- [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) (Microsoft) for working with Java server code
- [Java Platform Extension for Visual Studio Code](https://marketplace.visualstudio.com/items?itemName=Oracle.oracle-java)
- [GitHub Pull Requests](https://marketplace.visualstudio.com/items?itemName=GitHub.vscode-pull-request-github) for handling Git pull requests
- [GitLens - Git supercharged](https://marketplace.visualstudio.com/items?itemName=eamodio.gitlens) for Git insights
- [Gradle for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-gradle) for Java development
- [GraphQL: Inline Operation](https://marketplace.visualstudio.com/items?itemName=GraphQL.vscode-graphql-execution) for GraphQL execution
- [GraphQL: Language Feature](https://marketplace.visualstudio.com/items?itemName=GraphQL.vscode-graphql) for GraphQL schema & query development
- [GraphQL: Syntax Highlighting](https://marketplace.visualstudio.com/items?itemName=GraphQL.vscode-graphql-syntax)
- [Language Support for Java](https://marketplace.visualstudio.com/items?itemName=redhat.java) (RedHat) for Java development
- [Project Manager for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-dependency)
- [Spring Boot Extension Pack](https://marketplace.visualstudio.com/items?itemName=vmware.vscode-boot-dev-pack) for Spring Boot development
- [Spring Boot Dashboard](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-spring-boot-dashboard) for Spring Boot monitoring
- [Spring Boot Tools](https://marketplace.visualstudio.com/items?itemName=vmware.vscode-spring-boot)
- [Tailwind CSS IntelliSense](https://marketplace.visualstudio.com/items?itemName=bradlc.vscode-tailwindcss) for TailwindCSS development
- [Test Runner for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-test) for Java testing

# Regenerating the GraphQL Code

*Caveat*: The code produced by [graphql-java-generator](https://github.com/graphql-java-generator) suffers from [various shortcomings](graphql-java-generator-comments.txt), and as a consequence it was eventually deemed necessary to modify and source-control the generated code in order to rectify these shortcomings and, for example, to take advantage of idiomatic Java inheritance to eliminate vast tranches of identical code. Consequently, when generating or regenerating code, it is necessary manually to merge the generated code with the source-controlled code in order to apply/preserve the patterns therein.

## Generate Client Code

- Edit the client [build.gradle](/client/build.gradle) file and uncomment:
    - ```id "com.graphql-java-generator.graphql-gradle-plugin3"``` at the top of the file
    - ```apply from: 'codegen.gradle'``` at the bottom of the file.
- Also, in the ```tasks.named('test')``` section comment out:
    - ```dependsOn project(':server').tasks.named('bootIntegrationTestServerBG')```
    - ```finalizedBy project(':server').tasks.named('terminateIntegrationTestServer')```
- Execute ```./gradlew client:generateClientCode```.
- Revert the changes using Undo (Ctrl+Z), Save (Ctrl+S).
- Find the generated code at ```client/build/generated/sources/graphqlClient/io/github/demonfiddler/ee/client```.

## Generate Server Code

- Edit the server [build.gradle](/server/build.gradle) file and uncomment:
    - ```id "com.graphql-java-generator.graphql-gradle-plugin3"``` at the top of the file
    - ```apply from: 'codegen.gradle'``` at the bottom of the file
- Execute ```./gradlew server:generateServerCode```
- Revert the changes using Undo (Ctrl+Z), Save (Ctrl+S).
- Find the generated code at ```server/build/generated/sources/graphqlGradlePlugin/io/github/demonfiddler/ee/server```

# Modifying the Schema

## Quick Checklist

The following tables summarise what needs to be done and where, in order to add a new type, property, query or mutation to the system. The uppercase placeholders TYPE, PROPERTY/ALL, QUERY and MUTATION should be replaced by the actual, appropriately-cased type, property, query or mutation name(s), following the Java/JavaBean conventions for naming types, fields and property accessor methods. Existing workspace resources are hyperlinked for convenience. See also the corresponding spreadsheet, with a live 'Checklist' page that can be copied and edited for each additional TYPE/PROPERTY/QUERY/MUTATION. ALL signifies 'for all properties'.

### Table 1: Locations of Type Files

|Project|Resource|Generated|Source|Notes|
|-------|--------|---------|------|-----|
|client|```TYPE.java```|```client/build/generated/sources/graphqlClient/io/github/demonfiddler/ee/client/```|```client/src/main/java/io/github/demonfiddler/ee/client/```||
||```TYPEInput.java```|```client/build/generated/sources/graphqlClient/io/github/demonfiddler/ee/client/```|```client/src/main/java/io/github/demonfiddler/ee/client/```||
||```TYPEPage.java```|```client/build/generated/sources/graphqlClient/io/github/demonfiddler/ee/client/```|```client/src/main/java/io/github/demonfiddler/ee/client/```||
|web-client|```TYPE.ts```|n/a|```web-client/app/model/```|```TYPE``` as processed by Apollo GraphQL|
||```TYPE.ts```|n/a|```web-client/app/ui/validators/```|```TYPE``` as edited/validated in a form|
||```TYPEInput```|n/a|[```web-client/app/model/schema.ts```](web-client/app/model/schema.ts)||
||```page.tsx```|n/a|```web-client/app/(navigable)[/(linkable)]/TYPEs/```|If the ```TYPE``` has a corresponding app page|
|server|```TYPE.java```|```server/build/generated/sources/graphqlGradlePlugin/io/github/demonfiddler/ee/server/```|```server/src/main/java/io/github/demonfiddler/ee/server/model/```||
||```TYPEInput.java```|```server/build/generated/sources/graphqlGradlePlugin/io/github/demonfiddler/ee/server/```|```server/src/main/java/io/github/demonfiddler/ee/server/model/```||
||```TYPEPage.java```|```server/build/generated/sources/graphqlGradlePlugin/io/github/demonfiddler/ee/server/```|```server/src/main/java/io/github/demonfiddler/ee/server/model/```||
||```TYPEController.java```|```server/build/generated/sources/graphqlGradlePlugin/io/github/demonfiddler/ee/server/util/```|```server/src/main/java/io/github/demonfiddler/ee/server/controller/```||
||```DataFetchersDelegateTYPE.java```|```server/build/generated/sources/graphqlGradlePlugin/io/github/demonfiddler/ee/server/util/```|```server/src/main/java/io/github/demonfiddler/ee/server/datafetcher/```||
||```DataFetchersDelegateTYPEImpl.java```|n/a|```server/src/main/java/io/github/demonfiddler/ee/server/datafetcher/impl/```|Manually coded|

### Table 2: Adding a New Type, Property, Query or Mutation

Resource & Member column entry key:
- ~IDENTIFIER: modify existing file/class/method
- +IDENTIFIER: add new file/class/method (possibly to an existing file/class)

|Add|Project|Resource|Member|☑|Notes|
|---|-------|--------|------|-|-----|
|[Type](#adding-a-type) [^1]|client|~[```typeMapping.csv```](client/src/main/resources/typeMapping.csv)|+```TYPE```||Maps GraphQL type name to Java fully qualified class name.|
||||+```TYPEInput```|||
||||+```TYPEPage```|||
|||~[```CustomJacksonDeserializers```](client/src/main/java/io/github/demonfiddler/ee/client/util/CustomJacksonDeserializers.java)|+```ListTYPE```|||
|||+```TYPE```|+ALL||Extend [```AbstractBaseEntity```](client/src/main/java/io/github/demonfiddler/ee/client/AbstractBaseEntity.java) / [```AbstractTrackedEntity```](client/src/main/java/io/github/demonfiddler/ee/client/AbstractTrackedEntity.java) / [```AbstractLinkableEntity```](client/src/main/java/io/github/demonfiddler/ee/client/AbstractLinkableEntity.java).|
||||+```equals()```  [^2]|||
||||+```hashCode()``` [^2]|||
|||+```TYPE.Builder```|+ALL||Extend [```AbstractBaseEntity.Builder```](client/src/main/java/io/github/demonfiddler/ee/client/AbstractBaseEntity.java) / [```AbstractTrackedEntity.Builder```](client/src/main/java/io/github/demonfiddler/ee/client/AbstractTrackedEntity.java) / [```AbstractLinkableEntity.Builder```](client/src/main/java/io/github/demonfiddler/ee/client/AbstractLinkableEntity.java). One ```Builder.PROPERTY``` field per ```TYPE``` property.|
||||+```withALL()```||One ```Builder.withPROPERTY()``` method per ```TYPE``` property.|
||||+```build()```||One ```_object.setPROPERTY()``` call per ```TYPE``` property.|
|||+```TYPEInput```|+ALL||Only if ```TYPE``` is updateable through GraphQL API: extend [```AbstractBaseEntityInput```](client/src/main/java/io/github/demonfiddler/ee/client/AbstractBaseEntityInput.java).|
|||+```TYPEInput.Builder```|+ALL||Extend [```AbstractBaseEntityInput.Builder```](client/src/main/java/io/github/demonfiddler/ee/client/AbstractBaseEntityInput.java). One ```Builder.PROPERTY``` field per ```TYPEInput``` property.|
||||+```withALL()```||One ```Builder.withPROPERTY()``` method per ```TYPEInput``` property.|
||||+```build()```||One ```_object.setPROPERTY()``` call per ```TYPEInput``` property.|
|||+```TYPEPage```|+ALL||Extend [```AbstractPage<TYPE>```](client/src/main/java/io/github/demonfiddler/ee/client/AbstractPage.java).|
|||+```TYPEPage.Builder```|+```build()```||Extend [```AbstractPage.Builder<Builder, TYPEPage, TYPE>```](client/src/main/java/io/github/demonfiddler/ee/client/AbstractPage.java) and ```return build(new TYPEPage());```.|
||web-client|~[```apolloClient```](web-client/lib/graphql-utils.ts)|~```cache.possibleTypes```||Add ```TYPE``` to ```IBaseEntity```, ```ITrackedEntity```, ```ILinkableEntity``` as appropriate and ```TYPEPage``` to ```IPage```.|
|||+```TYPE```|+ALL||The Apollo GraphQL ```TYPE```.|
|||~[```TYPEInput```](web-client/app/model/schema.ts)|+ALL||Only if ```TYPE``` is updateable through GraphQL API: the corresponding GraphQL ```TYPEInput```.|
|||+```TYPE```|+ALL||The form validation ```TYPE```.|
|||+```TYPEs```|+ALL||If ```TYPE``` has a corresponding app page.|
||server|~[```schema-mariadb.sql```](server/src/main/resources/db/schema-mariadb.sql)|+```TYPE```||Also implement ```TYPEs```, ```TYPEById``` [queries](#adding-a-query) and ```createTYPE```, ```updateTYPE``` and ```deleteTYPE``` [mutations](#adding-a-mutation).|
|||[~```schema-h2.sql```](server/src/main/resources/db/schema-h2.sql)|+```TYPE```|||
|||[~```schema.graphqls```](server/src/main/resources/graphql/schema.graphqls)|+```TYPE```|||
|||[~```GraphQLPluginAutoConfiguration```](server/src/main/java/io/github/demonfiddler/ee/server/GraphQLPluginAutoConfiguration.java)|+```TYPEController()```|||
|||+```TYPE```|+ALL||Extend [```AbstractBaseEntity```](server/src/main/java/io/github/demonfiddler/ee/server/model/AbstractBaseEntity.java) (DOESN'T EXIST!) / [```AbstractTrackedEntity```](server/src/main/java/io/github/demonfiddler/ee/server/model/AbstractTrackedEntity.java) / [```AbstractLinkableEntity```](server/src/main/java/io/github/demonfiddler/ee/server/model/AbstractLinkableEntity.java).|
||||+```equals()``` [^2]|||
||||+```hashCode()``` [^2]|||
|||+```TYPE.Builder```|+ALL||Extend [```AbstractBaseEntity.Builder```](server/src/main/java/io/github/demonfiddler/ee/server/model/AbstractBaseEntity.java) (DOESN'T EXIST!) / [```AbstractTrackedEntity.Builder```](server/src/main/java/io/github/demonfiddler/ee/server/model/AbstractTrackedEntity.java) / [```AbstractLinkableEntity.Builder```](server/src/main/java/io/github/demonfiddler/ee/server/model/AbstractLinkableEntity.java). One Builder.PROPERTY field per TYPE property.|
||||+```withALL()```||One Builder.withPROPERTY() method per TYPE property.|
||||+```build()```||One _object.setPROPERTY() call per TYPE property.|
|||+```TYPEInput```|+ALL||Extend [```AbstractBaseEntityInput```](server/src/main/java/io/github/demonfiddler/ee/server/model/AbstractBaseEntityInput.java).|
|||+```TYPEInput.Builder```|+ALL||Extend [```AbstractBaseEntityInput.Builder```](server/src/main/java/io/github/demonfiddler/ee/server/model/AbstractBaseEntityInput.java). One ```Builder.PROPERTY``` field per ```TYPEInput``` property.|
||||+```withALL()```||One ```Builder.withPROPERTY()``` method per ```TYPEInput``` property.|
||||+```build()```||One ```_object.setPROPERTY()``` call per ```TYPEInput``` property.|
|||+```TYPEPage```|||Extend [```AbstractPage<TYPE>```](server/src/main/java/io/github/demonfiddler/ee/server/model/AbstractPage.java).|
|||+```TYPEPage.Builder```|+```build()```||Extend [```AbstractPage.Builder<Builder, TYPEPage, TYPE>```](server/src/main/java/io/github/demonfiddler/ee/server/model/AbstractPage.java) and ```return build(new TYPEPage());```.|
|||+```TYPEController```||||
|||+```TYPEPageController```||||
|||+```DataFetchersDelegateTYPE```|||Extend [```DataFetchersDelegateIBaseEntity<TYPE>```](server/src/main/java/io/github/demonfiddler/ee/server/datafetcher/DataFetchersDelegateIBaseEntity.java) / [```DataFetchersDelegateITrackedEntity<TYPE>```](server/src/main/java/io/github/demonfiddler/ee/server/datafetcher/DataFetchersDelegateITrackedEntity.java) / [```DataFetchersDelegateILinkableEntity<TYPE>```](server/src/main/java/io/github/demonfiddler/ee/server/datafetcher/DataFetchersDelegateILinkableEntity.java).|
|||+```DataFetchersDelegateTYPEImpl```|||Extend [```DataFetchersDelegateIBaseEntityImpl<TYPE>```](server/src/main/java/io/github/demonfiddler/ee/server/datafetcher/impl/DataFetchersDelegateIBaseEntityImpl.java) / [```DataFetchersDelegateITrackedEntityImpl<TYPE>```](server/src/main/java/io/github/demonfiddler/ee/server/datafetcher/impl/DataFetchersDelegateITrackedEntityImpl.java) / [```DataFetchersDelegateILinkableEntityImpl<TYPE>```](server/src/main/java/io/github/demonfiddler/ee/server/datafetcher/impl/DataFetchersDelegateILinkableEntityImpl.java).|
|||+```DataFetchersDelegateTYPEPage```|||Extend [```DataFetchersDelegatePage<TYPEPage, TYPE>```](server/src/main/java/io/github/demonfiddler/ee/server/datafetcher/DataFetchersDelegatePage.java).|
|||+```DataFetchersDelegateTYPEPageImpl```|||Extend [```DataFetchersDelegateIPageImpl```](server/src/main/java/io/github/demonfiddler/ee/server/datafetcher/impl/DataFetchersDelegateIPageImpl.java).|
|||+```TYPERepository```|||Extend ```JpaRepository<TYPE, Long>```, ```CustomTYPERepository```.|
|[Property](#adding-a-property)|client|~```TYPE```|+```PROPERTY```|||
||||+```getPROPERTY()```|||
||||+```setPROPERTY()```|||
||||~```equals()``` [^2]|||
||||~```hashCode()``` [^2]|||
||||~```toString()``` [^2]|||
|||~```TYPE.Builder```|+```PROPERTY```|||
||||+```withPROPERTY()```|||
||||~```build()```||Call ```_object.setPROPERTY(this.PROPERTY)```.|
|||~```TYPEInput```|+```PROPERTY```||Only if ```PROPERTY``` is included in ```TYPEInput```.|
||||+```getPROPERTY()```|||
||||+```setPROPERTY()```|||
||||~```toString()``` [^2]|||
|||~```TYPEInput.Builder```|+```PROPERTY```||Only if ```PROPERTY``` is included in ```TYPEInput```.|
||||+```withPROPERTY()```|||
||||~```build()```||Call ```_object.setPROPERTY(this.PROPERTY)```.|
||web-client|~```TYPE```|+```PROPERTY```||The Apollo GraphQL ```TYPE```.```PROPERTY```.|
|||~```TYPE```|+```PROPERTY```||The form validation ```TYPE```.```PROPERTY```.|
|||~[```TYPEInput```](web-client/app/model/schema.ts)|+```PROPERTY```||Only if ```TYPE``` is updateable through GraphQL API: the corresponding GraphQL ```TYPEInput```.|
|||~```TYPEs```|+```PROPERTY```||If ```TYPE``` has a corresponding app page and ```PROPERTY``` appears on that page.|
||server|~[```schema-mariadb.sql```](server/src/main/resources/db/schema-mariadb.sql)|+```TYPE.PROPERTY```|||
|||~[```schema-h2.sql```](server/src/main/resources/db/schema-h2.sql)|+```TYPE.PROPERTY```|||
|||~[```schema.graphqls```](server/src/main/resources/graphql/schema.graphqls)|+```TYPE.PROPERTY```|||
|||~```TYPE```|+```PROPERTY```|||
||||+```getPROPERTY()```|||
||||+```setPROPERTY()```|||
||||+```equals()``` [^2]|||
||||+```hashCode()``` [^2]|||
||||+```toString()``` [^2]|||
|||~```TYPE.Builder```|+```PROPERTY```|||
||||+```withPROPERTY()```|||
||||+```build()```||Call ```_object.setPROPERTY(this.PROPERTY)```.|
|||~```TYPEInput```|+```PROPERTY```||Only if ```PROPERTY``` is included in ```TYPEInput```.|
||||+```getPROPERTY()```|||
||||+```setPROPERTY()```|||
||||~```toString()``` [^2]|||
|||~```TYPEInput.Builder```|+```PROPERTY```||Only if ```PROPERTY``` is included in ```TYPEInput```.|
||||+```withPROPERTY()```|||
||||~```build()```||Call ```_object.setPROPERTY(this.PROPERTY)```.|
|||~```TYPEController```|+```PROPERTY()```|||
|||~```DataFetchersDelegateTYPE```|+```PROPERTY()```||Only for structured or parameterised properties.|
|||~```DataFetchersDelegateTYPEImpl```|+```PROPERTY()```||Only for structured or parameterised properties.|
|||~[```DataFetchersDelegateMutationImpl```](server/src/main/java/io/github/demonfiddler/ee/server/datafetcher/impl/DataFetchersDelegateMutationImpl.java)|~```createTYPE()```||Handle new property.|
||||~```updateTYPE()```||Handle new property.|
|[Query](#adding-a-query)|client|~[```Query```](client/src/main/java/io/github/demonfiddler/ee/client/Query.java)|+```QUERY```|||
||||+```setQUERY()```|||
||||+```getQUERY()```|||
||||~```toString()```|||
|||~[```Query.Builder```](client/src/main/java/io/github/demonfiddler/ee/client/Query.java)|+```QUERY```|||
||||+```withQUERY()```|||
||||~```build()```||Call ```_object.setQUERY(this.QUERY)```.|
|||~[```QueryExecutor```](client/src/main/java/io/github/demonfiddler/ee/client/util/QueryExecutor.java)|+```QUERY()```||2 overloads.|
||||+```QUERYWithBindValues()```||2 overloads.|
||||+```getQUERYResponseBuilder()```|||
||||+```getQUERYGraphQLRequest()```|||
|||~[```QueryReactiveExecutor```](client/src/main/java/io/github/demonfiddler/ee/client/util/QueryReactiveExecutor.java)|+```QUERY()```||2 overloads.|
||||+```QUERYWithBindValues()```||2 overloads.|
||||+```getQUERYResponseBuilder()```|||
||||+```getQUERYGraphQLRequest()```|||
||server|~[```schema.graphqls```](server/src/main/resources/graphql/schema.graphqls)|+```QUERY```|||
|||~[```Query```](server/src/main/java/io/github/demonfiddler/ee/server/model/Query.java)|+```QUERY```|||
||||+```getQUERY()```|||
||||+```setQUERY()```|||
||||~```toString()```|||
|||~[```Query.Builder```](server/src/main/java/io/github/demonfiddler/ee/server/model/Query.java)|+```QUERY```|||
||||+```withQUERY()```|||
||||~```build()```||Call ```_object.setQUERY(this.QUERY)```.|
|||~[```QueryController```](server/src/main/java/io/github/demonfiddler/ee/server/controller/QueryController.java)|+```QUERY()```|||
|||~[```DataFetchersDelegateQuery```](server/src/main/java/io/github/demonfiddler/ee/server/datafetcher/DataFetchersDelegateQuery.java)|+```QUERY()```|||
|||~[```DataFetchersDelegateQueryImpl```](server/src/main/java/io/github/demonfiddler/ee/server/datafetcher/impl/DataFetchersDelegateQueryImpl.java)|+```QUERY()```|||
|[Mutation](#adding-a-mutation)|client|~[```Mutation```](client/src/main/java/io/github/demonfiddler/ee/client/Mutation.java)|+```MUTATION```|||
||||+```getMUTATION()```|||
||||+```setMUTATION()```|||
||||~```toString()```|||
|||~[```Mutation.Builder```](client/src/main/java/io/github/demonfiddler/ee/client/Mutation.java)|+```MUTATION```|||
||||+```withMUTATION()```|||
||||~```build()```||Call ```_object.setMUTATION(this.MUTATION)```.|
|||~[```MutationExecutor```](client/src/main/java/io/github/demonfiddler/ee/client/util/MutationExecutor.java)|+```MUTATION()```||2 overloads.|
||||+```MUTATIONWithBindValues()```||2 overloads.|
||||+```getMUTATIONResponseBuilder()```|||
||||+```getMUTATIONGraphQLRequest()```|||
|||~[```MutationReactiveExecutor```](client/src/main/java/io/github/demonfiddler/ee/client/util/MutationReactiveExecutor.java)|+```MUTATION()```||2 overloads.|
||||+```MUTATIONWithBindValues()```||2 overloads.|
||||+```getMUTATIONResponseBuilder()```|||
||||+```getMUTATIONGraphQLRequest()```|||
||server|~[```schema.graphqls```](server/src/main/resources/graphql/schema.graphqls)|+```MUTATION```|||
|||~[```Mutation```](server/src/main/java/io/github/demonfiddler/ee/server/model/Mutation.java)|+```MUTATION```|||
||||+```getMUTATION()```|||
||||+```setMUTATION()```|||
||||~```toString()```|||
|||~[```Mutation.Builder```](server/src/main/java/io/github/demonfiddler/ee/server/model/Mutation.java)|+```MUTATION```|||
||||+```withMUTATION()```|||
||||~```build()```||Call ```_object.setMUTATION(this.MUTATION)```.|
|||~[```MutationController```](server/src/main/java/io/github/demonfiddler/ee/server/controller/MutationController.java)|+```MUTATION()```|||
|||~[```DataFetchersDelegateMutation```](server/src/main/java/io/github/demonfiddler/ee/server/datafetcher/DataFetchersDelegateMutation.java)|+```MUTATION()```|||
|||~[```DataFetchersDelegateMutationImpl```](server/src/main/java/io/github/demonfiddler/ee/server/datafetcher/impl/DataFetchersDelegateMutationImpl.java)|+```MUTATION()```|||

[^1] Also implement ```TYPEs```, ```TYPEById``` [queries](#adding-a-query) and ```createTYPE```, ```updateTYPE``` and ```deleteTYPE``` [mutations](#adding-a-mutation)

[^2] Avoid infinite mutual recursion with structured fields

## Adding a Type

- Add a corresponding database table to the ```schema-mariadb.sql``` and ```schema-h2.sql``` DDL code in ```server/src/main/resources/db```
- Add the ```TYPE``` and associated ```TYPEInput``` and ```TYPEPage``` definitions to ```server/src/main/resources/graphql/schema.graphqls```.
- Add ```TYPEs``` and ```TYPEById``` operations to the ```Query``` type, and ```createTYPE``` and ```updateTYPE``` and ```deleteTYPE``` operations to ```server/src/main/resources/graphql/schema.graphqls``` (see [Adding a Query](#adding-a-query) and [Adding a Mutation](#adding-a-mutation)).
- Regenerate [client](#generate-client-code) and [server](#generate-server-code) code
- Copy the generated client-side ```TYPE```, ```TYPEInput```, ```TYPEPage``` Java files:
    - from:	```client/build/generated/sources/graphqlClient/io/github/demonfiddler/ee/client```
    - to:	```client/src/main/java/io/github/demonfiddler/ee/client```
- Copy the corresponding new fields and methods from the generated client-side ```client/build/generated/sources/graphqlClient/io/github/demonfiddler/ee/client/util/CustomJacksonDeserializers``` class to the existing one in ```client/src/main/java/io/github/demonfiddler/ee/client/util```.
- Copy the generated server-side ```TYPE```, ```TYPEInput```, ```TYPEPage```, ```util/TYPEController```, ```util/DataFetchersDelegateTYPE``` Java files:
    - from:	```server/build/generated/sources/graphqlGradlePlugin/io/github/demonfiddler/ee/server```
    - to:	```server/src/main/java/io/github/demonfiddler/ee/server/```
        -   ```model/```
        -   ```controller/```
        -   ```datafetcher/```
- Apply the correct design patterns and coding style to the type files, e.g.
    - ```TYPE``` should extend ```AbstractBaseEntity```, ```AbstractTrackedEntity``` or ```AbstractLinkableEntity``` as appropriate; remove inherited methods.
    - ```TYPE.Builder``` should extend ```AbstractBaseEntity.Builder```, ```AbstractTrackedEntity.Builder``` or ```AbstractLinkableEntity.Builder``` respectively; remove generated members where already inherited.
    - ```DataFetchersDelegateTYPE``` should extend ```DataFetchersDelegateITrackedEntity<TYPE>``` or ```DataFetchersDelegateIBaseEntity<TYPE>``` as appropriate, removing generated methods that are already inherited.
    - use the IDE to generate ```equals()``` and ```hashCode()``` methods.
    - import ALL fully qualified type references and unqualify ALL ```java.lang.*``` type references.
- Provide a ```server/src/main/java/io/github/demonfiddler/ee/server/repository/TYPERepository``` JPA repository interface, supplemented if necessary by a colocated ```CustomTYPERepository``` interface extending ```CustomRepository<TYPE, F>``` and a ```CustomTYPERepositoryImpl``` implementation extending ```AbstractCustomRepositoryImpl```, ```CustomTrackedEntityRepositoryImpl<TYPE>``` or ```CustomLinkableEntityRepositoryImpl<TYPE>``` as appropriate.
- Provide a ```server/src/main/java/io/github/demonfiddler/ee/server/datafetcher/impl/DataFetcherTYPEImpl``` implementation that uses the new repository.
- Declare a ```TYPEController``` bean in ```server/src/main/java/io/github/demonfiddler/ee/server/GraphQLPluginAutoConfiguration```.
- Add a type mapping to ```client/src/main/resources/typeMapping.csv```

## Adding a Property

Using the code locations given in the previous section:
- Add a corresponding database column to the existing ```type``` table in ```schema-mariadb.sql``` and ```schema-h2.sql```.
- Add the property definition to the existing ```TYPE``` in ```schema.graphqls```.
- Regenerate [client](#generate-client-code) and [server](#generate-server-code) code
- Copy the new fields and accessor methods from the generated client and server ```TYPE``` code to the respective existing classes.
- If generated, copy the new server-side ```getPROPERTY()``` method to the existing ```DataFetchersDelegateTYPE``` interface and implement it in the existing ```DataFetchersDelegateTYPEImpl``` class.
- Apply the correct design patterns and coding style to the copied members.
- Include the property in the client- and server-side ```toString()```, ```equals()``` and ```hashCode()``` methods and the ```TYPE.Builder``` classes.

## Adding a Query

- Add the operation to the existing ```Query``` type in ```schema.graphqls```.
- Regenerate client and server code.
- Copy the corresponding new fields and methods from the generated client-side ```client/build/generated/sources/graphqlClient/io/github/demonfiddler/ee/client/Query``` to the existing ```client/src/main/java/io/github/demonfiddler/ee/client/Query``` class.
- Copy the corresponding new fields and methods from the generated client-side ```QueryExecutor``` and ```QueryReactiveExecutor``` classes in ```client/build/generated/sources/graphqlClient/io/github/demonfiddler/ee/client/util/``` to the existing classes in ```client/src/main/java/io/github/demonfiddler/ee/client/util```.
- Copy the corresponding new fields and methods from the generated server-side ```server/build/generated/sources/graphqlGradlePlugin/io/github/demonfiddler/ee/server/Query``` to the existing ```server/src/main/java/io/github/demonfiddler/ee/server/model/Query``` class.
- Copy the corresponding new fields and methods from the generated server-side ```server/build/generated/sources/graphqlGradlePlugin/io/github/demonfiddler/ee/server/util/QueryController``` to the existing ```server/src/main/java/io/github/demonfiddler/ee/server/controller/QueryController``` class.
- Apply the correct design patterns and coding style to the copied members.

## Adding a Mutation

- Add the operation to the existing ```Mutation``` type in ```schema.graphqls```.
- Regenerate client and server code.
- Copy the corresponding new fields and methods from the generated client-side ```client/build/generated/sources/graphqlClient/io/github/demonfiddler/ee/client/Mutation``` to the existing ```client/src/main/java/io/github/demonfiddler/ee/client/Mutation``` class.
- Copy the corresponding new fields and methods from the generated client-side ```MutationExecutor``` and ```MutationReactiveExecutor``` classes in ```client/build/generated/sources/graphqlClient/io/github/demonfiddler/ee/client/util/``` to the existing classes in ```client/src/main/java/io/github/demonfiddler/ee/client/util```.
- Copy the corresponding new fields and methods from the generated server-side ```server/build/generated/sources/graphqlGradlePlugin/io/github/demonfiddler/ee/server/util/MutationController``` to the existing ```server/src/main/java/io/github/demonfiddler/ee/server/controller/MutationController``` class.
- Apply the correct design patterns and coding style to the copied members.
