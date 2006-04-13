/*
 * $Id: SOAPType.java,v 1.2 2006-04-13 01:30:07 ofung Exp $
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

import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.soap.SOAPVersion;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class SOAPType extends AbstractType {
    
    protected SOAPType() {
        setVersion(SOAPVersion.SOAP_11.toString());
    }
    
    protected SOAPType(QName name) {
        this(name, SOAPVersion.SOAP_11);
    }
    
    protected SOAPType(QName name, SOAPVersion version) {
        this(name, null, version);
    }
    
    protected SOAPType(QName name, JavaType javaType) {
        this(name, javaType, SOAPVersion.SOAP_11);
    }
    
    protected SOAPType(QName name, JavaType javaType, SOAPVersion version) {
        super(name, javaType, version != null ? version.toString() : null);
    }
    
    public boolean isNillable() {
        return true;
    }
    
    public boolean isReferenceable() {
        return true;
    }
    
    public boolean isSOAPType() {
        return true;
    }
    
    public abstract void accept(SOAPTypeVisitor visitor) throws Exception;
}
