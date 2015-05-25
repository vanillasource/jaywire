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

package com.vanillasource.jaywire.spark;

import com.vanillasource.jaywire.standalone.StandaloneModule;
import com.vanillasource.jaywire.web.DelimitedRequestScopeModule;
import com.vanillasource.jaywire.web.WeakSessionScopeModule;
import spark.Spark;

/**
 * A module that includes all the standard scopes, and
 * wires automatically into Spark to handle request and
 * session scopes.
 */
public abstract class SparkModule extends StandaloneModule
      implements DelimitedRequestScopeModule, WeakSessionScopeModule {
   public void addRoutes() {
      Spark.before((request, response) -> {
         getDelimetedRequestScope().open();
         getWeakSessionScope().open(request.session(true).raw());
      });
      Spark.after((request, response) -> {
         getDelimetedRequestScope().close();
      });
   }
}

