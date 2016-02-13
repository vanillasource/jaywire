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

package com.vanillasource.jaywire.servlet;

import com.vanillasource.jaywire.Scope;
import com.vanillasource.jaywire.standalone.StandaloneModule;
import com.vanillasource.jaywire.web.ServletRequestScopeModule;
import com.vanillasource.jaywire.web.HttpSessionScopeModule;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Integration with standard Java Servlet contexts. The module
 * automatically supports request and session scopes, override the
 * <code>registerComponents()</code> method to register fully constructed
 * components into the servlet container.<br>
 * The module automatically closes if the web application is destroyed
 * or unloaded, so all components registered with <code>closeWithModule()</code>
 * will also be closed.<br>
 * Register this module as a <code>ServletContextListener</code> to make it work.
 * Either annotate the class with <code>WebListener</code> or register it
 * in the <i>web.xml</i> as a listener.
 */
public abstract class ServletModule
      extends StandaloneModule
      implements ServletRequestScopeModule, HttpSessionScopeModule, ServletContextListener {

   @Override
   public void contextInitialized(ServletContextEvent event) {
      ServletContext context = event.getServletContext();
      registerInfrastructure(context);
      registerComponents(context);
   }

   private void registerInfrastructure(ServletContext context) {
      context.addFilter("JayWireScopeFilter", new Filter() {
         @Override
         public void init(FilterConfig config) {
         }

         @Override
         public void destroy() {
         }

         @Override
         public void doFilter(ServletRequest request, 
               ServletResponse response, FilterChain chain) throws ServletException, IOException {
            getServletRequestScope().setServletRequest(request);
            if (request instanceof HttpServletRequest) {
               HttpServletRequest httpRequest = (HttpServletRequest) request;
               getHttpSessionScope().setHttpSession(httpRequest.getSession(true));
            }
            try {
               chain.doFilter(request, response);
            } finally {
               getServletRequestScope().clearServletRequest();
               getHttpSessionScope().clearHttpSession();
            }
         }
      }).addMappingForUrlPatterns(null, false, "/*");
      context.log("JayWire servlet module "+getClass().getName()+" activated");
   }

   /**
    * Override this method to register all servlet, filter and
    * listener components of your application.
    */
   public abstract void registerComponents(ServletContext context);

   @Override
   public void contextDestroyed(ServletContextEvent event) {
      try {
         close();
      } catch (Exception e) {
         throw new RuntimeException("error closing servlet module", e);
      }
   }
}

