/*
 * Copyright 2010 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.platform.camel.ihe.hl7v3.iti55;

import java.net.URISyntaxException;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.openehealth.ipf.commons.ihe.hl7v3.Hl7v3ClientFactory;
import org.openehealth.ipf.commons.ihe.hl7v3.Hl7v3ServiceFactory;
import org.openehealth.ipf.commons.ihe.hl7v3.Hl7v3WsTransactionConfiguration;
import org.openehealth.ipf.commons.ihe.hl7v3.iti55.Iti55AuditStrategy;
import org.openehealth.ipf.commons.ihe.ws.JaxWsClientFactory;
import org.openehealth.ipf.commons.ihe.ws.JaxWsServiceFactory;
import org.openehealth.ipf.platform.camel.ihe.ws.DefaultItiConsumer;
import org.openehealth.ipf.platform.camel.ihe.ws.DefaultItiEndpoint;
import org.openehealth.ipf.platform.camel.ihe.ws.DefaultItiWebService;

/**
 * The Camel endpoint for the ITI-55 transaction.
 */
public class Iti55Endpoint extends DefaultItiEndpoint<Hl7v3WsTransactionConfiguration> {

    /**
     * Constructs the endpoint.
     * @param endpointUri
     *          the endpoint URI.
     * @param address
     *          the endpoint address from the URI.
     * @param iti55Component
     *          the component creating this endpoint.
     * @param customInterceptors
     *          user-defined additional CXF interceptors.
     * @throws URISyntaxException
     *          if the endpoint URI was not a valid URI.
     */
    public Iti55Endpoint(
            String endpointUri, 
            String address, 
            Iti55Component iti55Component,
            InterceptorProvider customInterceptors) throws URISyntaxException 
    {
        super(endpointUri, address, iti55Component, customInterceptors);
    }

    public Producer createProducer() throws Exception {
        JaxWsClientFactory clientFactory = new Hl7v3ClientFactory(
                getWebServiceConfiguration(),
                getServiceUrl(),
                isAudit() ? new Iti55AuditStrategy(false, isAllowIncompleteAudit()) : null,
                getCorrelator(),
                getCustomInterceptors());
        return new Iti55Producer(this, clientFactory);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        JaxWsServiceFactory serviceFactory = new Hl7v3ServiceFactory(
                getWebServiceConfiguration(),
                getServiceAddress(),
                isAudit() ? new Iti55AuditStrategy(true, isAllowIncompleteAudit()) : null,
                getCustomInterceptors(),
                getRejectionHandlingStrategy());
        ServerFactoryBean serverFactory =
            serviceFactory.createServerFactory(Iti55Service.class);
        Server server = serverFactory.create();
        DefaultItiWebService service = (DefaultItiWebService) serverFactory.getServiceBean();
        return new DefaultItiConsumer(this, processor, service, server);
    }
}
