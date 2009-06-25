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
package org.openehealth.ipf.platform.camel.ihe.xds.commons.transform.ebrs21;

import static org.junit.Assert.*;
import static org.openehealth.ipf.platform.camel.ihe.xds.commons.transform.ebrs21.Ebrs21TestUtils.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.Address;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.Author;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.AvailabilityStatus;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.EntryUUID;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.PatientInfo;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.Recipient;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.SubmissionSet;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.UniqueID;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.Vocabulary;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.stub.ebrs21.rim.ClassificationType;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.stub.ebrs21.rim.RegistryPackageType;

/**
 * Tests for {@link SubmissionSetTransformer}.
 * @author Jens Riemschneider
 */
public class SubmissionSetTransformerTest {
    private SubmissionSetTransformer transformer;
    private SubmissionSet set;
    
    @Before
    public void setUp() {
        transformer = new SubmissionSetTransformer();

        Author author = new Author();
        author.setAuthorPerson(createPerson(1));
        author.getAuthorInstitution().add("inst1");
        author.getAuthorInstitution().add("inst2");
        author.getAuthorRole().add("role1");
        author.getAuthorRole().add("role2");
        author.getAuthorSpecialty().add("spec1");
        author.getAuthorSpecialty().add("spec2");
        
        Address address = new Address();
        address.setCity("city");
        address.setCountry("country");
        address.setCountyParishCode("countyParishCode");
        address.setOtherDesignation("otherDesignation");
        address.setStateOrProvince("stateOrProvince");
        address.setStreetAddress("streetAddress");
        address.setZipOrPostalCode("zipOrPostalCode");
        
        PatientInfo sourcePatientInfo = new PatientInfo();
        sourcePatientInfo.setAddress(address);
        sourcePatientInfo.setDateOfBirth("dateOfBirth");
        sourcePatientInfo.setGender("F");
        sourcePatientInfo.setName(createName(3));
        sourcePatientInfo.getIds().add(createIdentifiable(5));
        sourcePatientInfo.getIds().add(createIdentifiable(6));

        set = new SubmissionSet();
        set.setAuthor(author);
        set.setAvailabilityStatus(AvailabilityStatus.APPROVED);
        set.setComments(createLocal(10));
        set.setSubmissionTime("123");
        set.setEntryUUID(new EntryUUID("uuid"));
        set.setPatientID(createIdentifiable(3));
        set.setTitle(createLocal(11));
        set.setUniqueID(new UniqueID("uniqueId"));
        set.setContentTypeCode(createCode(6));
        set.setSourceID("sourceId");
        set.getIntendedRecipients().add(new Recipient(createOrganization(20), createPerson(22)));
        set.getIntendedRecipients().add(new Recipient(createOrganization(21), null));
        set.getIntendedRecipients().add(new Recipient(null, createPerson(23)));
    }

    @Test
    public void testToEbXML21() {
        RegistryPackageType ebXML = transformer.toEbXML21(set);        
        assertNotNull(ebXML);
        
        assertEquals("Approved", ebXML.getStatus());
        assertEquals("uuid", ebXML.getId());
        assertNull(ebXML.getObjectType());
        
        assertEquals(createLocal(10), toLocal(ebXML.getDescription()));        
        assertEquals(createLocal(11), toLocal(ebXML.getName()));
        
        assertSlot(Vocabulary.SLOT_NAME_SUBMISSION_TIME, ebXML.getSlot(), "123");
        
        assertSlot(Vocabulary.SLOT_NAME_INTENDED_RECIPIENT, ebXML.getSlot(), 
                "orgName 20^^id 20^namespace 20&uni 20&uniType 20|id 22^familyName 22^givenName 22^prefix 22^second 22^suffix 22^^^namespace 22&uni 22&uniType 22",
                "orgName 21^^id 21^namespace 21&uni 21&uniType 21",
                "|id 23^familyName 23^givenName 23^prefix 23^second 23^suffix 23^^^namespace 23&uni 23&uniType 23");

        
        List<ClassificationType> classifications = ebXML.getClassification();
        
        ClassificationType classification = assertClassification(Vocabulary.SUBMISSION_SET_AUTHOR_CLASS_SCHEME, classifications, 0, ebXML, "", -1);
        assertSlot(Vocabulary.SLOT_NAME_AUTHOR_PERSON, classification.getSlot(), "id 1^familyName 1^givenName 1^prefix 1^second 1^suffix 1^^^namespace 1&uni 1&uniType 1");
        assertSlot(Vocabulary.SLOT_NAME_AUTHOR_INSTITUTION, classification.getSlot(), "inst1", "inst2");
        assertSlot(Vocabulary.SLOT_NAME_AUTHOR_ROLE, classification.getSlot(), "role1", "role2");
        assertSlot(Vocabulary.SLOT_NAME_AUTHOR_SPECIALTY, classification.getSlot(), "spec1", "spec2");
        
        classification = assertClassification(Vocabulary.SUBMISSION_SET_CONTENT_TYPE_CODE_CLASS_SCHEME, classifications, 0, ebXML, "code 6", 6);
        assertSlot(Vocabulary.SLOT_NAME_CODING_SCHEME, classification.getSlot(), "scheme 6");
        
        
        assertExternalIdentifier(Vocabulary.SUBMISSION_SET_PATIENT_ID_EXTERNAL_ID, ebXML.getExternalIdentifier(), 
                "id 3^^^namespace 3&uni 3&uniType 3", Vocabulary.SUBMISSION_SET_LOCALIZED_STRING_PATIENT_ID);

        assertExternalIdentifier(Vocabulary.SUBMISSION_SET_UNIQUE_ID_EXTERNAL_ID, ebXML.getExternalIdentifier(), 
                "uniqueId", Vocabulary.SUBMISSION_SET_LOCALIZED_STRING_UNIQUE_ID);

        assertExternalIdentifier(Vocabulary.SUBMISSION_SET_SOURCE_ID_EXTERNAL_ID, ebXML.getExternalIdentifier(), 
                "sourceId", Vocabulary.SUBMISSION_SET_LOCALIZED_STRING_SOURCE_ID);
        
        assertEquals(2, classifications.size());
        assertEquals(2, ebXML.getSlot().size());
        assertEquals(3, ebXML.getExternalIdentifier().size());
    }

    @Test
    public void testToEbXML21Null() {
        assertNull(transformer.toEbXML21(null));
    }
   
    @Test
    public void testToEbXML21Empty() {
        RegistryPackageType ebXML = transformer.toEbXML21(new SubmissionSet());        
        assertNotNull(ebXML);
        
        assertNull(ebXML.getStatus());
        assertNull(ebXML.getId());
        
        assertNull(ebXML.getDescription());        
        assertNull(ebXML.getName());
        
        assertEquals(0, ebXML.getSlot().size());
        assertEquals(0, ebXML.getClassification().size());
        assertEquals(0, ebXML.getExternalIdentifier().size());
    }
    
    
    
    @Test
    public void testFromEbXML21() {
        RegistryPackageType ebXML = transformer.toEbXML21(set);
        SubmissionSet result = transformer.fromEbXML21(ebXML);
        
        assertNotNull(result);
        assertEquals(set, result);
    }
    
    @Test
    public void testFromEbXML21Null() {
        assertNull(transformer.fromEbXML21(null));
    }
    
    @Test
    public void testFromEbXML21Empty() {
        SubmissionSet result = transformer.fromEbXML21(new RegistryPackageType());
        assertEquals(new SubmissionSet(), result);
    }
}
