/*
 * $Id: SchemaAnalyzer111.java,v 1.1 2006-04-12 20:34:01 kohlert Exp $ 
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.modeler.wsdl;

import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.config.ModelInfo;
import com.sun.xml.rpc.processor.model.java.JavaSimpleType;
import com.sun.xml.rpc.processor.model.literal.LiteralSimpleType;
import com.sun.xml.rpc.processor.model.soap.SOAPSimpleType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator;
import com.sun.xml.rpc.processor.schema.TypeDefinitionComponent;
import com.sun.xml.rpc.wsdl.framework.AbstractDocument;

/**
 * @author JAX-RPC Development Team
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SchemaAnalyzer111 extends SchemaAnalyzer11 {

    /**
     * @param document
     * @param modelInfo
     * @param options
     * @param conflictingClassNames
     * @param javaTypes
     */
    public SchemaAnalyzer111(
        AbstractDocument document,
        ModelInfo modelInfo,
        Properties options,
        Set conflictingClassNames,
        JavaSimpleTypeCreator javaTypes) {
        super(document, modelInfo, options, conflictingClassNames, javaTypes);
    }

    protected SOAPType nillableSchemaTypeToSOAPType(TypeDefinitionComponent component) {
        // bug fix: 4961579
        QName baseTypeName = getSimpleTypeBaseName(component);                                                                              
        JavaSimpleType javaType =
              (
                  JavaSimpleType) _builtinSchemaTypeToJavaWrapperTypeMap
                      .get(baseTypeName);                                

//        JavaSimpleType javaType =
//            (JavaSimpleType) _builtinSchemaTypeToJavaWrapperTypeMap.get(
//                component.getName());
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
            // bug fix 4961579
            result = new SOAPSimpleType(baseTypeName, javaType);           
//            result = new SOAPSimpleType(component.getName(), javaType);
            result.setSchemaTypeRef(component.getName());
            setReferenceable(result);
            _nillableSimpleTypeComponentToSOAPTypeMap.put(component, result);
            return result;
        }
    }
    
    // bug fix 4961579
    protected LiteralSimpleType getNillableLiteralSimpleType(QName typeName, 
        TypeDefinitionComponent typeDef) {


        QName baseTypeName = getSimpleTypeBaseName(typeDef);                                                                              
        JavaSimpleType javaType =
            (JavaSimpleType) _builtinSchemaTypeToJavaWrapperTypeMap
                                                     .get(baseTypeName);

        if (javaType == null) {
            return null;
        }
           
        LiteralSimpleType result =
            (LiteralSimpleType) _nillableSimpleTypeComponentToLiteralTypeMap
                .get(typeDef);
        if (result == null) {
            result =
                new LiteralSimpleType(typeName,
                    javaType,
                    true);                                        
            result.setSchemaTypeRef(typeDef.getName());
            _nillableSimpleTypeComponentToLiteralTypeMap
                .put(typeDef, result);
        }
        return result;
    }    

}
