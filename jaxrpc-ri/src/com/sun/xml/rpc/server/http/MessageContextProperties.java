/*
 * $Id: MessageContextProperties.java,v 1.1 2006-04-12 20:33:40 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.server.http;

/**
 *
 * @author JAX-RPC Development Team
 */
public interface MessageContextProperties {

    public static final String SERVLET_CONTEXT =
        "com.sun.xml.rpc.server.http.ServletContext";
    public static final String HTTP_SERVLET_REQUEST =
        "com.sun.xml.rpc.server.http.HttpServletRequest";
    public static final String HTTP_SERVLET_RESPONSE =
        "com.sun.xml.rpc.server.http.HttpServletResponse";
    public static final String IMPLEMENTOR =
        "com.sun.xml.rpc.server.http.Implementor";
    public static final String ONE_WAY_OPERATION =
        "com.sun.xml.rpc.server.OneWayOperation";
    public static final String CLIENT_BAD_REQUEST =
        "com.sun.xml.rpc.server.http.ClientBadRequest";
    public static final String CLIENT_INVALID_CONTENT_TYPE =
        "com.sun.xml.rpc.server.http.ClientInvalidContentType";        
}
