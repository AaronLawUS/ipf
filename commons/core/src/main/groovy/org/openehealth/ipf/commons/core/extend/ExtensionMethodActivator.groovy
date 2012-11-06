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
package org.openehealth.ipf.commons.core.extend

import java.lang.reflect.Modifier

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Martin Krasser
 */
@Deprecated
class ExtensionMethodActivator implements ConditionalActivator {
    
     private static final Logger LOG = LoggerFactory.getLogger(ExtensionMethodActivator.class);
     
     boolean supports(Class<?> extensionClass) {
         getExtensionMethods(extensionClass).size() > 0
     }
      
     boolean supports(Object extensionObject) {
         getExtensionMethods(extensionObject.class).size() > 0
     }
      
     void activate(Class<?> extensionClass) {
         StringBuffer buffer = new StringBuffer()
         getExtensionMethods(extensionClass).each { method ->
             generateExtensionCode(extensionClass, method, buffer)
         }
         activateExtensionCode(extensionClass, buffer.toString())
     }
     
     void activate(Object extensionObject) {
         StringBuffer buffer = new StringBuffer()
         getExtensionMethods(extensionObject.class).each { method ->
             generateExtensionCode(extensionObject.class, method, buffer)
         }
         activateExtensionCode(extensionObject.class, buffer.toString())
     }

     private static def getExtensionMethods(def clazz) {
         clazz.declaredMethods
             .findAll{it.modifiers & Modifier.STATIC}
             .findAll{it.modifiers & Modifier.PUBLIC}
             .findAll{it.name != 'getExtensions'}
             .findAll{it.name != 'setExtensions'}
             .findAll{it.name != '__$swapInit'}
     }

     private static def activateExtensionCode(def clazz, def code) {
         LOG.debug("Activate extension code: {}", code)
         new GroovyShell(clazz.classLoader).evaluate(code)
     }
     
     private static def generateExtensionCode(def clazz, def method, def buffer) {
         // the class to extend 
         def target = method.parameterTypes[0]
         // all parameter types except first
         def paramTypes = (method.parameterTypes as List).tail()
         // create comma-sparated parameter list
         def paramTypesList = (0..<paramTypes.size()).collect{
             idx -> paramTypes[idx].name + ' p' + idx
         }.join(',')
         // create comma-sparated argument list
         def paramNamesList = (0..<paramTypes.size()).collect(['delegate']){
             idx -> 'p' + idx
         }.join(',')

         def code = 
             "${target.name}.metaClass.${method.name} = {" +
             "${paramTypesList} -> ${clazz.name}.${method.name}(${paramNamesList}) " +
             "}"
         buffer.append('\n').append(code)
     }
     
}
