/*
 * $Id: SOAPResponseStructure.java,v 1.1 2006-04-12 20:34:06 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding.soap;

import java.util.Map;

import com.sun.xml.rpc.util.StructMap;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SOAPResponseStructure {
    public Object returnValue;
    public Map outParameters = new StructMap();
    public Map outParametersStringKeys = new StructMap();
}