/*
 * $Id: SchemaAnalyzer101.java,v 1.2 2006-04-13 01:31:26 ofung Exp $ 
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

/**
 * @author JAX-RPC Development Team
 * 
 * JAXRPC 1.0.1 specific SchemaAnalyzer
 */
package com.sun.xml.rpc.processor.modeler.wsdl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.InternalEncodingConstants;
import com.sun.xml.rpc.encoding.simpletype.XSDByteEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDDecimalEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDDoubleEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDFloatEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDIntEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDIntegerEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDLongEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDShortEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDStringEncoder;
import com.sun.xml.rpc.processor.config.ModelInfo;
import com.sun.xml.rpc.processor.model.java.JavaEnumerationType;
import com.sun.xml.rpc.processor.model.java.JavaSimpleType;
import com.sun.xml.rpc.processor.model.literal.LiteralFragmentType;
import com.sun.xml.rpc.processor.model.literal.LiteralSimpleType;
import com.sun.xml.rpc.processor.model.literal.LiteralStructuredType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.soap.SOAPAnyType;
import com.sun.xml.rpc.processor.model.soap.SOAPSimpleType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator;
import com.sun.xml.rpc.processor.schema.AttributeUseComponent;
import com.sun.xml.rpc.processor.schema.ComplexTypeDefinitionComponent;
import com.sun.xml.rpc.processor.schema.ElementDeclarationComponent;
import com.sun.xml.rpc.processor.schema.EnumerationFacet;
import com.sun.xml.rpc.processor.schema.Facet;
import com.sun.xml.rpc.processor.schema.ParticleComponent;
import com.sun.xml.rpc.processor.schema.SimpleTypeDefinitionComponent;
import com.sun.xml.rpc.processor.schema.UnimplementedFeatureException;
import com.sun.xml.rpc.wsdl.document.schema.BuiltInTypes;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import com.sun.xml.rpc.wsdl.framework.AbstractDocument;

public class SchemaAnalyzer101 extends SchemaAnalyzerBase {

