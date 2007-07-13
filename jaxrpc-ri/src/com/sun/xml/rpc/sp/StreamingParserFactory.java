/*
 * $Id: StreamingParserFactory.java,v 1.3 2007-07-13 23:36:28 ofung Exp $
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
