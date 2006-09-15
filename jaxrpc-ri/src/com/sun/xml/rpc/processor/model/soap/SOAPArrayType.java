/*
 * $Id: SOAPArrayType.java,v 1.2 2006-04-13 01:30:01 ofung Exp $
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

package com.sun.xml.rpc.processor.model.soap;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.soap.SOAPVersion;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SOAPArrayType extends SOAPType {
    
    public SOAPArrayType() {}
    
    public SOAPArrayType(QName name) {
        this(name, SOAPVersion.SOAP_11);
    }
    
    public SOAPArrayType(QName name, SOAPVersion version) {
        this(name, null, null, null, version);
    }
    
    public SOAPArrayType(QName name, QName elementName, SOAPType elementType,
        JavaType javaType) {
            
        this(name, elementName, elementType, javaType, SOAPVersion.SOAP_11);
    }
    
    public SOAPArrayType(QName name, QName elementName, SOAPType elementType,
        JavaType javaType, SOAPVersion version) {
            
        super(name, javaType, version);
        this.elementName = elementName;
        this.elementType = elementType;
    }
    
    public QName getElementName() {
        return elementName;
    }
    
    public void setElementName(QName name) {
        elementName = name;
    }
    
    public SOAPType getElementType() {
        return elementType;
    }
    
    public void setElementType(SOAPType type) {
        elementType = type;
    }
    
    public int getRank() {
        return rank;
    }
    
    public void setRank(int i) {
        rank = i;
    }
    
    public int[] getSize() {
        return size;
    }
    
    public void setSize(int[] a) {
        size = a;
    }
    
    public void accept(SOAPTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    private QName elementName;
    private SOAPType elementType;
    private int rank;
    private int[] size;
}