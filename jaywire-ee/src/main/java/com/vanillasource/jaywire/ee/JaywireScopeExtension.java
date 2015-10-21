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

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import java.io.Serializable;

public class JaywireScopeExtension implements Extension, Serializable {
   public void addScope(@Observes BeforeBeanDiscovery event) {
      event.addScope(Jaywire.class, true, false);
   }

   public void addBeans(@Observes AfterBeanDiscovery event, BeanManager beanManager) {
      event.addContext(new JaywireContext());
      getModule(beanManager).getBeans().stream().forEach(event::addBean);
   }

   @SuppressWarnings("unchecked")
   private EnterpriseModule getModule(BeanManager beanManager) {
      Bean<EnterpriseModule> moduleBean = (Bean<EnterpriseModule>) beanManager.resolve(
            beanManager.getBeans(EnterpriseModule.class));
      try {
         EnterpriseModule module = (EnterpriseModule) moduleBean.getBeanClass().newInstance();
         module.registerBeans();
         return module;
      } catch (Exception e) {
         throw new IllegalStateException("can not instantiate "+moduleBean.getBeanClass()+", please make sure it has an empty constructor", e);
      }
   }
}
