/*
 * $Id: XSDStringEncoder.java,v 1.1 2006-04-12 20:34:31 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding.simpletype;

import com.sun.xml.rpc.streaming.*;
import javax.activation.DataHandler;

/**
 *
 * @author JAX-RPC Development Team
 */
public class XSDStringEncoder
    extends SimpleTypeEncoderBase
    implements AttachmentEncoder {
        
    private static final SimpleTypeEncoder encoder = new XSDStringEncoder();

    private XSDStringEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer)
        throws Exception {
            
        return (String) obj;
    }

    public Object stringToObject(String str, XMLReader reader)
        throws Exception {
            
        return str;
    }

    public DataHandler objectToDataHandler(Object obj) throws Exception {
        DataHandler dataHandler = new DataHandler(obj, "text/plain");

        return dataHandler;
    }

    public Object dataHandlerToObject(DataHandler dataHandler)
        throws Exception {
        return dataHandler.getContent();
    }
}