    /**
     * @param document
     * @param modelInfo
     * @param options
     * @param conflictingClassNames
     * @param javaTypes
     */
    public SchemaAnalyzer101(
        AbstractDocument document,
        ModelInfo modelInfo,
        Properties options,
        Set conflictingClassNames,
        JavaSimpleTypeCreator javaTypes) {
        super(document, modelInfo, options, conflictingClassNames, javaTypes);
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
                        SOAPSimpleType simpleType =
                            new SOAPSimpleType(component.getName());
                        simpleType.setSchemaTypeRef(component.getName());
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
                        simpleType.setJavaType(javaType);
                        setReferenceable(simpleType);
                        return simpleType;
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
                    //failUnsupported("U004", component.getName());
                    fail(
                        "model.schema.invalidSimpleType",
                        new Object[] { component.getName()});
                }
            } else if (
                component.getVarietyTag()
                    == SimpleTypeDefinitionComponent.VARIETY_LIST) {
                if (doWeHandleSimpleSchemaTypeDerivationByList())
                    return listToSOAPType(component, nameHint);
                //failUnsupported("U005", component.getName());
                fail(
                    "model.schema.listNotSupported",
                    new Object[] { component.getName()});

            } else {
                // union
                //failUnsupported("U005", component.getName());
                fail(
                    "model.schema.unionNotSupported",
                    new Object[] { component.getName()});
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
                                //failUnsupported("U006", component.getName());
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
                    //failUnsupported("U006", component.getName());
                    fail(
                        "model.schema.invalidSimpleType.noNamespaceURI",
                        new Object[] { component.getName()});
                }
            } else if (
                component.getVarietyTag()
                    == SimpleTypeDefinitionComponent.VARIETY_LIST) {
                if (doWeHandleSimpleSchemaTypeDerivationByList())
                    return listToSOAPType(component, nameHint);
                //failUnsupported("U005", component.getName());
                fail(
                    "model.schema.listNotSupported",
                    new Object[] { component.getName()});
            } else {
                // union
                //failUnsupported("U005", component.getName());
                fail(
                    "model.schema.unionNotSupported",
                    new Object[] { component.getName()});
            }
        }
        return null; // keep compiler happy
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
            } else {
                //union
                return getLiteralSimpleStringTypeFor(component, nameHint);
            }
        } else {
            return anonymousSimpleSchemaTypeToLiteralType(
                component,
                nameHint,
                mappingNameHint);
        }
    }

    public LiteralType schemaElementTypeToLiteralType(QName elementName) {
        try {
            ElementDeclarationComponent component =
                _schema.findElementDeclaration(elementName);
            LiteralType literalType =
                schemaTypeToLiteralType(
                    component.getTypeDefinition(),
                    elementName);
            if (literalType.getName() == null) {
                // to get a better output
                literalType.setName(getUniqueTypeNameForElement(elementName));
                // literalType.setName(new QName(elementName.getNamespaceURI(), elementName.getLocalPart() + "__" + getUniqueID() + "__AnonymousType"));
            }
            return literalType;
        } catch (UnimplementedFeatureException e) {
            // NOTE - this seems wrong, but there isn't much else we can do
            //        because the type of the element may be defined inline,
            //        hence be anonymous; even for element whose type is not
            //        anonymous, it's very hard to reach into the schema
            //        and grab the relevant data from here
            LiteralType literalType = new LiteralFragmentType();
            literalType.setName(elementName);
            literalType.setJavaType(javaTypes.SOAPELEMENT_JAVATYPE);
            return literalType;
        }
    }

    /**
    * @param component
    * @param nameHint
    * @return
    */
    protected LiteralType anonymousSimpleSchemaTypeToLiteralType(
        SimpleTypeDefinitionComponent component,
        QName nameHint,
        QName mappingNameHint) {
        SimpleTypeDefinitionComponent baseTypeComponent =
            component.getBaseTypeDefinition();
        if (component.getVarietyTag()
            == SimpleTypeDefinitionComponent.VARIETY_ATOMIC) {
            String nsURI = baseTypeComponent.getName().getNamespaceURI();
            if (nsURI != null && (nsURI.equals(SchemaConstants.NS_XSD))) {
                // restriction of a well-know type
                LiteralType baseType =
                    schemaTypeToLiteralType(baseTypeComponent, nameHint);
                return baseType;
            } else {
                return getLiteralSimpleStringTypeFor(component, nameHint);
            }

        } else {
            // list or union
            return getLiteralSimpleStringTypeFor(component, nameHint);
        }
    }

    protected LiteralType soapStructureExtensionComplexSchemaTypeToLiteralType(
        ComplexTypeDefinitionComponent component,
        LiteralStructuredType parentType,
        QName nameHint) {
        return getLiteralFragmentTypeFor(component, nameHint);

    }

    /**
     * @return
     */
    protected boolean isAttributeEnumeration(LiteralType attributeType) {
        return false;
    }

    /**
     * @param attributeUse
     * @return
     */
    protected boolean isAttributeOptional(AttributeUseComponent attributeUse) {
        return false;
    }

    /**
     * @param memberParticle
     * @return
     */
    protected boolean isParticleOptional(ParticleComponent memberParticle) {
        return false;
    }

    protected SOAPType listToSOAPType(
        SimpleTypeDefinitionComponent component,
        QName nameHint) {
        //failUnsupported("U005", component.getName());
        fail(
            "model.schema.listNotSupported",
            new Object[] { component.getName()});
        return null;
    }

    protected LiteralType listToLiteralType(
        SimpleTypeDefinitionComponent component,
        QName nameHint) {
        return getLiteralSimpleStringTypeFor(component, nameHint);
    }

    /**
     * @return
     */
    protected boolean doWeHandleSimpleSchemaTypeDerivationByList() {
        return false;
    }

    /**
     * @return
     */
    protected boolean doWeHandleWildcard() {
        return false;
    }

    /**
     * @return
     */
    protected boolean doWeHandleComplexSchemaTypeExtensionBySimpleContent() {
        return false;
    }

    /**
     * @param javaEnumType
     */
    protected void resolveEnumerationNamingConflictsFor(JavaEnumerationType javaEnumType) {
        resolveNamingConflictsFor(javaEnumType);
    }

    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.modeler.wsdl.SchemaAnalyzerBase#makeMemberBoxedType()
     */
    protected boolean doMakeMemberBoxedType() {
        return false;
    }

    protected void initializeMaps() {
        _builtinSchemaTypeToJavaTypeMap = new HashMap();
        if (_useDataHandlerOnly) {
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_IMAGE,
                javaTypes.DATA_HANDLER_JAVATYPE);
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_MIME_MULTIPART,
                javaTypes.DATA_HANDLER_JAVATYPE);
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_SOURCE,
                javaTypes.DATA_HANDLER_JAVATYPE);
        } else {
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_IMAGE,
                javaTypes.IMAGE_JAVATYPE);
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_MIME_MULTIPART,
                javaTypes.MIME_MULTIPART_JAVATYPE);
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_SOURCE,
                javaTypes.SOURCE_JAVATYPE);
        }
        _builtinSchemaTypeToJavaTypeMap.put(
            InternalEncodingConstants.QNAME_TYPE_DATA_HANDLER,
            javaTypes.DATA_HANDLER_JAVATYPE);

        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.STRING,
            javaTypes.STRING_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.INTEGER,
            javaTypes.BIG_INTEGER_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.INT,
            javaTypes.INT_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.LONG,
            javaTypes.LONG_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.SHORT,
            javaTypes.SHORT_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.DECIMAL,
            javaTypes.DECIMAL_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.FLOAT,
            javaTypes.FLOAT_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.DOUBLE,
            javaTypes.DOUBLE_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.BOOLEAN,
            javaTypes.BOOLEAN_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.BYTE,
            javaTypes.BYTE_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.QNAME,
            javaTypes.QNAME_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.DATE_TIME,
            javaTypes.CALENDAR_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.BASE64_BINARY,
            javaTypes.BYTE_ARRAY_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.HEX_BINARY,
            javaTypes.BYTE_ARRAY_JAVATYPE);

        _builtinSchemaTypeToJavaTypeMap.put(
            SchemaConstants.QNAME_TYPE_URTYPE,
            javaTypes.OBJECT_JAVATYPE);

        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeString(),
            javaTypes.STRING_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeInteger(),
            javaTypes.BIG_INTEGER_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeInt(),
            javaTypes.BOXED_INTEGER_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeLong(),
            javaTypes.BOXED_LONG_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeShort(),
            javaTypes.BOXED_SHORT_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeDecimal(),
            javaTypes.DECIMAL_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeFloat(),
            javaTypes.BOXED_FLOAT_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeDouble(),
            javaTypes.BOXED_DOUBLE_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeBoolean(),
            javaTypes.BOXED_BOOLEAN_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeByte(),
            javaTypes.BOXED_BYTE_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeQName(),
            javaTypes.QNAME_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeDateTime(),
            javaTypes.CALENDAR_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeBase64Binary(),
            javaTypes.BYTE_ARRAY_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeHexBinary(),
            javaTypes.BYTE_ARRAY_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeBase64(),
            javaTypes.BYTE_ARRAY_JAVATYPE);

        _builtinSchemaTypeToJavaTypeMap.put(
            InternalEncodingConstants.QNAME_TYPE_COLLECTION,
            javaTypes.COLLECTION_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            InternalEncodingConstants.QNAME_TYPE_LIST,
            javaTypes.LIST_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            InternalEncodingConstants.QNAME_TYPE_SET,
            javaTypes.SET_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            InternalEncodingConstants.QNAME_TYPE_ARRAY_LIST,
            javaTypes.ARRAY_LIST_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            InternalEncodingConstants.QNAME_TYPE_VECTOR,
            javaTypes.VECTOR_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            InternalEncodingConstants.QNAME_TYPE_STACK,
            javaTypes.STACK_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            InternalEncodingConstants.QNAME_TYPE_LINKED_LIST,
            javaTypes.LINKED_LIST_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            InternalEncodingConstants.QNAME_TYPE_HASH_SET,
            javaTypes.HASH_SET_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            InternalEncodingConstants.QNAME_TYPE_TREE_SET,
            javaTypes.TREE_SET_JAVATYPE);

        _builtinSchemaTypeToJavaTypeMap.put(
            InternalEncodingConstants.QNAME_TYPE_MAP,
            javaTypes.MAP_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            InternalEncodingConstants.QNAME_TYPE_JAX_RPC_MAP_ENTRY,
            javaTypes.JAX_RPC_MAP_ENTRY_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            InternalEncodingConstants.QNAME_TYPE_HASH_MAP,
            javaTypes.HASH_MAP_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            InternalEncodingConstants.QNAME_TYPE_TREE_MAP,
            javaTypes.TREE_MAP_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            InternalEncodingConstants.QNAME_TYPE_HASHTABLE,
            javaTypes.HASHTABLE_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            InternalEncodingConstants.QNAME_TYPE_PROPERTIES,
            javaTypes.PROPERTIES_JAVATYPE);

        _builtinSchemaTypeToJavaWrapperTypeMap = new HashMap();
        _builtinSchemaTypeToJavaWrapperTypeMap.put(
            BuiltInTypes.INT,
            javaTypes.BOXED_INTEGER_JAVATYPE);
        _builtinSchemaTypeToJavaWrapperTypeMap.put(
            BuiltInTypes.LONG,
            javaTypes.BOXED_LONG_JAVATYPE);
        _builtinSchemaTypeToJavaWrapperTypeMap.put(
            BuiltInTypes.SHORT,
            javaTypes.BOXED_SHORT_JAVATYPE);
        _builtinSchemaTypeToJavaWrapperTypeMap.put(
            BuiltInTypes.FLOAT,
            javaTypes.BOXED_FLOAT_JAVATYPE);
        _builtinSchemaTypeToJavaWrapperTypeMap.put(
            BuiltInTypes.DOUBLE,
            javaTypes.BOXED_DOUBLE_JAVATYPE);
        _builtinSchemaTypeToJavaWrapperTypeMap.put(
            BuiltInTypes.BOOLEAN,
            javaTypes.BOXED_BOOLEAN_JAVATYPE);
        _builtinSchemaTypeToJavaWrapperTypeMap.put(
            BuiltInTypes.BYTE,
            javaTypes.BOXED_BYTE_JAVATYPE);

        _simpleTypeEncoderMap = new HashMap();
        _simpleTypeEncoderMap.put(
            BuiltInTypes.STRING,
            XSDStringEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.INTEGER,
            XSDIntegerEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.INT,
            XSDIntEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.LONG,
            XSDLongEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.SHORT,
            XSDShortEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.DECIMAL,
            XSDDecimalEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.FLOAT,
            XSDFloatEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.DOUBLE,
            XSDDoubleEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.BYTE,
            XSDByteEncoder.getInstance());
        // _simpleTypeEncoderMap.put(BuiltInTypes.QNAME, XSDQNameEncoder.getInstance());

    }

    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.modeler.wsdl.SchemaAnalyzerBase#doWeHandleComplexSchemaTypeExtensionByComplexType()
     */
    protected boolean doWeHandleComplexSchemaTypeExtensionByComplexType() {
        return false;
    }

}
