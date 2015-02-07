JayWire Dependency Injection
============================

JayWire is a dependency injection framework designed with
a very unique set of requirements that caters to developers and
projects that want to minimize the reliance on heavy frameworks
like CDI (Weld) and want to preserve all the flexibility of pure
Java Code.

Specifically, the design goals are the following (with priorities in 
appoximate order):

 * Java Code only.
 * Compile-time wireing.
 * Typesafe.
 * Support pure POJO components.
 * No configuration.
 * No classpath scanning.
 * No reflection.
 * No annotations.
 * No bytecode enhancement / weaving.
 * No transparent proxies.
 * No code generation.
 * No annotation processing.
 * No (heavy) container.
 * Small.
 * Easy to use.

Note, that JayWire requires Java 8 or higher. This is necessary for usability reasons.

## Quickstart

### Getting the library

For use in standalone context, add this to your Maven build:

```xml
<dependency>
   <groupId>com.vanillasource.jaywire</groupId>
   <artifactId>jaywire</artifactId>
   <version>1.0.0</version>
</dependency>
```

### Making your first Module

JayWire assumes that there are different Services or Components
that have dependencies to eachother. All these Components can be
then implemented by defining dependencies directly (preferably
through the constructor) on the other Components. There is no need
to involve JayWire at this point, there is no required annotations
or interfaces that need to be used at this point.

```java
public class DatabaseConfiguration {
   ...

   public DatabaseConfiguration(DataSource dataSource) {
      ...
   }

   ...
}

public class DatabaseTaxiRegistry {
   ...
   public DatabaseTaxiRegistry(DataSource dataSource, Configuration configuration) {
      ...
   }
   ...
}

public class DatabaseClientRegistry {
   ...
   public DatabaseClientRegistry(DataSource dataSource, Configuration configuration) {
      ...
   }
   ...
}
```

To wire these components together, a Module object needs to be implemented
"at the end of the world". That means JayWire only has to be involved at the
very top of the dependency tree, in a standalone project near the "main" method for
example, where everything comes together.

The wireing here requires that both the Taxi and Client registries receive the
same datasource and configuration. This means that those need to be *singletons*,
or at least singletons in the module. Normally this can be easily implemented with
just storing these in an instance variable. However JayWire generalises this to
support any arbitrary *scope*, with its own simple syntax. Of course you can
use instance variables if you want to, since everything is normal Java Code and
there is no magic containers or hidden state.

The Module object would look like the following:

```java
import com.vanillasource.jaywire.standalone.StandaloneModule;

public class AppModule extends StandaloneModule {
   public DataSource getDataSource() {
      return singleton(() -> { ...get datasource ... });
   }

   public DatabaseConfiguration getDatabaseConfiguration() {
      return singleton(() -> new DatabaseConfiguration(getDataSource()));
   }

   public DatabaseTaxiRegistry getDatabaseTaxiRegistry() {
      return singleton(() -> new DatabaseTaxiRegistry(getDataSource(), getDatabaseConfiguration()));
   }

   public DatabaseClientRegistry getDatabaseClientRegistry() {
      return singleton(() -> new DatabaseClientRegistry(getDataSource(), getDatabaseConfiguration()));
   }
}
```

That's it. In summary: by writing a single Module class where all the
components are explicitly wired together you can get rid of all the
magic usually seen (unseen?) in other dependency injection frameworks.

## Scopes

JayWire comes with the usual DI scopes:

 * Singleton
 * ThreadLocal
 * Request
 * Session

All scopes implement the *Scope* interface, which is defined as:

```java
public interface Scope {
   <T> T get(Supplier<T> factory);

   default <T> Supplier<T> apply(Supplier<T> factory) {
      return () -> get(factory);
   }
}
```

The `get()` method retrieves a component of type `T` from the Scope. Whether
the scope uses the `Supplier<T>` or not is decided by the scope semantic.

The `apply()` method can be used to create a `Supplier<T>` with the Scope's
semantic added. See chapter *Dynamic Components*.

Scopes are components themselves available in the Module.

### Singleton

The singleton scope instantiates each Supplier object at most once per Module. The
singleton scope is available through the method `getSingletonScope()`, and there
is a convenience method called `singleton()`.

### ThreadLocal

This scope instantiates each given Supplier once in each Thread. Available through
`getThreadLocalScope()`, or through the convenice method `threadLocal()`.

### Request

The request scope implementation supplied in the `StandaloneModule` needs to be
explicitly controlled. Each time when a new "request" is created the `open()` method
needs to be called, and when the request is finished, the `close()` method.

Between these two calls, all components are instantiated at most once.

### Session

The implementation for session scope supplied in the `StandaloneModule` needs to
be explicitly controlled, and relies on the request scope to also properly work. After
a request scope is opened, the session scope needs to be opened with the 
`open(session)` method.

The scope will in turn use the request scope to remember which session is active for
the duration of the request, and use the given session object as a key to get
components.

All components are weakly linked to the provided session (key), so if the session
object itself gets garbage collected (expires), all the related objects also
get garbage collected.

Note: don't use Strings or Integers as keys, because those may not be garbage 
collected at all. Use the full session object that will only be referenced as
long as the session is active.

### Writing your own Scope

Scopes are not some magic infrastructure of JayWire, they are normal objects
like any other. You only have to implement the `Scope` interface, and use the
resulting object in your Module.

