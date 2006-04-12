/*
 * $Id: XMLReaderException.java,v 1.1 2006-04-12 20:32:51 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.streaming;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 * <p> XMLReaderException represents an exception that occurred while reading an
 * XML document. </p>
 *
 * @see XMLReader
 * @see com.sun.xml.rpc.util.exception.JAXRPCExceptionBase
 *
 * @author JAX-RPC Development Team
 */
public class XMLReaderException extends JAXRPCExceptionBase {

    public XMLReaderException(String key) {
        super(key);
    }

    public XMLReaderException(String key, String arg) {
        super(key, arg);
    }

    public XMLReaderException(String key, Object[] args) {
        super(key, args);
    }

    public XMLReaderException(String key, Localizable arg) {
        super(key, arg);
    }

    public XMLReaderException(Localizable arg) {
        super("xmlreader.nestedError", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.streaming";
    }
}
