/*
 * Copyright 2008 the original author or authors.
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
package org.openehealth.ipf.platform.camel.core.extend

import static org.apache.camel.builder.Builder.*

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.FilterDefinition
import org.apache.camel.model.ProcessorDefinition
import org.apache.camel.model.RouteDefinition

/**
 * @author Martin Krasser
 */
@Deprecated
class SampleModelExtension {
    
    static extensions = { 
        
        RouteBuilder.metaClass.input = { String endpointUri ->
            delegate.from(endpointUri)
        }
        
        ProcessorDefinition.metaClass.output = {String endpointUri ->
            delegate.to(endpointUri)
        }
        
        ProcessorDefinition.metaClass.filter = { String msgBody ->
            delegate.filter(body().isNotEqualTo(msgBody))
        }
        
        ProcessorDefinition.metaClass.transmogrifyAndFilter = { String bean, String body ->
            delegate
                .transmogrify(bean) // also an extension (core)
                .filter(body)     // also an extension (sample)
        }
        
    }
    
}
