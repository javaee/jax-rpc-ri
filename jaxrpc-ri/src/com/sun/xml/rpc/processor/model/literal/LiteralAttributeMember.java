/*
 * $Id: LiteralAttributeMember.java,v 1.2 2006-04-13 01:29:49 ofung Exp $
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

import com.sun.xml.rpc.processor.model.java.JavaStructureMember;

/**
 *
 * @author JAX-RPC Development Team
 */
public class LiteralAttributeMember {
    
    public LiteralAttributeMember() {}
    
    public LiteralAttributeMember(QName name, LiteralType type) {
        this(name, type, null);
    }
    
    public LiteralAttributeMember(QName name, LiteralType type,
        JavaStructureMember javaStructureMember) {
            
        _name = name;
        _type = type;
        _javaStructureMember = javaStructureMember;
    }
    
    public QName getName() {
        return _name;
    }
    
    public void setName(QName n) {
        _name = n;
    }
    
    public LiteralType getType() {
        return _type;
    }
    
    public void setType(LiteralType t) {
        _type = t;
    }
    
    public JavaStructureMember getJavaStructureMember() {
        return _javaStructureMember;
    }
    
    public void setJavaStructureMember(
        JavaStructureMember javaStructureMember) {
            
        _javaStructureMember = javaStructureMember;
    }
    
    public boolean isRequired() {
        return _required;
    }
    
    public void setRequired(boolean b) {
        _required = b;
    }
    
    public boolean isInherited() {
        return isInherited;
    }
    
    public void setInherited(boolean b) {
        isInherited = b;
    }
    
    private QName _name;
    private LiteralType _type;
    private JavaStructureMember _javaStructureMember;
    private boolean _required;
    private boolean isInherited = false;
}
