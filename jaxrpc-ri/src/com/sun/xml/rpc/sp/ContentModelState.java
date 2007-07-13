/*
 * $Id: ContentModelState.java,v 1.3 2007-07-13 23:36:27 ofung Exp $
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

/**
 * A content model state. This is basically an index into a content
 * model node, emulating an automaton with primitives to consume tokens.
 * It may create new content model states as a consequence of that
 * consumption, or modify the current state.  "Next" is used to track
 * states that are pending completion after the "current automaton"
 * completes its task.
 *
 * @see ContentModel
 * @see ValidatingParser
 *
 * @author David Brownell
 * @author Arthur van Hoff
 * @author JAX-RPC RI Development Team
 */
class ContentModelState {
    private ContentModel model;
    private boolean sawOne;
    private ContentModelState next;

    /**
     * Create a content model state for a content model.  When
     * the state advances to null, this automaton has finished.
     */
    ContentModelState(ContentModel model) {
        this(model, null);
    }

    /**
     * Create a content model state for a content model, stacking
     * a state for subsequent processing.
     */
    private ContentModelState(Object content, ContentModelState next) {
        this.model = (ContentModel) content;
        this.next = next;
        this.sawOne = false;
    }

    /**
     * Check if the state can be terminated.  That is, there are no more
     * tokens required in the input stream.
     * @return true if the model can terminate without further input
     */
    boolean terminate() {
        switch (model.type) {
            case '+' :
                if (!sawOne && !((ContentModel) model).empty())
                    return false;
                // FALLTHROUGH
            case '*' :
            case '?' :
                return (next == null) || next.terminate();

            case '|' :
                return model.empty() && (next == null || next.terminate());

            case ',' :
                ContentModel m;
                for (m = model;(m != null) && m.empty(); m = m.next)
                    continue;
                if (m != null)
                    return false;
                return (next == null) || next.terminate();

            case 0 :
                return false;

            default :
                throw new InternalError();
        }
    }

    /**
     * Advance this state to a new state, or throw an
     * exception (use a more appropriate one?) if the
     * token is illegal at this point in the content model.
     * The current state is modified if possible, conserving
     * memory that's already been allocated.
     * @return next state after reducing a token
     */
    ContentModelState advance(String token) throws EndOfInputException {
        switch (model.type) {
            case '+' :
            case '*' :
                if (model.first(token)) {
                    sawOne = true;
                    if (model.content instanceof String)
                        return this;
                    return new ContentModelState(model.content, this).advance(
                        token);
                }
                if ((model.type == '*' || sawOne) && next != null)
                    return next.advance(token);
                break;

            case '?' :
                if (model.first(token)) {
                    if (model.content instanceof String)
                        return next;
                    return new ContentModelState(model.content, next).advance(
                        token);
                }
                if (next != null)
                    return next.advance(token);
                break;

            case '|' :
                for (ContentModel m = model; m != null; m = m.next) {
                    if (m.content instanceof String) {
                        if (token == m.content)
                            return next;
                        continue;
                    }
                    if (((ContentModel) m.content).first(token))
                        return new ContentModelState(m.content, next).advance(
                            token);
                }
                if (model.empty() && next != null)
                    return next.advance(token);
                break;

            case ',' :
                if (model.first(token)) {
                    ContentModelState nextState;

                    if (model.type == 0)
                        return next;
                    if (model.next == null)
                        nextState = new ContentModelState(model.content, next);
                    else {
                        nextState = new ContentModelState(model.content, this);
                        model = model.next;
                    }
                    return nextState.advance(token);
                } else if (model.empty() && next != null) {
                    return next.advance(token);
                }
                break;

            case 0 :
                if (model.content == token)
                    return next;
                // FALLTHROUGH

            default :
                // FALLTHROUGH
        }
        throw new EndOfInputException();
    }
}
