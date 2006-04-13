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

// @(#) 1.1 jsr109ri/src/java/com/ibm/webservices/ri/xml/jaxrpcmap1_1/javaXmlTypeMappingType.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:33:39 [10/7/02 11:55:19]
/*************************************************************************
   Licensed Materials - Property of IBM
   5639-D57, 5630-A36, 5630-A37, 5724-D18 (C) COPYRIGHT International Business M
achines Corp. 2002
   All Rights Reserved
   US Government Users Restricted Rights - Use, duplication, or
   disclosure restricted by GSA ADP Schedule Contract  with
   IBM Corp.
**************************************************************************/
package com.sun.xml.rpc.processor.modeler.j2ee.xml;

/**
* This class represents the complex type <javaXmlTypeMappingType>
*/
public class javaXmlTypeMappingType extends ComplexType {
    public javaXmlTypeMappingType() {
    }

    public void setJavaType(javaTypeType javaType) {
        setElementValue("java-type", javaType);
    }

    public javaTypeType getJavaType() {
        return (javaTypeType) getElementValue("java-type", "javaTypeType");
    }

    public boolean removeJavaType() {
        return removeElement("java-type");
    }

    public void setRootTypeQname(xsdQNameType rootTypeQname) {
        setElementValue("root-type-qname", rootTypeQname);
    }

    public xsdQNameType getRootTypeQname() {
        return (xsdQNameType) getElementValue(
            "root-type-qname",
            "xsdQNameType");
    }

    public boolean removeRootTypeQname() {
        return removeElement("root-type-qname");
    }

    public void setAnonymousTypeQname(string anonymousTypeQname) {
        setElementValue("anonymous-type-qname", anonymousTypeQname);
    }

    public string getAnonymousTypeQname() {
        return (string) getElementValue("anonymous-type-qname", "string");
    }

    public boolean removeAnonymousTypeQname() {
        return removeElement("anonymous-type-qname");
    }

    public void setQnameScope(qnameScopeType qnameScope) {
        setElementValue("qname-scope", qnameScope);
    }

    public qnameScopeType getQnameScope() {
        return (qnameScopeType) getElementValue(
            "qname-scope",
            "qnameScopeType");
    }

    public boolean removeQnameScope() {
        return removeElement("qname-scope");
    }

    public void setVariableMapping(
        int index,
        variableMappingType variableMapping) {
        setElementValue(index, "variable-mapping", variableMapping);
    }

    public variableMappingType getVariableMapping(int index) {
        return (variableMappingType) getElementValue(
            "variable-mapping",
            "variableMappingType",
            index);
    }

    public int getVariableMappingCount() {
        return sizeOfElement("variable-mapping");
    }

    public boolean removeVariableMapping(int index) {
        return removeElement(index, "variable-mapping");
    }

    public void setId(String id) {
        setAttributeValue("id", id);
    }

    public String getId() {
        return getAttributeValue("id");
    }

    public boolean removeId() {
        return removeAttribute("id");
    }

}
