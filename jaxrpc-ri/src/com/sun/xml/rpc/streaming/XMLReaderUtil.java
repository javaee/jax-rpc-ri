/*
 * $Id: XMLReaderUtil.java,v 1.1 2006-04-12 20:32:48 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.streaming;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.util.xml.XmlUtil;

/**
 * <p> XMLReaderUtil provides some utility methods intended to be used
 * in conjunction with a XMLReader. </p>
 *
 * @see XMLReader
 *
 * @author JAX-RPC Development Team
 */
public class XMLReaderUtil {

    private XMLReaderUtil() {
    }

    // sample method signature:
    // public static void foo(XMLReader reader, args...);
    //

    public static QName getQNameValue(XMLReader reader, QName attributeName) {
        String attribute = reader.getAttributes().getValue(attributeName);
        return attribute == null ? null : decodeQName(reader, attribute);
    }

    public static QName decodeQName(XMLReader reader, String rawName) {
        // NOTE: Here it is assumed that we do not want to use default namespace
        // declarations and therefore a null prefix means "no namespace" and
        // not "default namespace"

        String prefix = XmlUtil.getPrefix(rawName);
        String local = XmlUtil.getLocalPart(rawName);
        String uri = ((prefix == null) ? null : reader.getURI(prefix));
        return new QName(uri, local);
    }

    public static void verifyReaderState(XMLReader reader, int expectedState) {
        if (reader.getState() != expectedState) {
            throw new XMLReaderException(
                "xmlreader.unexpectedState",
                new Object[] {
                    getStateName(expectedState),
                    getLongStateName(reader)});
        }
    }

    public static String getStateName(XMLReader reader) {
        return getStateName(reader.getState());
    }
    public static String getLongStateName(XMLReader reader) {
        int state = reader.getState();
        String name = getStateName(state);
        if (state == XMLReader.START || state == XMLReader.START) {
            name += ": " + reader.getName();
        }
        return name;
    }
    public static String getStateName(int state) {
        switch (state) {
            case XMLReader.BOF :
                return "BOF";
            case XMLReader.START :
                return "START";
            case XMLReader.END :
                return "END";
            case XMLReader.CHARS :
                return "CHARS";
            case XMLReader.PI :
                return "PI";
            case XMLReader.EOF :
                return "EOF";
            default :
                return "UNKNOWN";
        }
    }

}
