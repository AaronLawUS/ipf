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
package org.openehealth.ipf.platform.camel.test.performance.server;

import org.apache.camel.spring.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mitko Kolev
 */
public class PerformanceMeasurementServer {

    private final static Logger LOG = LoggerFactory.getLogger(PerformanceMeasurementServer.class);

    public static void main(String[] args) throws Exception {
        LOG.info("Starting performance measurement server");
        Main.main("-ac", "/context-performance-measurement-server.xml");
    }
}
