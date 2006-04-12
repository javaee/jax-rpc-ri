/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.client.dii.webservice.parser;

import javax.xml.namespace.QName;

/**
 * @author JAX-RPC Development Team
 */
public interface Constants {
    public static final String NS_NAME =
        "http://java.sun.com/xml/ns/jax-rpc/ri/client";

    public static final QName QNAME_CLIENT =
        new QName(NS_NAME, "webServicesClient");
    public static final QName QNAME_SERVICE = new QName(NS_NAME, "service");

    public static final QName QNAME_PROPERTY = new QName(NS_NAME, "property");

    public static final String ATTR_WSDL_LOCATION = "wsdlLocation";
    public static final String ATTR_MODEL = "model";

    public static final String ATTR_NAME = "name";
    public static final String ATTR_VALUE = "value";

    public static final String ATTRVALUE_VERSION_1_0 = "1.0";
    public static final String ATTRVALUE_CLIENT = "client";
    public static final String ATTRVALUE_SERVER = "server";

}
