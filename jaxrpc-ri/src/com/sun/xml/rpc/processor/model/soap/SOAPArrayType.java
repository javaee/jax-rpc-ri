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
