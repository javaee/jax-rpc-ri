/*
 * $Id: StreamingParserFactoryImpl.java,v 1.1 2006-04-12 20:34:23 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.sp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of the factory class for creating demand-driven parsers.
 *
 * @author Zhenghua Li
 * @author JAX-RPC RI Development Team
 */

public class StreamingParserFactoryImpl extends StreamingParserFactory {

    private boolean validating = false;
    private boolean coalescing = false;
    private boolean namespaceAware = false;

    /**
     * The constructor is made public now to allow access from
     * javax.xml.marshal.
     */
    public StreamingParserFactoryImpl() {
    }

    /**
     * Sets the <i>validating</i> property of this factory.
     *
     * @param   validating
     *          Parsers henceforth created by this factory will perform
     *          validation if, and only if, this parameter is <tt>true</tt>
     *
     * @throws  UnsupportedOperationException
     *          If the parser implementation does not support the requested
     *          value 
     */
    public void setValidating(boolean validating) {
        if (validating) {
            throw new UnsupportedOperationException(
               "Validating parser is not supported");
        } else {
            this.validating = validating;
        }
    }

    /**
     * Returns the <i>validating</i> property of this factory.
     *
     * @return  <tt>true</tt> if, and only if, all parsers henceforth created
     *          by this factory will perform validation
     */
    public boolean isValidating() {
        return this.validating;
    }

    /**
     * Sets the <i>coalescing</i> property of this factory.  If coalescing is
     * enabled then the parser will always coalesce adjacent runs of character
     * data, i.e., the {@link StreamingParser#CHARS} state will never occur
     * more than once in sequence.
     *
     * @param   coalescing
     *          Parsers henceforth created by this factory will coalesce
     *          character data if, and only if, this parameter is <tt>true</tt> 
     *
     * @throws  UnsupportedOperationException
     *          If the parser implementation does not support the requested
     *          value 
     */
    public void setCoalescing(boolean coalescing) {
        this.coalescing = coalescing;
    }

    /**
     * Returns the <i>coalescing</i> property of this factory.
     *
     * @return  <tt>true</tt> if, and only if, all parsers henceforth created
     *          by this factory will coalesce adjacent runs of character data
     */
    public boolean isCoalescing() {
        return this.coalescing;
    }

    /**
     * Sets the <i>namespaceAware</i> property of this factory.
     *
     * @param   namespaceAware
     *          Parsers henceforth created by this factory will support
     *          namespace if, and only if, this parameter is <tt>true</tt> 
     *
     * @throws  UnsupportedOperationException
     *          If the parser implementation does not support the requested
     *          value 
     */
    public void setNamespaceAware(boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
    }

    /**
     * Returns the <i>namespaceAware</i> property of this factory.
     *
     * @return  <tt>true</tt> if, and only if, all parsers henceforth created
     *          by this factory will support namespace
     */
    public boolean isNamespaceAware() {
        return this.namespaceAware;
    }

    /**
     * Creates a new parser instance that reads from the given input stream.
     * No parsing is done by this method; the {@link StreamingParser#parse
     * parse} method of the resulting parser must be invoked to parse the
     * initial component of the input document.
     *
     * @param   in
     *          The input stream from which the XML document will be read
     */
    public StreamingParser newParser(InputStream in) {
        return new com.sun.xml.rpc.sp.StreamingParserImpl(this, in);
    }

    /**
     * Creates a new demand-driven parser that reads from the given file.  No
     * parsing is done by this constructor; the {@link StreamingParser#parse
     * parse} method must be invoked to parse the initial component of the
     * given document.
     *
     * @param   file
     *          The file from which the XML document will be read
     *
     * @throws  IOException
     *          If an I/O error occurs
     */
    public StreamingParser newParser(File file) throws IOException {
        return new com.sun.xml.rpc.sp.StreamingParserImpl(this, file);
    }

}
