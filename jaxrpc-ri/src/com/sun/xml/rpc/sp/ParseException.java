/*
 * $Id: ParseException.java,v 1.2 2006-04-13 01:32:35 ofung Exp $
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

package com.sun.xml.rpc.sp;

/**
 *
 * @author JAX-RPC RI Development Team
 */
public class ParseException extends Exception {

    private String publicId;
    private String systemId;
    private int line;
    private int col;

    public ParseException(
        String message,
        String publicId,
        String systemId,
        int line,
        int col) {
        super(message);
        this.publicId = publicId;
        this.systemId = systemId;
        this.line = line;
        this.col = col;
    }

    public ParseException(String message, StreamingParser parser) {
        this(
            message,
            parser.publicId(),
            parser.systemId(),
            parser.line(),
            parser.column());
    }

    public ParseException(String message, String publicId, String systemId) {
        this(message, publicId, systemId, -1, -1);
    }

    public ParseException(String message) {
        this(message, null, null, -1, -1);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getName());
        if (publicId != null)
            sb.append(": " + publicId);
        if (systemId != null)
            sb.append(": " + systemId);
        if (line != -1) {
            sb.append(":" + line);
            if (col != -1)
                sb.append("," + col);
        }
        if (getMessage() != null)
            sb.append(": " + getMessage());
        return sb.toString();
    }

}
