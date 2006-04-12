/*
 * $Id: TypeMappingInfo.java,v 1.1 2006-04-12 20:34:48 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
