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

License
-------
Apache v2 License.

