rd-commondata-api

Commondata Api


## Purpose

Provides Data to clients, implemented as a Java/SpringBoot application.

Please refer to the confluence for more information.
https://tools.hmcts.net/confluence/display/RTRD/Common+Reference+Data

### Prerequisites

To run the project you will need to have the following installed:

* Java 21
* Docker

For information about the software versions used to build this API and a complete list of it's dependencies see build.gradle

While not essential, it is highly recommended to use the pre-push git hook included in this repository to ensure that all tests are passing. This can be done by running the following command:
`$ git config core.hooksPath .githooks`

### Running the application
### Environment Vars

If running locally for development or testing you will need to set some environment variables within the application.yaml.
Some of these values can be found in the 'secrets' section of the Azure rd-aat key vault.

- Uncomment line 22 if running a local database in docker.
- line 50 - add the COMMONDATA_API_S2S_SECRET value.
- line 53 - Replace ${POSTGRES_CONNECTION_OPTIONS:} with ?currentSchema=dbcommondata
- line 98 - add the COMMONDATA_API_S2S_SECRET value.
- line 110 - add the LD_SDK_KEY value.

### Changes to V1_1_init_tables.sql
We will be using the schema name 'dbcommondata' instead of the default of 'public' which is usually used, so you will need to make the following updates to 'src/main/resources/db/migration/V1_1__init_tables.sql'. Easiest to do a find on the file and search for 'public.' and you should find the two entries which need to be updated to 'dbcommondata.':

Update (around line 12):
-INSERT INTO public.flag_details (id,flag_code,value_en,value_cy,category_id) VALUES
+INSERT INTO dbcommondata.flag_details (id,flag_code,value_en,value_cy,category_id) VALUES

Update (around line 1780):
- INSERT INTO public.panel_member_type (categorykey,serviceid,"key",value_en,value_cy,hinttext_en,hinttext_cy,lov_order,parentcategory,parentkey,active) VALUES
+ INSERT INTO dbcommondata.panel_member_type (categorykey,serviceid,"key",value_en,value_cy,hinttext_en,hinttext_cy,lov_order,parentcategory,parentkey,active) VALUES

Please Make sure you are connected to the VPN before running application
(https://portal.platform.hmcts.net/vdesk/webtop.eui?webtop=/Common/webtop_full&webtop_type=webtop_full)

To run the API quickly use the docker helper script as follows:

```
./bin/run-in-docker.sh install
```
or

```
docker-compose up
```


After, you can start the application from the current source files using Gradle as follows:

```
./gradlew clean bootRun
```

If required, to run with a low memory consumption, the following can be used:

```
./gradlew --no-daemon assemble && java -Xmx384m -jar build/libs/rd-commondata-api.jar
```

### Using the application

To understand if the application is working, you can call it's health endpoint:

```
curl http://localhost:4550/health
```

If the API is running, you should see this response:

```
{"status":"UP"}
```


If the Application is running, you can see API's in swagger :

```
http://localhost:4550/swagger-ui.html
```


### DB Initialisation˙

The application uses a Postgres database which can be run through a docker container on its own if required.

this

The application should automatically apply any database migrations using flyway.

### Running integration tests:


You can run the *integration tests* as follows:

```
./gradlew integration
```

### Running functional tests:

If the API is running (either inside a Docker container or via `gradle bootRun`) you can run the *functional tests* as follows:

```
./gradlew functional
```

If you want to run a specific scenario use this command:

```
./gradlew functional --tests <TestClassName> --info -Dscenario=<Scenario>
```

### Running smoke tests:

If the API is running (either inside a Docker container or via `gradle bootRun`) you can run the *smoke tests* as follows:

```
./gradlew smoke
```

### Running mutation tests tests:

If you have some time to spare, you can run the *mutation tests* as follows:

```
./gradlew pitest
```

As the project grows, these tests will take longer and longer to execute but are useful indicators of the quality of the test suite.

More information about mutation testing can be found here:
http://pitest.org/

### Testing in Postman

To test in Postman the easiest way is to start this service using the ./bin/run-in-docker.sh script.  The in postman paste the following script:

```
pm.sendRequest('http://127.0.0.1:4550/token', function (err, res) {
    if (err) {
        console.log(err);
    } else {
        pm.environment.set("token", res.text());
    }
});
```
into the pre-script window.  Also add a header as follows:

```
ServiceAuthorization: Bearer {{token}}
```

Authorization :  Bearer copy IDAM access token

### Contract testing with pact

To publish against remote broker:
`./gradlew pactPublish`

Turn on VPN and verify on url `https://pact-broker.platform.hmcts.net/`
The pact contract(s) should be published


To publish against local broker:
Uncomment out the line found in the build.gradle:
`pactBrokerUrl = 'http://localhost:9292'`
comment out the real broker

Start the docker container from the root dir run
`docker-compose -f broker-compose.yml up`

Publish via the gradle command
`./gradlew pactPublish`

Once Verify on url `http://localhost:9292/`
The pact contract(s) should be published

Remember to return the localhost back to the remote broker

for more information, Please refer to the confluence on how to run and publish PACT tests.
https://tools.hmcts.net/confluence/display/RTRD/PACT+testing

