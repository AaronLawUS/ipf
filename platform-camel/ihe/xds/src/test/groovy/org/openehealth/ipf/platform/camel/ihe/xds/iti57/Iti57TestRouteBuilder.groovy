/*
 * Copyright 2013 the original author or authors.
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
package org.openehealth.ipf.platform.camel.ihe.xds.iti57

import org.apache.camel.spring.SpringRouteBuilder
import org.openehealth.ipf.commons.ihe.xds.core.requests.RegisterDocumentSet
import org.openehealth.ipf.commons.ihe.xds.core.responses.Response
import org.openehealth.ipf.platform.camel.core.util.Exchanges

import static org.openehealth.ipf.commons.ihe.xds.core.responses.Status.FAILURE
import static org.openehealth.ipf.commons.ihe.xds.core.responses.Status.SUCCESS
import static org.openehealth.ipf.platform.camel.ihe.xds.XdsCamelValidators.iti57RequestValidator
import static org.openehealth.ipf.platform.camel.ihe.xds.XdsCamelValidators.iti57ResponseValidator

/**
 * @author Boris Stanojevic
 */
public class Iti57TestRouteBuilder extends SpringRouteBuilder {
    @Override
    public void configure() throws Exception {
        from('xds-iti57:xds-iti57-service1')
            .process(iti57RequestValidator())
            .process { checkValue(it, 'service 1')}
            .process(iti57ResponseValidator())

        from('xds-iti57:xds-iti57-service2')
            .process { checkValue(it, 'service 2')}
    }

    void checkValue(exchange, expected) {
        def value = exchange.in.getBody(RegisterDocumentSet.class).documentEntries[0].comments.value        
        def response = new Response(expected == value ? SUCCESS : FAILURE)
        Exchanges.resultMessage(exchange).body = response
    }
}
