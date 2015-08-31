/*
 * Copyright 2005-2011 WSO2, Inc. (http://wso2.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.appfactory.s4.integration.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.appfactory.common.AppFactoryConfiguration;
import org.wso2.carbon.appfactory.core.ApplicationEventsHandler;
import org.wso2.carbon.appfactory.core.TenantCloudInitializer;
import org.wso2.carbon.appfactory.s4.integration.DomainMapperEventHandler;
import org.wso2.carbon.appfactory.s4.integration.DomainMappingListener;
import org.wso2.carbon.appfactory.s4.integration.DomainMappingManagementService;
import org.wso2.carbon.appfactory.s4.integration.S4TenantCloudInitializer;
import org.wso2.carbon.utils.ConfigurationContextService;

/**
 * @scr.component name=
 *                "org.wso2.carbon.appfactory.s4.integration"
 *                immediate="true"
 * @scr.reference name="appfactory.configuration"
 *                interface=
 *                "org.wso2.carbon.appfactory.common.AppFactoryConfiguration"
 *                cardinality="1..1" policy="dynamic"
 *                bind="setAppFactoryConfiguration"
 *                unbind="unsetAppFactoryConfiguration"
 * @scr.reference name="configuration.context.service"
 *                interface="org.wso2.carbon.utils.ConfigurationContextService"
 *                cardinality="1..1" policy="dynamic"
 *                bind="setConfigurationContextService"
 *                unbind="unsetConfigurationContextService"
 * @scr.reference name="domain.mapping.events.handler"
 *                interface=
 *                "org.wso2.carbon.appfactory.s4.integration.DomainMapperEventHandler"
 *                cardinality="0..n" policy="dynamic"
 *                bind="setDomainMapperEventsHandler"
 *                unbind="unsetDomainMapperEventsHandler"
 */
public class S4IntegrationServiceComponent {
	private static Log log = LogFactory.getLog(S4IntegrationServiceComponent.class);

	protected void activate(ComponentContext context) {
		try {
            DomainMappingManagementService domainMappingManagementService =new DomainMappingManagementService();
			context.getBundleContext().registerService(TenantCloudInitializer.class.getName(),
			                                           new S4TenantCloudInitializer(), null);
			context.getBundleContext()
			       .registerService(DomainMappingManagementService.class.getName(),
                                    domainMappingManagementService, null);
            ServiceReferenceHolder.getInstance().setDomainMappingManagementService(domainMappingManagementService);
            AppFactoryConfiguration appFactoryConfiguration = ServiceReferenceHolder.getInstance().getAppFactoryConfiguration();
            int listenerPriority = Integer.parseInt(appFactoryConfiguration.getFirstProperty("EventHandlers.DomainMappingListener.priority"));
            context.getBundleContext().registerService(ApplicationEventsHandler.class.getName(),
                                                       new DomainMappingListener("DomainMappingListener", listenerPriority), null);
            if (log.isDebugEnabled()) {
				log.debug("S4IntegrationServiceComponent  bundle is activated");
			}
		} catch (Exception e) {
			log.error("S4IntegrationServiceComponent activation failed.", e);
		}
	}

	protected void deactivate(ComponentContext context) {
		if (log.isDebugEnabled()) {
			log.debug("S4IntegrationServiceComponent bundle is deactivated ");
		}
	}

	protected void setAppFactoryConfiguration(AppFactoryConfiguration configuration) {
		ServiceReferenceHolder.getInstance().setAppFactoryConfiguration(configuration);
	}

	protected void unsetAppFactoryConfiguration(AppFactoryConfiguration configuration) {
		ServiceReferenceHolder.getInstance().setAppFactoryConfiguration(null);
	}

	protected void setConfigurationContextService(ConfigurationContextService configurationContextService) {
		ServiceReferenceHolder.getInstance()
		                      .setConfigurationContextService(configurationContextService);
	}

	protected void unsetConfigurationContextService(ConfigurationContextService configurationContextService) {
		ServiceReferenceHolder.getInstance().setConfigurationContextService(null);
	}

	public static void setDomainMapperEventsHandler(DomainMapperEventHandler domainMapperEventHandler) {
		ServiceReferenceHolder.getInstance().addDomainMapperEventHandler(domainMapperEventHandler);
	}

	public static void unsetDomainMapperEventsHandler(DomainMapperEventHandler domainMapperEventHandler) {
		ServiceReferenceHolder.getInstance()
		                      .removeDomainMapperEventHandler(domainMapperEventHandler);
	}
}