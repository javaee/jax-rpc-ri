/*
 * $Id: TypeMappingInfo.java,v 1.2 2006-04-13 01:28:29 ofung Exp $
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

package com.sun.xml.rpc.processor.config;

import javax.xml.namespace.QName;

/**
 *
 * @author JAX-RPC Development Team
 */
public class TypeMappingInfo {

    public TypeMappingInfo() {}
    
    public TypeMappingInfo(String encodingStyle,
                           QName xmlType,
                           String javaTypeName,
                           String serializerFactoryName,
                           String deserializerFactoryName) {
            
        this.encodingStyle = encodingStyle;
        this.xmlType = xmlType;
        this.javaTypeName = javaTypeName;
        this.serializerFactoryName = serializerFactoryName;
        this.deserializerFactoryName = deserializerFactoryName;
    }

    public String getEncodingStyle() {
        return encodingStyle;
    }
    
    public void setEncodingStyle(String s) {
        encodingStyle = s;
    }

    public QName getXMLType() {
        return xmlType;
    }
    
    public void setXMLType(QName n) {
        xmlType = n;
    }

    public String getJavaTypeName() {
        return javaTypeName;
    }
    
    public void setJavaTypeName(String s) {
        javaTypeName = s;
    }

    public String getSerializerFactoryName() {
        return serializerFactoryName;
    }
    
    public void setSerializerFactoryName(String s) {
        serializerFactoryName = s;
    }

    public String getDeserializerFactoryName() {
        return deserializerFactoryName;
    }
    
    public void setDeserializerFactoryName(String s) {
        deserializerFactoryName = s;
    }

    private String encodingStyle;
    private QName xmlType;
    private String javaTypeName;
    private String serializerFactoryName;
    private String deserializerFactoryName;
}
