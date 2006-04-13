/*
 * $Id: JavaEnumerationType.java,v 1.2 2006-04-13 01:29:41 ofung Exp $
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
 
package com.sun.xml.rpc.processor.model.java;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author JAX-RPC Development Team
 */
public class JavaEnumerationType extends JavaType {
    
    public JavaEnumerationType() {}
    
    public JavaEnumerationType(String name, JavaType baseType,
        boolean present) {
            
        super(name, present, "null");
        this.baseType = baseType;
    }
    
    public JavaType getBaseType() {
        return baseType;
    }
    
    public void setBaseType(JavaType t) {
        baseType = t;
    }
    
    public void add(JavaEnumerationEntry e) {
        entries.add(e);
    }
    
    public Iterator getEntries() {
        return entries.iterator();
    }
    
    public int getEntriesCount() {
        return entries.size();
    }
    
    /* serialization */
    public List getEntriesList() {
        return entries;
    }
    
    /* serialization */
    public void setEntriesList(List l) {
        entries = l;
    }
    
    private List entries = new ArrayList();
    private JavaType baseType;
}
