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

import static org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.Vocabulary.*;
import static org.openehealth.ipf.platform.camel.ihe.xds.commons.transform.ebrs21.Ebrs21.*;

import java.util.List;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.Code;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.DocumentEntry;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.stub.ebrs21.rim.ClassificationType;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.stub.ebrs21.rim.ExtrinsicObjectType;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.stub.ebrs21.rim.SlotType1;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.transform.ebxml.IdentifiableTransformer;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.transform.ebxml.UriTransformer;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.transform.hl7.PatientInfoTransformer;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.transform.hl7.PersonTransformer;

/**
 * Transforms between a {@link DocumentEntry} and its ebXML 2.1 representation.
 * @author Jens Riemschneider
 */
public class DocumentEntryTransformer extends XDSMetaClassTransformer<ExtrinsicObjectType, DocumentEntry> {
    private final AuthorTransformer authorTransformer = new AuthorTransformer();
    private final CodeTransformer codeTransformer = new CodeTransformer();
    private final PersonTransformer personTransformer = new PersonTransformer();
    private final IdentifiableTransformer identifiableTransformer = new IdentifiableTransformer();
    private final PatientInfoTransformer patientInfoTransformer = new PatientInfoTransformer();
    private final UriTransformer uriTransformer = new UriTransformer();   
    
    /**
     * Constructs the transformer.
     */
    public DocumentEntryTransformer() {
        super(DOC_ENTRY_PATIENT_ID_EXTERNAL_ID, 
                DOC_ENTRY_LOCALIZED_STRING_PATIENT_ID, 
                DOC_ENTRY_UNIQUE_ID_EXTERNAL_ID,
                DOC_ENTRY_LOCALIZED_STRING_UNIQUE_ID);
    }
    
    @Override
    protected ExtrinsicObjectType createEbXMLInstance() {
        return createExtrinsicObject();
    }
    
    @Override
    protected DocumentEntry createMetaClassInstance() {
        return new DocumentEntry();
    }

    @Override
    protected void addAttributesFromEbXML(DocumentEntry docEntry, ExtrinsicObjectType extrinsic) {
        super.addAttributesFromEbXML(docEntry, extrinsic);
        docEntry.setMimeType(extrinsic.getMimeType());
    }

    @Override
    protected void addAttributes(DocumentEntry docEntry, ExtrinsicObjectType extrinsic) {
        super.addAttributes(docEntry, extrinsic);
        extrinsic.setMimeType(docEntry.getMimeType());
        extrinsic.setObjectType(DOC_ENTRY_CLASS_NODE);
    }

    @Override
    protected void addSlotsFromEbXML(DocumentEntry docEntry, ExtrinsicObjectType extrinsic) {
        super.addSlotsFromEbXML(docEntry, extrinsic);
        List<SlotType1> slots = extrinsic.getSlot();
        
        docEntry.setCreationTime(getSingleSlotValue(slots, SLOT_NAME_CREATION_TIME));
        docEntry.setHash(getSingleSlotValue(slots, SLOT_NAME_HASH));
        docEntry.setLanguageCode(getSingleSlotValue(slots, SLOT_NAME_LANGUAGE_CODE));
        docEntry.setServiceStartTime(getSingleSlotValue(slots, SLOT_NAME_SERVICE_START_TIME));
        docEntry.setServiceStopTime(getSingleSlotValue(slots, SLOT_NAME_SERVICE_STOP_TIME));
        docEntry.setUri(uriTransformer.fromEbXML(getSlotValues(slots, SLOT_NAME_URI)));
        
        String size = getSingleSlotValue(slots, SLOT_NAME_SIZE);
        docEntry.setSize(size != null ? Long.parseLong(size) : null);
        
        String hl7LegalAuthenticator = getSingleSlotValue(slots, SLOT_NAME_LEGAL_AUTHENTICATOR);
        docEntry.setLegalAuthenticator(personTransformer.fromHL7(hl7LegalAuthenticator));
        
        String sourcePatient = getSingleSlotValue(slots, SLOT_NAME_SOURCE_PATIENT_ID);
        docEntry.setSourcePatientID(identifiableTransformer.fromEbXML(sourcePatient));
        
        List<String> slotValues = getSlotValues(slots, SLOT_NAME_SOURCE_PATIENT_INFO);        
        docEntry.setSourcePatientInfo(patientInfoTransformer.fromHL7(slotValues));
    }
    
    @Override
    protected void addSlots(DocumentEntry docEntry, ExtrinsicObjectType extrinsic) {
        super.addSlots(docEntry, extrinsic);
        
        List<SlotType1> slots = extrinsic.getSlot();
        
        addSlot(slots, SLOT_NAME_CREATION_TIME, docEntry.getCreationTime());        
        addSlot(slots, SLOT_NAME_HASH, docEntry.getHash());
        addSlot(slots, SLOT_NAME_LANGUAGE_CODE, docEntry.getLanguageCode());
        addSlot(slots, SLOT_NAME_SERVICE_START_TIME, docEntry.getServiceStartTime());
        addSlot(slots, SLOT_NAME_SERVICE_STOP_TIME, docEntry.getServiceStopTime());        
        addSlot(slots, SLOT_NAME_URI, uriTransformer.toEbXML(docEntry.getUri()));
        
        Long size = docEntry.getSize();
        addSlot(slots, SLOT_NAME_SIZE, size != null ? size.toString() : null);
        
        String hl7LegalAuthenticator = personTransformer.toHL7(docEntry.getLegalAuthenticator());
        addSlot(slots, SLOT_NAME_LEGAL_AUTHENTICATOR, hl7LegalAuthenticator);
        
        String sourcePatient = identifiableTransformer.toEbXML(docEntry.getSourcePatientID());
        addSlot(slots, SLOT_NAME_SOURCE_PATIENT_ID, sourcePatient);
        
        List<String> slotValues = patientInfoTransformer.toHL7(docEntry.getSourcePatientInfo());
        addSlot(slots, SLOT_NAME_SOURCE_PATIENT_INFO, slotValues.toArray(new String[0]));
    }

