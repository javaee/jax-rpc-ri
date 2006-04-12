/*
 * $Id: StreamingParserFactory.java,v 1.1 2006-04-12 20:34:23 kohlert Exp $
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
 * Factory class for creating demand-driven parsers.
 *
 * In typical use an instance of this class is created and configured, and then 
 * it is used to create parser instances as required, like so:
 *
 * <pre>
 *     StreamingParserFactory spf
 *         = StreamingParserFactory.newInstance();
 *     pf.setValidating(true);
 *     pf.setCoalescing(false);
 *     StreamingParser sp = spf.newParser(in);</pre>
 *
 * @author Mark Reinhold
 * @author JAX-RPC RI Development Team
 */

public abstract class StreamingParserFactory {

    protected StreamingParserFactory() {
    }

    /**
     * Creates a new factory of demand-driven parsers.
     */
    public static StreamingParserFactory newInstance() {
        return new com.sun.xml.rpc.sp.StreamingParserFactoryImpl();
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
    public abstract void setValidating(boolean validating);

    /**
     * Returns the <i>validating</i> property of this factory.
     *
     * @return  <tt>true</tt> if, and only if, all parsers henceforth created
     *          by this factory will perform validation
     */
    public abstract boolean isValidating();

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
    public abstract void setCoalescing(boolean coalescing);

    /**
     * Returns the <i>coalescing</i> property of this factory.
     *
     * @return  <tt>true</tt> if, and only if, all parsers henceforth created
     *          by this factory will coalesce adjacent runs of character data
     */
    public abstract boolean isCoalescing();

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
    public abstract void setNamespaceAware(boolean namespaceAware);

    /**
     * Returns the <i>namespaceAware</i> property of this factory.
     *
     * @return  <tt>true</tt> if, and only if, all parsers henceforth created
     *          by this factory will support namespace
     */
    public abstract boolean isNamespaceAware();

    /**
     * Creates a new parser instance that reads from the given input stream.
     * No parsing is done by this method; the {@link StreamingParser#parse
     * parse} method of the resulting parser must be invoked to parse the
     * initial component of the input document.
     *
     * @param   in
     *          The input stream from which the XML document will be read
     */
    public abstract StreamingParser newParser(InputStream in);

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
    public abstract StreamingParser newParser(File file) throws IOException;

}
