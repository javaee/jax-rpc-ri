/*
 * $Id: StandardTypeMappings.java,v 1.1 2006-04-12 20:33:09 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/**
 * <p> Singletons for TypeMappings that contain standard (de)serializers for
 * SOAP and Literal encoding. </p>
 *
 * @author JAX-RPC Development Team
 */

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.encoding.literal.StandardLiteralTypeMappings;
import com.sun.xml.rpc.encoding.soap.StandardSOAPTypeMappings;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

public class StandardTypeMappings {

    public static ExtendedTypeMapping getSoap() {
        return getSoap(SOAPVersion.SOAP_11);
    }

    public static ExtendedTypeMapping getSoap(SOAPVersion ver) {
        try {
            return new StandardSOAPTypeMappings(ver);
        } catch (Exception e) {
            throw new TypeMappingException(
                "typemapping.nested.exception.static.initialization",
                new LocalizableExceptionAdapter(e));
        }
    }

    public static ExtendedTypeMapping getLiteral() {
        try {
            return new TypeMappingImpl();
        } catch (Exception e) {
            throw new TypeMappingException(
                "typemapping.nested.exception.static.initialization",
                new LocalizableExceptionAdapter(e));
        }
    }

    public static ExtendedTypeMapping getRPCLiteral() {
        try {
            return new StandardLiteralTypeMappings();
        } catch (Exception e) {
            throw new TypeMappingException(
                "typemapping.nested.exception.static.initialization",
                new LocalizableExceptionAdapter(e));
        }
    }

}