    @Override
    protected void addClassificationsFromEbXML(DocumentEntry docEntry, ExtrinsicObjectType extrinsic) {
        super.addClassificationsFromEbXML(docEntry, extrinsic);
        List<ClassificationType> classifications = extrinsic.getClassification();
        
        ClassificationType author = getSingleClassification(classifications, DOC_ENTRY_AUTHOR_CLASS_SCHEME);
        docEntry.setAuthor(authorTransformer.fromEbXML21(author));
        
        ClassificationType classCode = getSingleClassification(classifications, DOC_ENTRY_CLASS_CODE_CLASS_SCHEME);
        docEntry.setClassCode(codeTransformer.fromEbXML21(classCode));

        ClassificationType formatCode = getSingleClassification(classifications, DOC_ENTRY_FORMAT_CODE_CLASS_SCHEME);
        docEntry.setFormatCode(codeTransformer.fromEbXML21(formatCode));

        ClassificationType healthcareFacility = getSingleClassification(classifications, DOC_ENTRY_HEALTHCARE_FACILITY_TYPE_CODE_CLASS_SCHEME);
        docEntry.setHealthcareFacilityTypeCode(codeTransformer.fromEbXML21(healthcareFacility));
        
        ClassificationType practiceSetting = getSingleClassification(classifications, DOC_ENTRY_PRACTICE_SETTING_CODE_CLASS_SCHEME);
        docEntry.setPracticeSettingCode(codeTransformer.fromEbXML21(practiceSetting));
        
        ClassificationType typeCode = getSingleClassification(classifications, DOC_ENTRY_TYPE_CODE_CLASS_SCHEME);
        docEntry.setTypeCode(codeTransformer.fromEbXML21(typeCode));
        
        List<Code> confidentialityCodes = docEntry.getConfidentialityCodes();
        for (ClassificationType code : getClassifications(classifications, DOC_ENTRY_CONFIDENTIALITY_CODE_CLASS_SCHEME)) {
            confidentialityCodes.add(codeTransformer.fromEbXML21(code));
        }

        List<Code> eventCodeList = docEntry.getEventCodeList();
        for (ClassificationType code : getClassifications(classifications, DOC_ENTRY_EVENT_CODE_CLASS_SCHEME)) {
            eventCodeList.add(codeTransformer.fromEbXML21(code));
        }
    }

    @Override
    protected void addClassifications(DocumentEntry docEntry, ExtrinsicObjectType extrinsic) {
        super.addClassifications(docEntry, extrinsic);
        
        List<ClassificationType> classifications = extrinsic.getClassification();
        
        ClassificationType author = authorTransformer.toEbXML21(docEntry.getAuthor());
        addClassification(classifications, author, extrinsic, DOC_ENTRY_AUTHOR_CLASS_SCHEME);
        
        ClassificationType classCode = codeTransformer.toEbXML21(docEntry.getClassCode());
        addClassification(classifications, classCode, extrinsic, DOC_ENTRY_CLASS_CODE_CLASS_SCHEME);
        
        ClassificationType formatCode = codeTransformer.toEbXML21(docEntry.getFormatCode());
        addClassification(classifications, formatCode, extrinsic, DOC_ENTRY_FORMAT_CODE_CLASS_SCHEME);

        ClassificationType healthcareFacility = codeTransformer.toEbXML21(docEntry.getHealthcareFacilityTypeCode());
        addClassification(classifications, healthcareFacility, extrinsic, DOC_ENTRY_HEALTHCARE_FACILITY_TYPE_CODE_CLASS_SCHEME);
        
        ClassificationType practiceSetting = codeTransformer.toEbXML21(docEntry.getPracticeSettingCode());
        addClassification(classifications, practiceSetting, extrinsic, DOC_ENTRY_PRACTICE_SETTING_CODE_CLASS_SCHEME);
        
        ClassificationType typeCode = codeTransformer.toEbXML21(docEntry.getTypeCode());
        addClassification(classifications, typeCode, extrinsic, DOC_ENTRY_TYPE_CODE_CLASS_SCHEME);
        
        for (Code confCode : docEntry.getConfidentialityCodes()) {
            ClassificationType conf = codeTransformer.toEbXML21(confCode);
            addClassification(classifications, conf, extrinsic, DOC_ENTRY_CONFIDENTIALITY_CODE_CLASS_SCHEME);
        }
        
        for (Code eventCode : docEntry.getEventCodeList()) {
            ClassificationType event = codeTransformer.toEbXML21(eventCode);
            addClassification(classifications, event, extrinsic, DOC_ENTRY_EVENT_CODE_CLASS_SCHEME);
        }
    }
}
