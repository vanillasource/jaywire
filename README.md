JayWire Dependency Injection
============================

A very small and lightweight dependency injection framework for Java 8 without magic. Main features are:

* 100% pure Java code (no XML, no config files)
* Explicit, compile-time wireing
* Typesafe
* Modular and extendable
* Small and easy

JayWire avoids any magic, that means:

* No classpath scanning
* No reflection
* No annotations
* No bytecode enhancement / weaving
* No transparent proxies
* No code generation
* No hidden / static state

### Getting the library

Add this dependency to your Maven build:

```xml
<dependency>
   <groupId>com.vanillasource.jaywire</groupId>
   <artifactId>jaywire</artifactId>
   <version>1.0.1</version>
</dependency>
```

This will include the basic interfaces and scopes for standalone usage. 
For integration with Web-frameworks see Wiki.

### Wireing everything together

For a simple application a single "Module" object wireing everything together is written this way:

```java
import com.vanillasource.jaywire.standalone.StandaloneModule;

public class MyAppModule extends StandaloneModule {
   public Database getDatabase() {
      return singleton(() -> new MyAppDatabase(...));
   }

   public ServiceA getServiceA() {
      return singleton(() -> new ServiceA(getDatabase()));
   }

   public ServiceB getServiceB() {
      return singleton(() -> new ServiceB(getDatabase(), getServiceA());
   }
}
```

In this example there is a Database, ServiceA and ServiceB, all of them singletons. 
Singleton here means, that they will only be instantiated once for an MyAppModule instance.
Dependencies are "injected" by simply calling the appropriate method and supplying them as parameters to objects.

You can use the _MyAppModule_ then at the "top" of your application to start processing:

```java
public class MyApp {
   public static final void main(String[] argv) {
      MyAppModule module = new MyAppModule();
      module.getServiceB().run();
   }
}
```

