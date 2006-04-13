/*
 * $Id: J2EESchemaAnalyzerIf.java,v 1.2 2006-04-13 01:30:18 ofung Exp $
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
package com.sun.xml.rpc.processor.modeler.j2ee;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.modeler.wsdl.SchemaAnalyzerBase.SchemaJavaMemberInfo;
import com.sun.xml.rpc.processor.schema.ComplexTypeDefinitionComponent;
import com.sun.xml.rpc.processor.schema.ElementDeclarationComponent;
import com.sun.xml.rpc.processor.schema.TypeDefinitionComponent;


public interface J2EESchemaAnalyzerIf {
    public SOAPType getSuperSOAPMemberType(
        ComplexTypeDefinitionComponent component,
        SOAPStructureType structureType,
        ElementDeclarationComponent element,
        QName nameHint,
        boolean occursZeroOrOne);
    public SchemaJavaMemberInfo getSuperJavaMemberInfo(
        TypeDefinitionComponent component,
        ElementDeclarationComponent element);
}
