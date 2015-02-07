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

The singleton scope instantiates each Supplier object exactly once per Module. The
singleton scope is available through the method `getSingletonScope()`, and there
is a convenience method called `singleton()`.

### ThreadLocal

This scope instantiates each given Supplier once in each Thread. Available through
`getThreadLocalScope()`, or through the convenice method `threadLocal()`.

### Request

The request scope implementation supplied in the `StandaloneModule` needs to be
explicitly controlled. Each time when a new "request" is created the `open()` method
needs to be called, and when the request is finished, the `close()` method.

Between these two calls, all components are instantiated exactly once.

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

## Modularisation

## Closeing the Module

## Integration

### Spark Framework


