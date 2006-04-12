/*
 * $Id: XMLWriterException.java,v 1.1 2006-04-12 20:32:47 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.streaming;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 * <p> XMLWriterException represents an exception that occurred while writing
 * an XML document. </p>
 *
 * @see XMLWriter
 * @see com.sun.xml.rpc.util.exception.JAXRPCExceptionBase
 *
 * @author JAX-RPC Development Team
 */
public class XMLWriterException extends JAXRPCExceptionBase {

    public XMLWriterException(String key) {
        super(key);
    }

    public XMLWriterException(String key, String arg) {
        super(key, arg);
    }

    public XMLWriterException(String key, Object[] args) {
        super(key, args);
    }

    public XMLWriterException(String key, Localizable arg) {
        super(key, arg);
    }

    public XMLWriterException(Localizable arg) {
        super("xmlwriter.nestedError", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.streaming";
    }
}
