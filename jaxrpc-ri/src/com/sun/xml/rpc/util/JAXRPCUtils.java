/*
 * $Id: JAXRPCUtils.java,v 1.1 2006-04-12 20:32:05 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.util;

/**
 * @author Vivek Pandey
 *
 * Wrapper utility class to be used from the generated code or run time.
 */
public final class JAXRPCUtils {
    public static String getUUID(){        
         return com.sun.xml.rpc.util.UUID.randomUUID().toString();
    }
}
