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

import java.io.IOException;

/**
 * A simple demand-driven XML parser interface.
 *
 * <p> There are no public constructors for this class.  New instances are
 * created by first creating and configuring, if necessary, an instance of the
 * {@link StreamingParserFactory} class.
 *
 * <p> After each invocation of the {@link #parse parse} method an instance of
 * this class will be in one of the following states.  In each state the {@link
 * #name name}, {@link #value value}, and {@link #uriString uriString}
 * methods may be invoked to return the data specified in the following table.
 * If no data is specified for a given method and state then invoking that
 * method in that state will cause an {@link java.lang.IllegalStateException
 * IllegalStateException} to be thrown.
 *
 * <blockquote>
 * <table cellspacing=3>
 * <tr><td valign=top><i>state</i>&nbsp;&nbsp;&nbsp;&nbsp;</td>
 *     <td valign=top><i>description</i>&nbsp;&nbsp;&nbsp;&nbsp;</td>
 *     <td valign=top>{@link #name}&nbsp;&nbsp;&nbsp;&nbsp;</td>
 *     <td valign=top>{@link #value}&nbsp;&nbsp;&nbsp;&nbsp;</td>
 *     <td valign=top>{@link #uriString}</td></tr>
 * <tr><td valign=top>START&nbsp;&nbsp;&nbsp;&nbsp;</td>
 *     <td valign=top>Start tag&nbsp;&nbsp;&nbsp;&nbsp;</td>
 *     <td valign=top>Element name&nbsp;&nbsp;&nbsp;&nbsp;</td>
 *     <td valign=top></td>
 *     <td valign=top>Element namespace</td>
 *     </tr>
 * <tr><td valign=top>END&nbsp;&nbsp;&nbsp;&nbsp;</td>
 *     <td valign=top>End tag&nbsp;&nbsp;&nbsp;&nbsp;</td>
 *     <td valign=top>Element name&nbsp;&nbsp;&nbsp;&nbsp;</td>
 *     <td valign=top></td>
 *     <td valign=top>Element namespace</td>
 *     </tr>
 * <tr><td valign=top>ATTR&nbsp;&nbsp;&nbsp;&nbsp;</td>
 *     <td valign=top>Attribute&nbsp;&nbsp;&nbsp;&nbsp;</td>
 *     <td valign=top>Name&nbsp;&nbsp;&nbsp;&nbsp;</td>
 *     <td valign=top>Value&nbsp;&nbsp;&nbsp;&nbsp;</td>
 *     <td valign=top>Attribute namespace</td>
 *     </tr>
 * <tr><td valign=top>CHARS&nbsp;&nbsp;&nbsp;&nbsp;</td>
 *     <td valign=top>Character data&nbsp;&nbsp;&nbsp;&nbsp;</td>
 *     <td valign=top></td>
 *     <td valign=top>Data&nbsp;&nbsp;&nbsp;&nbsp;</td>
 *     </tr>
 * <tr><td valign=top>IWS</td>
 *     <td valign=top>Ignorable whitespace&nbsp;&nbsp;&nbsp;&nbsp;</td>
 *     <td valign=top></td>
 *     <td valign=top>Whitespace</td>
 *     </tr>
 * <tr><td valign=top>PI&nbsp;&nbsp;&nbsp;&nbsp;</td>
 *     <td valign=top>Processing instruction&nbsp;&nbsp;&nbsp;&nbsp;</td>
 *     <td valign=top>Target name&nbsp;&nbsp;&nbsp;&nbsp;</td>
 *     <td valign=top>Content</td>
 *     </tr>
 * </table>
 * </blockquote>
 *
 * If a start tag has any attributes then the {@link #START} state for that tag
 * will immediately be followed by a sequence of {@link #ATTR} states, one per
 * attribute.  The attributes are parsed in the order in which they appear in
 * the document.
 *
 * <p> An empty tag, that is, a tag of the form <tt>&lt;foo/&gt;</tt>, will
 * yield a {@link #START} state and then an {@link #END} state, possibly with
 * some {@link #ATTR} states in between.
 *
 * <p> If the parser is namespace-aware then the {@link #uriString uriString}
 * method may be invoked in the {@link #START}, {@link #END}, and {@link #ATTR}
 * states to return the namespace in which the element or attribute is defined.
 * Otherwise, the {@link #uriString uriString} method just returns
 * <tt>null</tt> at those states.
 *
 * <p> Note that because detecting ignorable whitespace requires a DTD, the
 * {@link #IWS} state will be entered only when validation is being performed.
 *
 * <p> There is otherwise no restriction on the allowable state transitions
 * other than those induced by the structure of the XML document being parsed.
 *
 * @author Mark Reinhold
 * @author JAX-RPC RI Development Team
 */

public abstract class StreamingParser {

    /**
     * State value indicating that a start tag has been parsed.
     * The {@link #name} method will return the name of the element just
     * started.
     */
    public static final int START = 0;

    /**
     * State value indicating that an end tag has been parsed.
     * The {@link #name} method will return the name of the element just
     * ended.
     */
    public static final int END = 1;

    /**
     * State value indicating that an attribute has been parsed.
     * The {@link #name} method will return the name of the attribute,
     * while the {@link #value} method will return its value.
     */
    public static final int ATTR = 2;

    /**
     * State value indicating that character data has been parsed.
     * The {@link #value} method will return the parsed characters.
     */
    public static final int CHARS = 3;

    /**
     * State value indicating that ignorable whitespace has been parsed.
     * The {@link #value} method will return the whitespace.
     */
    public static final int IWS = 4;

    /**
     * State value indicating that a processing instruction has been parsed.
     * The {@link #name} method will return the target of the instruction,
     * while the {@link #value} method will return its content.
     */
    public static final int PI = 5;

