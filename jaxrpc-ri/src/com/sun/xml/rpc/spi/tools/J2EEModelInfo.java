/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.tools;

/**
 * This class is implemented by 
 * com.sun.xml.rpc.processor.config.J2EEModelInfo
 */
public interface J2EEModelInfo extends ModelInfo {
    public void setLocation(String s);
    public void setJavaPackageName(String s);
}
