/*
 * $Id: InternalSchemaBuilderBase.java,v 1.2 2006-04-13 01:31:45 ofung Exp $
 */

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.xml.rpc.processor.schema;

import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.ProcessorOptions;
import com.sun.xml.rpc.processor.model.ModelException;
import com.sun.xml.rpc.util.xml.XmlUtil;
import com.sun.xml.rpc.wsdl.framework.AbstractDocument;
import com.sun.xml.rpc.wsdl.framework.ValidationException;

import com.sun.xml.rpc.wsdl.document.schema.SchemaAttribute;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import com.sun.xml.rpc.wsdl.document.schema.SchemaKinds;
import com.sun.xml.rpc.wsdl.document.schema.SchemaElement;
import com.sun.xml.rpc.wsdl.document.schema.SchemaEntity;

import com.sun.xml.rpc.wsdl.document.soap.SOAPConstants;
import com.sun.xml.rpc.wsdl.parser.Constants;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class InternalSchemaBuilderBase {
    private boolean _noDataBinding = false;
    public InternalSchemaBuilderBase(AbstractDocument document,
        Properties options) {
            
        _document = document;
        _schema = new InternalSchema(this);
        createWellKnownTypes();
        createWellKnownAttributes();
        createWellKnownAttributeGroups();
        createWellKnownElements();
        _noDataBinding =
            Boolean
            .valueOf(
                    options.getProperty(
                            ProcessorOptions.NO_DATA_BINDING_PROPERTY))
            .booleanValue();
    }
    
    public InternalSchema getSchema() {
        return _schema;
    }
    
    public TypeDefinitionComponent buildTypeDefinition(QName name) {
        boolean createdTypeComponentMap = false;
        if (_namedTypeComponentsBeingDefined == null) {
            _namedTypeComponentsBeingDefined = new HashMap();
            createdTypeComponentMap = true;
        }
        
        try {
            TypeDefinitionComponent component =
                (TypeDefinitionComponent) _wellKnownTypes.get(name);
            if (component != null) {
                return component;
            }
            
            SchemaEntity entity =
                (SchemaEntity) _document.find(SchemaKinds.XSD_TYPE, name);
            SchemaElement element = entity.getElement();
            component = buildTopLevelTypeDefinition(element, _schema);
            _schema.add(component);
            
            return component;
        } catch (ValidationException e) {
            throw new ModelException(e);
        } finally {
            if (createdTypeComponentMap) {
                _namedTypeComponentsBeingDefined = null;
            }
        }
    }
    
    public AttributeDeclarationComponent buildAttributeDeclaration(QName name) {
        try {
            AttributeDeclarationComponent component =
                (AttributeDeclarationComponent) _wellKnownAttributes.get(name);
            if (component != null) {
                return component;
            }
            
            SchemaEntity entity =
                (SchemaEntity) _document.find(SchemaKinds.XSD_ATTRIBUTE, name);
            SchemaElement element = entity.getElement();
            component = buildTopLevelAttributeDeclaration(element, _schema);
            _schema.add(component);
            
            return component;
        } catch (ValidationException e) {
            throw new ModelException(e);
        }
    }
    
    public ElementDeclarationComponent buildElementDeclaration(QName name) {
        try {
            ElementDeclarationComponent component =
                (ElementDeclarationComponent) _wellKnownElements.get(name);
            if (component != null) {
                return component;
            }
            
            SchemaEntity entity =
                (SchemaEntity) _document.find(SchemaKinds.XSD_ELEMENT, name);
            SchemaElement element = entity.getElement();
            component = buildTopLevelElementDeclaration(element, _schema);
            return component;
        } catch (ValidationException e) {
            throw new ModelException(e);
        }
    }
    
    public AttributeGroupDefinitionComponent buildAttributeGroupDefinition(
        QName name) {
            
        try {
            AttributeGroupDefinitionComponent component =
                (AttributeGroupDefinitionComponent)
                    _wellKnownAttributeGroups.get(name);
            if (component != null) {
                return component;
            }
            
            SchemaEntity entity = (SchemaEntity)
                _document.find(SchemaKinds.XSD_ATTRIBUTE_GROUP, name);
            SchemaElement element = entity.getElement();
            component = buildTopLevelAttributeGroupDefinition(element, _schema);
            _schema.add(component);
            
            return component;
        } catch (ValidationException e) {
            throw new ModelException(e);
        }
    }
    
    public ModelGroupDefinitionComponent buildModelGroupDefinition(QName name) {
        
        // TODO - implement this
        failUnimplemented("F002");
        return null; // keep compiler happy
    }
    
    public ComplexTypeDefinitionComponent getUrType() {
        return _urType;
    }
    
    public SimpleTypeDefinitionComponent getSimpleUrType() {
        return _simpleUrType;
    }
    
    protected ElementDeclarationComponent buildTopLevelElementDeclaration(
        SchemaElement element, InternalSchema schema) {
        
        ElementDeclarationComponent component =
          new ElementDeclarationComponent();

        // property: name, targetNamespace
        String nameAttr =
            element.getValueOfMandatoryAttribute(Constants.ATTR_NAME);
        component.setName(new QName(element.getSchema().getTargetNamespaceURI(),
            nameAttr));
        
        //java.net Issue#9 and CR 5104509 fix for circular references
        _schema.add(component);

        // SPEC - 3.3.2
        internalBuildElementDeclaration(component, element, schema);
        if (element.getValueOfAttributeOrNull(
            Constants.ATTR_MIN_OCCURS) != null) {
                
            failValidation("validation.invalidAttribute",
                Constants.ATTR_MIN_OCCURS, element.getLocalName());
        }
        
        if (element.getValueOfAttributeOrNull(
            Constants.ATTR_MAX_OCCURS) != null) {
                
            failValidation("validation.invalidAttribute",
                Constants.ATTR_MAX_OCCURS, element.getLocalName());
        }
        return component;
    }
    
    protected AttributeGroupDefinitionComponent
        buildTopLevelAttributeGroupDefinition(SchemaElement element,
            InternalSchema schema) {
        
        // SPEC - 3.6
        AttributeGroupDefinitionComponent component =
            new AttributeGroupDefinitionComponent();
        
        // property: name, targetNamespace
        String nameAttr =
            element.getValueOfMandatoryAttribute(Constants.ATTR_NAME);
        component.setName(new QName(element.getSchema().getTargetNamespaceURI(),
            nameAttr));
        
        // property: attribute uses, attribute wildcard
        for (Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement) iter.next();
            if (child.getQName().equals(SchemaConstants.QNAME_ATTRIBUTE)) {
                component.addAttributeUse(
                    buildAttributeUse(child, null, schema));
            } else if (child.getQName().equals(
                SchemaConstants.QNAME_ATTRIBUTE_GROUP)) {
                    
                String refAttr = child.getValueOfMandatoryAttribute(
                    Constants.ATTR_REF);
                component.addAttributeGroup(schema.findAttributeGroupDefinition(
                    child.asQName(refAttr)));
            } else if (child.getQName().equals(
                SchemaConstants.QNAME_ANY_ATTRIBUTE)) {
                    
                failUnimplemented("F003");
            } else if (child.getQName().equals(
                SchemaConstants.QNAME_ANNOTATION)) {
                    
                // ignore it
                continue;
            } else {
                failValidation("validation.invalidElement",
                    child.getLocalName());
            }
        }
        return component;
    }
    
    protected AttributeDeclarationComponent buildTopLevelAttributeDeclaration(
        SchemaElement element, InternalSchema schema) {
        
        // SPEC - 3.2.2
        AttributeDeclarationComponent component =
            new AttributeDeclarationComponent();
        
        // property: name, targetNamespace
        String nameAttr =
            element.getValueOfMandatoryAttribute(Constants.ATTR_NAME);
        
        component.setName(new QName(
            element.getSchema().getTargetNamespaceURI(), nameAttr));
        
        // property: scope
        component.setScope(null);
        
        // property: type definition
        boolean foundType = false;
        for (Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement) iter.next();
            if (child.getQName().equals(SchemaConstants.QNAME_SIMPLE_TYPE)) {
                if (foundType) {
                    failValidation("validation.invalidElement",
                        element.getLocalName());
                }
                component.setTypeDefinition(
                    buildSimpleTypeDefinition(child, schema));
                foundType = true;
            } else if (child.getQName().equals(
                SchemaConstants.QNAME_ANNOTATION)) {
                    
                // ignore it
                    
            } else {
                failValidation("validation.invalidElement",
                    child.getLocalName());
            }
        }
        if (foundType) {
            assertNoAttribute(element, Constants.ATTR_TYPE);
        } else {
            String typeAttr = element.getValueOfAttributeOrNull(
                Constants.ATTR_TYPE);
            if (typeAttr == null) {
                component.setTypeDefinition(getSimpleUrType());
            } else {
                TypeDefinitionComponent typeComponent =
                    schema.findTypeDefinition(element.asQName(typeAttr));
                if (typeComponent instanceof SimpleTypeDefinitionComponent) {
                    component.setTypeDefinition(
                        (SimpleTypeDefinitionComponent) typeComponent);
                } else {
                    failValidation("validation.notSimpleType",
                        component.getName().getLocalPart());
                }
            }
        }
        
        // property: annotation
        component.setAnnotation(buildNonSchemaAttributesAnnotation(element));
        
        // property: value constraint
        // NOTE - here values should be parsed with respect to the simple type
        String defaultAttr =
            element.getValueOfAttributeOrNull(Constants.ATTR_DEFAULT);
        String fixedAttr =
            element.getValueOfAttributeOrNull(Constants.ATTR_FIXED);
        if (defaultAttr != null && fixedAttr != null) {
            fail("validation.exclusiveAttributes",
                Constants.ATTR_DEFAULT, Constants.ATTR_FIXED);
        }
        if (defaultAttr != null) {
            component.setValue(defaultAttr);
            component.setValueKind(Symbol.DEFAULT);
        }
        if (fixedAttr != null) {
            component.setValue(defaultAttr);
            component.setValueKind(Symbol.FIXED);
        }
        
        return component;
    }
    
    protected void processElementParticle(SchemaElement element,
        ParticleComponent component,
        ComplexTypeDefinitionComponent scope,
        InternalSchema schema) {
        
        // SPEC - 3.9
        component.setTermTag(ParticleComponent.TERM_ELEMENT);
        
        // SPEC - 3.3.2
        ElementDeclarationComponent term =
            new ElementDeclarationComponent();

        // property: term
        internalBuildElementDeclaration(term, element, schema);

        String refAttr = element.getValueOfAttributeOrNull(Constants.ATTR_REF);
        if (refAttr != null) {
            
            //bug fix: 4859814, support for xsd:ref in element
            term = schema.findElementDeclaration(element.asQName(refAttr));
        }else {
            
            //     property: name, target namespace
            String nameAttr =
                element.getValueOfMandatoryAttribute(Constants.ATTR_NAME);
            String formAttr =
                element.getValueOfAttributeOrNull(Constants.ATTR_FORM);
            if (formAttr == null) {
                formAttr = element.getRoot().getValueOfAttributeOrNull(
                    Constants.ATTR_ELEMENT_FORM_DEFAULT);
                if (formAttr == null) {
                    formAttr = "";
                }
            }
            if (formAttr.equals(Constants.ATTRVALUE_QUALIFIED)) {
                term.setName(new QName(
                    element.getSchema().getTargetNamespaceURI(), nameAttr));
            } else {
                term.setName(new QName(nameAttr));
            }
        }
        
        //     property: scope
        term.setScope(scope);
        
        component.setTermTag(ParticleComponent.TERM_ELEMENT);
        component.setElementTerm(term);
    }
    
    protected void internalBuildElementDeclaration(
            ElementDeclarationComponent component, SchemaElement element, InternalSchema schema) {
        // property: type definition
        boolean foundType = false;
        for (Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement) iter.next();
            if (child.getQName().equals(SchemaConstants.QNAME_SIMPLE_TYPE)) {
                if (foundType) {
                    failValidation("validation.invalidElement",
                        element.getLocalName());
                }
                component.setTypeDefinition(
                    buildSimpleTypeDefinition(child, schema));
                foundType = true;
            } else if (child.getQName().equals(
                SchemaConstants.QNAME_COMPLEX_TYPE)) {
                    
                if (foundType) {
                    failValidation("validation.invalidElement",
                        element.getLocalName());
                }
                component.setTypeDefinition(
                    buildComplexTypeDefinition(child, schema));
                foundType = true;
            }
        }
        if (foundType) {
            assertNoAttribute(element, Constants.ATTR_TYPE);
            assertNoAttribute(element, Constants.ATTR_SUBSTITUTION_GROUP);
        } else {
            String typeAttr = element.getValueOfAttributeOrNull(
                Constants.ATTR_TYPE);
            String substitutionGroupAttr = element.getValueOfAttributeOrNull(
                Constants.ATTR_SUBSTITUTION_GROUP);
            if (typeAttr == null && substitutionGroupAttr == null) {
                component.setTypeDefinition(getUrType());
            } else {
                if (typeAttr != null && substitutionGroupAttr != null && !_noDataBinding) {
                    failValidation("validation.exclusiveAttributes",
                        Constants.ATTR_TYPE, Constants.ATTR_SUBSTITUTION_GROUP);
                } else if (typeAttr != null) {
                    component.setTypeDefinition(
                        schema.findTypeDefinition(element.asQName(typeAttr)));
                } else {
                    
                    // TODO - implement this
                    failUnimplemented("F005");
                }
            }
        }
        
        // property: nillable
        component.setNillable(element.getValueOfBooleanAttributeOrDefault(
            Constants.ATTR_NILLABLE, false));
        
        // property: value constraint
        // NOTE - here values should be parsed with respect to the simple type
        String defaultAttr =
            element.getValueOfAttributeOrNull(Constants.ATTR_DEFAULT);
        String fixedAttr =
            element.getValueOfAttributeOrNull(Constants.ATTR_FIXED);
        if (defaultAttr != null && fixedAttr != null) {
            fail("validation.exclusiveAttributes", Constants.ATTR_DEFAULT,
                Constants.ATTR_FIXED);
        }
        if (defaultAttr != null) {
            component.setValue(defaultAttr);
            component.setValueKind(Symbol.DEFAULT);
            if (component.getTypeDefinition() instanceof
                ComplexTypeDefinitionComponent) {
                    
                failValidation("validation.notSimpleType",
                    component.getName().getLocalPart());
            }
        }
        if (fixedAttr != null) {
            component.setValue(defaultAttr);
            component.setValueKind(Symbol.FIXED);
            if (component.getTypeDefinition() instanceof
                ComplexTypeDefinitionComponent) {
                    
                failValidation("validation.notSimpleType",
                    component.getName().getLocalPart());
            }
        }
        
        // property: identity-constraint definitions
        for (Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement) iter.next();
            if (child.getQName().equals(SchemaConstants.QNAME_KEY) ||
                child.getQName().equals(SchemaConstants.QNAME_KEYREF) ||
                child.getQName().equals(SchemaConstants.QNAME_UNIQUE)) {
                    
                // TODO - implement this
                failUnimplemented("F006");
            }
        }
        
        // property: substitution-group affiliation
        QName substitutionGroupAttr = element.getValueOfQNameAttributeOrNull(
            Constants.ATTR_SUBSTITUTION_GROUP);
        if (substitutionGroupAttr != null) {
            
            // TODO - implement this
            failUnimplemented("F007");
        }
        
        // property: disallowed substitutions
        String blockAttr =
            element.getValueOfAttributeOrNull(Constants.ATTR_BLOCK);
        if (blockAttr == null) {
            blockAttr = element.getRoot().getValueOfAttributeOrNull(
                Constants.ATTR_BLOCK_DEFAULT);
            if (blockAttr == null) {
                blockAttr = "";
            }
        }
        if (blockAttr.equals("")) {
            
            // no disallowed substitutions
            component.setDisallowedSubstitutions(_setEmpty);
        } else if (blockAttr.equals(Constants.ATTRVALUE_ALL)) {
            component.setDisallowedSubstitutions(_setExtResSub);
        } else {
            component.setDisallowedSubstitutions(parseSymbolSet(blockAttr,
                _setExtResSub));
            
            // TODO - implement this
            failUnimplemented("F008");
        }
        
        // property: substitution group exclusions
        String finalAttr =
            element.getValueOfAttributeOrNull(Constants.ATTR_FINAL);
        if (finalAttr == null) {
            finalAttr = element.getRoot().getValueOfAttributeOrNull(
                Constants.ATTR_FINAL_DEFAULT);
            if (finalAttr == null) {
                finalAttr = "";
            }
        }
        if (finalAttr.equals("")) {
            
            // no disallowed substitutions
            component.setSubstitutionsGroupExclusions(_setEmpty);
        } else if (finalAttr.equals(Constants.ATTRVALUE_ALL)) {
            component.setSubstitutionsGroupExclusions(_setExtRes);
        } else {
            component.setSubstitutionsGroupExclusions(parseSymbolSet(finalAttr,
                _setExtRes));
            
            // TODO - implement this
            failUnimplemented("F009");
        }
        
        // property: abstract
        component.setAbstract(element.getValueOfBooleanAttributeOrDefault(
            Constants.ATTR_ABSTRACT, false));
        
        // NOTE - annotations are ignored
    }
    
    protected TypeDefinitionComponent buildTopLevelTypeDefinition(
        SchemaElement element, InternalSchema schema) {
            
        TypeDefinitionComponent component = null;
        
        if (element.getQName().equals(SchemaConstants.QNAME_SIMPLE_TYPE)) {
            component = buildSimpleTypeDefinition(element, schema);
        } else if (element.getQName().equals(
            SchemaConstants.QNAME_COMPLEX_TYPE)) {
                
            component = buildComplexTypeDefinition(element, schema);
        } else {
            failValidation("validation.invalidElement", element.getLocalName());
        }
        
        return component;
    }
    
    protected SimpleTypeDefinitionComponent buildSimpleTypeDefinition(
        SchemaElement element, InternalSchema schema) {
        
        // SPEC - 3.14
        
        SimpleTypeDefinitionComponent component =
            new SimpleTypeDefinitionComponent();
        
        // property: name, targetNamespace
        String nameAttr =
            element.getValueOfAttributeOrNull(Constants.ATTR_NAME);
        
        //fix for bug: 4851694
        if ((nameAttr != null) &&
        element.getParent().getQName().equals(SchemaConstants.QNAME_ELEMENT)) {
            failValidation("validation.invalidSimpleTypeInElement", nameAttr,
                element.getParent().getValueOfAttributeOrNull(
                    Constants.ATTR_NAME));
        }
        if (nameAttr != null) {
            component.setName(new QName(
                element.getSchema().getTargetNamespaceURI(), nameAttr));
            
            // register it so that we avoid loops
            _namedTypeComponentsBeingDefined.put(
                component.getName(), component);
        }
        
        boolean gotOne = false;
        for (Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement) iter.next();
            if (gotOne) {
                
                // must have only one child
                failValidation("validation.invalidElement",
                    child.getLocalName());
            }
            
            if (child.getQName().equals(SchemaConstants.QNAME_RESTRICTION)) {
                buildRestrictionSimpleTypeDefinition(component, child, schema);
                gotOne = true;
            } else if (child.getQName().equals(SchemaConstants.QNAME_LIST)) {
                buildListSimpleTypeDefinition(component, child, schema);
                gotOne = true;
            } else if (child.getQName().equals(SchemaConstants.QNAME_UNION)) {
                failUnimplemented("F011");
            } else if (child.getQName().equals(
                SchemaConstants.QNAME_ANNOTATION)) {
                    
                continue;
            } else {
                failValidation("validation.invalidElement",
                    child.getLocalName());
            }
        }
        
        return component;
    }
    
    protected void buildRestrictionSimpleTypeDefinition(
        SimpleTypeDefinitionComponent component,
        SchemaElement element,
        InternalSchema schema) {
        
        // property: base type definition
        String baseAttr =
            element.getValueOfAttributeOrNull(Constants.ATTR_BASE);
        if (baseAttr != null) {
            TypeDefinitionComponent base =
                schema.findTypeDefinition(element.asQName(baseAttr));
            if (base.isSimple()) {
                component.setBaseTypeDefinition(
                    (SimpleTypeDefinitionComponent) base);
            } else {
                failValidation("validation.notSimpleType",
                    base.getName().getLocalPart());
            }
        } else {
            failUnimplemented("F012");
        }
        
        // property: variety
        component.setVarietyTag(
            component.getBaseTypeDefinition().getVarietyTag());
        
        // property: primitive type definition
        component.setPrimitiveTypeDefinition(
            component.getBaseTypeDefinition().getPrimitiveTypeDefinition());
        
        // property: final
        String finalAttr =
            element.getValueOfAttributeOrNull(Constants.ATTR_FINAL);
        if (finalAttr == null) {
            finalAttr = element.getRoot().getValueOfAttributeOrNull(
                Constants.ATTR_FINAL_DEFAULT);
            if (finalAttr == null) {
                finalAttr = "";
            }
        }
        if (finalAttr.equals("")) {
            
            // no disallowed substitutions
            component.setFinal(_setEmpty);
        } else if (finalAttr.equals(Constants.ATTRVALUE_ALL)) {
            component.setFinal(_setExtResListUnion);
        } else {
            component.setFinal(parseSymbolSet(finalAttr, _setExtResListUnion));

            // TODO - implement this
            failUnimplemented("F013");
        }
        
        // right now, we only support the enumeration facet
        boolean gotOne = false;
        EnumerationFacet enumeration = new EnumerationFacet();
        for (Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement) iter.next();
            gotOne = true;
            if (child.getQName().equals(SchemaConstants.QNAME_ENUMERATION)) {
                
                //bug fix: 4927847
                if(baseAttr.equals("xsd:QName")) {
                    addPrefixesToEnumerationFacet(enumeration, element);
                }
                String valueAttr =
                    child.getValueOfAttributeOrNull(Constants.ATTR_VALUE);
                if (valueAttr == null) {
                    failValidation("validation.missingRequiredAttribute",
                        Constants.ATTR_VALUE, child.getQName().getLocalPart());
                }
                enumeration.addValue(valueAttr);
            } else if (child.getQName().equals(SchemaConstants.QNAME_LENGTH) ||
                child.getQName().equals(SchemaConstants.QNAME_MAX_INCLUSIVE) ||
                child.getQName().equals(SchemaConstants.QNAME_MIN_INCLUSIVE) ||
                child.getQName().equals(SchemaConstants.QNAME_MIN_EXCLUSIVE) ||
                child.getQName().equals(SchemaConstants.QNAME_MAX_EXCLUSIVE) ||
                child.getQName().equals(SchemaConstants.QNAME_MAX_LENGTH) ||
                child.getQName().equals(SchemaConstants.QNAME_MIN_LENGTH) ||
                child.getQName().equals(SchemaConstants.QNAME_PATTERN) ||
                child.getQName().equals(SchemaConstants.QNAME_TOTAL_DIGITS) ||
                child.getQName().equals(SchemaConstants.QNAME_FRACTION_DIGITS) ||
                child.getQName().equals(SchemaConstants.QNAME_WHITE_SPACE)){
                    
                // bug fix 4893905, added all the facets
                // DOUG TODO do something here?
                continue;
            } else {
                failUnimplemented("F014");
            }
        }
        
        //add facets if there're values
        if(enumeration.values().hasNext()) {
            component.addFacet(enumeration);
        }
    }
    
    /**
     * Build a prefix,nsURI map in the EnumerationFacet. This map latter
     * will be used to resolve the prefix while building
     * JavaEnuerationType for xsd:QName in SchemaAnalyzerBase.
     *
     * @param enumeration
     * @param element
     */
    private void addPrefixesToEnumerationFacet(EnumerationFacet enumeration,
        SchemaElement element) {
            
        for(Iterator iter = element.prefixes();iter.hasNext();) {
            String pfix = (String)iter.next();
            if(pfix != null) {
                String ns = element.getURIForPrefix(pfix);
                if(ns != null) {
                    enumeration.addPrefix(pfix, ns);
                }
            }
        }
        SchemaElement p =  element.getParent();
        if(p != null) {
            addPrefixesToEnumerationFacet(enumeration, p);
        }
    }
    
    protected void buildListSimpleTypeDefinition(
        SimpleTypeDefinitionComponent component,
        SchemaElement element,
        InternalSchema schema) {
        
        // property: base type definition
        component.setBaseTypeDefinition(getSimpleUrType());
        
        // property: item type
        String itemTypeAttr =
            element.getValueOfAttributeOrNull(Constants.ATTR_ITEM_TYPE);
        if (itemTypeAttr != null) {
            TypeDefinitionComponent itemType =
                schema.findTypeDefinition(element.asQName(itemTypeAttr));
            if (itemType.isSimple()) {
                component.setItemTypeDefinition(
                    (SimpleTypeDefinitionComponent) itemType);
            } else {
                failValidation("validation.notSimpleType",
                    itemType.getName().getLocalPart());
            }
        } else {
            SchemaElement simpleTypeElement =
                getOnlyChildIgnoring(element, SchemaConstants.QNAME_ANNOTATION);
            if (!simpleTypeElement.getQName().equals(
                SchemaConstants.QNAME_SIMPLE_TYPE)) {
                    
                failValidation("validation.invalidElement",
                    simpleTypeElement.getLocalName());
            }
            
            TypeDefinitionComponent itemType =
                buildSimpleTypeDefinition(simpleTypeElement, schema);
            component.setItemTypeDefinition(
                (SimpleTypeDefinitionComponent) itemType);
        }
        
        // property: variety
        component.setVarietyTag(SimpleTypeDefinitionComponent.VARIETY_LIST);
        
        
        // property: final
        String finalAttr =
            element.getValueOfAttributeOrNull(Constants.ATTR_FINAL);
        if (finalAttr == null) {
            finalAttr = element.getRoot().getValueOfAttributeOrNull(
                Constants.ATTR_FINAL_DEFAULT);
            if (finalAttr == null) {
                finalAttr = "";
            }
        }
        if (finalAttr.equals("")) {
            
            // no disallowed substitutions
            component.setFinal(_setEmpty);
        } else if (finalAttr.equals(Constants.ATTRVALUE_ALL)) {
            component.setFinal(_setExtResListUnion);
        } else {
            component.setFinal(parseSymbolSet(finalAttr, _setExtResListUnion));
            
            // TODO - implement this
            failUnimplemented("F013");
        }
    }
    
    protected ComplexTypeDefinitionComponent buildComplexTypeDefinition(
        SchemaElement element, InternalSchema schema) {
        
        // SPEC - 3.4
        
        for (Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement) iter.next();
            if (child.getQName().equals(SchemaConstants.QNAME_SIMPLE_CONTENT)) {
                return buildSimpleContentComplexTypeDefinition(element, schema);
            } else if (child.getQName().equals(
                SchemaConstants.QNAME_COMPLEX_CONTENT)) {
                    
                boolean mixedContent =
                    element.getValueOfBooleanAttributeOrDefault(
                        Constants.ATTR_MIXED, false);
                return buildExplicitComplexContentComplexTypeDefinition(element,
                    mixedContent, schema);
            }
        }
        
        boolean mixedContent = element.getValueOfBooleanAttributeOrDefault(
            Constants.ATTR_MIXED, false);
        return buildImplicitComplexContentComplexTypeDefinition(
            element, mixedContent, schema);
    }
    
    protected ComplexTypeDefinitionComponent commonBuildComplexTypeDefinition(
        SchemaElement element, InternalSchema schema) {
        
        // SPEC - 3.4
        
        ComplexTypeDefinitionComponent component =
            new ComplexTypeDefinitionComponent();
        
        // property: name, targetNamespace
        String nameAttr =
            element.getValueOfAttributeOrNull(Constants.ATTR_NAME);
        
        // bug fix 4850909, throw exception when element has named complexType
        if ((nameAttr != null) && element.getParent().getQName().equals(
            SchemaConstants.QNAME_ELEMENT)) {
                
            failValidation("validation.invalidComplexTypeInElement", nameAttr,
                element.getParent().getValueOfAttributeOrNull(
                    Constants.ATTR_NAME));
        }
        if (nameAttr != null) {
            component.setName(new QName(
                element.getSchema().getTargetNamespaceURI(), nameAttr));
            
            // register it so that we avoid loops
            _namedTypeComponentsBeingDefined.put(
                component.getName(), component);
        }
        
        // property: abstract
        component.setAbstract(element.getValueOfBooleanAttributeOrDefault(
            Constants.ATTR_ABSTRACT, false));
        
        // property: disallowed substitutions
        String blockAttr =
            element.getValueOfAttributeOrNull(Constants.ATTR_BLOCK);
        if (blockAttr == null) {
            blockAttr = element.getRoot().getValueOfAttributeOrNull(
                Constants.ATTR_BLOCK_DEFAULT);
            if (blockAttr == null) {
                blockAttr = "";
            }
        }
        if (blockAttr.equals("")) {
            
            // no disallowed substitutions
            component.setProhibitedSubstitutions(_setEmpty);
        } else if (blockAttr.equals(Constants.ATTRVALUE_ALL)) {
            component.setProhibitedSubstitutions(_setExtRes);
        } else {
            component.setProhibitedSubstitutions(parseSymbolSet(blockAttr,
                _setExtRes));
            
            // TODO - implement this
            failUnimplemented("F015");
        }
        
        // property: final
        String finalAttr =
            element.getValueOfAttributeOrNull(Constants.ATTR_FINAL);
        if (finalAttr == null) {
            finalAttr = element.getRoot().getValueOfAttributeOrNull(
                Constants.ATTR_FINAL_DEFAULT);
            if (finalAttr == null) {
                finalAttr = "";
            }
        }
        if (finalAttr.equals("")) {
            
            // no disallowed substitutions
            component.setFinal(_setEmpty);
        } else if (finalAttr.equals(Constants.ATTRVALUE_ALL)) {
            component.setFinal(_setExtRes);
        } else {
            component.setFinal(parseSymbolSet(finalAttr, _setExtRes));

            // TODO - implement this
            failUnimplemented("F016");
        }
        
        // NOTE - annotations are ignored
        
        return component;
    }
    
    protected ComplexTypeDefinitionComponent
        buildSimpleContentComplexTypeDefinition(SchemaElement element,
            InternalSchema schema) {
        
        // SPEC - 3.4
        
        ComplexTypeDefinitionComponent component =
            commonBuildComplexTypeDefinition(element, schema);
        
        SchemaElement simpleContentElement =
            getOnlyChildIgnoring(element, SchemaConstants.QNAME_ANNOTATION);
        if (!simpleContentElement.getQName().equals(
            SchemaConstants.QNAME_SIMPLE_CONTENT)) {
                
            failValidation("validation.invalidElement",
                simpleContentElement.getLocalName());
        }
        
        component.setContentTag(ComplexTypeDefinitionComponent.CONTENT_SIMPLE);
        
        SchemaElement derivationElement = getOnlyChildIgnoring(
            simpleContentElement, SchemaConstants.QNAME_ANNOTATION);
        boolean isRestriction = true;
        if (derivationElement.getQName().equals(
            SchemaConstants.QNAME_RESTRICTION)) {
                
            // no-op
        } else if (derivationElement.getQName().equals(
            SchemaConstants.QNAME_EXTENSION)) {
                
            isRestriction = false;
        } else {
            failValidation("validation.invalidElement",
                derivationElement.getLocalName());
        }
        
        // property: base type definition
        String baseAttr = derivationElement.getValueOfAttributeOrNull(
            Constants.ATTR_BASE);
        if (baseAttr == null) {
            component.setBaseTypeDefinition(getUrType());
        } else {
            component.setBaseTypeDefinition(
                schema.findTypeDefinition(element.asQName(baseAttr)));
        }
        
        // property: derivation method
        component.setDerivationMethod(
            isRestriction ? Symbol.RESTRICTION : Symbol.EXTENSION);
        
        if (isRestriction) {
            processRestrictionSimpleTypeDefinition(derivationElement, 
                component, schema);
        } else {
            processExtensionSimpleTypeDefinition(derivationElement,
                component, schema);
        }
        
        return component;
    }
    
    protected void processRestrictionSimpleTypeDefinition(SchemaElement element,
        ComplexTypeDefinitionComponent component, InternalSchema schema) {
        
        // SPEC - ???
        
        boolean gotContent = false;
        for (Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement) iter.next();
            if (child.getQName().equals(SchemaConstants.QNAME_ATTRIBUTE)) {
                AttributeUseComponent attribute =
                    buildAttributeUse(child, component, schema);
                if (attribute == null) {
                    
                    // the attribute must be prohibited
                    failUnimplemented("F019");
                } else {
                    component.addAttributeUse(attribute);
                }
            } else if (child.getQName().equals(
                SchemaConstants.QNAME_ATTRIBUTE_GROUP)) {
                    
                String refAttr =
                    child.getValueOfMandatoryAttribute(Constants.ATTR_REF);
                component.addAttributeGroup(schema.findAttributeGroupDefinition(
                    child.asQName(refAttr)));
            } else if (child.getQName().equals(
                SchemaConstants.QNAME_ANY_ATTRIBUTE)) {
                    
                // TODO - implement this
                failUnimplemented("F020");
            } else if (child.getQName().equals(
                SchemaConstants.QNAME_ANNOTATION)) {
                    
                // ignore it
                continue;
            } else {
                // must be a facet
                failUnimplemented("F023");
            }
        }
    }
    
    protected void processExtensionSimpleTypeDefinition(SchemaElement element,
        ComplexTypeDefinitionComponent component, InternalSchema schema) {
        
        // SPEC - ???
        
        boolean gotContent = false;
        for (Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement) iter.next();
            if (child.getQName().equals(SchemaConstants.QNAME_ATTRIBUTE)) {
                AttributeUseComponent attribute = buildAttributeUse(child,
                    component, schema);
                if (attribute == null) {
                    
                    // the attribute must be prohibited
                    failUnimplemented("F019");
                } else {
                    component.addAttributeUse(attribute);
                }
            } else if (child.getQName().equals(
                SchemaConstants.QNAME_ATTRIBUTE_GROUP)) {
                    
                String refAttr = child.getValueOfMandatoryAttribute(
                    Constants.ATTR_REF);
                component.addAttributeGroup(schema.findAttributeGroupDefinition(
                    child.asQName(refAttr)));
            } else if (child.getQName().equals(
                SchemaConstants.QNAME_ANY_ATTRIBUTE)) {
                    
                // TODO - implement this
                failUnimplemented("F020");
            } else if (child.getQName().equals(
                SchemaConstants.QNAME_ANNOTATION)) {
                    
                // ignore it
                continue;
            } else {
                // must be a facet
                failUnimplemented("F023");
            }
        }
    }
    
    protected ComplexTypeDefinitionComponent
        buildExplicitComplexContentComplexTypeDefinition(SchemaElement element,
            boolean mixedContent, InternalSchema schema) {
        
        // SPEC - 3.4
        
        ComplexTypeDefinitionComponent component =
            commonBuildComplexTypeDefinition(element, schema);
        
        SchemaElement complexContentElement = getOnlyChildIgnoring(element,
            SchemaConstants.QNAME_ANNOTATION);
        if (!complexContentElement.getQName().equals(
            SchemaConstants.QNAME_COMPLEX_CONTENT)) {
                
            failValidation("validation.invalidElement",
                complexContentElement.getLocalName());
        }
        
        boolean mixed =
            complexContentElement.getValueOfBooleanAttributeOrDefault(
                Constants.ATTR_MIXED, mixedContent);
        
        SchemaElement derivationElement = getOnlyChildIgnoring(
            complexContentElement, SchemaConstants.QNAME_ANNOTATION);
        boolean isRestriction = true;
        if (derivationElement.getQName().equals(
            SchemaConstants.QNAME_RESTRICTION)) {
                
            // no-op
        } else if (derivationElement.getQName().equals(
            SchemaConstants.QNAME_EXTENSION)) {
                
            isRestriction = false;
        } else {
            failValidation("validation.invalidElement",
                derivationElement.getLocalName());
        }
        
        if (isRestriction) {
            String baseAttr = derivationElement.getValueOfMandatoryAttribute(
                Constants.ATTR_BASE);
            TypeDefinitionComponent baseType =
                schema.findTypeDefinition(derivationElement.asQName(baseAttr));
            component.setBaseTypeDefinition(baseType);
            if (mixed) {
                component.setContentTag(
                    ComplexTypeDefinitionComponent.CONTENT_MIXED);
            } else {
                component.setContentTag(
                    ComplexTypeDefinitionComponent.CONTENT_ELEMENT_ONLY);
            }
            processRestrictionComplexTypeDefinition(derivationElement,
                component, schema);
        } else {
            String baseAttr = derivationElement.getValueOfMandatoryAttribute(
                Constants.ATTR_BASE);
            TypeDefinitionComponent baseType =
                schema.findTypeDefinition(derivationElement.asQName(baseAttr));
            component.setBaseTypeDefinition(baseType);
            if (mixed) {
                component.setContentTag(
                    ComplexTypeDefinitionComponent.CONTENT_MIXED);
            } else {
                component.setContentTag(
                    ComplexTypeDefinitionComponent.CONTENT_ELEMENT_ONLY);
            }
            processExtensionComplexTypeDefinition(derivationElement,
                component, schema);
        }
        
        // property: derivation method
        component.setDerivationMethod(
            isRestriction ? Symbol.RESTRICTION : Symbol.EXTENSION);
        
        return component;
    }
    
    protected ComplexTypeDefinitionComponent
        buildImplicitComplexContentComplexTypeDefinition(SchemaElement element,
            boolean mixedContent, InternalSchema schema) {
        
        // SPEC - 3.4
        
        ComplexTypeDefinitionComponent component =
            commonBuildComplexTypeDefinition(element, schema);
        component.setBaseTypeDefinition(getUrType());
        if (mixedContent) {
            component.setContentTag(
                ComplexTypeDefinitionComponent.CONTENT_MIXED);
        } else {
            component.setContentTag(
                ComplexTypeDefinitionComponent.CONTENT_ELEMENT_ONLY);
        }
        processRestrictionComplexTypeDefinition(element, component, schema);
        return component;
    }
    
    protected void processRestrictionComplexTypeDefinition(
        SchemaElement element,
        ComplexTypeDefinitionComponent component,
        InternalSchema schema) {
            
        /* NOTE - the "element" argument is the <restriction/> element or, in
         * the implicit case, the <complexContent/> element implicitly
         * restricting the ur-type
         */
        
        // SPEC - 3.8
        
        boolean gotContent = false;
        for (Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement) iter.next();
            if (child.getQName().equals(SchemaConstants.QNAME_ATTRIBUTE)) {
                AttributeUseComponent attribute =
                    buildAttributeUse(child, component, schema);
                if (attribute == null) {
                    
                    // the attribute must be prohibited
                    failUnimplemented("F019");
                } else {
                    component.addAttributeUse(attribute);
                }
            } else if (child.getQName().equals(
                SchemaConstants.QNAME_ATTRIBUTE_GROUP)) {
                    
                String refAttr =
                    child.getValueOfMandatoryAttribute(Constants.ATTR_REF);
                component.addAttributeGroup(schema.findAttributeGroupDefinition(
                    child.asQName(refAttr)));
            } else if (child.getQName().equals(
                SchemaConstants.QNAME_ANY_ATTRIBUTE)) {
                    
                // TODO - implement this
                failUnimplemented("F020");
            } else if (child.getQName().equals(
                SchemaConstants.QNAME_ANNOTATION)) {
                    
                // ignore it
                continue;
            } else {
                
                // must be content
                if (gotContent) {
                    failValidation("validation.invalidElement",
                        child.getLocalName());
                }
                gotContent = true;
                if (child.getQName().equals(SchemaConstants.QNAME_GROUP)) {
                    
                    // TODO - implement this
                    failUnimplemented("F021");
                } else {
                    component.setParticleContent(buildParticle(child,
                        component, schema));
                }
            }
        }
        
        if (!gotContent) {
            component.setContentTag(
                ComplexTypeDefinitionComponent.CONTENT_EMPTY);
        }
    }
    
    protected void processExtensionComplexTypeDefinition(SchemaElement element,
        ComplexTypeDefinitionComponent component, InternalSchema schema) {
            
        /* NOTE - the "element" argument is the <extension/> element or, in
         * the implicit case, the <complexContent/> element implicitly
         * restricting the ur-type
         */
        
        // TODO - double check this, right now we just copied the code used to process restrictions
        
        // SPEC - 3.8
        
        boolean gotContent = false;
        for (Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement) iter.next();
            if (child.getQName().equals(SchemaConstants.QNAME_ATTRIBUTE)) {
                AttributeUseComponent attribute = buildAttributeUse(child,
                    component, schema);
                if (attribute == null) {
                    
                    // the attribute must be prohibited
                    failUnimplemented("F019");
                } else {
                    component.addAttributeUse(attribute);
                }
            } else if (child.getQName().equals(
                SchemaConstants.QNAME_ATTRIBUTE_GROUP)) {
                    
                String refAttr = child.getValueOfMandatoryAttribute(
                    Constants.ATTR_REF);
                component.addAttributeGroup(schema.findAttributeGroupDefinition(
                    child.asQName(refAttr)));
            } else if (child.getQName().equals(
                SchemaConstants.QNAME_ANY_ATTRIBUTE)) {
                    
                // TODO - implement this
                failUnimplemented("F020");
            } else if (child.getQName().equals(
                SchemaConstants.QNAME_ANNOTATION)) {
                    
                // ignore it
                continue;
            } else {
                
                // must be content
                if (gotContent) {
                    failValidation("validation.invalidElement",
                        child.getLocalName());
                }
                gotContent = true;
                if (child.getQName().equals(SchemaConstants.QNAME_GROUP)) {
                    
                    // TODO - implement this
                    failUnimplemented("F021");
                } else {
                    component.setParticleContent(buildParticle(child,
                        component, schema));
                }
            }
        }
        
        if (!gotContent) {
            component.setContentTag(
                ComplexTypeDefinitionComponent.CONTENT_EMPTY);
        }
    }
    
    protected AttributeUseComponent buildAttributeUse(SchemaElement element,
        ComplexTypeDefinitionComponent scope, InternalSchema schema) {
        
        // SPEC - 3.2.2
        
        AttributeUseComponent component = new AttributeUseComponent();
        
        // property: required
        String useAttr = element.getValueOfAttributeOrNull(Constants.ATTR_USE);
        if (useAttr != null) {
            if (useAttr.equals(Constants.ATTRVALUE_REQUIRED)) {
                component.setRequired(true);
            } else if (useAttr.equals(Constants.ATTRVALUE_PROHIBITED)) {
                return null;
            }
        }
        
        String refAttr = element.getValueOfAttributeOrNull(Constants.ATTR_REF);
        if (refAttr != null) {
            assertNoAttribute(element, Constants.ATTR_NAME);
            assertNoAttribute(element, Constants.ATTR_TYPE);
            
            component.setAttributeDeclaration(
                schema.findAttributeDeclaration(element.asQName(refAttr)));
            
            /* NOTE - this was added to support the wsdl:arrayType attribute,
             *        see the note in AttributeUseComponent for more information
             */
            component.setAnnotation(
                buildNonSchemaAttributesAnnotation(element));
                
            // bug fix: 4968046
            AttributeDeclarationComponent attComp = component.getAttributeDeclaration();
            component.setValue(attComp.getValue());
            component.setValueKind(attComp.getValueKind());
        } else {
            
            // property: attribute declaration
            AttributeDeclarationComponent declaration =
                new AttributeDeclarationComponent();
            
            // property: name, targetNamespace
            String nameAttr =
                element.getValueOfMandatoryAttribute(Constants.ATTR_NAME);
            String formAttr =
                element.getValueOfAttributeOrNull(Constants.ATTR_FORM);
            if (formAttr == null) {
                formAttr = element.getRoot().getValueOfAttributeOrNull(
                    Constants.ATTR_ATTRIBUTE_FORM_DEFAULT);
                if (formAttr == null) {
                    formAttr = "";
                }
            }
            if (formAttr.equals(Constants.ATTRVALUE_QUALIFIED)) {
                declaration.setName(new QName(
                    element.getSchema().getTargetNamespaceURI(), nameAttr));
            } else {
                declaration.setName(new QName(nameAttr));
            }
            
            //      property: scope
            declaration.setScope(scope);
            
            //      property: type definition
            boolean foundType = false;
            for (Iterator iter = element.children(); iter.hasNext();) {
                SchemaElement child = (SchemaElement) iter.next();
                if (child.getQName().equals(
                    SchemaConstants.QNAME_SIMPLE_TYPE)) {
                        
                    if (foundType) {
                        failValidation("validation.invalidElement",
                            element.getLocalName());
                    }
                    declaration.setTypeDefinition(buildSimpleTypeDefinition(
                        child, schema));
                    foundType = true;
                } else if (child.getQName().equals(
                    SchemaConstants.QNAME_ANNOTATION)) {
                        
                    // ignore it
                } else {
                    failValidation("validation.invalidElement",
                        child.getLocalName());
                }
            }
            if (foundType) {
                assertNoAttribute(element, Constants.ATTR_TYPE);
            } else {
                String typeAttr = element.getValueOfAttributeOrNull(
                    Constants.ATTR_TYPE);
                if (typeAttr == null) {
                    declaration.setTypeDefinition(getSimpleUrType());
                } else {
                    TypeDefinitionComponent typeComponent =
                        schema.findTypeDefinition(element.asQName(typeAttr));
                    if (typeComponent instanceof
                        SimpleTypeDefinitionComponent) {
                            
                        declaration.setTypeDefinition(
                            (SimpleTypeDefinitionComponent) typeComponent);
                    } else {
                        failValidation("validation.notSimpleType",
                            declaration.getName().getLocalPart());
                    }
                }
            }
            
            // property: annotation
            declaration.setAnnotation(
                buildNonSchemaAttributesAnnotation(element));
            
            component.setAttributeDeclaration(declaration);
        }
        
        
        // property: value constraint
        // NOTE - here values should be parsed with respect to the simple type
        String defaultAttr =
            element.getValueOfAttributeOrNull(Constants.ATTR_DEFAULT);
        String fixedAttr =
            element.getValueOfAttributeOrNull(Constants.ATTR_FIXED);
        if (defaultAttr != null && fixedAttr != null) {
            fail("validation.exclusiveAttributes",
                Constants.ATTR_DEFAULT, Constants.ATTR_FIXED);
        }
        if (defaultAttr != null) {
            component.setValue(defaultAttr);
            component.setValueKind(Symbol.DEFAULT);
        }
        if (fixedAttr != null) {
            component.setValue(defaultAttr);
            component.setValueKind(Symbol.FIXED);
        }
        
        return component;
    }
    
    protected ParticleComponent buildParticle(SchemaElement element,
        ComplexTypeDefinitionComponent scope, InternalSchema schema) {
        
        // SPEC - 3.9
        
        ParticleComponent component = new ParticleComponent();
        
        // property: min occurs, max occurs
        int minOccurs = element.getValueOfIntegerAttributeOrDefault(
            Constants.ATTR_MIN_OCCURS, 1);
        component.setMinOccurs(minOccurs);
        String maxOccursAttr = element.getValueOfAttributeOrNull(
            Constants.ATTR_MAX_OCCURS);
        if (maxOccursAttr == null) {
            component.setMaxOccurs(1);
        } else {
            if (maxOccursAttr.equals(Constants.ATTRVALUE_UNBOUNDED)) {
                component.setMaxOccursUnbounded();
            } else {
                try {
                    int i = Integer.parseInt(maxOccursAttr);
                    if (i < 0 || i < minOccurs) {
                        failValidation("validation.invalidAttributeValue",
                            Constants.ATTR_MAX_OCCURS, maxOccursAttr);
                    }
                    component.setMaxOccurs(i);
                } catch (NumberFormatException e) {
                    failValidation("validation.invalidAttributeValue",
                        Constants.ATTR_MAX_OCCURS, maxOccursAttr);
                }
            }
        }
        
        if (element.getQName().equals(SchemaConstants.QNAME_ELEMENT)) {
            processElementParticle(element, component, scope, schema);
        } else if (element.getQName().equals(SchemaConstants.QNAME_ALL) ||
            element.getQName().equals(SchemaConstants.QNAME_CHOICE) ||
            element.getQName().equals(SchemaConstants.QNAME_SEQUENCE)) {
                
            component.setTermTag(ParticleComponent.TERM_MODEL_GROUP);
            component.setModelGroupTerm(
                buildModelGroup(element, scope, schema));
        } else if (element.getQName().equals(SchemaConstants.QNAME_ANY)) {
            component.setTermTag(ParticleComponent.TERM_WILDCARD);
            component.setWildcardTerm(buildAnyWildcard(element, scope, schema));
        } else {
            failValidation("validation.invalidElement", element.getLocalName());
        }
        
        return component;
    }
    
    protected ModelGroupComponent buildModelGroup(SchemaElement element,
        ComplexTypeDefinitionComponent scope, InternalSchema schema) {
            
        ModelGroupComponent component = new ModelGroupComponent();
        
        if (element.getQName().equals(SchemaConstants.QNAME_ALL)) {
            component.setCompositor(Symbol.ALL);
        } else if (element.getQName().equals(SchemaConstants.QNAME_CHOICE)) {
            component.setCompositor(Symbol.CHOICE);
        } else if (element.getQName().equals(SchemaConstants.QNAME_SEQUENCE)) {
            component.setCompositor(Symbol.SEQUENCE);
        } else {
            failValidation("validation.invalidElement", element.getLocalName());
        }
        
        for (Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement) iter.next();
            if (child.getQName().equals(SchemaConstants.QNAME_ANNOTATION)) {
                
                // ignore it
                continue;
            }
            ParticleComponent particle = buildParticle(child, scope, schema);
            component.addParticle(particle);
        }
        
        return component;
    }
    
    protected WildcardComponent buildAnyWildcard(SchemaElement element,
        ComplexTypeDefinitionComponent scope, InternalSchema schema) {
        
        // SPEC - 3.10
        
        WildcardComponent component = new WildcardComponent();
        
        // property: process contents
        String processContentsAttr =
            element.getValueOfAttributeOrNull(Constants.ATTR_PROCESS_CONTENTS);
        if (processContentsAttr != null) {
            Symbol processContents =
                parseSymbolInSet(processContentsAttr, _setLaxSkipStrict);
            if (processContents == null) {
                failValidation("validation.invalidAttribute",
                    Constants.ATTR_PROCESS_CONTENTS, element.getLocalName());
            }
            component.setProcessContents(processContents);
        } else {
            component.setProcessContents(Symbol.STRICT);
        }
        
        // property: namespace constraint
        String namespaceAttr =
            element.getValueOfAttributeOrNull(Constants.ATTR_NAMESPACE);
        if (namespaceAttr != null) {
            if (namespaceAttr.equals(Constants.ATTRVALUE_ANY)) {
                component.setNamespaceConstraintTag(
                    WildcardComponent.NAMESPACE_CONSTRAINT_ANY);
            } else if (namespaceAttr.equals(Constants.ATTRVALUE_OTHER)) {
                String targetNamespaceURI =
                    element.getSchema().getTargetNamespaceURI();
                if (targetNamespaceURI == null ||
                    targetNamespaceURI.equals("")) {
                        
                    component.setNamespaceConstraintTag(
                        WildcardComponent.NAMESPACE_CONSTRAINT_NOT_ABSENT);
                } else {
                    component.setNamespaceConstraintTag(
                        WildcardComponent.NAMESPACE_CONSTRAINT_NOT);
                    component.setNamespaceName(targetNamespaceURI);
                }
            } else {
                
                // TODO - implement this
                failUnimplemented("F022");
            }
        } else {
            
            // default is any
            component.setNamespaceConstraintTag(WildcardComponent.NAMESPACE_CONSTRAINT_ANY);
        }
        
        return component;
    }
    
    protected AnnotationComponent buildNonSchemaAttributesAnnotation(
        SchemaElement element) {
            
        AnnotationComponent annotation = null;
        for (Iterator iter = element.attributes(); iter.hasNext();) {
            SchemaAttribute attribute = (SchemaAttribute) iter.next();
            if (attribute.getNamespaceURI() != null &&
                !attribute.getNamespaceURI().equals("") &&
                !attribute.getNamespaceURI().equals(SchemaConstants.NS_XSD)) {
                    
                //a non-schema attribute: save it
                if (annotation == null) {
                    annotation = new AnnotationComponent();
                }
                
                annotation.addAttribute(attribute);
            }
        }
        
        return annotation;
    }
    
    public TypeDefinitionComponent getTypeDefinitionComponentBeingDefined(
        QName name) {
            
        if (_namedTypeComponentsBeingDefined != null) {
            return (TypeDefinitionComponent)
                _namedTypeComponentsBeingDefined.get(name);
        }
        return null;
    }
    
    ////////
    
    protected void createWellKnownTypes() {
        _wellKnownTypes = new HashMap();
        
        // SPEC - 3.4.7
        _urType = new ComplexTypeDefinitionComponent();
        _urType.setName(InternalSchemaConstants.QNAME_TYPE_URTYPE);
        _urType.setBaseTypeDefinition(_urType);
        _urType.setFinal(_setEmpty);
        _urType.setProhibitedSubstitutions(_setEmpty);
        _urType.setDerivationMethod(Symbol.RESTRICTION);
        _urType.setContentTag(ComplexTypeDefinitionComponent.CONTENT_MIXED);
        ParticleComponent utp = new ParticleComponent();
        utp.setMinOccurs(1);
        utp.setMaxOccurs(1);
        ModelGroupComponent utpmg = new ModelGroupComponent();
        utpmg.setCompositor(Symbol.SEQUENCE);
        ParticleComponent utpmgp = new ParticleComponent();
        utpmgp.setMinOccurs(0);
        utpmgp.setMaxOccursUnbounded();
        WildcardComponent utpmgpw = new WildcardComponent();
        utpmgpw.setNamespaceConstraintTag(
            WildcardComponent.NAMESPACE_CONSTRAINT_ANY);
        utpmgp.setTermTag(ParticleComponent.TERM_WILDCARD);
        utpmgp.setWildcardTerm(utpmgpw);
        utpmg.addParticle(utpmgp);
        utp.setTermTag(ParticleComponent.TERM_MODEL_GROUP);
        utp.setModelGroupTerm(utpmg);
        _urType.setParticleContent(utp);
        // TODO - add other ur-type characteristics, esp. those related to attributes
        
        _wellKnownTypes.put(_urType.getName(), _urType);
        
        // SPEC - 3.4.7
        _simpleUrType = new SimpleTypeDefinitionComponent();
        _simpleUrType.setName(InternalSchemaConstants.QNAME_TYPE_SIMPLE_URTYPE);
        _simpleUrType.setBaseTypeDefinition(_simpleUrType);
        _simpleUrType.setFinal(_setEmpty);
        // TODO - add other simple ur-type characteristics
        
        _wellKnownTypes.put(_simpleUrType.getName(), _simpleUrType);
        
        for (Iterator iter = _primitiveTypeNames.iterator(); iter.hasNext();) {
            QName name = (QName) iter.next();
            
            // SPEC - see 3.4.7
            SimpleTypeDefinitionComponent type =
                new SimpleTypeDefinitionComponent();
            type.setName(name);
            type.setVarietyTag(SimpleTypeDefinitionComponent.VARIETY_ATOMIC);
            type.setFinal(_setEmpty);
            type.setBaseTypeDefinition(_simpleUrType);
            type.setPrimitiveTypeDefinition(type);
            
            _wellKnownTypes.put(type.getName(), type);
        }
        
        for (Iterator iter = _soapTypeNames.iterator(); iter.hasNext();) {
            QName name = (QName) iter.next();
            ComplexTypeDefinitionComponent type =
                new ComplexTypeDefinitionComponent();
            type.setName(name);
            type.setBaseTypeDefinition(_urType);
            type.setContentTag(ComplexTypeDefinitionComponent.CONTENT_SIMPLE);
            QName xName =
                new QName(SchemaConstants.NS_XSD, name.getLocalPart());
            SimpleTypeDefinitionComponent xComponent =
                (SimpleTypeDefinitionComponent) _wellKnownTypes.get(xName);
            if (xComponent == null) {
                continue;
            }
            type.setSimpleTypeContent(xComponent);
            // TODO - add other soap-enc type characteristics (e.g. the common soap-enc attributes)
            
            _wellKnownTypes.put(type.getName(), type);
        }
        
        /* construct soap-enc:base64 specially because, unlike all other
         * soap-enc types, it's a simple type, not a complex one; notice that
         * we don't define it in the second for loop above because there is
         * no xsd:base64 type!
         */
        SimpleTypeDefinitionComponent base64Type =
            new SimpleTypeDefinitionComponent();
        base64Type.setName(SOAPConstants.QNAME_TYPE_BASE64);
        base64Type.setVarietyTag(SimpleTypeDefinitionComponent.VARIETY_ATOMIC);
        base64Type.setFinal(_setEmpty);
        base64Type.setBaseTypeDefinition(_simpleUrType);
        base64Type.setPrimitiveTypeDefinition(base64Type);
        _wellKnownTypes.put(base64Type.getName(), base64Type);
        
        // construct the SOAP-ENC:Array type, or rather a placeholder for it
        ComplexTypeDefinitionComponent arrayType =
            new ComplexTypeDefinitionComponent();
        arrayType.setName(SOAPConstants.QNAME_TYPE_ARRAY);
        arrayType.setBaseTypeDefinition(_urType);
        arrayType.setDerivationMethod(Symbol.RESTRICTION);
        arrayType.setContentTag(
            ComplexTypeDefinitionComponent.CONTENT_ELEMENT_ONLY);
        ParticleComponent atp = new ParticleComponent();
        atp.setMinOccurs(1);
        atp.setMaxOccurs(1);
        ModelGroupComponent atpmg = new ModelGroupComponent();
        atpmg.setCompositor(Symbol.SEQUENCE);
        ParticleComponent atpmgp = new ParticleComponent();
        atpmgp.setMinOccurs(0);
        atpmgp.setMaxOccursUnbounded();
        WildcardComponent atpmgpw = new WildcardComponent();
        atpmgpw.setNamespaceConstraintTag(
            WildcardComponent.NAMESPACE_CONSTRAINT_ANY);
        atpmgp.setTermTag(ParticleComponent.TERM_WILDCARD);
        atpmgp.setWildcardTerm(atpmgpw);
        atpmg.addParticle(atpmgp);
        atp.setTermTag(ParticleComponent.TERM_MODEL_GROUP);
        atp.setModelGroupTerm(atpmg);
        arrayType.setParticleContent(atp);
        // TODO - add other SOAP array type characteristics
        
        _wellKnownTypes.put(arrayType.getName(), arrayType);
    }
    
    protected void createWellKnownAttributes() {
        _wellKnownAttributes = new HashMap();
        
        AttributeDeclarationComponent arrayTypeAttr =
            new AttributeDeclarationComponent();
        arrayTypeAttr.setName(SOAPConstants.QNAME_ATTR_ARRAY_TYPE);
        
        /* NOTE - since this method is called by our constructor, it's better
         * to bypass the InternalSchema.findXXX() methods to avoid circularities
         */
        arrayTypeAttr.setTypeDefinition((SimpleTypeDefinitionComponent)
            _wellKnownTypes.get(SchemaConstants.QNAME_TYPE_STRING));
        
        _wellKnownAttributes.put(arrayTypeAttr.getName(), arrayTypeAttr);
        
        AttributeDeclarationComponent offsetAttr =
            new AttributeDeclarationComponent();
        offsetAttr.setName(SOAPConstants.QNAME_ATTR_OFFSET);
        
        /* in reality, it should be of type soap-enc:arrayCoordinate, which in
         * turn is a restriction of xsd:string
         */
        offsetAttr.setTypeDefinition((SimpleTypeDefinitionComponent)
            _wellKnownTypes.get(SchemaConstants.QNAME_TYPE_STRING));
        _wellKnownAttributes.put(offsetAttr.getName(), offsetAttr);
        
        AttributeDeclarationComponent xmlLangAttr =
            new AttributeDeclarationComponent();
        xmlLangAttr.setName(new QName(
            com.sun.xml.rpc.wsdl.parser.Constants.NS_XML, "lang"));
        
        // this should really be of type "language", a subtype of xsd:string
        xmlLangAttr.setTypeDefinition((SimpleTypeDefinitionComponent)
            _wellKnownTypes.get(SchemaConstants.QNAME_TYPE_STRING));
        _wellKnownAttributes.put(xmlLangAttr.getName(), xmlLangAttr);
        
        /* TODO - add these attributes too
         *   SOAPConstants.QNAME_ATTR_POSITION
         *   as well as the built-in XSD attributes, such as xsi:type,
         *   xsi:nil, ...
         */
    }
    
    protected void createWellKnownAttributeGroups() {
        _wellKnownAttributeGroups = new HashMap();
        
        AttributeGroupDefinitionComponent commonAttributesAttrGroup =
            new AttributeGroupDefinitionComponent();
        commonAttributesAttrGroup.setName(
            SOAPConstants.QNAME_ATTR_GROUP_COMMON_ATTRIBUTES);
        
        AttributeDeclarationComponent idAttr =
            new AttributeDeclarationComponent();
        idAttr.setName(SOAPConstants.QNAME_ATTR_ID);
        idAttr.setTypeDefinition((SimpleTypeDefinitionComponent)
            _wellKnownTypes.get(SchemaConstants.QNAME_TYPE_ID));
        AttributeUseComponent idAttrUse = new AttributeUseComponent();
        idAttrUse.setAttributeDeclaration(idAttr);
        commonAttributesAttrGroup.addAttributeUse(idAttrUse);
        
        AttributeDeclarationComponent hrefAttr =
            new AttributeDeclarationComponent();
        hrefAttr.setName(SOAPConstants.QNAME_ATTR_HREF);
        hrefAttr.setTypeDefinition((SimpleTypeDefinitionComponent)
            _wellKnownTypes.get(SchemaConstants.QNAME_TYPE_ANY_URI));
        AttributeUseComponent hrefAttrUse = new AttributeUseComponent();
        hrefAttrUse.setAttributeDeclaration(hrefAttr);
        commonAttributesAttrGroup.addAttributeUse(hrefAttrUse);
        
        // TODO - there is also a wildcard to be defined!
        
        _wellKnownAttributeGroups.put(
            commonAttributesAttrGroup.getName(),commonAttributesAttrGroup);
    }
    
    protected void createWellKnownElements() {
        _wellKnownElements = new HashMap();
        
        // TODO - add SOAP-specific elements, such as soap-enc:string
    }
    
    ////////
    
    protected Set parseSymbolSet(String s, Set values) {
        if (s.equals(Constants.ATTRVALUE_ALL)) {
            return values;
        }
        
        Set result = new HashSet();
        List tokens = XmlUtil.parseTokenList(s);
        for (Iterator iter = tokens.iterator(); iter.hasNext();) {
            String v = (String) iter.next();
            Symbol sym = Symbol.named(v);
            if (sym != null && values.contains(sym)) {
                result.add(sym);
            }
            
            // NOTE - per the XSD spec, other values are ignored
        }
        
        return result;
    }
    
    private Symbol parseSymbolInSet(String s, Set values) {
        Symbol sym = Symbol.named(s);
        if (sym != null && values.contains(sym)) {
            return sym;
        } else {
            return null;
        }
    }
    
    private SchemaElement getOnlyChildIgnoring(SchemaElement element,
        QName name) {
            
        SchemaElement result = null;
        for (Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement) iter.next();
            if (!child.getQName().equals(name)) {
                if (result != null) {
                    failValidation("validation.invalidElement",
                        child.getLocalName());
                }
                result = child;
            }
        }
        if (result == null) {
            failValidation("validation.invalidElement", element.getLocalName());
        }
        return result;
    }
    
    private void assertNoAttribute(SchemaElement element, String name) {
        String value = element.getValueOfAttributeOrNull(name);
        if (value != null) {
            
            //bug fix: 4851686
            failValidation("validation.invalidAttribute", name,
                element.getValueOfAttributeOrNull(Constants.ATTR_NAME));
        }
    }
    
    private void fail(String key) {
        throw new ModelException(key);
    }
    
    private void fail(String key, QName name) {
        fail(key, toString(name));
    }
    
    private void fail(String key, String arg) {
        throw new ModelException(key, arg);
    }
    
    private void fail(String key, String arg1, String arg2) {
        throw new ModelException(key, new Object[] { arg1, arg2 });
    }
    
    protected void failUnimplemented(String arg) {
        throw new UnimplementedFeatureException(arg);
    }
    
    private void failValidation(String key) {
        throw new ValidationException(key);
    }
    
    protected void failValidation(String key, String arg) {
        throw new ValidationException(key, arg);
    }
    
    protected void failValidation(String key, String arg1, String arg2) {
        throw new ValidationException(key, new Object[] { arg1, arg2 });
    }
    
    private String toString(QName name) {
        return name.getLocalPart() + " (" + name.getNamespaceURI() + ")";
    }
    
    private AbstractDocument _document;
    private InternalSchema _schema;
    private Map _wellKnownTypes;
    private Map _wellKnownAttributes;
    private Map _wellKnownAttributeGroups;
    private Map _wellKnownElements;
    private ComplexTypeDefinitionComponent _urType;
    private SimpleTypeDefinitionComponent _simpleUrType;
    private Map _namedTypeComponentsBeingDefined;
    
    ////////
    
    private static final Set _primitiveTypeNames;
    private static final Set _soapTypeNames;
    protected static final Set _setEmpty = new HashSet();
    private static final Set _setExtRes;
    private static final Set _setExtResSub;
    protected static final Set _setExtResListUnion;
    private static final Set _setLaxSkipStrict;
    
    static {
        _setExtRes = new HashSet();
        _setExtRes.add(Symbol.EXTENSION);
        _setExtRes.add(Symbol.RESTRICTION);
        
        _setExtResSub = new HashSet();
        _setExtResSub.add(Symbol.EXTENSION);
        _setExtResSub.add(Symbol.RESTRICTION);
        _setExtResSub.add(Symbol.SUBSTITUTION);
        
        _setExtResListUnion = new HashSet();
        _setExtResListUnion.add(Symbol.EXTENSION);
        _setExtResListUnion.add(Symbol.RESTRICTION);
        _setExtResListUnion.add(Symbol.LIST);
        _setExtResListUnion.add(Symbol.UNION);
        
        _setLaxSkipStrict = new HashSet();
        _setLaxSkipStrict.add(Symbol.LAX);
        _setLaxSkipStrict.add(Symbol.SKIP);
        _setLaxSkipStrict.add(Symbol.STRICT);
        
        _primitiveTypeNames = new HashSet();
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_STRING);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_NORMALIZED_STRING);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_TOKEN);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_BYTE);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_UNSIGNED_BYTE);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_BASE64_BINARY);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_HEX_BINARY);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_INTEGER);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_POSITIVE_INTEGER);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_NEGATIVE_INTEGER);
        _primitiveTypeNames.add(
            SchemaConstants.QNAME_TYPE_NON_NEGATIVE_INTEGER);
        _primitiveTypeNames.add(
            SchemaConstants.QNAME_TYPE_NON_POSITIVE_INTEGER);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_INT);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_UNSIGNED_INT);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_LONG);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_UNSIGNED_LONG);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_SHORT);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_UNSIGNED_SHORT);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_DECIMAL);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_FLOAT);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_DOUBLE);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_BOOLEAN);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_TIME);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_DATE_TIME);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_DURATION);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_DATE);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_G_MONTH);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_G_YEAR);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_G_YEAR_MONTH);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_G_DAY);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_G_MONTH_DAY);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_NAME);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_QNAME);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_NCNAME);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_ANY_URI);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_ID);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_IDREF);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_IDREFS);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_ENTITY);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_ENTITIES);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_NOTATION);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_NMTOKEN);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_NMTOKENS);

        // New types 12/3/02
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_LANGUAGE);
        
        
        _soapTypeNames = new HashSet();
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_STRING);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_NORMALIZED_STRING);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_TOKEN);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_BYTE);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_UNSIGNED_BYTE);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_BASE64_BINARY);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_HEX_BINARY);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_INTEGER);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_POSITIVE_INTEGER);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_NEGATIVE_INTEGER);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_NON_NEGATIVE_INTEGER);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_NON_POSITIVE_INTEGER);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_INT);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_UNSIGNED_INT);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_LONG);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_UNSIGNED_LONG);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_SHORT);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_UNSIGNED_SHORT);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_DECIMAL);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_FLOAT);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_DOUBLE);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_BOOLEAN);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_TIME);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_DATE_TIME);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_DURATION);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_DATE);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_G_MONTH);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_G_YEAR);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_G_YEAR_MONTH);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_G_DAY);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_G_MONTH_DAY);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_NAME);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_QNAME);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_NCNAME);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_ANY_URI);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_ID);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_IDREF);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_IDREFS);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_ENTITY);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_ENTITIES);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_NOTATION);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_NMTOKEN);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_NMTOKENS);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_BASE64);
    }
}
