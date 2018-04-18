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
 * $Id: J2EESchemaAnalyzer112.java,v 1.3 2007-07-13 23:36:08 ofung Exp $
 */

package com.sun.xml.rpc.processor.modeler.j2ee;

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
