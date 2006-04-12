/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.model;

/**
 * This class is implemented by 
 * com.sun.xml.rpc.processor.model.ModelProperties
 */
public interface ModelProperties {
    public static final String PROPERTY_WSDL_PORT_NAME =
        "com.sun.xml.rpc.processor.model.WSDLPortName";
    public static final String PROPERTY_WSDL_PORT_TYPE_NAME =
        "com.sun.xml.rpc.processor.model.WSDLPortTypeName";
    public static final String PROPERTY_WSDL_BINDING_NAME =
        "com.sun.xml.rpc.processor.model.WSDLBindingName";
    public static final String PROPERTY_WSDL_MESSAGE_NAME =
        "com.sun.xml.rpc.processor.model.WSDLMessageName";
    public static final String PROPERTY_MODELER_NAME =
        "com.sun.xml.rpc.processor.model.ModelerName";
    public static final String PROPERTY_STUB_CLASS_NAME =
        "com.sun.xml.rpc.processor.model.StubClassName";
    public static final String PROPERTY_TIE_CLASS_NAME =
        "com.sun.xml.rpc.processor.model.TieClassName";
    public static final String PROPERTY_JAVA_PORT_NAME =
        "com.sun.xml.rpc.processor.model.JavaPortName";
}
