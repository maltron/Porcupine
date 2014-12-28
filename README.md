# Porcupine: REST Security for JAX-RS
##### [Java EE 7 using WildFly 8, Java 8, MySQL 5.1, Apache 2 License]
![REST-Security](https://github.com/maltron/Porcupine/blob/master/server/javaee/7/porcupine/images/rest-security.jpg)

Porcupine is a [OAuth 2](http://oauth.net/2/) implementation as an [Java EE 7](http://www.oracle.com/technetwork/java/javaee/overview/index.html) application with all the parts needed to provide security for RESTfull endpoints written in [JAX-RS 2.0](https://jax-rs-spec.java.net/). Essentially, Porcupine provides a 3-legged applications in order to fullfill all [OAuth 2](http://oauth.net/2/) needs. Those are:

1. Authorization Server: This application has information about each different client applications allowed to get access to a certain resources. The way each client reads a resource it's through a an Access Token, which gives the Client the unique ability to access an protected resource.
2. Resource Server: This is the application providing a simple REST (using JAX-RS 2.0) and needs to be secured, using [OAuth 2](http://oauth.net/2) specification.
3. Client: This is the application which it will fetch a Protected Resource, using one of four different grants defined by [OAuth 2](http://oauth.net/2/) specification.

Installation
------------


