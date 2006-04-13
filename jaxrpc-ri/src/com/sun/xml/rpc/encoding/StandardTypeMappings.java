/*
 * $Id: StandardTypeMappings.java,v 1.2 2006-04-13 01:27:26 ofung Exp $
 */

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
