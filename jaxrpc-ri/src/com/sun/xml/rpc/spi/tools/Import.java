/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.tools;

/**
 * This class is implemented by 
 * com.sun.xml.rpc.wsdl.document.Import
 */
public interface Import {
    public String getNamespace();
    public String getLocation();
}
