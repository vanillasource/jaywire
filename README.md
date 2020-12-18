![Build Status](https://img.shields.io/travis/vanillasource/jaywire.svg)
![Published Version](https://img.shields.io/maven-central/v/com.vanillasource.jaywire/jaywire-parent.svg)
[![Join the chat at https://gitter.im/vanillasource/jaywire](https://badges.gitter.im/vanillasource/jaywire.svg)](https://gitter.im/vanillasource/jaywire?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

JayWire Dependency Injection
============================

A very small and lightweight dependency injection framework for Java 8 without magic. Main features are:

* 100% pure Java code (no XML, no config files)
* Explicit, compile-time wiring
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
   <version>1.1.0</version>
</dependency>
```

This will include the basic interfaces and scopes for standalone usage. 
For integration with Web-frameworks see Wiki.

### Wiring everything together

For a simple application a single "Module" object wiring everything together is written this way:

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

In this example there is a Database, ServiceA and ServiceB, all of them singletons. It is assumed
that these services / classes are written in the usual Object-Oriented approach by taking all required
dependencies as constructor parameters.
These dependencies are then "injected" by simply calling the appropriate methods to get fully constructed
dependencies and supplying them as parameters to objects.

You can use the _MyAppModule_ then at the "top" of your application to start processing:

```java
public class MyApp {
   public static final void main(String[] argv) {
      MyAppModule module = new MyAppModule();
      module.getServiceB().run();
   }
}
```

### Documentation

The JayWire [API Documentation](http://vanillasource.github.io/jaywire/apidocs/).

Please visit the [JayWire Wiki](https://github.com/vanillasource/jaywire/wiki) for more information.

### Related external projects

 * [Pouch](https://bitbucket.org/cowwoc/pouch/) is an interesting rethinking of the Service Locator pattern.
