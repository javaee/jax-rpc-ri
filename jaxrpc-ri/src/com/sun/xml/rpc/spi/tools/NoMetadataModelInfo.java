/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.tools;

import javax.xml.namespace.QName;

/**
 * This class is implemented by 
 * com.sun.xml.rpc.processor.config.NoMetadataModelInfo
 */
public interface NoMetadataModelInfo extends ModelInfo {
    public void setLocation(String s);
    public void setInterfaceName(String s);
    public void setPortName(QName n);
}
