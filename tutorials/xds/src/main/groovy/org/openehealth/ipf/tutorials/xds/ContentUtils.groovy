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
package org.openehealth.ipf.tutorials.xds

import org.apache.commons.io.IOUtils

import javax.activation.DataHandler
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.io.InputStream
import java.io.IOException

/**
 * Utility functionality for document content.
 * @author Jens Riemschneider
 */
abstract class ContentUtils {
    private ContentUtils() {
        throw new UnsupportedOperationException('Cannot be instantiated')
    }
    
    /**
     * Calculates the size of the given content stream.
     * @param dataHandler
     *          the data handler to access the content.
     * @return the size in bytes.
     */
    static def size(dataHandler) {
        getContent(dataHandler).length
    }

    /**
     * Calculates the SHA-1 of the given content stream.
     * @param dataHandler
     *          the data handler to access the content.
     * @return the SHA-1.
     */
    static def sha1(dataHandler) {
        calcSha1(getContent(dataHandler))
    }

    private static def calcSha1(content) {
        def digest = MessageDigest.getInstance('SHA-1')
        def builder = new StringBuilder()
        digest.digest(content).each {
            def hexString = Integer.toHexString((int)it & 0xff)
            builder.append(hexString.length() == 2 ? hexString : '0' + hexString)
        }
        builder.toString()
    }

    static def getContent(dataHandler) {
        def inputStream = dataHandler.inputStream
        try {
            IOUtils.toByteArray(inputStream)
        }
        finally {
            inputStream.close()
        }
    }
}
