/*
 * $Id: XMLReaderUtil.java,v 1.3 2007-07-13 23:36:33 ofung Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
