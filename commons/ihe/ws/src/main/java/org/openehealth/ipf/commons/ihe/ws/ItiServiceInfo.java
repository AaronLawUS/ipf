/*
 * Copyright 2009 the original author or authors.
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
package org.openehealth.ipf.commons.ihe.ws;

import org.apache.commons.lang3.Validate;
import javax.xml.namespace.QName;

/**
 * Contains information about a web-service (e.g. the service class). 
 * This is the static information about the web-service, i.e. all data that does
 * not depend on the dynamic endpoint configuration (e.g. if SOAP 1.1 or 1.2 is
 * used).
 */
public class ItiServiceInfo {
    private final QName bindingName;
    private final Class<?> serviceClass;
    private final QName serviceName;
    private final String wsdlLocation;
    private final boolean mtom;
    private final boolean addressing;
    private final boolean swaOutSupport;
    private final boolean auditRequestPayload;

    /**
     * Constructs the service info.
     * @param serviceName
     *          the qualified name of the service.
     * @param serviceClass
     *          the class of the service interface.
     * @param bindingName
     *          the qualified name of the binding to use.
     * @param mtom
     *          {@code true} if this service requires MTOM.
     * @param wsdlLocation
     *          the location of the WSDL of this webservice.
     * @param addressing
     *          {@code true} if this service requires WS-Addressing.
     * @param swaOutSupport
     *          <code>true</code> if this service requires SwA for its output.
     * @param auditRequestPayload
     *          <code>true</code> if this service must save payload in audit record.
     */
    public ItiServiceInfo(QName serviceName, 
                          Class<?> serviceClass,
                          QName bindingName,
                          boolean mtom, 
                          String wsdlLocation, 
                          boolean addressing, 
                          boolean swaOutSupport,
                          boolean auditRequestPayload)
    {
        Validate.notNull(serviceName, "serviceName");
        Validate.notNull(serviceClass, "serviceClass");
        Validate.notNull(bindingName, "bindingName");
        Validate.notNull(wsdlLocation, "wsdlLocation");
        
        this.serviceClass = serviceClass;
        this.serviceName = serviceName;
        this.bindingName = bindingName;
        this.mtom = mtom;
        this.wsdlLocation = wsdlLocation;
        this.addressing = addressing;
        this.swaOutSupport = swaOutSupport;
        this.auditRequestPayload = auditRequestPayload;
    }

    /**
     * @return the qualified name of the binding to use.
     */
    public QName getBindingName() {
        return bindingName;
    }

    /**
     * @return the qualified name of the service.
     */
    public QName getServiceName() {
        return serviceName;
    }

    /**
     * @return the class of the service interface.
     */
    public Class<?> getServiceClass() {
        return serviceClass;
    }

    /**
     * @return {@code true} if this service requires MTOM.
     */
    public boolean isMtom() {
        return mtom;
    }

    /**
     * @return the location of the WSDL of this webservice.
     */
    public String getWsdlLocation() {
        return wsdlLocation;
    }

    /**
     * Whether WS-Addressing should be supported.  
     * Currently affects only the client side (i.e. the Camel producer). 
     * 
     * @return {@code true} if this service requires WS-Addressing.
     */
    public boolean isAddressing() {
        return addressing;
    }

    /**
     * @return <code>true</code> if this service requires SwA for its output.
     */
    public boolean isSwaOutSupport() {
        return swaOutSupport;
    }

    /**
     * @return <code>true</code> if this service must save payload in audit record.
     */
    public boolean isAuditRequestPayload() {
        return auditRequestPayload;
    }
}
