/*
 * $Id: LiteralIDType.java,v 1.2 2006-04-13 01:29:53 ofung Exp $
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

import com.sun.xml.rpc.processor.model.java.JavaSimpleType;

/**
 *
 * @author JAX-RPC Development Team
 */
public class LiteralIDType extends LiteralType {
    
    public LiteralIDType() {}
    
    public LiteralIDType(QName name) {
        this(name, null);
    }
    
    public LiteralIDType(QName name, JavaSimpleType javaType) {
        this(name, javaType, false);
    }
    
    public LiteralIDType(QName name, JavaSimpleType javaType,
        boolean resolveIDREF) {
            
        super(name, javaType);
        this.resolveIDREF = resolveIDREF;
    }
    
    public void accept(LiteralTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    public boolean getResolveIDREF() {
        return resolveIDREF;
    }
    
    public void setResolveIDREF(boolean resolveIDREF) {
        this.resolveIDREF = resolveIDREF;
    }
    
    //flag which represents command line -f:resolveidref flag.
    private boolean resolveIDREF;
    
}
