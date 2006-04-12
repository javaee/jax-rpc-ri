/*
 * $Id: EnumerationFacet.java,v 1.1 2006-04-12 20:35:09 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
