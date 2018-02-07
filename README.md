Carrier Pickup Notification
============================

Description
-----------
AWS Server-less application that notifies carriers when a trailer can be picked up earlier.

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
