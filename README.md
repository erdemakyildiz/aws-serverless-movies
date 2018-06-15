Serverless Movie Service
============================

Description
-----------
AWS Server-less demo application / movie database.

![](docs/architecture.png)


Build
-----
Run `gradlew build`

Install
-------
```
cd terraform
terraform apply
```

Intellij setup
--------------
* Checkout the source code and import the project from existing sources; select _build.gradle_ when importing.
* In Intellij settings, search for _annotation processors_ and check _Enable annotation processing_ (required for Lombok)

Testing
-------
The tests start a local Dynamo instance that requires native libraries. These libraries are downloaded by the
 gradle build in _build/test-libs_.
 
When running unit tests from within Intellij, you will have to add the `-Djava.library.path=build/test-libs` JVM option.
This is best done on the _JUnit_ run configuration in _Defaults_.

The local Dynamo instance also requires (dummy) credentials. Either create a file in your user home dir
 _${user.home}/.aws/credentials_ with following content:
 
 ```
[default]
	aws_access_key_id ="id"
	aws_secret_access_key ="key"
```

or set the _AWS_ACCESS_KEY_ID_ and _AWS_SECRET_ACCESS_KEY_ environment variables with a non-empty value.

Run local
---------
Prerequisites:
* docker
* python 2.x
* aws cli: `pip install awscli`
* sam cli: `pip install aws-sam-cli`

Prepare local dynamodb docker:
* create a docker network so that sam can connect to the dynamodb docker: `docker network create lambda-local`
* create dynamodb docker: `docker run -d -v "$PWD/build":/dynamodb_local_db -p 8000:8000 --network lambda-local --name dynamodb cnadiminti/dynamodb-local`
* start dynamodb docker: `docker start dynamodb`
* build the lambda: `gradlew build`
* start sam local: `sam local start-api --docker-network lambda-local`

Sam local should output the API URLs at http://127.0.0.1:3000/

Debug local
-----------
Start sam local with a debug port: `sam local start-api --debug-port 5858`
You have to wait for the lambda JVM to start, which will only happen when you make a API request. Then you can attach
a debugger (with breakpoint set) to the lambda.