However, if you want to enable Modularisation (see chapter below), you might
want to define an interface for use in module fragments, like this:

```java
public interface MySpecialScopeSupport {
   Scope getMySpecialScope();
}
```

Sometimes it is handy to add a convenience method to use your scope
(similar to what the standard scopes do):

```java
public interface MySpecialScopeSupport {
   Scope getMySpecialScope();

   default <T> T my(Supplier<T> supplier) {
      return getMySpecialScope().get(supplier);
   }
}
```

## Dynamic Components

Sometimes a dependency for an object might not be static.
A component may want to create new instances of another component, for
example a JobRunner may want to create new configured Job objects each time.
Or a dependency crosses scope boundaries, for example a singleton service may
want to access a session scoped user object.

For these cases initializing the component with its dependency in the constructor
would mean that it always uses the same object. This is usually where a
*Factory* is used, where the component is created with the *Factory* which
can produce a correct instance of its dependency each time. A Factory however
implies that a new object is created each time, which not be accurate so Java 8
uses the term `Supplier`.

A dynamic dependency is therefore defined this way:

```java
import java.util.function.Supplier;

public class PayrollService {
   private Supplier<User> currentUserSupplier;

   public PayrollService(Supplier<User> currentUserSupplier) {
      this.currentUserSupplier = currentUserSupplier;
   }

   ...
   public BigDecimal getMonthlySalary() {
      User currentUser = currentUserSupplier.get();
      ...
   }
   ...
}
```

Each time the payroll service `get()`s a User from the *currentUserSupplier* it
receives the current user of this request / session in a thread-safe manner.

This is how this service can be wired together:

```java
public class AppModule extends SparkModule {
   public Supplier<User> getCurrentUserSupplier() {
      return getSessionScope().apply(() -> new User());
   }

   public PayrollService getPayrollService() {
      return singleton(() -> new PayrollService(getCurrentUserSupplier()));
   }
}
```

Easy and explicit. With this configuration there will be exaclty always one
*User* object in each session, and the *PayrollService* will always access
the current one, even if multiple threads use the same *PayrollService* instance.

Note also that the definition of the service itself does not require JayWire, it
is plain Java.

## Modularisation

Having one Module object defined at the top of an application might be
sufficient for some projects, however if there are multiple loosely coupled subsystems,
or subsystems that are used by multiple parties, it may be useful to provide
a wireing module *fragment* only for the components in the given subsystems to
contain a default wireing.

Consider for example a system with these independent subsystems (packages): Printing,
Communication, Storage. Suppose each wants to provide a default wireing for
its services. Defining a Module class for each would not be appropriate because 
that would require multiple inheritance somewhere at the top. Also, defining a Module
would define the scope implementations, and this would not allow to use the Module
both in a standalone and a web context for example.

So this module *fragment* must be an interface, in which case it can be
defined this way:

```java
import com.vanillasource.jaywire.StandardScopesSupport;

public interface PrintingModule extends StandardScopesSupport {
   default PrintingService getPrintingService() {
      return singleton(...);
   }

   default PrinterRegistry getPrinterRegistry() {
      return singleton(...);
   }
}

public interface CommunicationModule extends StandardScopesSupport {
   default EmailService getEmailService() {
      return singleton(...);
   }
}

...
```

These fragments are all interfaces, and the `StandardScopesSupport` just defines
the standard scope methods to be implemented later. This enables 
a concrete Module class to pull as many fragments as necessary without problems.

A top level Module class for a standalone program would look like the following:
```java
public class AppModule
   extends StandaloneModule
   implements PrintingModule, CommunicationModule, StorageModule {
}
```

No need to implement anything, since everything is provided in the default methods
from the fragments, and the scope implementations are provided by the `StandaloneModule`
class. The same fragments can therefore be used the same way with other scope
implementations or in other combinations.

## Closing Components

There is no need to explicitly close the `StandaloneModule` as there are no
additional resources that need to be let go. However some services,
such as database connections may need to be closed when the module is no longer
used.

You can use the standard `AutoCloseable` interface to mark your Component or Service
as closeable. The `StandaloneModule` also implements the `AutoCloseable` interface,
and can be closed or used in a try-with-resources construct. You may
register your Components to be closed together with the Module the following way:

```java
public class AppModule extends StandaloneModule {
   public Database getDatabase() {
      singleton(() -> closeWithModule(new ...));
   }
}
```

The `closeWithModule()` method registers the *Database* to be closed together with
the Module. Note: register each object only once!

## Integration

JayWire provides the `StandaloneModule` by default which can be used to wire
components together for a standalone program, providing no integration with its
environment. However, because all scopes are just normal objects these scopes
can be replaced/adapted to integrate with the environment they are in.

### Spark Framework

Spark (http://sparkjava.com/) is a small framework to implement Web Applications.
JayWire's `jaywire-spark` artifact provides integration with this framework by
only needing to use the `SparkModule` class instead of `StandaloneModule`.

By using `SparkModule` the request and session scopes will automatically work. The
module registers the necessary filters on construction.

It is available under following coordinates:

```xml
<dependency>
   <groupId>com.vanillasource.jaywire</groupId>
   <artifactId>jaywire-spark</artifactId>
   <version>1.0.0</version>
</dependency>
```

