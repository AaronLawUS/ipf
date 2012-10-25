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
package org.openehealth.ipf.modules.cda.builder


import groovytools.builder.MetaBuilder

/**
 * ModelExtension that recursively calls other extension classes. Overwrite the
 * modelExtensions() method to include the other extension classes
 * 
 * @author Christian Ohr
 */
abstract class CompositeModelExtension extends BaseModelExtension {
     
     CompositeModelExtension() {         
     }
     
     CompositeModelExtension(builder) {
         super(builder)
     }

     def register(Collection registered) {
         super.register(registered)
         modelExtensions().each {
             if (registered.contains(it.templateId())) {
                 LOG.debug("Skip {}", it.templateId())
             } else {
                 it.builder = builder
                 it.register(registered)
             }
         }
     }
     
     Collection modelExtensions() {
         []
     }   
    
 }
