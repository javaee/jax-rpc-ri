/*
 * $Id: Constants.java,v 1.1 2006-04-12 20:34:15 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.tools.wsdeploy;

import javax.xml.namespace.QName;

/**
 *
 * @author JAX-RPC Development Team
 */
public interface Constants {
    public static final String NS_DD = "http://java.sun.com/xml/ns/jax-rpc/ri/dd";
    
    public static final QName QNAME_WEB_SERVICES            = new QName(NS_DD, "webServices");
    public static final QName QNAME_ENDPOINT                = new QName(NS_DD, "endpoint");
    public static final QName QNAME_HANDLER_CHAINS          = new QName(NS_DD, "handlerChains");
    public static final QName QNAME_CHAIN                   = new QName(NS_DD, "chain");
    public static final QName QNAME_HANDLER                 = new QName(NS_DD, "handler");
    public static final QName QNAME_PROPERTY                = new QName(NS_DD, "property");
    public static final QName QNAME_ENDPOINT_MAPPING        = new QName(NS_DD, "endpointMapping");
    public static final QName QNAME_ENDPOINT_CLIENT_MAPPING = new QName(NS_DD, "client");
    
    public static final String ATTR_SERVICE               = "service";
    public static final String ATTR_VERSION               = "version";
    public static final String ATTR_TARGET_NAMESPACE_BASE = "targetNamespaceBase";
    public static final String ATTR_TYPE_NAMESPACE_BASE   = "typeNamespaceBase";
    public static final String ATTR_URL_PATTERN_BASE      = "urlPatternBase";
    public static final String ATTR_NAME                  = "name";
    public static final String ATTR_DISPLAY_NAME          = "displayName";
    public static final String ATTR_DESCRIPTION           = "description";
    public static final String ATTR_INTERFACE             = "interface";
    public static final String ATTR_IMPLEMENTATION        = "implementation";
    public static final String ATTR_PORT                  = "port";
    public static final String ATTR_WSDL                  = "wsdl";
    public static final String ATTR_MODEL                 = "model";
    public static final String ATTR_ENDPOINT_NAME         = "endpointName";
    public static final String ATTR_URL_PATTERN           = "urlPattern";
    public static final String ATTR_RUN_AT                = "runAt";
    public static final String ATTR_ROLES                 = "roles";
    public static final String ATTR_CLASS_NAME            = "className";
    public static final String ATTR_HEADERS               = "headers";
    public static final String ATTR_VALUE                 = "value";
    
    public static final String ATTRVALUE_VERSION_1_0 = "1.0";
    public static final String ATTRVALUE_CLIENT      = "client";
    public static final String ATTRVALUE_SERVER      = "server";
    public static final int    ENDPOINTLIST_INDEX    = 0;
}
