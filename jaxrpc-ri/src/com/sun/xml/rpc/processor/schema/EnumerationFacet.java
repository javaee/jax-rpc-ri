/*
 * $Id: EnumerationFacet.java,v 1.2 2006-04-13 01:31:40 ofung Exp $
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

package com.sun.xml.rpc.processor.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;

/**
 *
 * @author JAX-RPC Development Team
 */
public class EnumerationFacet extends ConstrainingFacet {
    
    public EnumerationFacet() {
        super(SchemaConstants.QNAME_ENUMERATION);
    }
    
    public void addValue(String s) {
        values.add(s);
    }
    
    public Iterator values() {
        return values.iterator();
    }
    
    public void addPrefix(String prefix, String nspace) {
        prefixes.put(prefix, nspace);
    }
    
    public String getNamespaceURI(String prefix) {
        return (String)prefixes.get(prefix);
    }
    
    public Map getPrefixes() {
        return prefixes;
    }
    
    private List values = new ArrayList();
    private Map prefixes = new HashMap();
}
