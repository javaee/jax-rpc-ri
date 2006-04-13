/*
 * $Id: StreamingException.java,v 1.2 2006-04-13 01:33:17 ofung Exp $
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

package com.sun.xml.rpc.streaming;

import java.io.IOException;

import com.sun.xml.rpc.sp.ParseException;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 *
 * @author JAX-RPC Development Team
 */

public class StreamingException extends JAXRPCExceptionBase {

    public StreamingException(IOException e) {
        this("streaming.ioException", e.toString());
    }

    public StreamingException(ParseException e) {
        this("streaming.parseException", e.toString());
    }

    public StreamingException(String key) {
        super(key);
    }

    public StreamingException(String key, String arg) {
        super(key, arg);
    }

    public StreamingException(String key, Localizable localizable) {
        super(key, localizable);
    }

    public StreamingException(String key, Object[] args) {
        super(key, args);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.streaming";
    }
}
