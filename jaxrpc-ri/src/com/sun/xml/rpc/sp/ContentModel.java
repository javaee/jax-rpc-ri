/*
 * $Id: ContentModel.java,v 1.2 2006-04-13 01:32:29 ofung Exp $
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
 * A representation of a "children" content model.  These are basically a
 * regular expression; other content models are simpler.  There is an
 * SGML compatibility restriction on DTDs that such content models be
 * deterministic, which in this sense just means that backtracking isn't
 * needed to validate against it.
 *
 * <P> At the moment, for expediency, nondeterministic models are neither
 * tested for nor are they handled reasonably.  This could be done after
 * each element's content model is fully parsed.
 *
 * <P> The most efficient way to do this would be to compile each content
 * model pattern into a deterministic finite automaton (no stack) and
 * just walk the DFA's graph ... but for now, these aren't compiled.
 *
 * @author Arthur van Hoff
 * @author David Brownell
 * @author JAX-RPC RI Development Team
 */
final class ContentModel {
    /**
     * Type. Either '*', '?', '+'; or connectives ',', '|'; or
     * zero for content that's an element.
     */
    public char type;

    /**
     * The content. Either an Element name, or a ContentModel.
     */
    public Object content;

    /**
     * The next content model (in a ',' or '|' connective expression).
     * "next" has a list of connectives of the same type.
     */
    public ContentModel next;

    //
    // Cache mapping element names --> TRUE or FALSE based on whether
    // they can be 'first' in this content model or not.  NOTE:  it'd
    // be nice to have a lower cost cache, e.g. numbering elements and
    // using byte arrays.
    //
    private SimpleHashtable cache = new SimpleHashtable();

    /**
     * Create a content model for an element.
     */
    public ContentModel(String element) {
        this.type = 0;
        this.content = element;
    }

    /**
     * Create a content model of a particular type.
     * Normally used to specify a frequency, or to start a connective.
     */
    public ContentModel(char type, ContentModel content) {
        this.type = type;
        this.content = content;
    }

    /**
     * Return true if the content model could
     * match an empty input stream.
     */
    public boolean empty() {
        // if it matters, this could cache as a simple boolean!

        switch (type) {
            case '*' :
            case '?' :
                return true;

            case '+' :
            case 0 :
                return false;

            case '|' :
                if (content instanceof ContentModel
                    && ((ContentModel) content).empty()) {
                    return true;
                }
                for (ContentModel m = (ContentModel) next;
                    m != null;
                    m = m.next) {
                    if (m.empty())
                        return true;
                }
                return false;

            case ',' :
                if (content instanceof ContentModel) {
                    if (!((ContentModel) content).empty()) {
                        return false;
                    }
                } else {
                    return false;
                }
                for (ContentModel m = (ContentModel) next;
                    m != null;
                    m = m.next) {
                    if (!m.empty())
                        return false;
                }
                return true;

            default :
                throw new InternalError();
        }
    }

    /**
     * Return true if the token could potentially be the
     * first token in the input stream.
     */
    public boolean first(String token) {
        Boolean b = (Boolean) cache.get(token);
        boolean retval;

        if (b != null)
            return b.booleanValue();

        // if we had no cached result, compute it
        switch (type) {
            case '*' :
            case '?' :
            case '+' :
            case 0 :
                if (content instanceof String)
                    retval = (content == token);
                else
                    retval = ((ContentModel) content).first(token);
                break;

            case ',' :
                if (content instanceof String)
                    retval = (content == token);
                else if (((ContentModel) content).first(token))
                    retval = true;
                else if (!((ContentModel) content).empty())
                    retval = false;
                else if (next != null)
                    retval = ((ContentModel) next).first(token);
                else
                    retval = false;
                break;

            case '|' :
                if (content instanceof String && content == token)
                    retval = true;
                else if (((ContentModel) content).first(token))
                    retval = true;
                else if (next != null)
                    retval = ((ContentModel) next).first(token);
                else
                    retval = false;
                break;

            default :
                throw new InternalError();
        }

        // store the result, so we can be faster next time
        if (retval)
            cache.put(token, Boolean.TRUE);
        else
            cache.put(token, Boolean.FALSE);

        return retval;
    }

    /**
     * Convert to a string (for debugging).
     *
    public String toString() {
        return toString(true);
    }

    private String contentString() {
        if (content instanceof ContentModel)
            return ((ContentModel) content).toString(false);
        else
            return (String) content;
    }

    private String toString(boolean isOuter) {
        String temp = contentString();

        switch (type) {
            case '*' :
            case '?' :
            case '+' :
                if (isOuter && temp.charAt(0) != '(')
                    return "(" + temp + type + ")";
                else
                    return temp + type;

            case 0 :
                if (isOuter)
                    return "(" + temp + ")";
                else
                    return temp;

            case ',' :
            case '|' :
                if (next == null)
                    return temp;
                for (ContentModel m = next; m != null; m = m.next)
                    temp += type + m.contentString();
                return "(" + temp + ")";

            default :
                throw new InternalError("foo");
        }
    }
    /**/
}
