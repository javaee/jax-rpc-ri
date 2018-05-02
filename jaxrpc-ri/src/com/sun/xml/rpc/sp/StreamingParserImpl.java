/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2018 Sun Microsystems, Inc. All rights reserved.
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
 * A demand-driven streaming parser implementation.
 *
 * @author Zhenghua Li
 * @author JAX-RPC RI Development Team
 */

public final class StreamingParserImpl extends StreamingParser {

    private Parser parser = null;

    private static final int DOC_END = -1,
        DOC_START = -2,
        EMPTY = -3,
        EXCEPTION = -4;

    private int cur = EMPTY;
    private String curName = null;
    private String curValue = null;
    private String curURI = null;

    private boolean validating;
    private boolean coalescing;
    private boolean namespaceAware;

    private int curLine = -1;
    private int curCol = -1;

    /* Set once by DocHandler at startup */
    private String publicId = null;
    private String systemId = null;

    /* -- Constructors -- */

    private StreamingParserImpl(StreamingParserFactory pf) {
        this.validating = pf.isValidating();
        this.coalescing = pf.isCoalescing();
        this.namespaceAware = pf.isNamespaceAware();
    }

    StreamingParserImpl(StreamingParserFactory pf, InputStream in) {
        this(pf);
        this.parser = new Parser(in, coalescing, namespaceAware);
    }

    StreamingParserImpl(StreamingParserFactory pf, File file)
        throws IOException {
        this(pf);
        this.parser = new Parser(file, coalescing, namespaceAware);
    }

    /* -- Methods -- */

    /**
     * Parses the next component of the document being parsed.
     *
     * @return  The parser's current state, one of {@link #START},
     *          {@link #END}, {@link #ATTR}, {@link #CHARS}, {@link #IWS},
     *          or {@link #PI}, or <tt>-1</tt> if the end of the document has
     *          been reached
     *
     * @throws ParseException
     *         If an XML parsing error occurs
     *
     * @throws IOException
     *         If an I/O error occurs
     */
    public int parse() throws ParseException, IOException {
        if (cur == DOC_END) {
            return -1;
        } else {
            cur = parser.parse();
            curName = parser.getCurName();
            curValue = parser.getCurValue();
            curURI = parser.getCurURI();
            curLine = parser.getLineNumber();
            curCol = parser.getColumnNumber();
            return cur;
        }
    }

    /**
     * Returns the current state of the parser.
     *
     * @return  The parser's current state, one of {@link #START},
     *          {@link #END}, {@link #ATTR}, {@link #CHARS}, {@link #IWS},
     *          or {@link #PI}, or <tt>-1</tt> if the end of the document has
     *          been reached.
     *
     * @throws  IllegalStateException
     *          If the parser has yet not been started by invoking the
     *          {@link #parse} method
     */
    public int state() {
        if (cur == EMPTY)
            throw new IllegalStateException("Parser not started");
        if (cur < DOC_END)
            throw new InternalError();
        return cur;
    }

    /**
     * Returns a name string whose meaning depends upon the current state.
     *
     * @throws  IllegalStateException
     *          If there is no name data for the current parser state
     */
    public String name() {
        if (curName == null)
            throw new IllegalStateException("Name not defined in this state");
        return curName;
    }

    /**
     * Returns a value string whose meaning depends upon the current state.
     *
     * @throws  IllegalStateException
     *          If there is no value data for the current parser state
     */
    public String value() {
        if (curValue == null)
            throw new IllegalStateException("Value not defined in this state");
        return curValue;
    }

    /**
     * Returns the URI string of the current component.
     *
     * @throws  IllegalStateException
     *          If there is no URI for the current component
     */
    public String uriString() {
        if (!namespaceAware) {
            return null;
        } else if (curURI == null) {
            throw new IllegalStateException("Value not defined in this state");
        }
        return curURI;
    }

    /**
     * Returns the line number of the current component,
     * or <tt>-1</tt> if the line number is not known.
     */
    public int line() {
        return curLine;
    }

    /**
     * Returns the column number of the current component,
     * or <tt>-1</tt> if the column number is not known.
     */
    public int column() {
        return curCol;
    }

    /**
     * Returns the public identifer of the document being parsed,
     * or <tt>null</tt> if it has none.
     */
    public String publicId() {
        return publicId;
    }

    /**
     * Returns the system identifer of the document being parsed,
     * or <tt>null</tt> if it has none.
     */
    public String systemId() {
        return systemId;
    }

    /**
     * Returns the <i>validating</i> property of this parser.
     *
     * @return  <tt>true</tt> if, and only if, this parser
     *          will perform validation
     */
    public boolean isValidating() {
        return this.validating;
    }

    /**
     * Returns the <i>coalescing</i> property of this parser.
     *
     * @return  <tt>true</tt> if, and only if, this parser 
     *          will coalesce adjacent runs of character data
     */
    public boolean isCoalescing() {
        return this.coalescing;
    }

    /**
     * Returns the <i>namespaceAware</i> property of this parser.
     *
     * @return  <tt>true</tt> if, and only if, this parser will
     *          support namespace
     */
    public boolean isNamespaceAware() {
        return this.namespaceAware;
    }

    /**
     * Constructs a string describing the current state of this parser,
     * suitable for use in an error message or an exception detail string.
     *
     * @param   articleNeeded
     *          Whether an appropriate article ("a", "an", "some", or "the") is
     *          to be prepended to the description string
     *
     * @returns  A string describing the given parser state.
     */
    public String describe(boolean articleNeeded) {
        return describe(cur, curName, curValue, articleNeeded);
    }

}
