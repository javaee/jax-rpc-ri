/*
 * $Id: XSDAnyURIEncoder.java,v 1.1 2006-04-12 20:34:28 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding.simpletype;

import javax.activation.DataHandler;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class XSDAnyURIEncoder
    extends SimpleTypeEncoderBase
    implements AttachmentEncoder {
        
    private static final SimpleTypeEncoder encoder = new XSDAnyURIEncoder();

    private XSDAnyURIEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer)
        throws Exception {
            
        return ((java.net.URI) obj).toString();
    }

    public Object stringToObject(String str, XMLReader reader)
        throws Exception {
            
        return new java.net.URI(str);
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