=== Writing a Simple gRPC Server

To implement a gRPC server for the previously defined `helloworld.proto` definition you first need to generate the Java stubs using Gradle or Maven then create a class that extends from `GreeterGrpc.GreeterImplBase`.

For example:

snippet::helloworld.GreetingEndpoint[tags="imports,clazz", source="main"]

<1> The class extends from `GreeterGrpc.GreeterImplBase` and is annotated with `jakarta.inject.Singleton`
<2> You can dependency inject other beans into the implementation. In this case `GreetingService` is dependency injected.
<3> The `StreamObserver` is used to send a response to the client.

=== Running the gRPC Server

To run the server use the `Application` class or run `./gradlew run` for Gradle or `./mvnw compile exec:exec` for Maven.


The server by default runs on port 50051, however you can configure which port the server runs on by setting `grpc.server.port` to whichever value you wish (a value of `${random.port}` will use a random port).

=== Configuring the gRPC Server

The server can be be configured in a number of different ways. You can use the `io.micronaut.grpc.server.GrpcServerConfiguration` type to configure any property of gRPC's `NettyServerBuilder` class via `application.yml`.

For example:

.Configuring the gRPC server
[source,yaml]
----
grpc:
server:
port: 8080
keep-alive-time: 3h
max-inbound-message-size: 1024
ssl:
cert-chain: 'file://path/to/my.cert' # <1>
private-key: 'classpath:my.key' # <2>
----
<1> Load the certificate from `/path/to/my.cert` file.
<2> Load the private key from the classpath. The file should be in `src/main/resources/my.key`.

By default, the gRPC server will be enabled. To disable the gRPC server, set `grpc.server.enabled` to false.

Alternatively if you prefer programmatic configuration you can write a `BeanCreationListener` for example:

.Configuring the ServerBuilder
[source,java]
----
include::grpc-server-runtime/src/test/groovy/io/micronaut/grpc/ServerBuilderListener.java[]
----

=== Auto Injected Types

By default, the server will automatically be dependency injected with beans of the following types:

* `io.grpc.BindableService` - Any services declared as beans
* `io.grpc.ServerInterceptor` - Any interceptors declared as beans
* `io.grpc.ServerTransportFilter` - Any transport filters declared as beans

In addition, by default the server will be setup to use Micronaut's I/O executor service.

==== Server Interceptor Ordering
To produce a consistent and predictable order of execution for server interceptors, it is required
for the `io.grpc.ServerInterceptor` implementation to do one of the following:

.Implement `io.micronaut.core.order.Ordered` interface
[source,java]
----
@Singleton // <1>
public class CustomInterceptor implements ServerInterceptor, Ordered { // <2>
...

    @Override
    public int getOrder() {
        return 10; // <3>
    }

}
----
<1> Declare the server interceptor as a bean to have it registered automatically
<2> Implement `Ordered` in addition to `ServerInterceptor`
<3> Provide the specified order of execution in the server interceptor chain

.Wrap with `io.micronaut.grpc.server.interceptor.OrderedServerInterceptor`
[source,java]
----
@Factory // <1>
public class ServerInterceptorFactory {

    @Bean // <2>
    @Singleton
    public ServerInterceptor customServerInterceptor() {
        return new OrderedServerInterceptor(new CustomInterceptor(), 10); // <3>
    }

}
----
<1> Use a `@Factory` to create an instance of `ServerInterceptor` bean
<2> Register the created sever interceptor as a bean
<3> Wrap the `CustomInterceptor` with `OrderedServerInterceptor` and provide the `order` of execution

The order which is provided will dictate the order of execution of the interceptors when receiving the request message,
and then the order will be reversed when sending the response message.

.3 interceptors, with respective orders, `1`, `2`, and `3`:
[source,txt]
----
Request -> 1 -> 2 -> 3 -> business logic -> 3 -> 2 -> 1 -> Response
----

=== Health checks

==== gRPC Health checks

If the gRPC services dependency (`io.grpc:grpc-services`) is added, then https://github.com/grpc/grpc/blob/master/doc/health-checking.md[gRPC health checks] will be enabled.

To modify the status of a service, call the `setStatus` method on an instance of `HealthServiceManager`, for example:

snippet::helloworld.HealthService[tags="imports,clazz", source="main"]

If you wish to disable the gRPC health check while still using the services dependency you can set the property `grpc.server.health.enabled` to `false` in your application configuration.

==== Management Health checks

If the management dependency (`io.micronaut:micronaut-management`) is added, then Micronaut’s Health Endpoint can be used to expose the health status of the gRPC server.

For example, if gRPC is running then the `/health` endpoint will return:
[source,json]
----
{
"status": "UP",
"details": {
"grpc-server": {
"name": "your-project-name",
"status": "UP",
"details": {
"host": "localhost",
"port": 5050
}
}
},
...
}
----

If you wish to disable the Micronaut gRPC server health check while still using the management dependency you can set the property `grpc.server.health.enabled` to `false` in your application configuration.

=== Testing the Server

To test the server it is recommended that you use https://github.com/micronaut-projects/micronaut-test[Micronaut Test].

TIP: For detailed instructions on how to set up Micronaut Test for either Spock or JUnit 5 see the https://micronaut-projects.github.io/micronaut-test/latest/guide/index.html[documentation] on the subject.


You can then define a blocking stub bean in `src/test/java`. For example:

.Defining Test Clients
[source,java]
----
include::test-suite-java/src/test/java/helloworld/GreetingEndpointTest.java[tags=clients]
----

<1> A `ManagedChannel` is injected that can communicate with the server.
<2> The generated gRPC client blocking stub is created.

The above example uses the `@GrpcChannel` annotation to inject a gRPC `ManagedChannel` that can communicate with the running server. This channel will be automatically be shutdown when the application shuts down.

Now that you have a test client, writing the test becomes trivial:

snippet::helloworld.GreetingEndpointTest[tags="imports,test"]

<1> The test is annotated with `@MicronautTest`
<2> The client stub is injected into the test
<3> A request is sent and the response asserted.
