/*
 * $Id: LiteralType.java,v 1.2 2006-04-13 01:29:56 ofung Exp $
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

package com.sun.xml.rpc.processor.model.literal;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.java.JavaType;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class LiteralType extends AbstractType {
    
    protected LiteralType() {}
    
    protected LiteralType(QName name, JavaType javaType) {
        super(name, javaType);
    }
    
    public QName getSchemaTypeRef() {
        return _schemaTypeRef;
    }
    
    public void setSchemaTypeRef(QName n) {
        _schemaTypeRef = n;
    }
    
    public boolean isLiteralType() {
        return true;
    }
    
    public void setNillable(boolean nillable) {
        isNillable = nillable;
    }
    
    public boolean isNillable() {
        return isNillable;
    }
    
    public abstract void accept(LiteralTypeVisitor visitor) throws Exception;
    
    private QName _schemaTypeRef;
    protected boolean isNillable = false;
}
