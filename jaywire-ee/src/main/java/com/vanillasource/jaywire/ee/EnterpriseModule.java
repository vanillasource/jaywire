/**
 * Copyright (C) 2015 VanillaSource
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.vanillasource.jaywire.ee;

import com.vanillasource.jaywire.standalone.StandaloneModule;
import com.vanillasource.jaywire.SessionScopeSupport;
import com.vanillasource.jaywire.RequestScopeSupport;
import com.vanillasource.jaywire.Scope;
import javax.enterprise.inject.spi.*;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.context.Destroyed;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.LinkedList;
import java.util.function.Function;
import java.lang.annotation.Annotation;
import java.util.function.Supplier;
import javax.inject.Inject;

/**
 * Sublcass this module to integrate with Java Enteprise Edition 7, CDI 1.1. The subclass can be
 * used exactly like any other Jaywire Module, declaring dependencies through normal methods. The subclass
 * must be annotated with <code>ApplicationScoped</code> CDI context annotation.
 */
public abstract class EnterpriseModule extends StandaloneModule implements SessionScopeSupport, RequestScopeSupport {
   private static EnterpriseModule enterpriseModule;
   private static long nextId = 1;
   private List<Bean<?>> exportedBeans = new LinkedList<>();

   @Inject
   private CdiRequestScope requestScope;

   @Inject
   private CdiSessionScope sessionScope;

   List<Bean<?>> getBeans() {
      return exportedBeans;
   }

   @PostConstruct
   private void registerEnterpriseModule() {
      enterpriseModule = this;
   }

   private void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
   }

   private void destroy(@Observes @Destroyed(ApplicationScoped.class) Object init) throws Exception {
      close();
   }

   @Override
   public Scope getSessionScope() {
      return sessionScope;
   }

   @Override
   public Scope getRequestScope() {
      return requestScope;
   }

   private EnterpriseModule getEnterpriseModule() {
      if (enterpriseModule == null) {
         throw new IllegalStateException("enterprise module is not yet registered");
      }
      return enterpriseModule;
   }

   /**
    * Override this method to register beans to be exported to CDI.
    */
   protected void registerBeans() {
   }

   protected <T> BeanBuilder<T> beanClass(Class<T> beanClass) {
      return new BeanBuilder<>(beanClass);
   }

   protected class BeanBuilder<T> {
      private long id;
      private Class<T> beanClass;
      private String name;
      private Set<Annotation> qualifiers = new HashSet<>();
      private Set<Class<? extends Annotation>> stereotypes = new HashSet<>();
      private Set<Type> types = new HashSet<>();
      private boolean alternative;

      public BeanBuilder(Class<T> beanClass) {
         this.id = nextId++;
         this.beanClass = beanClass;
         addTypesFor(beanClass);
      }

      @SuppressWarnings("unchecked")
      private void addTypesFor(Type currentType) {
         if (currentType != null) {
            types.add(currentType);
            addTypesFor(((Class<?>)currentType).getGenericSuperclass());
            for (Type type: ((Class<?>)currentType).getGenericInterfaces()) {
               addTypesFor(type);
            }
         }
      }

      public BeanBuilder<T> withName(String name) {
         this.name = name;
         return this;
      }

      public BeanBuilder<T> withQualifier(Annotation annotation) {
         qualifiers.add(annotation);
         return this;
      }

      public BeanBuilder<T> withStereotype(Class<? extends Annotation> annotationClass) {
         stereotypes.add(annotationClass);
         return this;
      }

      public BeanBuilder<T> alternative() {
         this.alternative = true;
         return this;
      }

      @SuppressWarnings("unchecked")
      public <E extends EnterpriseModule> void toObject(Function<E, T> beanSupplier) {
         toSupplier(module -> () -> beanSupplier.apply((E)module));
      }

      public <E extends EnterpriseModule> void toSupplier(Function<E, Supplier<T>> beanSupplier) {
         exportedBeans.add(new JaywireBean<T>() {
            @Override
            public String getId() {
               return "JaywireBean-"+id;
            }

            @Override
            public boolean isNullable() {
               return true;
            }

            @Override
            public Class<?> getBeanClass() {
               return beanClass;
            }

            @Override
            public Set<InjectionPoint> getInjectionPoints() {
               return Collections.<InjectionPoint>emptySet();
            }

            @Override
            public String getName() {
               return name;
            }

            @Override
            @SuppressWarnings("unchecked")
            public T create(CreationalContext<T> creationalContext) {
               return beanSupplier.apply((E) getEnterpriseModule()).get();
            }

            @Override
            public void destroy(T instance, CreationalContext<T> creationalContext) {
            }

            @Override
            public Class<? extends Annotation> getScope() {
               return Jaywire.class;
            }

            @Override
            public Set<Annotation> getQualifiers() {
               return qualifiers;
            }

            @Override
            public Set<Class<? extends Annotation>> getStereotypes() {
               return stereotypes;
            }

            @Override
            public Set<Type> getTypes() {
               return types;
            }

            @Override
            public boolean isAlternative() {
               return alternative;
            }
         });
      }
   }

   public interface JaywireBean<T> extends Bean<T>, PassivationCapable {
   }
}

