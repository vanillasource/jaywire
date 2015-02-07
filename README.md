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
support any arbitrary *scope*.

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
magic of other dependency injection frameworks.


