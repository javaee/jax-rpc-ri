/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.runtime;

import java.lang.reflect.Method;

import javax.xml.soap.SOAPMessage;

/**
 * This class is implemented by 
 * com.sun.xml.rpc.server.StreamingHandler
 */
public interface StreamingHandler {
    public int getOpcodeForRequestMessage(SOAPMessage request);
    public Method getMethodForOpcode(int opcode)
        throws ClassNotFoundException, NoSuchMethodException;
}
