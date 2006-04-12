/*
 * $Id: StreamingException.java,v 1.1 2006-04-12 20:32:46 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.streaming;

import java.io.IOException;

import com.sun.xml.rpc.sp.ParseException;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 *
 * @author JAX-RPC Development Team
 */

public class StreamingException extends JAXRPCExceptionBase {

    public StreamingException(IOException e) {
        this("streaming.ioException", e.toString());
    }

    public StreamingException(ParseException e) {
        this("streaming.parseException", e.toString());
    }

    public StreamingException(String key) {
        super(key);
    }

    public StreamingException(String key, String arg) {
        super(key, arg);
    }

    public StreamingException(String key, Localizable localizable) {
        super(key, localizable);
    }

    public StreamingException(String key, Object[] args) {
        super(key, args);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.streaming";
    }
}
