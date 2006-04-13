/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
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
