korbit-scala-sdk
================

Warning, this is an experimental project. I developed it on my spare time, so it is not officially supported by Korbit. 
Use it at your own risk. It would be great if you can file an issue in case you found a bug. :-)

What?
-----
Korbit-Scala-SDK is a scala library that wraps HTTP Rest based Korbit API calls. 
It calls APIs asynchronously so that you can easily manage data in event driven way.

Why?
----
Concentrate on building your business logic instead of writing codes for managing access token, refresh token, and nonce.
You don't have to remember HTTP URLs, but simply call Scala functions.

How?
----
It uses Typesafe Akka and actor model to run APIs asynchronously. APIs that do not require access tokens or nonces are processed in parallel, whereas ones that requre them are processed in serial.

Running Sample Code
-------------------
You need [Play Framework v2.2.2](http://www.playframework.com/) or one with above version to build the SDK jar file and run the sample code. 
First set the following environment variables before running the sample code.
```
export KORBIT_API_KEY=Paste Your Korbit Single User API Key Here.
export KORBIT_API_SECRET=Paste Your Korbit Single User API Secret Here.
export KORBIT_API_USERNAME=Your email address registered on korbit.co.kr.
export KORBIT_API_PASSWORD=Your login password of the above user name.
```

Open up *play*, run the *run* command. Try to access http://localhost:9000/ on your web browser, and check the output on the terminal you ran *play*. 

Compiling, Creating SDK Jar File
--------------------------------
Open up *play*, run (1) *clean*, (2) *dist* command. You will get the jar file on the following path.
```
subprojs/tradeapi/target/scala-2.10/tradeapi_2.10-1.0-SNAPSHOT.jar
```

Troubleshooting
---------------
1. sun.security.validator.ValidatorException: PKIX path building failed.
Root cause : Java 7 doesn't recognise Godaddy's latest root certificate.
```
sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
```
Solution :
Run commands in the following link.
```
http://notes.richdougherty.com/2013/09/adding-godaddy-g2-root-cert-to-jdk-7.html
```

Korbit API Specification
------------------------
```
https://bitbucket.org/korbit/korbit-api/wiki/Home
```

Limitations
-----------
Only Single User API Key is supported. Multi User API Key is not supported.
This is a pre-release, so the test server(https://api.korbit.co.kr:8080) is supported only.

License
-------
Apache v2 License.

