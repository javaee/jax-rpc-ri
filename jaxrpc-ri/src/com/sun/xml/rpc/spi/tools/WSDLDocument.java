/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.tools;

import javax.xml.namespace.QName;

/**
 * This interface is implemented by 
 * com.sun.xml.rpc.wsdl.document.WSDLDocument
 * <p>
 * The implementation of this interface will provide information
 * on an wsdl file (including all the imported wsdl files).
 */
public interface WSDLDocument {

    public QName[] getAllServiceQNames();

    public QName[] getAllPortQNames();

    public QName[] getPortQNames(String serviceNameLocalPart);

}
