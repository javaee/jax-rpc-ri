/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2018 Sun Microsystems, Inc. All rights reserved.
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
