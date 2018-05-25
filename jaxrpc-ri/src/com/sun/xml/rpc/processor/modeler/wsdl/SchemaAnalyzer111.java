/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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
