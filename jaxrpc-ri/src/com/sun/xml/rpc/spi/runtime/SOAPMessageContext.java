/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.runtime;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;

/**
 * This class is implemented by 
 * com.sun.xml.rpc.soap.message.SOAPMessageContext
 */
public interface SOAPMessageContext
    extends javax.xml.rpc.handler.soap.SOAPMessageContext {

    public SOAPMessage createMessage(MimeHeaders headers, InputStream in)
        throws IOException;
    public void writeInternalServerErrorResponse();
    public void writeSimpleErrorResponse(QName faultCode, String faultString);
    public boolean isFailure();
}
