/*
 * $Id: XMLWriterUtil.java,v 1.1 2006-04-12 20:32:49 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.streaming;

import javax.xml.namespace.QName;

/**
 * <p> XMLWriterUtil provides some utility methods intended to be used
 * in conjunction with a XMLWriter. </p>
 *
 * @see XMLWriter
 *
 * @author JAX-RPC Development Team
 */
public class XMLWriterUtil {

    private XMLWriterUtil() {
    }

    // sample method signature:
    // public static void foo(XMLWriter writer, args...);
    //

    public static String encodeQName(XMLWriter writer, QName qname) {
        // NOTE: Here it is assumed that we do not serialize using default
        // namespace declarations and therefore that writer.getPrefix will
        // never return ""

        String namespaceURI = qname.getNamespaceURI();
        String localPart = qname.getLocalPart();

        if (namespaceURI == null || namespaceURI.equals("")) {
            return localPart;
        } else {
            String prefix = writer.getPrefix(namespaceURI);
            if (prefix == null) {
                writer.writeNamespaceDeclaration(namespaceURI);
                prefix = writer.getPrefix(namespaceURI);
            }
            return prefix + ":" + localPart;
        }
    }
}