    /**
     * Construct a new <tt>StreamingParser</tt>.
     */
    protected StreamingParser() {
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
    public abstract int parse() throws ParseException, IOException;

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
    public abstract int state();

    /**
     * Returns a name string whose meaning depends upon the current state.
     *
     * @throws  IllegalStateException
     *          If there is no name data for the current parser state
     */
    public abstract String name();

    /**
     * Returns a value string whose meaning depends upon the current state.
     *
     * @throws  IllegalStateException
     *          If there is no value data for the current parser state
     */
    public abstract String value();

    /**
     * Returns the URI string of the current component.
     *
     * @throws  IllegalStateException
     *          If there is no URI for the current component
     */
    public abstract String uriString();

    /**
     * Returns the line number of the current component,
     * or <tt>-1</tt> if the line number is not known.
     */
    public abstract int line();

    /**
     * Returns the column number of the current component,
     * or <tt>-1</tt> if the column number is not known.
     */
    public abstract int column();

    /**
     * Returns the public identifer of the document being parsed,
     * or <tt>null</tt> if it has none.
     */
    public abstract String publicId();

    /**
     * Returns the system identifer of the document being parsed,
     * or <tt>null</tt> if it has none.
     */
    public abstract String systemId();

    /**
     * Returns the <i>validating</i> property of this parser.
     *
     * @return  <tt>true</tt> if, and only if, this parser
     *          will perform validation
     */
    public abstract boolean isValidating();

    /**
     * Returns the <i>coalescing</i> property of this parser.
     *
     * @return  <tt>true</tt> if, and only if, this parser 
     *          will coalesce adjacent runs of character data
     */
    public abstract boolean isCoalescing();

    /**
     * Returns the <i>namespaceAware</i> property of this parser.
     *
     * @return  <tt>true</tt> if, and only if, this parser will
     *          support namespace
     */
    public abstract boolean isNamespaceAware();

    private static void quote(StringBuffer sb, String s, int max) {
        boolean needDots = false;
        int limit = Math.min(s.length(), max);
        if (limit > max - 3) {
            needDots = true;
            limit = max - 3;
        }
        sb.append('"');
        for (int i = 0; i < limit; i++) {
            char c = s.charAt(i);
            if ((c < ' ') || (c > '~')) {
                if (c <= 0xff) {
                    if (c == '\n') {
                        sb.append("\\n");
                        continue;
                    }
                    if (c == '\r') {
                        sb.append("\\r");
                        continue;
                    }
                    sb.append("\\x");
                    if (c < 0x10)
                        sb.append('0');
                    sb.append(Integer.toHexString(c));
                    continue;
                }
                if (c == '"') {
                    sb.append("\\\"");
                    continue;
                }
                sb.append("\\u");
                String n = Integer.toHexString(c);
                for (int j = n.length(); j < 4; j++)
                    sb.append('0');
                sb.append(n);
                continue;
            }
            sb.append(c);
        }
        if (needDots)
            sb.append("...");
        sb.append('"');
    }

    /**
     * Constructs a string describing the given parser state, suitable for use
     * in an error message or an exception detail string.
     *
     * @param   state
     *          A parser state, one of {@link #START}, {@link #END}, {@link
     *          #ATTR}, {@link #CHARS}, {@link #IWS}, or {@link #PI}, or
     *          <tt>-1</tt> if the end of the document has been reached
     *
     * @param   name
     *          The name data for the parser state, or <tt>null</tt> if there
     *          is none
     *
     * @param   value
     *          The value data for the parser state, or <tt>null</tt> if there
     *          is none
     *
     * @param   articleNeeded
     *          Whether an appropriate article ("a", "an", "some", or "the") is
     *          to be prepended to the description string
     *
     * @returns  A string describing the given parser state.
     */
    public static String describe(
        int state,
        String name,
        String value,
        boolean articleNeeded) {
        StringBuffer sb = new StringBuffer();
        switch (state) {
            case START :
                if (articleNeeded)
                    sb.append("a ");
                sb.append("start tag");
                if (name != null)
                    sb.append(" for a \"" + name + "\" element");
                break;
            case END :
                if (articleNeeded)
                    sb.append("an ");
                sb.append("end tag");
                if (name != null)
                    sb.append(" for a \"" + name + "\" element");
                break;
            case ATTR :
                if (name == null) {
                    if (articleNeeded)
                        sb.append("an ");
                    sb.append("attribute");
                } else {
                    if (articleNeeded)
                        sb.append("the ");
                    sb.append("attribute \"" + name + "\"");
                    if (value != null)
                        sb.append(" with value \"" + value + "\"");
                }
                break;
            case CHARS :
                if (articleNeeded)
                    sb.append("some ");
                sb.append("character data");
                if (value != null) {
                    sb.append(": ");
                    quote(sb, value, 40);
                }
                break;
            case IWS :
                if (articleNeeded)
                    sb.append("some ");
                sb.append("ignorable whitespace");
                break;
            case PI :
                if (articleNeeded)
                    sb.append("a ");
                sb.append("processing instruction");
                if (name != null)
                    sb.append(" with target \"" + name + "\"");
                break;
            case -1 :
                if (articleNeeded)
                    sb.append("the ");
                sb.append("end of the document");
                break;
            default :
                throw new InternalError("Unknown parser state");
        }

        return sb.toString();
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
    public abstract String describe(boolean articleNeeded);

    public String toString() {
        StringBuffer sb = new StringBuffer("[StreamingParser");
        if (systemId() != null)
            sb.append(" " + systemId());
        sb.append(": " + describe(false));
        sb.append("]");
        return sb.toString();
    }

}
