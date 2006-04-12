/*
 * $Id: SimpleTypeEncoder.java,v 1.1 2006-04-12 20:34:25 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding.simpletype;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;

/**
 *
 * @author JAX-RPC Development Team
 */
public interface SimpleTypeEncoder {
    public String objectToString(Object obj, XMLWriter writer)
        throws Exception;
        
    public Object stringToObject(String str, XMLReader reader)
        throws Exception;
        
    public void writeValue(Object obj, XMLWriter writer) throws Exception;
    
    public void writeAdditionalNamespaceDeclarations(
        Object obj,
        XMLWriter writer)
        throws Exception;
}
