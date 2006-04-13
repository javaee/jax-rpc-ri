/*
 * $Id: J2EESchemaAnalyzer112.java,v 1.2 2006-04-13 01:30:17 ofung Exp $
 */

package com.sun.xml.rpc.processor.modeler.j2ee;

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

import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.config.J2EEModelInfo;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.literal.LiteralStructuredType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator;
import com.sun.xml.rpc.processor.modeler.wsdl.SchemaAnalyzer112;
import com.sun.xml.rpc.processor.schema.ComplexTypeDefinitionComponent;
import com.sun.xml.rpc.processor.schema.ElementDeclarationComponent;
import com.sun.xml.rpc.processor.schema.TypeDefinitionComponent;
import com.sun.xml.rpc.wsdl.framework.AbstractDocument;

/**
 *
 * @author JAX-RPC RI Development Team
 */
public class J2EESchemaAnalyzer112 extends SchemaAnalyzer112
    implements J2EESchemaAnalyzerIf {

    private JavaSimpleTypeCreator javaSimpleTypeCreator;
    private J2EEModelInfo _j2eeModelInfo;
    private J2EESchemaAnalyzerHelper helper;

    public J2EESchemaAnalyzer112(
        AbstractDocument document,
        J2EEModelInfo modelInfo,
        Properties options,
        Set conflictingClassNames,
        JavaSimpleTypeCreator javaTypes) {
        super(document, modelInfo, options, conflictingClassNames, javaTypes);
        _j2eeModelInfo = (J2EEModelInfo) modelInfo;
        javaSimpleTypeCreator = new JavaSimpleTypeCreator();
        helper = new J2EESchemaAnalyzerHelper(this, modelInfo, _env, javaTypes);
    }

    protected String getJavaNameOfType(
        TypeDefinitionComponent component,
        QName nameHint) {

        return helper.getJavaNameOfType(component, nameHint);            
    }

    // Sets abstract if the java type is abstract or interface
    protected void updateModifiers(JavaStructureType javaStructureType) {
        helper.updateModifiers(javaStructureType);      
    }

    protected String getJavaNameOfSOAPStructureType(
        SOAPStructureType structureType,
        TypeDefinitionComponent component,
        QName nameHint) {
            
        return helper.getJavaNameOfSOAPStructureType(structureType, component,
            nameHint);
    }

    protected SchemaJavaMemberInfo getJavaMemberInfo(
        TypeDefinitionComponent component,
        ElementDeclarationComponent element) {

        return helper.getJavaMemberInfo(component, element);
    }

    protected String getJavaNameOfElementType(
        LiteralStructuredType structureType,
        TypeDefinitionComponent component,
        QName nameHint) {

        return helper.getJavaNameOfElementType(structureType, component,
            nameHint);          
    }

    protected SchemaJavaMemberInfo getJavaMemberOfElementInfo(
        QName typeName,
        String memberName) {

        return helper.getJavaMemberOfElementInfo(typeName, memberName);
    }

    protected SOAPType getSOAPMemberType(
        ComplexTypeDefinitionComponent component,
        SOAPStructureType structureType,
        ElementDeclarationComponent element,
        QName nameHint,
        boolean occursZeroOrOne) {

        return helper.getSOAPMemberType(component, structureType, element,
            nameHint, occursZeroOrOne);          
    }

    protected LiteralType getLiteralMemberType(
        ComplexTypeDefinitionComponent component,
        LiteralType memberType,
        ElementDeclarationComponent element,
        LiteralStructuredType structureType) {

        return helper.getLiteralMemberType(component,  memberType, element,
            structureType);
    }

    public SOAPType getSuperSOAPMemberType(
        ComplexTypeDefinitionComponent component,
        SOAPStructureType structureType,
        ElementDeclarationComponent element,
        QName nameHint,
        boolean occursZeroOrOne) {
            
        return super.getSOAPMemberType(component, structureType, element,
            nameHint, occursZeroOrOne);
    }

    public SchemaJavaMemberInfo getSuperJavaMemberInfo(
        TypeDefinitionComponent component,
        ElementDeclarationComponent element) {
            
        return super.getJavaMemberInfo(component, element);
    }

}
