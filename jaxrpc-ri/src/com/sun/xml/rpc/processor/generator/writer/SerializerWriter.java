/*
 * $Id: SerializerWriter.java,v 1.1 2006-04-12 20:35:10 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
