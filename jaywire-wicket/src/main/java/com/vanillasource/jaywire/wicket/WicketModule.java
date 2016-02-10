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

package com.vanillasource.jaywire.wicket;

import com.vanillasource.jaywire.standalone.StandaloneModule;
import com.vanillasource.jaywire.web.RequestScopeSupport;
import com.vanillasource.jaywire.web.ServletRequestScope;
import com.vanillasource.jaywire.web.HttpSessionScope;
import com.vanillasource.jaywire.web.SessionScopeSupport;
import com.vanillasource.jaywire.Scope;
import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.Page;
import org.apache.wicket.IApplicationListener;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.IPageFactory;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.function.Function;
import java.util.Map;
import java.util.HashMap;

public abstract class WicketModule extends StandaloneModule
      implements RequestScopeSupport, SessionScopeSupport {

   private final HttpSessionScope sessionScope = new HttpSessionScope();
   private final ServletRequestScope requestScope = new ServletRequestScope();
   private final Map<Class<?>, Function<PageParameters, ?>> pageFactories = new HashMap<>();

   @Override
   public Scope getRequestScope() {
      return requestScope;
   }

   @Override
   public Scope getSessionScope() {
      return sessionScope;
   }

   /**
    * Call in the <code>init()</code> method of the
    * application to setup this module to work with
    * the given application.
    */
   public void init(Application application) {
      registerInfrastructure(application);
      registerPages();
   }

   private void registerInfrastructure(Application application) {
      application.getRequestCycleListeners().add(new AbstractRequestCycleListener() {
         @Override
         public void onBeginRequest(RequestCycle requestCycle) {
            if (requestCycle.getRequest() != null && requestCycle.getRequest() instanceof ServletWebRequest) {
               ServletRequest request = ((ServletWebRequest) requestCycle.getRequest()).getContainerRequest();
               requestScope.setServletRequest(request);
               // Force creation of session
               if (request instanceof HttpServletRequest) {
                  application.getSessionStore().getSessionId(requestCycle.getRequest(), true);
                  Session session = application.fetchCreateAndSetSession(requestCycle);
                  if (session == null) {
                     throw new WicketRuntimeException("Could not create session, which is necessary for JayWire session scope.");
                  }
                  sessionScope.setHttpSession(((HttpServletRequest) request).getSession());
               }
            }
         }

         @Override
         public void onEndRequest(RequestCycle requestCycle) {
            sessionScope.clearHttpSession();
            requestScope.clearServletRequest();
         }
      });
      application.getApplicationListeners().add(new IApplicationListener() {
         @Override
         public void onAfterInitialized(Application application) {
         }

         @Override
         public void onBeforeDestroyed(Application application) {
            try {
               close();
            } catch (Exception e) {
               throw new WicketRuntimeException("Could not close JayWire module", e);
            }
         }
      });
   }

   /**
    * Override this method to add bookmarkable pages with the <code>addPage()</code>
    * method.
    */
   protected void registerPages() {
   }

   /**
    * Add a bookmarkable page to the page factory. In wicket there are two
    * types of pages, one that is bookmarkable, that is, it can be instantiated
    * automatically by wicket, and the other is self-instantiated. The latter
    * can be covered by providing <i>Suppliers</i> for each page. Bookmarkable
    * pages need to be however registered with this method. Each bookmarkable
    * page optionally can choose to support <i>PageParameters</i>.
    * @param pageClass The class of the page. This will be used as key by Wicket
    * to produce a new instance of a Page.
    * @param factory The factory function to create a new instance of the given
    * page. Page parameters will always be non-null, but the factory can choose
    * to ignore page parameters.
    */
   protected <P extends IRequestablePage> void addPage(
         Class<P> pageClass, Function<PageParameters, P> factory) {
      pageFactories.put(pageClass, factory);
   }

   public IPageFactory getPageFactory() {
      return new IPageFactory() {
         @Override
         public <P extends IRequestablePage> boolean isBookmarkable(Class<P> pageClass) {
            return pageFactories.containsKey(pageClass);
         }

         @Override
         public <P extends IRequestablePage> P newPage(Class<P> pageClass) {
            return newPage(pageClass, new PageParameters());
         }

         @Override
         @SuppressWarnings("unchecked")
         public <P extends IRequestablePage> P newPage(Class<P> pageClass, PageParameters pageParameters) {
            Function<PageParameters, P> pageFactory = 
               (Function<PageParameters, P>) pageFactories.get(pageClass);
            if (pageFactory == null) {
               throw new WicketRuntimeException("No page with class "+pageClass+" registered in JayWire Module. Please use the addPage() method to register bookmarkable pages.");
            }
            P page = pageFactory.apply(pageParameters);
            // This below is copied from DefaultPageFactory
            if (page.getPageParameters() != pageParameters) {
               page.getPageParameters().overwriteWith(pageParameters);
            }
            if (page instanceof Page) {
               ((Page) page).setWasCreatedBookmarkable(true);
            }
            return page;
         }
      };
   }
}

