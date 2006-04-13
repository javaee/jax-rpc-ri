/*
 * $Id: SerializerWriter.java,v 1.2 2006-04-13 01:29:18 ofung Exp $
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

/*
 * SerializerWriter.java
 *
 * Created on January 11, 2002, 12:58 PM
 */

package com.sun.xml.rpc.processor.generator.writer;

import java.io.IOException;

import com.sun.xml.rpc.processor.util.IndentingWriter;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract interface SerializerWriter {
    public void createSerializer(
        IndentingWriter p,
        StringBuffer typeName,
        String serName,
        boolean encodeTypes,
        boolean multiRefEncoding,
        String typeMapping)
        throws IOException;
    public void registerSerializer(
        IndentingWriter p,
        boolean encodeTypes,
        boolean multiRefEncoding,
        String typeMapping)
        throws IOException;
    public void declareSerializer(
        IndentingWriter p,
        boolean isStatic,
        boolean isFinal)
        throws IOException;
    public void initializeSerializer(
        IndentingWriter p,
        String typeName,
        String registry)
        throws IOException;
    public String serializerName();
    public String serializerMemberName();
    public String deserializerName();
    public String deserializerMemberName();
}
