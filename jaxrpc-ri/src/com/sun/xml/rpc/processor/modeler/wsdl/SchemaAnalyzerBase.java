/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

/*
 * $Id: SchemaAnalyzerBase.java,v 1.3 2007-07-13 23:36:17 ofung Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.xml.rpc.processor.modeler.wsdl;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.DeserializationException;
import com.sun.xml.rpc.encoding.InternalEncodingConstants;
import com.sun.xml.rpc.encoding.simpletype.EncoderUtils;
import com.sun.xml.rpc.encoding.simpletype.SimpleTypeEncoder;
import com.sun.xml.rpc.processor.ProcessorOptions;
import com.sun.xml.rpc.processor.config.ModelInfo;
import com.sun.xml.rpc.processor.config.NamespaceMappingInfo;
import com.sun.xml.rpc.processor.config.TypeMappingInfo;
import com.sun.xml.rpc.processor.generator.writer.SimpleTypeSerializerWriter;
import com.sun.xml.rpc.processor.model.ModelException;
import com.sun.xml.rpc.processor.model.ModelProperties;
import com.sun.xml.rpc.processor.model.java.JavaArrayType;
import com.sun.xml.rpc.processor.model.java.JavaCustomType;
import com.sun.xml.rpc.processor.model.java.JavaEnumerationEntry;
import com.sun.xml.rpc.processor.model.java.JavaEnumerationType;
import com.sun.xml.rpc.processor.model.java.JavaSimpleType;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.literal.LiteralAllType;
import com.sun.xml.rpc.processor.model.literal.LiteralAttributeMember;
import com.sun.xml.rpc.processor.model.literal.LiteralContentMember;
import com.sun.xml.rpc.processor.model.literal.LiteralElementMember;
import com.sun.xml.rpc.processor.model.literal.LiteralEnumerationType;
import com.sun.xml.rpc.processor.model.literal.LiteralFragmentType;
import com.sun.xml.rpc.processor.model.literal.LiteralIDType;
import com.sun.xml.rpc.processor.model.literal.LiteralListType;
import com.sun.xml.rpc.processor.model.literal.LiteralSequenceType;
import com.sun.xml.rpc.processor.model.literal.LiteralSimpleType;
import com.sun.xml.rpc.processor.model.literal.LiteralStructuredType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.literal.LiteralWildcardMember;
import com.sun.xml.rpc.processor.model.soap.SOAPAnyType;
import com.sun.xml.rpc.processor.model.soap.SOAPArrayType;
import com.sun.xml.rpc.processor.model.soap.SOAPAttributeMember;
import com.sun.xml.rpc.processor.model.soap.SOAPCustomType;
import com.sun.xml.rpc.processor.model.soap.SOAPEnumerationType;
import com.sun.xml.rpc.processor.model.soap.SOAPListType;
import com.sun.xml.rpc.processor.model.soap.SOAPOrderedStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPSimpleType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.model.soap.SOAPUnorderedStructureType;
import com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator;
import com.sun.xml.rpc.processor.schema.AttributeDeclarationComponent;
import com.sun.xml.rpc.processor.schema.AttributeUseComponent;
import com.sun.xml.rpc.processor.schema.ComplexTypeDefinitionComponent;
import com.sun.xml.rpc.processor.schema.ElementDeclarationComponent;
import com.sun.xml.rpc.processor.schema.EnumerationFacet;
import com.sun.xml.rpc.processor.schema.Facet;
import com.sun.xml.rpc.processor.schema.InternalSchema;
import com.sun.xml.rpc.processor.schema.ModelGroupComponent;
import com.sun.xml.rpc.processor.schema.ParticleComponent;
import com.sun.xml.rpc.processor.schema.SimpleTypeDefinitionComponent;
import com.sun.xml.rpc.processor.schema.Symbol;
import com.sun.xml.rpc.processor.schema.TypeDefinitionComponent;
import com.sun.xml.rpc.processor.schema.UnimplementedFeatureException;
import com.sun.xml.rpc.processor.schema.WildcardComponent;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPNamespaceConstants;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.soap.SOAPWSDLConstants;
import com.sun.xml.rpc.util.JAXRPCClassFactory;
import com.sun.xml.rpc.util.localization.LocalizableMessageFactory;
import com.sun.xml.rpc.util.xml.XmlUtil;
import com.sun.xml.rpc.wsdl.document.WSDLConstants;
import com.sun.xml.rpc.wsdl.document.schema.BuiltInTypes;
import com.sun.xml.rpc.wsdl.document.schema.SchemaAttribute;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import com.sun.xml.rpc.wsdl.document.schema.SchemaElement;
import com.sun.xml.rpc.wsdl.framework.AbstractDocument;
import com.sun.xml.rpc.wsdl.framework.ValidationException;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class SchemaAnalyzerBase {

    public SchemaAnalyzerBase(
        AbstractDocument document,
        ModelInfo modelInfo,
        Properties options,
        Set conflictingClassNames,
        JavaSimpleTypeCreator javaTypes) {
        init();
        _messageFactory =
            new LocalizableMessageFactory("com.sun.xml.rpc.resources.model");
        //bug fix:4914571
        _schema =
            JAXRPCClassFactory
                .newInstance()
                .createInternalSchemaBuilder(document, options)
                .getSchema();
        _modelInfo = modelInfo;
        _env = (ProcessorEnvironment) modelInfo.getParent().getEnvironment();
        _conflictingClassNames = conflictingClassNames;
        _typesBeingResolved = new HashSet();
        _namePool = new HashSet();
        _componentToSOAPTypeMap = new HashMap();
        _componentToLiteralTypeMap = new HashMap();
        _typeNameToCustomSOAPTypeMap = new HashMap();
        _nillableSimpleTypeComponentToSOAPTypeMap = new HashMap();
        _nillableSimpleTypeComponentToLiteralTypeMap = new HashMap();
        _nextUniqueID = 1;
        _noDataBinding =
            Boolean
                .valueOf(
                    options.getProperty(
                        ProcessorOptions.NO_DATA_BINDING_PROPERTY))
                .booleanValue();
        _useDataHandlerOnly =
            Boolean
                .valueOf(
                    options.getProperty(
                        ProcessorOptions.USE_DATA_HANDLER_ONLY))
                .booleanValue();
        _resolveIDREF =
            Boolean
                .valueOf(options.getProperty(ProcessorOptions.ENABLE_IDREF))
                .booleanValue();
        _strictCompliance =
            Boolean
                .valueOf(
                    options.getProperty(ProcessorOptions.STRICT_COMPLIANCE))
                .booleanValue();
        _jaxbEnumType =
            Boolean
                .valueOf(options.getProperty(ProcessorOptions.JAXB_ENUMTYPE))
                .booleanValue();
        this.javaTypes = javaTypes;
        initializeMaps();
    }

    /*
     * Creates multiple versions of the SOAPWSDLConstants class
     * to use with different versions of SOAP.
     */
    private void init() {
        soap11NamespaceConstants =
            SOAPConstantsFactory.getSOAPNamespaceConstants(SOAPVersion.SOAP_11);
        soap11WSDLConstants =
            SOAPConstantsFactory.getSOAPWSDLConstants(SOAPVersion.SOAP_11);

        soap12NamespaceConstants =
            SOAPConstantsFactory.getSOAPNamespaceConstants(SOAPVersion.SOAP_12);
        soap12WSDLConstants =
            SOAPConstantsFactory.getSOAPWSDLConstants(SOAPVersion.SOAP_12);
    }

    public SOAPType schemaTypeToSOAPType(QName typeName) {
        try {
            TypeDefinitionComponent component =
                _schema.findTypeDefinition(typeName);
            return schemaTypeToSOAPType(component, typeName);
        } catch (UnimplementedFeatureException e) {
            fail(
                "model.schema.unsupportedSchemaType",
                new Object[] { typeName });
            return null; // keep compiler happy
        }
    }

    public LiteralType schemaTypeToLiteralType(QName typeName) {
        try {
            TypeDefinitionComponent component =
                _schema.findTypeDefinition(typeName);
            return schemaTypeToLiteralType(component, typeName);
        } catch (UnimplementedFeatureException e) {
            LiteralType literalType = new LiteralFragmentType();
            literalType.setName(typeName);
            literalType.setSchemaTypeRef(typeName);
            literalType.setJavaType(javaTypes.SOAPELEMENT_JAVATYPE);
			 //Nagesh: To handle xsi:nil="true", added setNillable as true 
            literalType.setNillable(true);
            return literalType;
        }
    }

    public LiteralType schemaElementTypeToLiteralType(QName elementName) {
        ElementDeclarationComponent component = null;
        try {
            component = _schema.findElementDeclaration(elementName);
            String mappingNameHint = null;
            if (component.getTypeDefinition().getName() == null) {
                mappingNameHint = ">" + elementName.getLocalPart();
            } else {
                mappingNameHint =
                    component.getTypeDefinition().getName().getLocalPart();
            }
            LiteralType literalType =
                schemaTypeToLiteralType(
                    component.getTypeDefinition(),
                    elementName,
                    new QName(elementName.getNamespaceURI(),mappingNameHint));
            if (literalType.getName() == null) {
                // to get a better output
                literalType.setName(getUniqueTypeNameForElement(elementName));
                // literalType.setName(new QName(elementName.getNamespaceURI(), elementName.getLocalPart() + "__" + getUniqueID() + "__AnonymousType"));
            }
            //generate boxtype for nillable="true" global elements
            // bug fix: 4900902
            if (component.isNillable()) {
                LiteralSimpleType result =
                    (
                        LiteralSimpleType) _nillableSimpleTypeComponentToLiteralTypeMap
                            .get(
                        component.getTypeDefinition());
                if (result == null) {
                    // bug fix: 4961579
                    // bug fix: 6154958
                    QName baseTypeName = getSimpleTypeBaseName(component.getTypeDefinition());

                    JavaSimpleType javaType =
                          (
                              JavaSimpleType) _builtinSchemaTypeToJavaWrapperTypeMap
                                  .get(baseTypeName);      
//                    JavaSimpleType javaType =
//                        (
//                            JavaSimpleType) _builtinSchemaTypeToJavaWrapperTypeMap
//                                .get(
//                            component.getTypeDefinition().getName());
                    // handle types like String, Date, Calendar etc. that don't have a wrapper    
                    if (javaType == null
                        && literalType instanceof LiteralSimpleType) {
                        javaType =
                            (JavaSimpleType) ((LiteralSimpleType) literalType)
                                .getJavaType();
                    }
                    if (javaType != null) {
                        // bug fix: 4961579
                        result =
                            new LiteralSimpleType(
                                baseTypeName,
                                javaType,
                                true);                        
//                        result =
//                            new LiteralSimpleType(
//                                component.getTypeDefinition().getName(),
//                                javaType,
//                                true);
                        result.setSchemaTypeRef(
                            component.getTypeDefinition().getName());
                        _nillableSimpleTypeComponentToLiteralTypeMap.put(
                            component.getTypeDefinition(),
                            result);
                        return result;
                    }
                } else {
                    literalType = result;
                }
            }
            return literalType;
        } catch (UnimplementedFeatureException e) {
            // NOTE - this seems wrong, but there isn't much else we can do
            //        because the type of the element may be defined inline,
            //        hence be anonymous; even for element whose type is not
            //        anonymous, it's very hard to reach into the schema
            //        and grab the relevant data from here
            LiteralType literalType = (LiteralType)_componentToLiteralTypeMap.get(
                component);
            if (literalType == null) {
                literalType = new LiteralFragmentType();
                literalType.setName(elementName);
                literalType.setJavaType(javaTypes.SOAPELEMENT_JAVATYPE);
                _componentToLiteralTypeMap.put(component, literalType);
            }
            return literalType;
        }
    }

    protected SOAPType schemaTypeToSOAPType(
        TypeDefinitionComponent component,
        QName nameHint) {
        SOAPType result = (SOAPType) _componentToSOAPTypeMap.get(component);
        if (result == null) {
            try {
                if (component.isSimple()) {
                    result =
                        simpleSchemaTypeToSOAPType(
                            (SimpleTypeDefinitionComponent) component,
                            nameHint);
                } else if (component.isComplex()) {
                    result =
                        complexSchemaTypeToSOAPType(
                            (ComplexTypeDefinitionComponent) component,
                            nameHint);
                } else {
                    // should not happen
                    throw new IllegalArgumentException();
                }

                _componentToSOAPTypeMap.put(component, result);

            } finally {
            }
        }

        return result;
    }

    protected SOAPType nillableSchemaTypeToSOAPType(TypeDefinitionComponent component) {
        JavaSimpleType javaType =
            (JavaSimpleType) _builtinSchemaTypeToJavaWrapperTypeMap.get(
                component.getName());
        if (javaType == null) {
            // disregard the nullability, since it will be taken care of by the section 5 encoding rules anyway
            return schemaTypeToSOAPType(component, component.getName());
        } else {
            // nullability matters
            SOAPSimpleType result =
                (SOAPSimpleType) _nillableSimpleTypeComponentToSOAPTypeMap.get(
                    component);
            if (result != null) {
                return result;
            }
            result = new SOAPSimpleType(component.getName(), javaType);
            result.setSchemaTypeRef(component.getName());
            setReferenceable(result);
            _nillableSimpleTypeComponentToSOAPTypeMap.put(component, result);
            return result;
        }
    }

    protected SOAPType simpleSchemaTypeToSOAPType(
        SimpleTypeDefinitionComponent component,
        QName nameHint) {
        if (component.getBaseTypeDefinition() == _schema.getSimpleUrType()) {
            if (component.getVarietyTag()
                == SimpleTypeDefinitionComponent.VARIETY_ATOMIC) {
                // it's likely to be a built-in type
                String nsURI = component.getName().getNamespaceURI();
                if (nsURI != null
                    && (nsURI.equals(SchemaConstants.NS_XSD)
                        || nsURI.equals(
                            soap11WSDLConstants.getSOAPEncodingNamespace()))) {
                    // definitely a built-in type, make sure there are no facets
                    if (!component.facets().hasNext()) {
                        //bug fix 4855115
                        if (_strictCompliance
                            && (component
                                .getName()
                                .equals(SchemaConstants.QNAME_TYPE_IDREF)
                                || component.getName().equals(
                                    SchemaConstants.QNAME_TYPE_URTYPE))) {
                            if (!checkStrictCompliance(component.getName()))
                                return null;
                        }
                        // handle anyType specially
                        if (!_strictCompliance
                            && component.getName().equals(
                                SchemaConstants.QNAME_TYPE_URTYPE)) {
                            SOAPAnyType anyType =
                                new SOAPAnyType(component.getName());
                            JavaSimpleType javaType =
                                (
                                    JavaSimpleType) _builtinSchemaTypeToJavaTypeMap
                                        .get(
                                    component.getName());
                            if (javaType == null) {
                                // invalid simple type
                                //failUnsupported("U002", component.getName());
                                //fail("model.schema.invalidSimpleType", component.getName());
                                fail(
                                    "model.schema.invalidSimpleType.noJavaType",
                                    new Object[] { component.getName()});
                            }
                            anyType.setJavaType(javaType);
                            return anyType;
                        }
                        // bug fix: 4925400
                        return createSOAPSimpleType(component);
                    } else {
                        // a simple type with facets and with the simpleUrType as its base type
                        //failUnsupported("U003", component.getName());
                        fail(
                            "model.schema.simpleTypeWithFacets",
                            new Object[] {
                                component.getName(),
                                component.facets().next()});
                    }
                } else {
                    // a simple type we know nothing about
                    fail(
                        "model.schema.invalidSimpleType",
                        new Object[] { component.getName()});
                }
            } else if (
                component.getVarietyTag()
                    == SimpleTypeDefinitionComponent.VARIETY_LIST) {
                if (doWeHandleSimpleSchemaTypeDerivationByList())
                    return listToSOAPType(component, nameHint);
                fail(
                    "model.schema.listNotSupported",
                    new Object[] { component.getName()});
                // bug fix: 4925400
            } else if (
                component.getVarietyTag()
                    == SimpleTypeDefinitionComponent.VARIETY_UNION) {
                // union
                fail(
                    "model.schema.unionNotSupported",
                    new Object[] { component.getName()});
            } else {
                // bug fix: 4925400
                // URType
                if (component
                    .getName()
                    .equals(SchemaConstants.QNAME_TYPE_SIMPLE_URTYPE)) {
                    return createSOAPSimpleType(component);
                } else {
                    fail(
                        "model.schema.invalidSimpleType",
                        new Object[] { component.getName()});
                }
            }
        } else {
            // see if it is a well know type, in which case we map it directly,
            // no matter what the schema actually says
            JavaSimpleType javaType =
                (JavaSimpleType) _builtinSchemaTypeToJavaTypeMap.get(
                    component.getName());
            if (javaType != null) {
                SOAPSimpleType simpleType =
                    new SOAPSimpleType(component.getName());
                simpleType.setSchemaTypeRef(component.getName());
                simpleType.setJavaType(javaType);
                setReferenceable(simpleType);
                return simpleType;
            }
            SimpleTypeDefinitionComponent baseTypeComponent =
                component.getBaseTypeDefinition();
            Iterator iter = component.facets();
            // right now, this has better be an enumeration
            if (iter.hasNext()
                && component.getVarietyTag()
                    == SimpleTypeDefinitionComponent.VARIETY_ATOMIC) {
                Facet facet = (Facet) iter.next();
                if (facet instanceof EnumerationFacet) {
                    Iterator values = ((EnumerationFacet) facet).values();
                    if (values.hasNext()) {
                        // for anonymous enumeration type, map it by
                        // default to typesafe enumeration class. with
                        // switch jaxbenumtype, map it as per JAXB,
                        // which is to its base type.
                        if (_jaxbEnumType && (component.getName() == null)) {
                            String nsURI =
                                baseTypeComponent.getName().getNamespaceURI();
                            if (nsURI != null) {
                                return schemaTypeToSOAPType(
                                    baseTypeComponent,
                                    nameHint);
                            } else {
                                fail(
                                    "model.schema.invalidSimpleType.noNamespaceURI",
                                    new Object[] { component.getName()});

                            }
                        }
                        return enumerationToSOAPType(
                            component,
                            (EnumerationFacet) facet,
                            nameHint);
                    }
                }
            }
            if (component.getVarietyTag()
                == SimpleTypeDefinitionComponent.VARIETY_ATOMIC) {
                String nsURI = baseTypeComponent.getName().getNamespaceURI();
                if (nsURI != null) {
                    //XSD element or someother too. simpleTpye restricted from some other 
                    // simpleType will get mapped to the base of the super simpleType.
                    // OR restriction of a well-know type
                    SOAPType baseType =
                        schemaTypeToSOAPType(baseTypeComponent, nameHint);
                    return baseType;
                } else {
                    fail(
                        "model.schema.invalidSimpleType.noNamespaceURI",
                        new Object[] { component.getName()});
                }
            } else if (
                component.getVarietyTag()
                    == SimpleTypeDefinitionComponent.VARIETY_LIST) {
                if (doWeHandleSimpleSchemaTypeDerivationByList())
                    return listToSOAPType(component, nameHint);
                fail(
                    "model.schema.listNotSupported",
                    new Object[] { component.getName()});
            } else {
                // union
                fail(
                    "model.schema.unionNotSupported",
                    new Object[] { component.getName()});
            }
        }
        return null; // keep compiler happy
    }

    protected SOAPSimpleType createSOAPSimpleType(SimpleTypeDefinitionComponent component) {
        SOAPSimpleType simpleType = new SOAPSimpleType(component.getName());
        simpleType.setSchemaTypeRef(component.getName());
        JavaSimpleType javaType =
            (JavaSimpleType) _builtinSchemaTypeToJavaTypeMap.get(
                component.getName());
        if (javaType == null) {
            fail(
                "model.schema.invalidSimpleType.noJavaType",
                new Object[] { component.getName()});
        }
        simpleType.setJavaType(javaType);
        setReferenceable(simpleType);
        return simpleType;
    }

    protected String getJavaNameOfSOAPStructureType(
        SOAPStructureType structureType,
        TypeDefinitionComponent component,
        QName nameHint) {
        return makePackageQualified(
            _env.getNames().validJavaClassName(
                structureType.getName().getLocalPart()),
            structureType.getName());
    }

    protected String getJavaNameOfType(
        TypeDefinitionComponent component,
        QName nameHint) {
        QName componentName = component.getName();
        if (componentName == null) {
            //its anonymous type, so get the name from nameHint
            // TODO fix this with a more secure (non-clashable) name
            componentName =
                new QName(nameHint.getNamespaceURI(), nameHint.getLocalPart());
        }
        return makePackageQualified(
            _env.getNames().validJavaClassName(componentName.getLocalPart()),
            componentName);
    }

    protected SOAPType enumerationToSOAPType(
        SimpleTypeDefinitionComponent component,
        EnumerationFacet facet,
        QName nameHint) {
        // for now, we only support string enumerations
        SimpleTypeDefinitionComponent baseType =
            component.getBaseTypeDefinition();
        SimpleTypeEncoder encoder =
            (SimpleTypeEncoder) _simpleTypeEncoderMap.get(baseType.getName());

        /* If simpleType ns:Foo is derived from ns2:Bar and there are
           no explicit rules to define the mapping, the ns:Foo should
           map to javaType of ns:Foo */
        SimpleTypeDefinitionComponent tempComponent = component;
        while (encoder == null
            && !baseType.getName().equals(SchemaConstants.QNAME_TYPE_BOOLEAN)) {
            baseType = component.getBaseTypeDefinition();
            encoder =
                (SimpleTypeEncoder) _simpleTypeEncoderMap.get(
                    baseType.getName());
            component = baseType;
        }

        component = tempComponent;
        if (encoder != null) {
            QName componentName = component.getName();
            if (componentName == null) {
                //its anonymous type, so get the name from nameHint
                if (componentName == null) {
                    // TODO fix this with a more secure (non-clashable) name
                    componentName =
                        new QName(
                            nameHint.getNamespaceURI(),
                            nameHint.getLocalPart());
                }
            }
            JavaType javaEntryType =
                (JavaType) _builtinSchemaTypeToJavaTypeMap.get(
                    baseType.getName());
            String javaEnumName = getJavaNameOfType(component, nameHint);
            JavaEnumerationType javaEnumType =
                new JavaEnumerationType(javaEnumName, javaEntryType, false);
            //enumeration should have _Enumeration suffix on conflict as per JAXRPC spec 1.1
            resolveEnumerationNamingConflictsFor(javaEnumType);
            SOAPEnumerationType soapEnumType =
                new SOAPEnumerationType(
                    componentName,
                    schemaTypeToSOAPType(baseType, nameHint),
                    javaEnumType);

            boolean mustRename = false;
            for (Iterator values = facet.values(); values.hasNext();) {
                String value = (String) values.next();
                // TODO - for QName enumerations, we need to pass a XMLReader object to the encoder,
                // but it's unclear where to get one from at this stage
                try {
                    //bug fix: 4927847
                    JavaEnumerationEntry entry = null;
                    if (baseType
                        .getName()
                        .equals(SchemaConstants.QNAME_TYPE_QNAME))
                        entry =
                            new JavaEnumerationEntry(
                                value,
                                valueToQName(value, facet.getPrefixes()),
                                value);
                    else
                        entry =
                            new JavaEnumerationEntry(
                                value,
                                encoder.stringToObject(value, null),
                                value);

                    if (!mustRename && isInvalidEnumerationLabel(value)) {
                        mustRename = true;
                    }
                    javaEnumType.add(entry);
                } catch (Exception e) {
                    // most likely the encoder will have failed to produce a valid object
                    fail(
                        "model.schema.invalidLiteralInEnumeration",
                        value,
                        componentName);
                }
            }

            if (mustRename) {
                int index = 1;
                for (Iterator iter = javaEnumType.getEntries();
                    iter.hasNext();
                    ++index) {
                    JavaEnumerationEntry entry =
                        (JavaEnumerationEntry) iter.next();
                    entry.setName("value" + Integer.toString(index));
                }
            }

            return soapEnumType;
        } else {
            fail(
                "model.schema.encoderNotFound",
                new Object[] { component.getName()});
        }

        return null; // keep compiler happy
    }

    //vivekp, xsd:list
    protected LiteralType listToLiteralType(
        SimpleTypeDefinitionComponent component,
        QName nameHint) {
        LiteralType itemLiteralType = null;
        SimpleTypeDefinitionComponent itemType =
            component.getItemTypeDefinition();
        // bug fix: 4907872        
        if ((component.getName() != null)
            && (itemType.getName() != null)
            && component.getName().equals(itemType.getName())) {
            //bug fix: 4903760, self-referenced simpleType extended by list not allowed
            //TODO: throw error that a listType cant be extended by list
            fail(
                "model.schema.invalidSimpleType.invalidItemType",
                new Object[] { component.getName(), itemType.getName()});
        }

        //bug fix: 4852368, for null itemType, get the basetype
        itemLiteralType =
            schemaTypeToLiteralType(
                (itemType.getName() == null)
                    ? itemType.getBaseTypeDefinition().getName()
                    : itemType.getName());
        if (itemLiteralType != null) {
            JavaArrayType javaArrayType =
                new JavaArrayType(
                    itemLiteralType.getJavaType().getName() + "[]");
            javaArrayType.setElementType(itemLiteralType.getJavaType());
            JavaType javaMemberType = javaArrayType;

            /* Fix for Bug#4831561. Bug was due to binding xsd:list to
             * SchemaConstants.QNAME_LIST. It was resulting in the
             * conflict incase of mulpiple xsd:list with different
             * itemTypes having the same QNAME and javaType. The fix
             * is to bind it to component QNAME.
             */

            QName componentName = component.getName();
            if (componentName == null) {
                // TODO fix this with a more secure (non-clashable) name
                componentName =
                    new QName(
                        nameHint.getNamespaceURI(),
                        nameHint.getLocalPart() + "_Type");
            }
            return new LiteralListType(
                componentName,
                itemLiteralType,
                javaMemberType);
        } else {
            fail(
                "model.schema.invalidSimpleType.noItemLiteralType",
                new Object[] { component.getName(), itemType.getName()});
        }

        return null; // keep compiler happy 
    }

    /**
     * map xsd:list to SOAP type
     */
    protected SOAPType listToSOAPType(
        SimpleTypeDefinitionComponent component,
        QName nameHint) {
        SOAPType itemSOAPType = null;
        SimpleTypeDefinitionComponent itemType =
            component.getItemTypeDefinition();
        //bug fix: 4907872
        if ((component.getName() != null)
            && (itemType.getName() != null)
            && component.getName().equals(itemType.getName())) {
            // bug fix: 4903760, self-referenced simpleType extended by list not allowed
            //TODO: throw error that a listType cant be extended by list
            fail(
                "model.schema.invalidSimpleType.invalidItemType",
                new Object[] { component.getName(), itemType.getName()});
        }
        //bug fix: 4852368, for null itemType, get the basetype
        itemSOAPType =
            schemaTypeToSOAPType(
                (itemType.getName() == null)
                    ? itemType.getBaseTypeDefinition().getName()
                    : itemType.getName());

        if (itemSOAPType != null) {
            JavaArrayType javaArrayType =
                new JavaArrayType(itemSOAPType.getJavaType().getName() + "[]");
            javaArrayType.setElementType(itemSOAPType.getJavaType());
            JavaType javaMemberType = javaArrayType;
            QName componentName = component.getName();
            if (componentName == null) {
                // TODO fix this with a more secure (non-clashable) name
                componentName =
                    new QName(
                        nameHint.getNamespaceURI(),
                        nameHint.getLocalPart() + "_Type");
            }
            return new SOAPListType(
                componentName,
                itemSOAPType,
                javaMemberType);
        } else {
            fail(
                "model.schema.invalidSimpleType.noItemLiteralType",
                new Object[] { component.getName(), itemType.getName()});
        }
        return null; // keep compiler happy 
    }

    protected LiteralType enumerationToLiteralType(
        SimpleTypeDefinitionComponent component,
        EnumerationFacet facet,
        QName nameHint,
        QName mappingNameHint) {
        // for now, we only support string enumerations
        TypeDefinitionComponent baseType = component.getBaseTypeDefinition();
        SimpleTypeEncoder encoder =
            (SimpleTypeEncoder) _simpleTypeEncoderMap.get(baseType.getName());
        if (encoder != null
            && !baseType.getName().equals(SchemaConstants.QNAME_TYPE_BOOLEAN)) {
            JavaType javaEntryType =
                (JavaType) _builtinSchemaTypeToJavaTypeMap.get(
                    baseType.getName());
            QName componentName = component.getName();
            if (componentName == null) {
                // TODO fix this with a more secure (non-clashable) name
                componentName =
                    new QName(
                        nameHint.getNamespaceURI(),
                        nameHint.getLocalPart());
            }
            JavaEnumerationType javaEnumType =
                new JavaEnumerationType(
                    makePackageQualified(
                        _env.getNames().validJavaClassName(
                            (componentName.getLocalPart())),
                        componentName),
                    javaEntryType,
                    false);
            //enumeration should have _Enumeration suffix on conflict as per JAXRPC spec 1.1, 4.2.4
            resolveEnumerationNamingConflictsFor(javaEnumType);
            LiteralEnumerationType literalEnumType =
                new LiteralEnumerationType(
                    componentName,
                    schemaTypeToLiteralType(baseType, new QName("value")),
                    javaEnumType);

            //J2EE: only mess with it if it is an anonymous type
            if (component.getName() == null && mappingNameHint != null) {
                literalEnumType.setProperty(
                    ModelProperties.PROPERTY_ANONYMOUS_TYPE_NAME,
                    mappingNameHint.getLocalPart());
            }

            boolean mustRename = false;
            for (Iterator values = facet.values(); values.hasNext();) {
                String value = (String) values.next();
                // TODO - for QName enumerations, we need to pass a XMLReader object to the encoder,
                // but it's unclear where to get one from at this stage
                try {
                    //bug fix: 4927847
                    JavaEnumerationEntry entry = null;
                    if (baseType
                        .getName()
                        .equals(SchemaConstants.QNAME_TYPE_QNAME))
                        entry =
                            new JavaEnumerationEntry(
                                value,
                                valueToQName(value, facet.getPrefixes()),
                                value);
                    else
                        entry =
                            new JavaEnumerationEntry(
                                value,
                                encoder.stringToObject(value, null),
                                value);

                    if (!mustRename && isInvalidEnumerationLabel(value)) {
                        mustRename = true;
                    }
                    javaEnumType.add(entry);
                } catch (Exception e) {
                    // most likely the encoder will have failed to produce a valid object
                    fail(
                        "model.schema.invalidLiteralInEnumeration",
                        value,
                        component.getName());
                }
            }

            if (mustRename) {
                int index = 1;
                for (Iterator iter = javaEnumType.getEntries();
                    iter.hasNext();
                    ++index) {
                    JavaEnumerationEntry entry =
                        (JavaEnumerationEntry) iter.next();
                    entry.setName("value" + Integer.toString(index));
                }
            }

            return literalEnumType;
        } else {
            fail(
                "model.schema.encoderNotFound",
                new Object[] { component.getName()});
        }

        return null; // keep compiler happy
    }

    private QName valueToQName(String str, Map prefixes) throws Exception {
        if (str == null) {
            return null;
        }
        String uri = "";
        str = EncoderUtils.collapseWhitespace(str);
        String prefix = XmlUtil.getPrefix(str);
        if (prefix != null) {
            uri = (String) prefixes.get(prefix);
            if (uri == null) {
                throw new DeserializationException("xsd.unknownPrefix", prefix);
            }
        }

        String localPart = XmlUtil.getLocalPart(str);

        return new QName(uri, localPart);
    }

    protected SOAPType complexSchemaTypeToSOAPType(
        ComplexTypeDefinitionComponent component,
        QName nameHint) {
        SOAPCustomType userDefinedType = getCustomTypeFor(component);
        if (userDefinedType != null) {
            return userDefinedType;
        }
        if (component == _schema.getUrType()) {
            // handle anyType specially
            if (component
                .getName()
                .equals(SchemaConstants.QNAME_TYPE_URTYPE)) {
                //bug fix 4855115
                if (_strictCompliance) {
                    if (!checkStrictCompliance(component.getName()))
                        return null;
                }
                SOAPAnyType anyType = new SOAPAnyType(component.getName());
                JavaSimpleType javaType =
                    (JavaSimpleType) _builtinSchemaTypeToJavaTypeMap.get(
                        component.getName());
                if (javaType == null) {
                    // invalid simple type
                    //failUnsupported("U002", component.getName());
                    fail(
                        "model.schema.invalidSimpleType.noJavaType",
                        new Object[] { component.getName()});
                }
                anyType.setJavaType(javaType);
                return anyType;
            } else {
                SOAPSimpleType simpleType =
                    new SOAPSimpleType(component.getName());
                simpleType.setSchemaTypeRef(component.getName());
                JavaSimpleType javaType =
                    (JavaSimpleType) _builtinSchemaTypeToJavaTypeMap.get(
                        component.getName());
                if (javaType == null) {
                    // invalid simple type
                    //failUnsupported("U013", component.getName());
                    fail(
                        "model.schema.invalidSimpleType.noJavaType",
                        new Object[] { component.getName()});
                }
                simpleType.setJavaType(javaType);
                setReferenceable(simpleType);
                return simpleType;
            }
            //bug fix 4893908: check if component name is not null
        } else if (
            (component.getName() != null)
                && component.getName().equals(
                    soap11WSDLConstants.getQNameTypeArray())) {
            // handle SOAP-ENC:Array specially
            SOAPType anyType =
                schemaTypeToSOAPType(SchemaConstants.QNAME_TYPE_URTYPE);
            SOAPArrayType arrayType = new SOAPArrayType(component.getName());
            arrayType.setElementName(
                InternalEncodingConstants.ARRAY_ELEMENT_NAME);
            arrayType.setElementType(anyType);
            arrayType.setRank(1);
            arrayType.setSize(null);
            JavaArrayType javaArrayType =
                new JavaArrayType(anyType.getJavaType().getName() + "[]");
            javaArrayType.setElementType(anyType.getJavaType());
            arrayType.setJavaType(javaArrayType);
            return arrayType;
        } else if (component.getBaseTypeDefinition() == _schema.getUrType()) {
            return urTypeBasedComplexSchemaTypeToSOAPType(component, nameHint);
        } else if (
            //bug fix 4893908: check if component base type definition name is not null
         (
            component.getBaseTypeDefinition().getName() != null)
                && component.getBaseTypeDefinition().getName().equals(
                    soap11WSDLConstants.getQNameTypeArray())) {
            return soapArrayBasedComplexSchemaTypeToSOAPType(
                component,
                nameHint);
        } else {
            if (component.getName() != null) {
                JavaSimpleType javaType =
                    (JavaSimpleType) _builtinSchemaTypeToJavaTypeMap.get(
                        component.getName());
                if (javaType != null) {
                    SOAPSimpleType simpleType =
                        new SOAPSimpleType(component.getName());
                    simpleType.setSchemaTypeRef(component.getName());
                    simpleType.setJavaType(javaType);
                    setReferenceable(simpleType);
                    return simpleType;
                }
            }
            // it could be "inheritance"
            if (component.getDerivationMethod() == Symbol.EXTENSION) {
                SOAPType parentType =
                    schemaTypeToSOAPType(
                        component.getBaseTypeDefinition(),
                        nameHint);
                if (parentType instanceof SOAPStructureType) {
                    return soapStructureExtensionComplexSchemaTypeToSOAPType(
                        component,
                        (SOAPStructureType) parentType,
                        nameHint);
                } else {
                    // a parent type we cannot make sense of
                    return mustGetCustomTypeFor(component);
                }
            } else {
                // we don't support restrictions on complex types
                return mustGetCustomTypeFor(component);
            }
        }
    }

    protected SchemaJavaMemberInfo getJavaMemberInfo(
        TypeDefinitionComponent component,
        ElementDeclarationComponent element) {

        return new SchemaJavaMemberInfo(
            element.getName().getLocalPart(),
            false);
    }

    protected SOAPType urTypeBasedComplexSchemaTypeToSOAPType(
        ComplexTypeDefinitionComponent component,
        QName nameHint) {
        // first see if there is a built-in type for it
        if (component.getName() != null) {
            JavaSimpleType javaType =
                (JavaSimpleType) _builtinSchemaTypeToJavaTypeMap.get(
                    component.getName());
            if (javaType != null) {
                SOAPSimpleType simpleType =
                    new SOAPSimpleType(component.getName());
                simpleType.setSchemaTypeRef(component.getName());
                simpleType.setJavaType(javaType);
                setReferenceable(simpleType);
                return simpleType;
            }
        }
        // most likely, a structure type
        if (component.getContentTag()
            == ComplexTypeDefinitionComponent.CONTENT_ELEMENT_ONLY) {
            //if (component.hasNoAttributeUses()) {
            ParticleComponent particle = component.getParticleContent();
            if (particle.occursOnce()) {
                if (particle.getTermTag()
                    == ParticleComponent.TERM_MODEL_GROUP) {
                    ModelGroupComponent modelGroup =
                        particle.getModelGroupTerm();
                    if (modelGroup.getCompositor() == Symbol.ALL
                        || modelGroup.getCompositor() == Symbol.SEQUENCE) {

                        SOAPStructureType structureType = null;

                        if (modelGroup.getCompositor() == Symbol.ALL) {
                            // bug fix 4893908, get the unique name for the complexType
                            structureType =
                                new SOAPUnorderedStructureType(
                                    getUniqueQNameFor(component, nameHint));
                        } else {
                            structureType =
                                new SOAPOrderedStructureType(
                                    getUniqueQNameFor(component, nameHint));
                        }
                        // bug fix: 4893908, instead of component.getName(), get the name from structureType 
                        // avoid NPE
                        String javaTypeName =
                            getJavaNameOfSOAPStructureType(
                                structureType,
                                component,
                                nameHint);
                        JavaStructureType javaStructureType =
                            new JavaStructureType(
                                javaTypeName,
                                false,
                                structureType);
                        resolveNamingConflictsFor(javaStructureType);
                        structureType.setJavaType(javaStructureType);
                        _componentToSOAPTypeMap.put(component, structureType);

                        //handle attributes, bug fix 4926320 
                        for (Iterator iter = component.attributeUses();
                            iter.hasNext();
                            ) {
                            AttributeUseComponent attributeUse =
                                (AttributeUseComponent) iter.next();
                            AttributeDeclarationComponent attributeDeclaration =
                                (AttributeDeclarationComponent) attributeUse
                                    .getAttributeDeclaration();
                            //bug fix 4855115
                            if (attributeDeclaration
                                .getTypeDefinition()
                                .getName()
                                != null) {
                                if (_strictCompliance
                                    && attributeDeclaration
                                        .getTypeDefinition()
                                        .getName()
                                        .equals(
                                        SchemaConstants.QNAME_TYPE_IDREF)) {
                                    return mustGetCustomTypeFor(component);
                                }
                            }
                            SOAPType attributeType =
                                schemaTypeToSOAPType(
                                    attributeDeclaration.getTypeDefinition(),
                                    getAttributeQNameHint(
                                        attributeDeclaration,
                                        nameHint));

                            //as per JAXRPC 1.1 spec, map optional
                            //attribute with optional use and no
                            //default and fixed attribute should be
                            //mapped to a boxed type
                            if (isAttributeOptional(attributeUse)) {
                                // bug fix: 4961579                               
                                SOAPType tmpType = nillableSchemaTypeToSOAPType(
                                    attributeDeclaration.getTypeDefinition());
                                if (tmpType != null) {
                                    attributeType = tmpType;
                                }
//                                JavaSimpleType javaType =
//                                    (
//                                        JavaSimpleType) _builtinSchemaTypeToJavaWrapperTypeMap
//                                            .get(
//                                        attributeDeclaration
//                                            .getTypeDefinition()
//                                            .getName());
//                                if (javaType != null) {
//                                    SOAPSimpleType result =
//                                        (
//                                            SOAPSimpleType) _nillableSimpleTypeComponentToSOAPTypeMap
//                                                .get(
//                                            attributeDeclaration
//                                                .getTypeDefinition());
//                                    if (result == null) {
//                                        // Fix for bug 4900902
//                                        result =
//                                            new SOAPSimpleType(
//                                                attributeDeclaration
//                                                    .getTypeDefinition()
//                                                    .getName(),
//                                                javaType,
//                                                true);
//                                        result.setSchemaTypeRef(
//                                            attributeDeclaration
//                                                .getTypeDefinition()
//                                                .getName());
//                                        _nillableSimpleTypeComponentToSOAPTypeMap
//                                            .put(
//                                            attributeDeclaration
//                                                .getTypeDefinition(),
//                                            result);
//                                    }
//                                    if (result != null)
//                                        attributeType = result;
//                                }

                            }
                            //bug fix: 4863953
                            if (SimpleTypeSerializerWriter
                                .getTypeEncoder(attributeType)
                                == null
                                && !isAttributeEnumeration(attributeType)) {
                                // unsupported simple type
                                return mustGetCustomTypeFor(component);
                            }
                            SOAPAttributeMember member =
                                new SOAPAttributeMember(
                                    attributeDeclaration.getName(),
                                    attributeType);
                            if (attributeUse.isRequired()) {
                                member.setRequired(true);
                            }
                            SchemaJavaMemberInfo memberInfo =
                                getJavaMemberOfElementInfo(
                                    nameHint,
                                    attributeDeclaration
                                        .getName()
                                        .getLocalPart());
                            JavaStructureMember javaMember =
                                new JavaStructureMember(
                                    memberInfo.javaMemberName,
                                    attributeType.getJavaType(),
                                    member,
                                    memberInfo.isDataMember);
                            javaMember.setReadMethod(
                                _env.getNames().getJavaMemberReadMethod(
                                    javaMember));
                            javaMember.setWriteMethod(
                                _env.getNames().getJavaMemberWriteMethod(
                                    javaMember));
                            member.setJavaStructureMember(javaMember);
                            javaStructureType.add(javaMember);
                            structureType.add((SOAPAttributeMember) member);
                        }

                        for (Iterator iter = modelGroup.particles();
                            iter.hasNext();
                            ) {
                            ParticleComponent memberParticle =
                                (ParticleComponent) iter.next();
                            if (memberParticle.occursOnce()
                                || memberParticle.occursAtMostOnce()) {
                                if (memberParticle.getTermTag()
                                    == ParticleComponent.TERM_ELEMENT) {

                                    ElementDeclarationComponent element =
                                        memberParticle.getElementTerm();

                                    SOAPType memberType;
                                    //handle minOccurs=0/maxOccurs=1, to generate boxedType
                                    memberType =
                                        getSOAPMemberType(
                                            component,
                                            structureType,
                                            element,
                                            nameHint,
                                            memberParticle.occursZeroOrOne());
                                    SOAPStructureMember member =
                                        new SOAPStructureMember(
                                            element.getName(),
                                            memberType);
                                    SchemaJavaMemberInfo memberInfo =
                                        getJavaMemberInfo(component, element);
                                    JavaStructureMember javaMember =
                                        new JavaStructureMember(
                                            _env
                                                .getNames()
                                                .validJavaMemberName(
                                                memberInfo.javaMemberName),
                                            memberType.getJavaType(),
                                            member,
                                            memberInfo.isDataMember);
                                    javaMember.setReadMethod(
                                        _env
                                            .getNames()
                                            .getJavaMemberReadMethod(
                                            javaMember));
                                    javaMember.setWriteMethod(
                                        _env
                                            .getNames()
                                            .getJavaMemberWriteMethod(
                                            javaMember));
                                    member.setJavaStructureMember(javaMember);
                                    javaStructureType.add(javaMember);
                                    structureType.add(member);
                                } else {
                                    // cannot deal with non-element terms at this level
                                    return mustGetCustomTypeFor(component);
                                }
                            } else {
                                // cannot deal with multiple occurrences
                                return mustGetCustomTypeFor(component);
                            }
                        }

                        structureType.setJavaType(javaStructureType);
                        return structureType;
                    } else {
                        // cannot deal with choice
                        return mustGetCustomTypeFor(component);
                    }
                } else {
                    // wildcard or element -- cannot deal with them
                    return mustGetCustomTypeFor(component);
                }
            } else {
                // multiple occurrence, cannot deal with that right now
                return mustGetCustomTypeFor(component);
            }
        } else if (
            component.getContentTag()
                == ComplexTypeDefinitionComponent.CONTENT_EMPTY) {
            // empty content

            // check that there are no attributes
            SOAPOrderedStructureType structureType =
                new SOAPOrderedStructureType(component.getName());
            String javaName = getJavaNameOfType(component, component.getName());
            JavaStructureType javaStructureType =
                new JavaStructureType(javaName, false, structureType);
            structureType.setJavaType(javaStructureType);

            _componentToLiteralTypeMap.put(component, structureType);

            // handle attributes, bug fix 4926320 
            for (Iterator iter = component.attributeUses(); iter.hasNext();) {
                AttributeUseComponent attributeUse =
                    (AttributeUseComponent) iter.next();
                AttributeDeclarationComponent attributeDeclaration =
                    (AttributeDeclarationComponent) attributeUse
                        .getAttributeDeclaration();
                SOAPType attributeType =
                    schemaTypeToSOAPType(
                        attributeDeclaration.getTypeDefinition(),
                        getAttributeQNameHint(attributeDeclaration, nameHint));
                if (SimpleTypeSerializerWriter.getTypeEncoder(attributeType)
                    == null) {
                    // unsupported simple type
                    return mustGetCustomTypeFor(component);
                }
                SOAPAttributeMember member =
                    new SOAPAttributeMember(
                        attributeDeclaration.getName(),
                        attributeType);
                if (attributeUse.isRequired()) {
                    member.setRequired(true);
                }
                SchemaJavaMemberInfo memberInfo =
                    getJavaMemberOfElementInfo(
                        nameHint,
                        attributeDeclaration.getName().getLocalPart());
                JavaStructureMember javaMember =
                    new JavaStructureMember(
                        memberInfo.javaMemberName,
                        attributeType.getJavaType(),
                        member,
                        memberInfo.isDataMember);
                javaMember.setReadMethod(
                    _env.getNames().getJavaMemberReadMethod(javaMember));
                javaMember.setWriteMethod(
                    _env.getNames().getJavaMemberWriteMethod(javaMember));
                member.setJavaStructureMember(javaMember);
                javaStructureType.add(javaMember);
                structureType.add(member);
            }

            return structureType;
        } else if (
            component.getContentTag()
                == ComplexTypeDefinitionComponent.CONTENT_SIMPLE) {
            // simple content

            // this is a special case, in that all SOAP encoded types (e.g. soap-enc:int)
            // are complex types with simple content, rather than simple types
            if (component.hasNoAttributeUses()) {
                if (component
                    .getName()
                    .getNamespaceURI()
                    .equals(soap11WSDLConstants.getSOAPEncodingNamespace())) {
                    // we map soap-enc types to simple types!

                    SOAPSimpleType simpleType =
                        new SOAPSimpleType(component.getName());
                    simpleType.setSchemaTypeRef(component.getName());
                    JavaSimpleType javaType =
                        (JavaSimpleType) _builtinSchemaTypeToJavaTypeMap.get(
                            component.getName());
                    if (javaType == null) {
                        // invalid simple type
                        fail(
                            "model.schema.invalidSimpleType.noJavaType",
                            new Object[] { component.getName()});
                    }
                    simpleType.setJavaType(javaType);
                    setReferenceable(simpleType);
                    return simpleType;
                } else {
                    // not a soap-enc type, make it a custom type
                    return mustGetCustomTypeFor(component);
                }
            } else {
                // it has attributes, make it a custom type
                return mustGetCustomTypeFor(component);
            }
        } else {
            // mixed content - not a valid SOAP type
            // TODO - is it true that simpleContent is not valid in SOAP?
            //        and if this isn't a valid SOAP type, should we throw an exception here?
            return mustGetCustomTypeFor(component);
        }
    }

    /**
     * @param element
     * @param nameHint
     * @return
     */
    protected SOAPType getSOAPMemberType(
        ComplexTypeDefinitionComponent component,
        SOAPStructureType structureType,
        ElementDeclarationComponent element,
        QName nameHint,
        boolean occursZeroOrOne) {
        SOAPType memberType = null;
        if (element.isNillable() || occursZeroOrOne) {
            // handle nillable elements specially
            if (element.getTypeDefinition().isSimple()) {
                memberType =
                    nillableSchemaTypeToSOAPType(element.getTypeDefinition());
            } else {
                memberType =
                    schemaTypeToSOAPType(element.getTypeDefinition(), nameHint);
            }
        } else {
            memberType =
                schemaTypeToSOAPType(
                    element.getTypeDefinition(),
                    getElementQNameHint(element, nameHint));
        }
        return memberType;
    }

    // bug fix: 4923650
    protected LiteralType getLiteralMemberType(
        ComplexTypeDefinitionComponent component,
        LiteralType memberType,
        ElementDeclarationComponent element,
        LiteralStructuredType structureType) {
        return memberType;
    }

    protected SOAPType soapStructureExtensionComplexSchemaTypeToSOAPType(
        ComplexTypeDefinitionComponent component,
        SOAPStructureType parentType,
        QName nameHint) {

        //
        // TODO - unify this method with the previous one (urTypeBasedComplexSchemaTypeToSOAPType)
        // (formally, this is a bit odd, because the previous one is used for restrictions most of the time,
        // but in practice the effect is the same!)
        //

        // most likely, a structure type
        if (component.getContentTag()
            == ComplexTypeDefinitionComponent.CONTENT_ELEMENT_ONLY) {
            if (component.hasNoAttributeUses()) {
                ParticleComponent particle = component.getParticleContent();
                if (particle.occursOnce()) {
                    if (particle.getTermTag()
                        == ParticleComponent.TERM_MODEL_GROUP) {
                        ModelGroupComponent modelGroup =
                            particle.getModelGroupTerm();
                        if (modelGroup.getCompositor() == Symbol.ALL
                            || modelGroup.getCompositor() == Symbol.SEQUENCE) {

                            SOAPStructureType structureType = null;

                            if (modelGroup.getCompositor() == Symbol.ALL) {
                                structureType =
                                    new SOAPUnorderedStructureType(
                                        component.getName());
                            } else {
                                structureType =
                                    new SOAPOrderedStructureType(
                                        component.getName());
                            }

                            if (parentType != null
                                && parentType.getClass()
                                    != structureType.getClass()) {
                                // an odd mismatch, give up
                                return mustGetCustomTypeFor(component);
                            }
                            String javaName =
                                getJavaNameOfType(
                                    component,
                                    component.getName());
                            JavaStructureType javaStructureType =
                                new JavaStructureType(
                                    javaName,
                                    false,
                                    structureType);
                            resolveNamingConflictsFor(javaStructureType);
                            structureType.setJavaType(javaStructureType);
                            _componentToSOAPTypeMap.put(
                                component,
                                structureType);

                            if (parentType != null) {
                                processSuperType(
                                    parentType,
                                    structureType,
                                    javaStructureType);
                            }

                            for (Iterator iter = modelGroup.particles();
                                iter.hasNext();
                                ) {
                                ParticleComponent memberParticle =
                                    (ParticleComponent) iter.next();
                                if (memberParticle.occursOnce()
                                    || memberParticle.occursAtMostOnce()) {
                                    if (memberParticle.getTermTag()
                                        == ParticleComponent.TERM_ELEMENT) {

                                        ElementDeclarationComponent element =
                                            memberParticle.getElementTerm();

                                        SOAPType memberType;
                                        if (element.isNillable()) {
                                            // handle nillable elements specially
                                            if (element
                                                .getTypeDefinition()
                                                .isSimple()) {
                                                memberType =
                                                    nillableSchemaTypeToSOAPType(
                                                        element
                                                            .getTypeDefinition());
                                            } else {
                                                memberType =
                                                    schemaTypeToSOAPType(
                                                        element
                                                            .getTypeDefinition(),
                                                        nameHint);
                                            }
                                        } else {
                                            memberType =
                                                schemaTypeToSOAPType(
                                                    element.getTypeDefinition(),
                                                    nameHint);
                                        }
                                        SOAPStructureMember member =
                                            new SOAPStructureMember(
                                                element.getName(),
                                                memberType);
                                        SchemaJavaMemberInfo memberInfo =
                                            getJavaMemberInfo(
                                                component,
                                                element);
                                        JavaStructureMember javaMember =
                                            new JavaStructureMember(
                                                memberInfo.javaMemberName,
                                                memberType.getJavaType(),
                                                member,
                                                memberInfo.isDataMember);
                                        javaMember.setReadMethod(
                                            _env
                                                .getNames()
                                                .getJavaMemberReadMethod(
                                                javaMember));
                                        javaMember.setWriteMethod(
                                            _env
                                                .getNames()
                                                .getJavaMemberWriteMethod(
                                                javaMember));
                                        member.setJavaStructureMember(
                                            javaMember);
                                        javaStructureType.add(javaMember);
                                        structureType.add(member);
                                    } else {
                                        // cannot deal with non-element terms at this level
                                        return mustGetCustomTypeFor(component);
                                    }
                                } else {
                                    // cannot deal with multiple occurrences
                                    return mustGetCustomTypeFor(component);
                                }
                            }

                            structureType.setJavaType(javaStructureType);
                            return structureType;
                        } else {
                            // cannot deal with choice
                            return mustGetCustomTypeFor(component);
                        }
                    } else {
                        // wildcard or element -- cannot deal with them
                        return mustGetCustomTypeFor(component);
                    }
                } else {
                    // multiple occurrence, cannot deal with that right now
                    return mustGetCustomTypeFor(component);
                }
            } else {
                // it has attributes, make it a custom type
                return mustGetCustomTypeFor(component);
            }
        } else if (
            component.getContentTag()
                == ComplexTypeDefinitionComponent.CONTENT_EMPTY) {
            // empty content

            // check that there are no attributes
            if (component.hasNoAttributeUses()) {
                // use the same kind of structure type as the supertype, if present
                SOAPStructureType structureType = null;
                if (parentType != null) {
                    if (parentType instanceof SOAPOrderedStructureType) {
                        structureType =
                            new SOAPOrderedStructureType(component.getName());
                    } else {
                        structureType =
                            new SOAPUnorderedStructureType(component.getName());
                    }
                } else {
                    // use ordered by default
                    structureType =
                        new SOAPOrderedStructureType(component.getName());
                }
                String javaTypeName =
                    getJavaNameOfType(component, component.getName());
                JavaStructureType javaStructureType =
                    new JavaStructureType(javaTypeName, false, structureType);

                structureType.setJavaType(javaStructureType);

                if (parentType != null
                    && parentType.getClass() != structureType.getClass()) {
                    // an odd mismatch, give up
                    return mustGetCustomTypeFor(component);
                }

                if (parentType != null) {
                    processSuperType(
                        parentType,
                        structureType,
                        javaStructureType);
                }

                return structureType;
            } else {
                // it has attributes, make it a custom type
                return mustGetCustomTypeFor(component);
            }
        } else if (
            component.getContentTag()
                == ComplexTypeDefinitionComponent.CONTENT_SIMPLE) {
            // simple content

            // this is a special case, in that all SOAP encoded types (e.g. soap-enc:int)
            // are complex types with simple content, rather than simple types
            if (component.hasNoAttributeUses()) {
                if (component
                    .getName()
                    .getNamespaceURI()
                    .equals(soap11WSDLConstants.getSOAPEncodingNamespace())) {
                    // we map soap-enc types to simple types!

                    SOAPSimpleType simpleType =
                        new SOAPSimpleType(component.getName());
                    simpleType.setSchemaTypeRef(component.getName());
                    JavaSimpleType javaType =
                        (JavaSimpleType) _builtinSchemaTypeToJavaTypeMap.get(
                            component.getName());
                    if (javaType == null) {
                        // invalid simple type
                        fail(
                            "model.schema.invalidSimpleType.noJavaType",
                            new Object[] { component.getName()});
                    }
                    simpleType.setJavaType(javaType);
                    setReferenceable(simpleType);
                    return simpleType;
                } else {
                    // not a soap-enc type, make it a custom type
                    return mustGetCustomTypeFor(component);
                }
            } else {
                // it has attributes, make it a custom type
                return mustGetCustomTypeFor(component);
            }
        } else {
            // mixed content - not a valid SOAP type
            // TODO - is it true that simpleContent is not valid in SOAP?
            //        and if this isn't a valid SOAP type, should we throw an exception here?
            return mustGetCustomTypeFor(component);
        }
    }

    protected SOAPType soapArrayBasedComplexSchemaTypeToSOAPType(
        ComplexTypeDefinitionComponent component,
        QName nameHint) {
        // a SOAP array
        // first see if there is a built-in type for it
        if (component.getName() != null) {
            JavaSimpleType javaType =
                (JavaSimpleType) _builtinSchemaTypeToJavaTypeMap.get(
                    component.getName());
            if (javaType != null) {
                SOAPSimpleType simpleType =
                    new SOAPSimpleType(component.getName());
                simpleType.setSchemaTypeRef(component.getName());
                simpleType.setJavaType(javaType);
                setReferenceable(simpleType);
                return simpleType;
            }
        }
        // now see if there is an soap-enc:arrayType attribute with a wsdl:arrayType attribute on it
        boolean found = false;
        for (Iterator iter = component.attributeUses(); iter.hasNext();) {
            AttributeUseComponent attributeUse =
                (AttributeUseComponent) iter.next();
            AttributeDeclarationComponent attributeDeclaration =
                attributeUse.getAttributeDeclaration();
            if (attributeDeclaration.getName() != null
                && attributeDeclaration.getName().equals(
                    soap11WSDLConstants.getQNameAttrArrayType())) {
                if (found) {
                    // syntax error, presumably
                    return mustGetCustomTypeFor(component);
                }

                found = true;

                // look for a wsdl:arrayType attribute in the annotation of the component
                for (Iterator iter2 = attributeUse.getAnnotation().attributes();
                    iter2.hasNext();
                    ) {
                    SchemaAttribute attr = (SchemaAttribute) iter2.next();
                    if (attr
                        .getQName()
                        .equals(WSDLConstants.QNAME_ATTR_ARRAY_TYPE)) {
                        String typeSpecifier = attr.getValue();
                        if (typeSpecifier == null) {
                            throw new ModelException(
                                new ValidationException(
                                    "validation.invalidAttributeValue",
                                    new Object[] {
                                        typeSpecifier,
                                        "arrayType" }));
                        } else {
                            return processSOAPArrayType(
                                component,
                                attr.getParent(),
                                attr.getValue());
                        }
                    }
                }
            }
        }

        // no soap-enc:arrayType found, look for content
        if (component.getContentTag()
            == ComplexTypeDefinitionComponent.CONTENT_ELEMENT_ONLY) {
            ParticleComponent particle = component.getParticleContent();
            if (particle.occursOnce()) {
                if (particle.getTermTag()
                    == ParticleComponent.TERM_MODEL_GROUP) {
                    ModelGroupComponent modelGroup =
                        particle.getModelGroupTerm();
                    if (modelGroup.getCompositor() == Symbol.SEQUENCE) {

                        SOAPArrayType arrayType =
                            new SOAPArrayType(component.getName());
                        found = false;

                        for (Iterator iter = modelGroup.particles();
                            iter.hasNext();
                            ) {
                            ParticleComponent memberParticle =
                                (ParticleComponent) iter.next();

                            if (found) {
                                // more than one particle in the sequence, bail out
                                return mustGetCustomTypeFor(component);
                            } else {
                                found = true;
                            }
                            if (memberParticle.mayOccurMoreThanOnce()) {
                                if (memberParticle.getTermTag()
                                    == ParticleComponent.TERM_ELEMENT) {

                                    ElementDeclarationComponent element =
                                        memberParticle.getElementTerm();

                                    SOAPType arrayElementType =
                                        schemaTypeToSOAPType(
                                            element.getTypeDefinition(),
                                            nameHint);
                                    arrayType.setElementName(element.getName());
                                    arrayType.setElementType(arrayElementType);
                                    arrayType.setRank(1);
                                    if (arrayElementType.getJavaType()
                                        != null) {
                                        JavaArrayType javaArrayType =
                                            new JavaArrayType(
                                                arrayElementType
                                                    .getJavaType()
                                                    .getName()
                                                    + "[]");
                                        javaArrayType.setElementType(
                                            arrayElementType.getJavaType());
                                        arrayType.setJavaType(javaArrayType);
                                    }
                                } else {
                                    // cannot deal with non-element terms at this level
                                    return mustGetCustomTypeFor(component);
                                }
                            } else {
                                // uncommon range
                                return mustGetCustomTypeFor(component);
                            }
                        }

                        if (found) {
                            return arrayType;
                        } else {
                            // the model group is empty, so the array has no content!
                            return mustGetCustomTypeFor(component);
                        }
                    } else {
                        // not a sequence
                        return mustGetCustomTypeFor(component);
                    }
                } else {
                    // not a model group
                    // TODO - shouldn't an element term be legal in this context?
                    return mustGetCustomTypeFor(component);
                }
            } else {
                // particle doesn't occur just once
                return mustGetCustomTypeFor(component);
            }
        } else {
            // empty, mixed or simple content
            // TODO - I believe this should be an error
            return mustGetCustomTypeFor(component);
        }
    }

    protected SOAPType processSOAPArrayType(
        TypeDefinitionComponent component,
        SchemaElement element,
        String typeSpecifier) {

        try {

            // strategy: build the array types left to right
            int openingBracketIndex = typeSpecifier.indexOf('[');
            if (openingBracketIndex == -1) {
                throw new ValidationException(
                    "validation.invalidAttributeValue",
                    new Object[] { typeSpecifier, "arrayType" });
            }

            int currentRank = 0;
            String typeName =
                typeSpecifier.substring(0, openingBracketIndex).trim();
            QName typeQName = element.asQName(typeName);
            SOAPType elementType = schemaTypeToSOAPType(typeQName);
            if (elementType instanceof SOAPArrayType) {
                currentRank = ((SOAPArrayType) elementType).getRank();
            }

            for (;;) {
                int closingBracketIndex =
                    typeSpecifier.indexOf(']', openingBracketIndex);
                if (closingBracketIndex == -1) {
                    throw new ValidationException(
                        "validation.invalidAttributeValue",
                        new Object[] { typeSpecifier, "arrayType" });
                }

                int commaIndex =
                    typeSpecifier.indexOf(',', openingBracketIndex + 1);
                if (commaIndex == -1 || commaIndex > closingBracketIndex) {
                    // one-dimensional arrays are quite common, so we treat them specially
                    int[] size = null;
                    if (closingBracketIndex - openingBracketIndex > 1) {
                        int i =
                            Integer.parseInt(
                                typeSpecifier.substring(
                                    openingBracketIndex + 1,
                                    closingBracketIndex));
                        size = new int[] { i };
                    }

                    SOAPArrayType arrayType =
                        new SOAPArrayType(component.getName());
                    arrayType.setElementName(
                        InternalEncodingConstants.ARRAY_ELEMENT_NAME);
                    arrayType.setElementType(elementType);
                    arrayType.setRank(++currentRank);
                    arrayType.setSize(size);
                    if (elementType.getJavaType() != null) {
                        JavaArrayType javaArrayType =
                            new JavaArrayType(
                                elementType.getJavaType().getName() + "[]");
                        javaArrayType.setElementType(elementType.getJavaType());
                        arrayType.setJavaType(javaArrayType);
                    }
                    elementType = arrayType;
                } else {
                    List sizeList = null;
                    boolean allowSizeSpecifiers = true;
                    boolean timeToGo = false;
                    int rank = 0;
                    int contentIndex = openingBracketIndex + 1;
                    for (;;) {
                        ++rank;
                        if (commaIndex - contentIndex > 0) {
                            if (!allowSizeSpecifiers) {
                                throw new ValidationException(
                                    "validation.invalidAttributeValue",
                                    new Object[] {
                                        typeSpecifier,
                                        "arrayType" });
                            }
                            int i =
                                Integer.parseInt(
                                    typeSpecifier.substring(
                                        contentIndex,
                                        commaIndex));
                            if (sizeList == null) {
                                sizeList = new ArrayList();
                            }
                            sizeList.add(new Integer(i));
                        } else {
                            // no size specifier
                            if (sizeList != null) {
                                throw new ValidationException(
                                    "validation.invalidAttributeValue",
                                    new Object[] {
                                        typeSpecifier,
                                        "arrayType" });
                            }
                            allowSizeSpecifiers = false;
                        }

                        if (timeToGo) {
                            break;
                        }
                        contentIndex = commaIndex + 1;
                        commaIndex = typeSpecifier.indexOf(',', contentIndex);
                        if (commaIndex == -1
                            || commaIndex > closingBracketIndex) {
                            commaIndex = closingBracketIndex;
                            timeToGo = true;
                        }
                    }

                    SOAPArrayType arrayType =
                        new SOAPArrayType(component.getName());
                    arrayType.setElementName(
                        InternalEncodingConstants.ARRAY_ELEMENT_NAME);
                    arrayType.setElementType(elementType);
                    currentRank += rank;
                    arrayType.setRank(currentRank);
                    int[] size = null;
                    if (allowSizeSpecifiers && sizeList != null) {
                        size = new int[sizeList.size()];
                        for (int i = 0; i < size.length; ++i) {
                            size[i] = ((Integer) sizeList.get(i)).intValue();
                        }
                    }
                    arrayType.setSize(size);
                    if (elementType.getJavaType() != null) {
                        StringBuffer sb = new StringBuffer();
                        sb.append(elementType.getJavaType().getName());
                        for (int i = 0; i < rank; ++i) {
                            sb.append("[]");
                        }
                        String javaArrayTypeName = sb.toString();
                        JavaArrayType javaArrayType =
                            new JavaArrayType(javaArrayTypeName);
                        javaArrayType.setElementType(elementType.getJavaType());
                        arrayType.setJavaType(javaArrayType);
                    }
                    elementType = arrayType;
                }

                openingBracketIndex =
                    typeSpecifier.indexOf('[', closingBracketIndex + 1);
                if (openingBracketIndex == -1) {
                    if (closingBracketIndex != typeSpecifier.length() - 1) {
                        throw new ValidationException(
                            "validation.invalidAttributeValue",
                            new Object[] { typeSpecifier, "arrayType" });
                    }
                    break;
                }
            }
            //bug fix:4904604
            //set the hgolder name, it may be a inout parameter
            setSOAPArrayHolderName(elementType);
            return elementType;
        } catch (NumberFormatException e) {
            throw new ModelException(
                new ValidationException(
                    "validation.invalidAttributeValue",
                    new Object[] { typeSpecifier, "arrayType" }));
        } catch (ValidationException e) {
            throw new ModelException(e);
        }
    }

    /**
     * bug fix:4904604
     * soap array holder name is the name of type in schema
     */
    private void setSOAPArrayHolderName(SOAPType type) {
        if (type instanceof SOAPArrayType) {
            JavaType javaType = type.getJavaType();
            if (javaType instanceof JavaArrayType)
                ((JavaArrayType) javaType).setSOAPArrayHolderName(
                    type.getName().getLocalPart());
        }
    }

    // NOTE - a lot of the code for the schema-to-literal case is the same as for schema-to-soap,
    // except that the returned type objects are of the literal kind; investigate ways to avoid
    // this code duplication; on the other hand, it's possible that as we improve our data binding
    // facilities, the two code paths will diverge significantly, so maybe we shouldn't do anything
    // right now!
    protected LiteralType schemaTypeToLiteralType(
        TypeDefinitionComponent component,
        QName nameHint) {
        QName mappingNameHint = component.getName();
        QName hint = component.getName();
        //if (hint != null) {
        //    mappingNameHint = hint.getLocalPart();
        //}
        return schemaTypeToLiteralType(component, nameHint, mappingNameHint);
    }

    /**
     * @param component The type component in process
     * @param nameHint in case of anonymous type, hint for jax-rpc internal mapping name
     * @param mappingNameHint in case of anonymouse type, hint for J2EE mapping name for meta data
     */
    protected LiteralType schemaTypeToLiteralType(
        TypeDefinitionComponent component,
        QName nameHint,
        QName mappingNameHint) {
        LiteralType result =
            (LiteralType) _componentToLiteralTypeMap.get(component);
        if (result == null) {
            try {
                if (_noDataBinding) {
                    result = getLiteralFragmentTypeFor(component, nameHint);
                } else {
                    if (component.isSimple()) {
                        result =
                            simpleSchemaTypeToLiteralType(
                                (SimpleTypeDefinitionComponent) component,
                                nameHint,
                                mappingNameHint);
                    } else if (component.isComplex()) {
                        result =
                            complexSchemaTypeToLiteralType(
                                (ComplexTypeDefinitionComponent) component,
                                nameHint,
                                mappingNameHint);
                    } else {
                        // should not happen
                        throw new IllegalArgumentException();
                    }
                }

                // in some cases (e.g. some complex types), this will replace an entry already
                // in the map with exactly the same entry, so it doesn't do any real harm
                // and it's easier than trying to do this *exactly* once
                _componentToLiteralTypeMap.put(component, result);

            } finally {
            }
        }

        return result;
    }

    protected LiteralType simpleSchemaTypeToLiteralType(
        SimpleTypeDefinitionComponent component,
        QName nameHint) {
        return simpleSchemaTypeToLiteralType(component, nameHint, null);

    }
    protected LiteralType simpleSchemaTypeToLiteralType(
        SimpleTypeDefinitionComponent component,
        QName nameHint,
        QName mappingNameHint) {
        // instead of returning a fragment type (which would cause quite a bit of damage, because
        // it would force all the complex types that use it to become fragment types too), we cheat
        // a little bit and use a string as a replacement for any simple type we can't deal with

        if (component.getBaseTypeDefinition() == _schema.getSimpleUrType()) {
            if (component.getVarietyTag()
                == SimpleTypeDefinitionComponent.VARIETY_ATOMIC) {
                // it's likely to be a built-in type
                String nsURI = component.getName().getNamespaceURI();
                if (nsURI != null && (nsURI.equals(SchemaConstants.NS_XSD))) {
                    // definitely a built-in type, make sure there are no facets
                    if (!component.facets().hasNext()) {

                        //bug fix 4855115
                        if (_strictCompliance
                            && (component
                                .getName()
                                .equals(SchemaConstants.QNAME_TYPE_IDREF)
                                || component.getName().equals(
                                    SchemaConstants.QNAME_TYPE_URTYPE))) {
                            if (!checkStrictCompliance(component.getName()))
                                return null;
                        }
                        // handle anyType specially                        
                        if (!_strictCompliance
                            && component.getName().equals(
                                SchemaConstants.QNAME_TYPE_URTYPE)) {
                            return getLiteralFragmentTypeFor(
                                component,
                                nameHint);
                        }

                        //map xsd:ID and xsd:IDREF differently
                        //bug 4845163 fix
                        if (component
                            .getName()
                            .equals(SchemaConstants.QNAME_TYPE_ID)
                            || component.getName().equals(
                                SchemaConstants.QNAME_TYPE_IDREF)) {
                            return handleIDIDREF(component);
                        }
                        LiteralSimpleType simpleType =
                            new LiteralSimpleType(component.getName());
                        simpleType.setSchemaTypeRef(component.getName());
                        JavaSimpleType javaType =
                            (
                                JavaSimpleType) _builtinSchemaTypeToJavaTypeMap
                                    .get(
                                component.getName());
                        if (javaType == null) {
                            // invalid simple type
                            return getLiteralFragmentTypeFor(
                                component,
                                nameHint);
                        }
                        simpleType.setJavaType(javaType);
                        return simpleType;
                    } else {
                        // a simple type with facets and with the simpleUrType as its base type
                        return getLiteralSimpleStringTypeFor(
                            component,
                            nameHint);
                    }
                } else {
                    // a simple type we know nothing about
                    return getLiteralSimpleStringTypeFor(component, nameHint);
                }
            } else if (
                component.getVarietyTag()
                    == SimpleTypeDefinitionComponent.VARIETY_LIST) {
                if (doWeHandleSimpleSchemaTypeDerivationByList())
                    return listToLiteralType(component, nameHint);
                return getLiteralSimpleStringTypeFor(component, nameHint);
                // bug fix: 4925400
            } else if (
                component.getVarietyTag()
                    == SimpleTypeDefinitionComponent.VARIETY_UNION) {
                // union
                fail(
                    "model.schema.unionNotSupported",
                    new Object[] { component.getName()});
            } else {
                // bug fix: 4925400
                // URType
                if (component
                    .getName()
                    .equals(SchemaConstants.QNAME_TYPE_SIMPLE_URTYPE)) {
                    return getLiteralSimpleStringTypeFor(component, nameHint);
                } else {
                    fail(
                        "model.schema.invalidSimpleType",
                        new Object[] { component.getName()});
                }
            }
            fail(
                "model.schema.invalidSimpleType",
                new Object[] { component.getName()});
            // keep the compiler happy
            return null;
        } else {
            return anonymousSimpleSchemaTypeToLiteralType(
                component,
                nameHint,
                mappingNameHint);
        }
    }

    /**
     * @return
     */
    protected boolean doWeHandleSimpleSchemaTypeDerivationByList() {
        return true;
    }

    /**
     * @param component
     * @return
     */
    protected LiteralType handleIDIDREF(SimpleTypeDefinitionComponent component) {
        LiteralIDType baseType = new LiteralIDType(component.getName());
        baseType.setSchemaTypeRef(component.getName());
        JavaSimpleType javaType =
            (JavaSimpleType) _builtinSchemaTypeToJavaTypeMap.get(
                component.getName());
        baseType.setJavaType(javaType);
        baseType.setResolveIDREF(_resolveIDREF);
        return baseType;
    }

    /**
     * @param component
     * @param nameHint
     * @param mappingNameHint Hint for J2EE mapping of anonymous simple type
     * @return
     */
    protected LiteralType anonymousSimpleSchemaTypeToLiteralType(
        SimpleTypeDefinitionComponent component,
        QName nameHint,
        QName mappingNameHint) {
        // DOUG 12/20/02
        Iterator iter = component.facets();

        SimpleTypeDefinitionComponent baseTypeComponent =
            component.getBaseTypeDefinition();
        // right now, this has better be an enumeration
        if (iter.hasNext()
            && component.getVarietyTag()
                == SimpleTypeDefinitionComponent.VARIETY_ATOMIC) {
            Facet facet = (Facet) iter.next();
            if (facet instanceof EnumerationFacet) {
                Iterator values = ((EnumerationFacet) facet).values();
                if (values.hasNext()) {
                    // for anonymous enumeration type, map it by
                    // default to typesafe enumeration class. with
                    // switch jaxbenumtype, map it as per JAXB,
                    // which is to its base type.
                    if (_jaxbEnumType && (component.getName() == null)) {
                        String nsURI =
                            baseTypeComponent.getName().getNamespaceURI();
                        if ((nsURI != null)
                            && (nsURI.equals(SchemaConstants.NS_XSD))) {
                            return schemaTypeToLiteralType(
                                baseTypeComponent,
                                nameHint);
                        } else {
                            fail(
                                "model.schema.invalidSimpleType.noNamespaceURI",
                                new Object[] { component.getName()});
                        }
                    }
                    return enumerationToLiteralType(
                        component,
                        (EnumerationFacet) facet,
                        nameHint,
                        mappingNameHint);
                }
            }
        }

        if (component.getVarietyTag()
            == SimpleTypeDefinitionComponent.VARIETY_ATOMIC) {
            String nsURI = baseTypeComponent.getName().getNamespaceURI();
            if (nsURI != null && (nsURI.equals(SchemaConstants.NS_XSD))) {
                // restriction of a well-know type
                LiteralType baseType =
                    schemaTypeToLiteralType(
                        baseTypeComponent,
                        nameHint,
                        mappingNameHint);
                return baseType;
            } else {
                SimpleTypeEncoder encoder = null;
                SimpleTypeDefinitionComponent _baseType = null;
                while (encoder == null) {
                    if (baseTypeComponent.getVarietyTag()
                        == SimpleTypeDefinitionComponent.VARIETY_ATOMIC) {
                        _baseType = baseTypeComponent.getBaseTypeDefinition();
                    } else if (
                        baseTypeComponent.getVarietyTag()
                            == SimpleTypeDefinitionComponent.VARIETY_LIST) {
                        _baseType =
                            (baseTypeComponent
                                .getItemTypeDefinition()
                                .getName()
                                == null)
                                ? baseTypeComponent.getBaseTypeDefinition()
                                : baseTypeComponent.getItemTypeDefinition();
                    }
                    encoder =
                        (SimpleTypeEncoder) _simpleTypeEncoderMap.get(
                            _baseType.getName());

                    if (baseTypeComponent
                        .getName()
                        .equals(_baseType.getName())) {
                        break;
                    }
                    baseTypeComponent = _baseType;
                }

                /* feed it back to get the right literalType */
                return simpleSchemaTypeToLiteralType(
                    baseTypeComponent,
                    baseTypeComponent.getName(),
                    mappingNameHint);

                // return getLiteralSimpleStringTypeFor(component, nameHint);
            }

        } else if (
            component.getVarietyTag()
                == SimpleTypeDefinitionComponent.VARIETY_LIST) {
            if (doWeHandleSimpleSchemaTypeDerivationByList())
                return listToLiteralType(component, nameHint);
            return getLiteralSimpleStringTypeFor(component, nameHint);
        } else {
            // union
            return getLiteralSimpleStringTypeFor(component, nameHint);
        }
    }

    protected LiteralType complexSchemaTypeToLiteralType(
        ComplexTypeDefinitionComponent component,
        QName nameHint,
        QName mappingNameHint) {

        if (component == _schema.getUrType()) {

            // handle anyType specially
            if (component
                .getName()
                .equals(SchemaConstants.QNAME_TYPE_URTYPE)) {
                //bug fix 4855115
                if (_strictCompliance) {
                    if (!checkStrictCompliance(component.getName()))
                        return null;
                }
                LiteralType literalType = new LiteralFragmentType();
                literalType.setName(component.getName());
                literalType.setJavaType(javaTypes.SOAPELEMENT_JAVATYPE);
                return literalType;
            } else {
                LiteralSimpleType simpleType =
                    new LiteralSimpleType(component.getName());
                simpleType.setSchemaTypeRef(component.getName());
                JavaSimpleType javaType =
                    (JavaSimpleType) _builtinSchemaTypeToJavaTypeMap.get(
                        component.getName());
                if (javaType == null) {
                    // invalid simple type
                    return getLiteralFragmentTypeFor(component, nameHint);
                }
                simpleType.setJavaType(javaType);
                return simpleType;
            }
        } else if (component.getBaseTypeDefinition() == _schema.getUrType()) {
            return urTypeBasedComplexSchemaTypeToLiteralType(
                component,
                nameHint,
                mappingNameHint);
        } else if (
            doWeHandleComplexSchemaTypeExtensionBySimpleContent()
                && component.getContentTag()
                    == ComplexTypeDefinitionComponent.CONTENT_SIMPLE
                && component.getDerivationMethod() == Symbol.EXTENSION) {
            return complexSchemaTypeSimpleContentExtensionToLiteralType(
                component,
                nameHint);
        } else if (
            doWeHandleComplexSchemaTypeExtensionByComplexType()
            && //bug fix: 4919742
        component.getContentTag()
                == ComplexTypeDefinitionComponent.CONTENT_ELEMENT_ONLY
                && component.getDerivationMethod() == Symbol.EXTENSION) {
            return urTypeBasedComplexSchemaTypeToLiteralType(
                component,
                nameHint,
                mappingNameHint);
        } else {
            return getLiteralFragmentTypeFor(component, nameHint);
        }
    }

    /**
     * @return
     */
    protected boolean doWeHandleComplexSchemaTypeExtensionByComplexType() {
        return true;
    }

    /**
     * @return
     */
    protected boolean doWeHandleComplexSchemaTypeExtensionBySimpleContent() {
        return true;
    }

    protected String getJavaNameOfElementType(
        LiteralStructuredType structureType,
        TypeDefinitionComponent component,
        QName nameHint) {
        return makePackageQualified(
            _env.getNames().validJavaClassName(
                structureType.getName().getLocalPart()),
            structureType.getName());
    }

    protected SchemaJavaMemberInfo getJavaMemberOfElementInfo(
        QName typeName,
        String memberName) {
        return new SchemaJavaMemberInfo(
            _env.getNames().validJavaMemberName(memberName),
            false);
    }

    protected QName getSimpleTypeBaseName(
        TypeDefinitionComponent typeDefinition) {
                      
        if (typeDefinition instanceof SimpleTypeDefinitionComponent &&
            !((SimpleTypeDefinitionComponent)typeDefinition).getBaseTypeDefinition().getName()
                .equals(BuiltInTypes.ANY_SIMPLE_URTYPE)) {
               
            return getSimpleTypeBaseName(
                ((SimpleTypeDefinitionComponent)typeDefinition).getBaseTypeDefinition());
        }         
        return typeDefinition.getName();                                     
    }

    protected LiteralType urTypeBasedComplexSchemaTypeToLiteralType(
        ComplexTypeDefinitionComponent component,
        QName nameHint,
        QName mappingNameHint) {
        // most likely, a structure type
        if (component.getContentTag()
            == ComplexTypeDefinitionComponent.CONTENT_ELEMENT_ONLY) {
            ParticleComponent particle = component.getParticleContent();
            if (particle.occursOnce()) {
                if (particle.getTermTag()
                    == ParticleComponent.TERM_MODEL_GROUP) {
                    ModelGroupComponent modelGroup =
                        particle.getModelGroupTerm();
                    if (modelGroup.getCompositor() == Symbol.ALL
                        || modelGroup.getCompositor() == Symbol.SEQUENCE) {
                        LiteralStructuredType structureType = null;
                        if (modelGroup.getCompositor() == Symbol.ALL) {
                            structureType =
                                new LiteralAllType(
                                    getUniqueQNameFor(component, nameHint));
                        } else {
                            structureType =
                                new LiteralSequenceType(
                                    getUniqueQNameFor(component, nameHint));
                        }
                        //J2EE: only mess with it if it is an anonymous type
                        if (component.getName() == null && mappingNameHint != null) {
                            structureType.setProperty(
                                ModelProperties.PROPERTY_ANONYMOUS_TYPE_NAME,
                                mappingNameHint.getLocalPart());
                        }

                        String javaName =
                            getJavaNameOfElementType(
                                structureType,
                                component,
                                nameHint);
                        JavaStructureType javaStructureType =
                            new JavaStructureType(
                                javaName,
                                false,
                                structureType);
                        // sets if java type is abstract or not
                        updateModifiers(javaStructureType);  

                        resolveNamingConflictsFor(javaStructureType);
                        structureType.setJavaType(javaStructureType);
                        _componentToLiteralTypeMap.put(
                            component,
                            structureType);

                        //process if it derives from another complexType
                        //bug fix:4928751, do it only when derived from complexContent
                        if (doWeHandleComplexSchemaTypeExtensionByComplexType()
                            && //bug fix: 4919742
                        component.getContentTag()
                                == ComplexTypeDefinitionComponent
                                    .CONTENT_ELEMENT_ONLY
                            && component.getDerivationMethod()
                                == Symbol.EXTENSION) {
                            LiteralType parentType =
                                schemaTypeToLiteralType(
                                    component.getBaseTypeDefinition(),
                                    new QName(
                                        nameHint.getNamespaceURI(),
                                        nameHint.getLocalPart() + "_Base"));

                            if (parentType instanceof LiteralStructuredType) {
                                processSuperType(
                                    (LiteralStructuredType) parentType,
                                    structureType,
                                    javaStructureType);
                            }
                        }
                        // handle attributes
                        for (Iterator iter = component.attributeUses();
                            iter.hasNext();
                            ) {
                            AttributeUseComponent attributeUse =
                                (AttributeUseComponent) iter.next();
                            AttributeDeclarationComponent attributeDeclaration =
                                (AttributeDeclarationComponent) attributeUse
                                    .getAttributeDeclaration();
                            //bug fix 4855115
                            if (attributeDeclaration
                                .getTypeDefinition()
                                .getName()
                                != null) {
                                if (_strictCompliance
                                    && attributeDeclaration
                                        .getTypeDefinition()
                                        .getName()
                                        .equals(
                                        SchemaConstants.QNAME_TYPE_IDREF)) {
                                    return getLiteralFragmentTypeFor(
                                        attributeDeclaration
                                            .getTypeDefinition(),
                                        SchemaConstants.QNAME_TYPE_IDREF);
                                }
                            }
                            LiteralType attributeType =
                                schemaTypeToLiteralType(
                                    attributeDeclaration.getTypeDefinition(),
                                    getAttributeQNameHint(
                                        attributeDeclaration,
                                        nameHint));

                            //as per JAXRPC 1.1 spec, map optional
                            //attribute with optional use and no
                            //default and fixed attribute should be
                            //mapped to a boxed type
                            if (isAttributeOptional(attributeUse)) {
                                // bug fix: 4961579
                                LiteralType tmpType = getNillableLiteralSimpleType(
                                    attributeType.getName(), 
                                    attributeDeclaration.getTypeDefinition());
                                if (tmpType != null)
                                    attributeType = tmpType;
                            }
//                                QName baseTypeName = getSimpleTypeBaseName(
//                                    attributeDeclaration.getTypeDefinition());                                                                              
//                                JavaSimpleType javaType =
//                                      (
//                                          JavaSimpleType) _builtinSchemaTypeToJavaWrapperTypeMap
//                                              .get(baseTypeName);
//                                
////                                JavaSimpleType javaType =
////                                    (
////                                        JavaSimpleType) _builtinSchemaTypeToJavaWrapperTypeMap
////                                            .get(
////                                        attributeDeclaration
////                                            .getTypeDefinition()
////                                            .getName());
//                                if (javaType != null) {
//                                    attributeType = getNillableLiteralSimpleType(
//                                        attributeType.getName(), 
//                                        attributeDeclaration.getTypeDefinition(),
//                                        JavaType javaType);
//                                    LiteralSimpleType result =
//                                        (
//                                            LiteralSimpleType) _nillableSimpleTypeComponentToLiteralTypeMap
//                                                .get(
//                                            attributeDeclaration
//                                                .getTypeDefinition());
//                                    if (result == null) {
//                                        // Fix for bug 4900902
//                                        // bug fix 4961579
//                                        result =
//                                            new LiteralSimpleType(
//                                                attributeType.getName(),
//                                                javaType,
//                                                true);                                        
////                                        result =
////                                            new LiteralSimpleType(
////                                                attributeDeclaration
////                                                    .getTypeDefinition()
////                                                    .getName(),
////                                                javaType,
////                                                true);
//                                        result.setSchemaTypeRef(
//                                            attributeDeclaration
//                                                .getTypeDefinition()
//                                                .getName());
//                                        _nillableSimpleTypeComponentToLiteralTypeMap
//                                            .put(
//                                            attributeDeclaration
//                                                .getTypeDefinition(),
//                                            result);
//                                    }
//                                    attributeType = result;
//                                }
//
//                            }
                            //bug fix: 4863953                          
                            if (SimpleTypeSerializerWriter
                                .getTypeEncoder(attributeType)
                                == null
                                && !isAttributeEnumeration(attributeType)) {
                                // unsupported simple type
                                return getLiteralFragmentTypeFor(
                                    component,
                                    nameHint);
                            }
                            LiteralAttributeMember member =
                                new LiteralAttributeMember(
                                    attributeDeclaration.getName(),
                                    attributeType);
                            if (attributeUse.isRequired()) {
                                member.setRequired(true);
                            }
                            SchemaJavaMemberInfo memberInfo =
                                getJavaMemberOfElementInfo(
                                    nameHint,
                                    attributeDeclaration
                                        .getName()
                                        .getLocalPart());
                            JavaStructureMember javaMember =
                                new JavaStructureMember(
                                    memberInfo.javaMemberName,
                                    attributeType.getJavaType(),
                                    member,
                                    memberInfo.isDataMember);
                            javaMember.setReadMethod(
                                _env.getNames().getJavaMemberReadMethod(
                                    javaMember));
                            javaMember.setWriteMethod(
                                _env.getNames().getJavaMemberWriteMethod(
                                    javaMember));
                            member.setJavaStructureMember(javaMember);
                            javaStructureType.add(javaMember);
                            structureType.add(member);
                        }

                        // handle particles
                        for (Iterator iter = modelGroup.particles();
                            iter.hasNext();
                            ) {
                            ParticleComponent memberParticle =
                                (ParticleComponent) iter.next();

                            if (memberParticle.doesNotOccur()) {
                                continue;
                            }

                            if (memberParticle.getTermTag()
                                == ParticleComponent.TERM_ELEMENT) {

                                ElementDeclarationComponent element =
                                    memberParticle.getElementTerm();
                                //bug fix 4855115
                                if (element.getTypeDefinition().getName()
                                    != null) {
                                    if (_strictCompliance
                                        && (element
                                            .getTypeDefinition()
                                            .getName()
                                            .equals(
                                                SchemaConstants
                                                    .QNAME_TYPE_IDREF)
                                            || element
                                                .getTypeDefinition()
                                                .getName()
                                                .equals(
                                                SchemaConstants
                                                    .QNAME_TYPE_URTYPE))) {
                                        return getLiteralFragmentTypeFor(
                                            element.getTypeDefinition(),
                                            SchemaConstants.QNAME_TYPE_IDREF);
                                    }
                                }

                                LiteralType memberType = null;
                                if (element.getTypeDefinition().getName()
                                    != null) {
                                    memberType =
                                        schemaTypeToLiteralType(
                                            element.getTypeDefinition(),
                                            getElementQNameHint(
                                                element,
                                                nameHint));
                                } else { //J2EE: element has anonymous type
                                    memberType =
                                        schemaTypeToLiteralType(
                                            element.getTypeDefinition(),
                                            getElementQNameHint(
                                                element,
                                                nameHint),
                                            getElementMappingNameHint(
                                                element,
                                                mappingNameHint));
                                }

           

                                LiteralElementMember member =
                                    new LiteralElementMember(
                                        element.getName(),
                                        memberType);
                                JavaType javaMemberType = null;
                                //handle minOccurs=0 maxOccurs=1. They're mapped to boxedType                                    
                                if (element.isNillable()
                                    || isParticleOptional(memberParticle)) {
                                    // try to apply the nullability condition to the type and see if that
                                    // results in a different java type being picked up
                                    // bug fix: 4961579
                                    QName baseTypeName = getSimpleTypeBaseName(
                                        element.getTypeDefinition());  
                                        if (element.getName().getLocalPart().equals("maxEntities"))
                                            System.out.println("stop");                                                                            
                                    LiteralType tmpType = getNillableLiteralSimpleType(
                                        baseTypeName, element.getTypeDefinition());
                                    if (tmpType != null) {
                                        memberType = tmpType;
////                                    QName baseTypeName = getSimpleTypeBaseName(
////                                        element.getTypeDefinition());                                                                              
//                                    JavaSimpleType javaType =
//                                          (
//                                              JavaSimpleType) _builtinSchemaTypeToJavaWrapperTypeMap
//                                                  .get(baseTypeName);      
//
////                                    JavaSimpleType javaType =
////                                        (
////                                            JavaSimpleType) _builtinSchemaTypeToJavaWrapperTypeMap
////                                                .get(
////                                            element
////                                                .getTypeDefinition()
////                                                .getName());
//                                    if (javaType != null) {
//                                        // nullability matters
//                                        memberType = getNillableLiteralSimpleType(
//                                            baseTypeName, element.getTypeDefinition(), 
//                                            javaType);
//                                        LiteralSimpleType result =
//                                            (
//                                                LiteralSimpleType) _nillableSimpleTypeComponentToLiteralTypeMap
//                                                    .get(
//                                                element.getTypeDefinition());
//                                        if (result == null) {
//                                            // Fix for bug 4900902
//                                            // bug fix 4961579
//                                            result =
//                                                new LiteralSimpleType(
//                                                    baseTypeName,
//                                                    javaType,
//                                                    true);
//                                            
//                                            //                                          result = new LiteralSimpleType(element.getTypeDefinition().getName(), javaType);
////                                            result =
////                                                new LiteralSimpleType(
////                                                    element
////                                                        .getTypeDefinition()
////                                                        .getName(),
////                                                    javaType,
////                                                    true);
//                                            result.setSchemaTypeRef(
//                                                element
//                                                    .getTypeDefinition()
//                                                    .getName());
//                                            _nillableSimpleTypeComponentToLiteralTypeMap
//                                                .put(
//                                                element.getTypeDefinition(),
//                                                result);
//                                        }
//                                        memberType = result;

                                        //its nillable="true" so set this LiteralSimpleType for 
                                        // boxed type mapping
                                        if (doMakeMemberBoxedType())
                                            member.setType(memberType);
                                    }
                                }
                                
                                // bug fix: 4923650
                                memberType =
                                    getLiteralMemberType(
                                        component,
                                        memberType,
                                        element,
                                        structureType);
                                       

                                if (element.isNillable())
                                    member.setNillable(true);

                                if (memberParticle.occursAtLeastOnce()) {
                                    member.setRequired(true);
                                }
                                if (memberParticle.mayOccurMoreThanOnce()) {
                                    member.setRepeated(true);
                                    // elements with maxOccurs greater than one result in arrays
                                    JavaArrayType javaArrayType =
                                        new JavaArrayType(
                                            memberType.getJavaType().getName()
                                                + "[]");
                                    javaArrayType.setElementType(
                                        memberType.getJavaType());
                                    javaMemberType = javaArrayType;

                                    //J2EE: Handle anonymous array type
                                    String n =
                                        getJ2EEAnonymousArrayTypeName(
                                            memberParticle,
                                            element,
                                            mappingNameHint);

                                    structureType.setProperty(
                                        ModelProperties
                                            .PROPERTY_ANONYMOUS_ARRAY_TYPE_NAME,
                                        n);

                                    structureType.setProperty(
                                        ModelProperties
                                            .PROPERTY_ANONYMOUS_ARRAY_JAVA_TYPE,
                                        javaMemberType.getName());

                                } else {
                                    javaMemberType = memberType.getJavaType();
                                }
                                SchemaJavaMemberInfo memberInfo =
                                    getJavaMemberOfElementInfo(
                                        mappingNameHint,
                                        element.getName().getLocalPart());
                                JavaStructureMember javaMember =
                                    new JavaStructureMember(
                                        memberInfo.javaMemberName,
                                        javaMemberType,
                                        member,
                                        memberInfo.isDataMember);
                                javaMember.setReadMethod(
                                    _env.getNames().getJavaMemberReadMethod(
                                        javaMember));
                                javaMember.setWriteMethod(
                                    _env.getNames().getJavaMemberWriteMethod(
                                        javaMember));
                                member.setJavaStructureMember(javaMember);
                                javaStructureType.add(javaMember);
                                structureType.add(member);
                            } else if (
                                memberParticle.getTermTag()
                                    == ParticleComponent.TERM_WILDCARD
                                    && doWeHandleWildcard()) {
                                WildcardComponent wildcard =
                                    memberParticle.getWildcardTerm();

                                if (modelGroup.getCompositor() == Symbol.ALL) {
                                    // this should not happen
                                    fail(
                                        "model.schema.invalidWildcard.allCompositor",
                                        new Object[] { component.getName()});
                                }

                                //enclosing type of the wildcard
                                //valueType will be SOAPElement. Get
                                //it from getLiteralFragmentTypeFor()
                                //instead of schemaTypeToLiteralType()
                                //as xsd:anyType is not
                                //strictcompliance.
                                TypeDefinitionComponent anyURIcomponent =
                                    _schema.findTypeDefinition(
                                        SchemaConstants.QNAME_TYPE_URTYPE);
                                LiteralType memberType =
                                    getLiteralFragmentTypeFor(
                                        anyURIcomponent,
                                        SchemaConstants.QNAME_TYPE_URTYPE);
                                LiteralWildcardMember member =
                                    new LiteralWildcardMember(memberType);
                                if (wildcard.getNamespaceConstraintTag()
                                    == WildcardComponent
                                        .NAMESPACE_CONSTRAINT_NOT) {
                                    member.setExcludedNamespaceName(
                                        wildcard.getNamespaceName());
                                } else if (
                                    wildcard.getNamespaceConstraintTag()
                                        == WildcardComponent
                                            .NAMESPACE_CONSTRAINT_ANY) {
                                    // do nothing
                                } else {
                                    // more complex namespace name constraints are not supported for now
                                    return getLiteralFragmentTypeFor(
                                        component,
                                        nameHint);
                                }

                                JavaType javaMemberType = null;
                                if (memberParticle.occursAtLeastOnce()) {
                                    member.setRequired(true);
                                }
                                if (memberParticle.mayOccurMoreThanOnce()) {
                                    member.setRepeated(true);
                                    // elements with maxOccurs greater than one result in arrays
                                    JavaArrayType javaArrayType =
                                        new JavaArrayType(
                                            memberType.getJavaType().getName()
                                                + "[]");
                                    javaArrayType.setElementType(
                                        memberType.getJavaType());
                                    javaMemberType = javaArrayType;
                                } else {
                                    javaMemberType = memberType.getJavaType();
                                }

                                JavaStructureMember javaMember =
                                    new JavaStructureMember(
                                        getUniqueMemberName(
                                            javaStructureType,
                                            ANY_MEMBER_NAME_PREFIX),
                                        javaMemberType,
                                        member,
                                        false);
                                javaMember.setReadMethod(
                                    _env.getNames().getJavaMemberReadMethod(
                                        javaMember));
                                javaMember.setWriteMethod(
                                    _env.getNames().getJavaMemberWriteMethod(
                                        javaMember));
                                member.setJavaStructureMember(javaMember);
                                javaStructureType.add(javaMember);
                                structureType.add(member);
                            } else {
                                //bug fix: 4916147 
                                if ((memberParticle.getModelGroupTerm()
                                    != null)
                                    && memberParticle
                                        .getModelGroupTerm()
                                        .getCompositor()
                                        == Symbol.CHOICE) {
                                    warn(
                                        "model.schema.notImplemented.generatingSOAPElement",
                                        new Object[] {
                                            "xsd:choice",
                                            nameHint });
                                }
                                // cannot deal with a nested model group here
                                return getLiteralFragmentTypeFor(
                                    component,
                                    nameHint);
                            }
                        }
                        return structureType;
                    } else {
                        // cannot deal with choice
                        if (modelGroup.getCompositor() == Symbol.CHOICE) {
                            warn(
                                "model.schema.notImplemented.generatingSOAPElement",
                                new Object[] {
                                    "xsd:choice",
                                    getUniqueQNameFor(component, nameHint)});
                        }
                        return getLiteralFragmentTypeFor(component, nameHint);
                    }
                } else {
                    // wildcard or element -- cannot deal with them
                    return getLiteralFragmentTypeFor(component, nameHint);
                }
            } else {
                // multiple occurrence, cannot deal with that right now
                return getLiteralFragmentTypeFor(component, nameHint);
            }
        } else if (
            component.getContentTag()
                == ComplexTypeDefinitionComponent.CONTENT_EMPTY) {
            // return getLiteralFragmentTypeFor(component, nameHint);

            LiteralSequenceType structureType =
                new LiteralSequenceType(getUniqueQNameFor(component, nameHint));
            JavaStructureType javaStructureType =
                new JavaStructureType(
                    makePackageQualified(
                        _env.getNames().validJavaClassName(
                            structureType.getName().getLocalPart()),
                        structureType.getName()),
                    false,
                    structureType);
            resolveNamingConflictsFor(javaStructureType);
            structureType.setJavaType(javaStructureType);
            _componentToLiteralTypeMap.put(component, structureType);

            // handle attributes
            for (Iterator iter = component.attributeUses(); iter.hasNext();) {
                AttributeUseComponent attributeUse =
                    (AttributeUseComponent) iter.next();
                AttributeDeclarationComponent attributeDeclaration =
                    (AttributeDeclarationComponent) attributeUse
                        .getAttributeDeclaration();
                LiteralType attributeType =
                    schemaTypeToLiteralType(
                        attributeDeclaration.getTypeDefinition(),
                        getAttributeQNameHint(attributeDeclaration, nameHint));
                
                // Fix for Issue 7: https://jax-rpc.dev.java.net/issues/show_bug.cgi?id=7
                // bugster bug: 6181455
                //as per JAXRPC 1.1 spec, map optional
                //attribute with optional use and no
                //default and fixed attribute should be
                //mapped to a boxed type
                if (isAttributeOptional(attributeUse)) {
                    LiteralType tmpType = getNillableLiteralSimpleType(
                        attributeType.getName(), 
                        attributeDeclaration.getTypeDefinition());
                    if (tmpType != null)
                        attributeType = tmpType;
                }
                
                //bug fix: 4999385                          
                if (SimpleTypeSerializerWriter
                        .getTypeEncoder(attributeType)
                        == null
                        && !doWeHandleAttributeTypeEnumeration(attributeType)) {
                    // unsupported simple type
                    return getLiteralFragmentTypeFor(
                            component,
                            nameHint);
                }
                
                LiteralAttributeMember member =
                    new LiteralAttributeMember(
                        attributeDeclaration.getName(),
                        attributeType);
                if (attributeUse.isRequired()) {
                    member.setRequired(true);
                }
                SchemaJavaMemberInfo memberInfo =
                    getJavaMemberOfElementInfo(
                        nameHint,
                        attributeDeclaration.getName().getLocalPart());
                JavaStructureMember javaMember =
                    new JavaStructureMember(
                        memberInfo.javaMemberName,
                        attributeType.getJavaType(),
                        member,
                        memberInfo.isDataMember);
                javaMember.setReadMethod(
                    _env.getNames().getJavaMemberReadMethod(javaMember));
                javaMember.setWriteMethod(
                    _env.getNames().getJavaMemberWriteMethod(javaMember));
                member.setJavaStructureMember(javaMember);
                javaStructureType.add(javaMember);
                structureType.add(member);
            }

            return structureType;
        } else {
            // simple or mixed content
            return getLiteralFragmentTypeFor(component, nameHint);
        }
    }


    /**
     * bug fix: 4999385
     * @param attributeType
     * @return
     */
    protected boolean doWeHandleAttributeTypeEnumeration(LiteralType attributeType) {
        return false;
    }

    // bug fix 4961579
    protected LiteralSimpleType getNillableLiteralSimpleType(QName typeName, 
        TypeDefinitionComponent typeDef) {

//        QName baseTypeName = getSimpleTypeBaseName(typeDef);                                                                              
        JavaSimpleType javaType =
            (JavaSimpleType) _builtinSchemaTypeToJavaWrapperTypeMap
                                  .get(typeDef.getName());

        if (javaType == null) {
            return null;
        }
           
        LiteralSimpleType result =
            (LiteralSimpleType) _nillableSimpleTypeComponentToLiteralTypeMap
                .get(typeDef);
        if (result == null) {
            result =
                new LiteralSimpleType(typeDef.getName(),
                    javaType,
                    true);                                        
            result.setSchemaTypeRef(typeDef.getName());
            _nillableSimpleTypeComponentToLiteralTypeMap
                .put(typeDef, result);
        }
        return result;
    }
        
    /**
     * @param parentType
     * @param structureType
     * @param javaStructureType
     */
    private void processSuperType(
        SOAPStructureType parentType,
        SOAPStructureType structureType,
        JavaStructureType javaStructureType) {

        // copy all inherited members first
        for (Iterator iter = parentType.getMembers(); iter.hasNext();) {
            SOAPStructureMember member = (SOAPStructureMember) iter.next();
            SOAPStructureMember inheritedMember =
                new SOAPStructureMember(member.getName(), member.getType());
            inheritedMember.setInherited(true);
            JavaStructureMember javaMember = member.getJavaStructureMember();
            JavaStructureMember inheritedJavaMember =
                new JavaStructureMember(
                    javaMember.getName(),
                    javaMember.getType(),
                    inheritedMember,
                    javaMember.isPublic());
            inheritedJavaMember.setInherited(true);
            inheritedJavaMember.setReadMethod(javaMember.getReadMethod());
            inheritedJavaMember.setWriteMethod(javaMember.getWriteMethod());
            inheritedMember.setJavaStructureMember(inheritedJavaMember);
            structureType.add(inheritedMember);
            javaStructureType.add(inheritedJavaMember);
        }
        // added for bug fix: 4940424 
        // copy all inherited attributes
        for (Iterator iter = parentType.getAttributeMembers();
            iter.hasNext();
            ) {
            SOAPAttributeMember member = (SOAPAttributeMember) iter.next();
            SOAPAttributeMember inheritedMember =
                new SOAPAttributeMember(member.getName(), member.getType());
            inheritedMember.setRequired(member.isRequired());
            inheritedMember.setInherited(true);
            JavaStructureMember javaMember = member.getJavaStructureMember();
            JavaStructureMember inheritedJavaMember =
                new JavaStructureMember(
                    javaMember.getName(),
                    javaMember.getType(),
                    inheritedMember,
                    javaMember.isPublic());
            inheritedJavaMember.setInherited(true);
            inheritedJavaMember.setReadMethod(javaMember.getReadMethod());
            inheritedJavaMember.setWriteMethod(javaMember.getWriteMethod());
            inheritedMember.setJavaStructureMember(inheritedJavaMember);
            structureType.add(inheritedMember);
            javaStructureType.add(inheritedJavaMember);
        }
        parentType.addSubtype(structureType);
        ((JavaStructureType) parentType.getJavaType()).addSubclass(
            javaStructureType);
    }

    /**
     * @param parentType
     * @param structureType
     * @param javaStructureType
     */
    private void processSuperType(
        LiteralStructuredType parentType,
        LiteralStructuredType structureType,
        JavaStructureType javaStructureType) {
        if (parentType != null) {
            // copy all inherited members first
            for (Iterator iter = parentType.getElementMembers();
                iter.hasNext();
                ) {
                LiteralElementMember member =
                    (LiteralElementMember) iter.next();
                LiteralElementMember inheritedMember;
                
                //bug fix:  6189935, wildcard element propagation
                if(member.isWildcard() && member instanceof LiteralWildcardMember) {
                    inheritedMember = new LiteralWildcardMember(member.getType());
                    ((LiteralWildcardMember)inheritedMember).setExcludedNamespaceName(
                            ((LiteralWildcardMember)member).getExcludedNamespaceName());
                }else{
                    inheritedMember = new LiteralElementMember(member.getName(),member.getType());
                }
                
                inheritedMember.setNillable(member.isNillable());
                inheritedMember.setInherited(true);
                inheritedMember.setRepeated(member.isRepeated());
                JavaStructureMember javaMember =
                    member.getJavaStructureMember();
                JavaStructureMember inheritedJavaMember =
                    new JavaStructureMember(
                        javaMember.getName(),
                        javaMember.getType(),
                        inheritedMember,
                        javaMember.isPublic());
                inheritedJavaMember.setInherited(true);
                inheritedJavaMember.setReadMethod(javaMember.getReadMethod());
                inheritedJavaMember.setWriteMethod(javaMember.getWriteMethod());
                inheritedMember.setJavaStructureMember(inheritedJavaMember);
                structureType.add(inheritedMember);
                javaStructureType.add(inheritedJavaMember);
            }
            // added for bug fix: 4940424 
            // copy all attributes
            for (Iterator iter = parentType.getAttributeMembers();
                iter.hasNext();
                ) {
                LiteralAttributeMember member =
                    (LiteralAttributeMember) iter.next();
                LiteralAttributeMember inheritedMember =
                    new LiteralAttributeMember(
                        member.getName(),
                        member.getType());
                inheritedMember.setRequired(member.isRequired());
                inheritedMember.setInherited(true);
                JavaStructureMember javaMember =
                    member.getJavaStructureMember();
                JavaStructureMember inheritedJavaMember =
                    new JavaStructureMember(
                        javaMember.getName(),
                        javaMember.getType(),
                        inheritedMember,
                        javaMember.isPublic());
                inheritedJavaMember.setInherited(true);
                inheritedJavaMember.setReadMethod(javaMember.getReadMethod());
                inheritedJavaMember.setWriteMethod(javaMember.getWriteMethod());
                inheritedMember.setJavaStructureMember(inheritedJavaMember);
                structureType.add(inheritedMember);
                javaStructureType.add(inheritedJavaMember);
            }

            parentType.addSubtype(structureType);
            ((JavaStructureType) parentType.getJavaType()).addSubclass(
                javaStructureType);
        }
    }

    /**
     * @return
     */
    protected boolean doMakeMemberBoxedType() {
        return true;
    }

    /**
     * @return
     */
    protected boolean doWeHandleWildcard() {
        return true;
    }

    /**
     * @param memberParticle
     * @return
     */
    protected boolean isParticleOptional(ParticleComponent memberParticle) {
        return memberParticle.occursZeroOrOne();
    }

    /**
     * @return
     */
    protected boolean isAttributeEnumeration(LiteralType attributeType) {
        return (attributeType instanceof LiteralEnumerationType);
    }

    protected boolean isAttributeEnumeration(SOAPType attributeType) {
        return (attributeType instanceof SOAPEnumerationType);
    }

    /**
     * @param attributeUse
     * @return
     */
    protected boolean isAttributeOptional(AttributeUseComponent attributeUse) {
        return (
            !attributeUse.isRequired()
                && (attributeUse.getValueKind() != Symbol.DEFAULT)
                && (attributeUse.getValueKind() != Symbol.FIXED));
    }

    protected LiteralType complexSchemaTypeSimpleContentExtensionToLiteralType(
        ComplexTypeDefinitionComponent component,
        QName nameHint) {

        // a complexType definition that extends a simpleType specifying <xsd:simpleContent/>

        LiteralType baseType =
            schemaTypeToLiteralType(
                component.getBaseTypeDefinition(),
                nameHint);
        if (SimpleTypeSerializerWriter.getTypeEncoder(baseType) == null) {
            // unsupported simple type
            return getLiteralFragmentTypeFor(component, nameHint);
        }

        LiteralStructuredType structureType =
            new LiteralSequenceType(getUniqueQNameFor(component, nameHint));

        JavaStructureType javaStructureType =
            new JavaStructureType(
                makePackageQualified(
                    _env.getNames().validJavaClassName(
                        structureType.getName().getLocalPart()),
                    structureType.getName()),
                false,
                structureType);
        resolveNamingConflictsFor(javaStructureType);
        structureType.setJavaType(javaStructureType);
        _componentToLiteralTypeMap.put(component, structureType);

        // handle attributes
        for (Iterator iter = component.attributeUses(); iter.hasNext();) {
            AttributeUseComponent attributeUse =
                (AttributeUseComponent) iter.next();
            AttributeDeclarationComponent attributeDeclaration =
                (AttributeDeclarationComponent) attributeUse
                    .getAttributeDeclaration();
            LiteralType attributeType =
                schemaTypeToLiteralType(
                    attributeDeclaration.getTypeDefinition(),
                    getAttributeQNameHint(attributeDeclaration, nameHint));
            if (SimpleTypeSerializerWriter.getTypeEncoder(attributeType)
                == null
                && !(attributeType instanceof LiteralEnumerationType)) {
                // TODO fix this
                // unsupported simple type
                return getLiteralFragmentTypeFor(component, nameHint);
            }
            //big fix: 4851668
            if (attributeDeclaration
                .getName()
                .getLocalPart()
                .equals(VALUE_MEMBER_NAME)) {
                throw new ModelException(
                    "model.complexType.simpleContent.reservedName",
                    structureType.getName().getLocalPart());
            }
            LiteralAttributeMember member =
                new LiteralAttributeMember(
                    attributeDeclaration.getName(),
                    attributeType);
            if (attributeUse.isRequired()) {
                member.setRequired(true);
            }
            JavaStructureMember javaMember =
                new JavaStructureMember(
                    _env.getNames().validJavaMemberName(
                        attributeDeclaration.getName().getLocalPart()),
                    attributeType.getJavaType(),
                    member,
                    false);
            javaMember.setReadMethod(
                _env.getNames().getJavaMemberReadMethod(javaMember));
            javaMember.setWriteMethod(
                _env.getNames().getJavaMemberWriteMethod(javaMember));
            member.setJavaStructureMember(javaMember);
            javaStructureType.add(javaMember);
            structureType.add(member);
        }

        // handle character content
        LiteralContentMember member = new LiteralContentMember(baseType);
        JavaStructureMember javaMember =
            new JavaStructureMember(
                VALUE_MEMBER_NAME,
                baseType.getJavaType(),
                member,
                false);
        javaMember.setReadMethod(
            _env.getNames().getJavaMemberReadMethod(javaMember));
        javaMember.setWriteMethod(
            _env.getNames().getJavaMemberWriteMethod(javaMember));
        member.setJavaStructureMember(javaMember);
        javaStructureType.add(javaMember);
        structureType.setContentMember(member);

        return structureType;
    }

    protected LiteralFragmentType getLiteralFragmentTypeFor(
        TypeDefinitionComponent component,
        QName nameHint) {
        LiteralFragmentType literalType = new LiteralFragmentType();
        literalType.setName(getUniqueQNameFor(component, nameHint));
        // literalType.setSchemaType(component);
        literalType.setJavaType(javaTypes.SOAPELEMENT_JAVATYPE);
		 //Nagesh: To handle xsi:nil="true", added setNillable as true 
        literalType.setNillable(true);
        return literalType;
    }

    protected LiteralType getLiteralSimpleStringTypeFor(
        TypeDefinitionComponent component,
        QName nameHint) {
        LiteralSimpleType literalType =
            new LiteralSimpleType(getUniqueQNameFor(component, nameHint));
        // literalType.setSchemaType(component);
        literalType.setJavaType(javaTypes.STRING_JAVATYPE);
        return literalType;
    }

    protected String makePackageQualified(String s, QName name) {
        String javaPackageName = getJavaPackageName(name);
        if (javaPackageName != null) {
            return javaPackageName + "." + s;
        } else if (
            _modelInfo.getJavaPackageName() != null
                && !_modelInfo.getJavaPackageName().equals("")) {
            return _modelInfo.getJavaPackageName() + "." + s;
        } else {
            return s;
        }
    }

    protected QName makePackageQualified(QName name) {
        return new QName(
            name.getNamespaceURI(),
            makePackageQualified(name.getLocalPart(), name));
    }

    protected SOAPCustomType getCustomTypeFor(TypeDefinitionComponent component) {
        QName typeName = component.getName();
        if (typeName == null) {
            // users cannot possibly tell us how to deal with anonymous types
            return null;
        }

        SOAPCustomType customType =
            (SOAPCustomType) _typeNameToCustomSOAPTypeMap.get(typeName);
        if (customType == null) {
            if (_modelInfo.getTypeMappingRegistry() != null) {
                TypeMappingInfo tmi =
                    _modelInfo.getTypeMappingRegistry().getTypeMappingInfo(
                        soap11NamespaceConstants.getEncoding(),
                        typeName);
                if (tmi != null) {
                    customType = new SOAPCustomType(typeName);
                    // customType.setSchemaType(component);
                    JavaCustomType javaCustomType =
                        new JavaCustomType(tmi.getJavaTypeName(), tmi);
                    customType.setJavaType(javaCustomType);
                    _typeNameToCustomSOAPTypeMap.put(typeName, customType);
                }
            }
        }

        return customType;
    }

    protected LiteralType getIDREFLiteralType(
        SimpleTypeDefinitionComponent component,
        QName nameHint) {
        LiteralIDType baseType = new LiteralIDType(component.getName());
        baseType.setSchemaTypeRef(component.getName());

        JavaSimpleType javaType =
            (JavaSimpleType) _builtinSchemaTypeToJavaTypeMap.get(
                component.getName());

        baseType.setJavaType(javaType);
        baseType.setResolveIDREF(_resolveIDREF);

        if (!_resolveIDREF)
            return baseType;

        LiteralStructuredType structureType =
            new LiteralSequenceType(
                getUniqueQNameFor(
                    new SimpleTypeDefinitionComponent(),
                    nameHint));

        LiteralContentMember member = new LiteralContentMember(baseType);
        JavaStructureMember javaMember =
            new JavaStructureMember(
                VALUE_MEMBER_NAME,
                baseType.getJavaType(),
                member,
                false);

        JavaStructureType javaStructureType =
            new JavaStructureType(
                makePackageQualified(
                    _env.getNames().validJavaClassName(
                        structureType.getName().getLocalPart()),
                    structureType.getName()),
                false,
                structureType);

        resolveNamingConflictsFor(javaStructureType);
        structureType.setJavaType(javaStructureType);
        _componentToLiteralTypeMap.put(component, structureType);

        javaMember.setReadMethod(
            _env.getNames().getJavaMemberReadMethod(javaMember));
        javaMember.setWriteMethod(
            _env.getNames().getJavaMemberWriteMethod(javaMember));
        member.setJavaStructureMember(javaMember);
        javaStructureType.add(javaMember);
        structureType.setContentMember(member);

        return structureType;
    }

    protected SOAPCustomType mustGetCustomTypeFor(TypeDefinitionComponent component) {
        SOAPCustomType type = getCustomTypeFor(component);
        if (type == null) {
            // cannot handle arbitrary base types right now
            fail(
                "model.schema.unsupportedSchemaType",
                new Object[] { component.getName()});
        }

        return type;
    }

    protected boolean isInvalidEnumerationLabel(String s) {
        if (s == null
            || s.equals("")
            || !Character.isJavaIdentifierStart(s.charAt(0))) {
            return true;
        }

        for (int i = 1; i < s.length(); ++i) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                return true;
            }
        }

        return _env.getNames().isJavaReservedWord(s);
    }

    // sets if java type is abstract or not
    protected void updateModifiers(JavaStructureType javaStructureType) {
    }

    private String getJavaPackageName(QName name) {
        String packageName = null;
        if (_modelInfo.getNamespaceMappingRegistry() != null) {
            NamespaceMappingInfo i =
                _modelInfo
                    .getNamespaceMappingRegistry()
                    .getNamespaceMappingInfo(
                    name);
            if (i != null)
                return i.getJavaPackageName();
        }
        return packageName;
    }

    protected void fail(String key, String code, QName arg) {
        if (arg == null) {
            throw new ModelException(key + ".anonymous", code);
        } else {
            throw new ModelException(
                key,
                new Object[] {
                    code,
                    arg.getLocalPart(),
                    arg.getNamespaceURI()});
        }
    }

    protected void fail(String key, Object[] arg) {
        throw new ModelException(key, arg);
    }

    protected void failUnsupported(String code, QName arg) {
        fail("model.schema.unsupportedType", code, arg);
    }

    protected void warn(String key, Object[] args) {
        _env.warn(_messageFactory.getMessage(key, args));
    }

    protected QName getElementQNameHint(
        ElementDeclarationComponent component,
        QName nameHint) {
        QName componentName = component.getName();
        if (!componentName.getNamespaceURI().equals("")) {
            return componentName;
        } else {
            return new QName(
                nameHint.getNamespaceURI(),
                nameHint.getLocalPart() + "-" + componentName.getLocalPart());
        }
    }

    protected QName getElementMappingNameHint(
        ElementDeclarationComponent component,
        QName mappingNameHint) {

        String hint =
            (mappingNameHint == null) ? "" : mappingNameHint.getLocalPart() +
                ">" + component.getName().getLocalPart();
        if (component.getTypeDefinition().getName() == null) {
            hint = ">" + hint;
        }
        QName qnameHint = new QName(component.getName().getNamespaceURI(),
            hint);
        return qnameHint;
    }

    protected String getJ2EEAnonymousArrayTypeName(
        ParticleComponent memberParticle,
        ElementDeclarationComponent element,
        QName mappingNameHint) {

        String upperBound =
            memberParticle.isMaxOccursUnbounded()
                ? "unbounded"
                : String.valueOf(memberParticle.getMaxOccurs());

        String lowerBound = String.valueOf(memberParticle.getMinOccurs());

        //Since only local element can have min/maxOccurs, there is
        //no namespaceURL in this element's QName.
        String name = (mappingNameHint == null) ? "":mappingNameHint.getLocalPart() + ">" + element.getName().getLocalPart();
        String boundary = "[" + lowerBound + "," + upperBound + "]";

        return (name + boundary);
    }

    protected QName getAttributeQNameHint(
        AttributeDeclarationComponent component,
        QName nameHint) {
        return new QName(
            nameHint.getNamespaceURI(),
            nameHint.getLocalPart() + "-" + component.getName().getLocalPart());
    }

    protected QName getUniqueLiteralArrayTypeQNameFor(
        QName subTypeName,
        QName nameHint) {
        return new QName(
            subTypeName.getNamespaceURI(),
            subTypeName.getLocalPart() + "-Array-" + getUniqueID());
    }

    protected QName getUniqueTypeNameForElement(QName elementName) {
        // this is used as a last resort only
        return new QName(
            elementName.getNamespaceURI(),
            elementName.getLocalPart() + "-AnonType-" + getUniqueID());
    }

    protected String getUniqueNCNameFor(TypeDefinitionComponent component) {
        if (component.getName() != null) {
            return component.getName().getLocalPart();
        } else {
            return "genType-" + getUniqueID();
        }
    }

    protected QName getUniqueQNameFor(
        TypeDefinitionComponent component,
        QName nameHint) {
        if (component.getName() != null) {
            return component.getName();
        } else {
            // here we use a name pool so that we use the provided "name hint" as much as possible
            // before mangling it by adding various suffixes

            QName result = null;

            if (nameHint != null) {
                if (!_namePool.contains(nameHint)) {
                    result = nameHint;
                } else {
                    result =
                        new QName(
                            nameHint.getNamespaceURI(),
                            nameHint.getLocalPart() + "-gen-" + getUniqueID());
                }
            }
            _namePool.add(result);
            return result;
        }
    }

    protected int getUniqueID() {
        return _nextUniqueID++;
    }

    protected String getUniqueMemberName(
        JavaStructureType javaStructureType,
        String prefix) {
        JavaStructureMember member = javaStructureType.getMemberByName(prefix);
        if (member == null) {
            return prefix;
        }

        int i = 2;
        String name;
        do {
            name = prefix + Integer.toString(i);
        } while (javaStructureType.getMemberByName(name) != null);
        return name;
    }

    protected void resolveNamingConflictsFor(JavaType javaType) {
        resolveNamingConflictsFor(javaType, "_Type");
    }

    /**
     * @param javaEnumType
     */
    protected void resolveEnumerationNamingConflictsFor(JavaEnumerationType javaEnumType) {
        String enumSuffix = "_Enumeration";
        if(!_strictCompliance){
            if(enumerationNames == null)
                enumerationNames = new HashMap();
            if(enumerationNames.containsKey(javaEnumType.getName())){
                String originalName = javaEnumType.getName();
                Integer occur = (Integer)enumerationNames.get(javaEnumType.getName());
                String suffix = enumSuffix;
                for(int i = 0; i < occur.intValue(); i++){
                    suffix += enumSuffix;
                }
                javaEnumType.doSetName(javaEnumType.getName() + suffix);
                enumerationNames.put(originalName, new Integer(occur.intValue()+1));
            }else{
                enumerationNames.put(javaEnumType.getName(), new Integer(0));
            }
        }
        resolveNamingConflictsFor(javaEnumType, enumSuffix);
    }

    protected void resolveNamingConflictsFor(
        JavaType javaType,
        String suffix) {
        if (_conflictingClassNames != null
            && _conflictingClassNames.contains(javaType.getName())) {
            if (javaType.getName().equals(javaType.getRealName())) {
                // don't even try to fix things up if inner classes are involved
                javaType.doSetName(javaType.getName() + suffix);
            }
        }
    }

    protected void setReferenceable(SOAPSimpleType simpleType) {
        boolean referenceable = true;
        QName name = simpleType.getName();
        if (name.getNamespaceURI().equals(SchemaConstants.NS_XSD)
            && !(name.equals(BuiltInTypes.STRING)
                || name.equals(BuiltInTypes.BASE64_BINARY)
                || name.equals(BuiltInTypes.HEX_BINARY))) {
            referenceable = false;
        }
        simpleType.setReferenceable(referenceable);
    }

    protected boolean checkStrictCompliance(QName typeName) {
        boolean ret = true;
        //bug fix 4855115
        if (_strictCompliance
            && (typeName != null)
            && (typeName.equals(SchemaConstants.QNAME_TYPE_IDREF)
                || typeName.equals(
                    SchemaConstants
                        .QNAME_TYPE_URTYPE) // bug 4855115
        // jaxrpc-ri collections are not added to 
        //_builtinSchemaTypeToJavaTypeMap table if _strictCompliance flag is enabled
        // jaxrpc-ri collections,
        )) {
            fail(
                "model.schema.unsupportedSchemaType",
                new Object[] { typeName });
            return false;
        }
        return true;
    }

    /* information about Java member of an XML type */
    public static class SchemaJavaMemberInfo {
        public boolean isDataMember; // true if this is a field
        public String javaMemberName; // Java name of member

        public SchemaJavaMemberInfo() {
        }

        public SchemaJavaMemberInfo(
            String javaMemberName,
            boolean isDataMember) {
            this.javaMemberName = javaMemberName;
            this.isDataMember = isDataMember;
        }
    };

    private LocalizableMessageFactory _messageFactory;
    protected InternalSchema _schema;
    private ModelInfo _modelInfo;
    protected ProcessorEnvironment _env;
    private Set _typesBeingResolved;
    private Set _namePool;
    private Set _conflictingClassNames;
    private Map _componentToSOAPTypeMap;
    private Map _componentToLiteralTypeMap;
    private Map _typeNameToCustomSOAPTypeMap;
    protected Map _nillableSimpleTypeComponentToSOAPTypeMap;
    protected Map _nillableSimpleTypeComponentToLiteralTypeMap;
    private boolean _noDataBinding;
    protected boolean _useDataHandlerOnly;
    private int _nextUniqueID;
    protected boolean _resolveIDREF;
    protected boolean _strictCompliance;
    protected boolean _jaxbEnumType;
    protected JavaSimpleTypeCreator javaTypes;

    private static SOAPNamespaceConstants soap11NamespaceConstants = null;
    protected static SOAPWSDLConstants soap11WSDLConstants = null;

    private static SOAPNamespaceConstants soap12NamespaceConstants = null;
    private static SOAPWSDLConstants soap12WSDLConstants = null;

    private static final String VALUE_MEMBER_NAME = "_value";
    private static final String ANY_MEMBER_NAME_PREFIX = "_any";
    protected Map _builtinSchemaTypeToJavaTypeMap;
    protected Map _builtinSchemaTypeToJavaWrapperTypeMap;
    protected Map _simpleTypeEncoderMap;
    private Map enumerationNames;
    protected abstract void initializeMaps();
}
