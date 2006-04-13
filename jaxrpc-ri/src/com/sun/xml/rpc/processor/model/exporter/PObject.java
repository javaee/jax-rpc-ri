/*
 * $Id: PObject.java,v 1.2 2006-04-13 01:29:38 ofung Exp $
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


package com.sun.xml.rpc.processor.model.exporter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sun.xml.rpc.util.NullIterator;

/**
 * @author JAX-RPC Development Team
 */
public class PObject {
    
    public PObject() {}
    
    public String getType() {
        return type;
    }
    
    public void setType(String s) {
        type = s;
    }
    
    public Object getProperty(String name) {
        if (properties == null) {
            return null;
        } else {
            return properties.get(name);
        }
    }
    
    public void setProperty(String name, Object value) {
        if (properties == null) {
            properties = new HashMap();
        }
        properties.put(name, value);
    }
    
    public Iterator getProperties() {
        if (properties == null) {
            return NullIterator.getInstance();
        } else {
            return properties.values().iterator();
        }
    }
    
    public Iterator getPropertyNames() {
        if (properties == null) {
            return NullIterator.getInstance();
        } else {
            return properties.keySet().iterator();
        }
    }
    
    private Map properties;
    private String type;
}

