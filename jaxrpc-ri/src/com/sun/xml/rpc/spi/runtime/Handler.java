/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.runtime;

/**
 * This class is implemented by 
 * com.sun.xml.rpc.soap.message.Handler
 */
public interface Handler {
    public void handle(SOAPMessageContext context) throws Exception;
}
