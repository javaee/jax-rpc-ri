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

package com.sun.xml.rpc.streaming;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.sun.xml.rpc.sp.ParseException;
import com.sun.xml.rpc.sp.Parser;

/**
 * A streaming parser derived from com.sun.xml.rpc.sp.StreamingParser[Impl].
 *
 * This parser avoids throwing IllegalStateException in cases where some
 * of the state variables are null.
 *
 * Parsers of this kind are always namespace-aware, and by default they
 * are coalescing too.
 *
 * @author JAX-RPC Development Team
 */

public final class StreamingParser {

    public static final int START = 0;
    public static final int END = 1;
    public static final int ATTR = 2;
    public static final int CHARS = 3;
    public static final int IWS = 4;
    public static final int PI = 5;
    public static final int AT_END = 6;

    private static final int DOC_END = -1;
    private static final int DOC_START = -2;
    private static final int EMPTY = -3;
    private static final int EXCEPTION = -4;

    private Parser parser = null;

    private int currentState = EMPTY;
    private String currentName = null;
    private String currentValue = null;
    private String currentURI = null;
    private int currentLine = -1;

    public StreamingParser(InputStream in) {
        parser = new Parser(in, true, true);
    }

    public StreamingParser(File file) throws IOException {
        parser = new Parser(file, true, true);
    }

    public Stream getStream() {
        return new Stream() {
            public int next(Event event) {
                int state = StreamingParser.this.next();
                event.state = currentState;
                event.name = currentName;
                event.value = currentValue;
                event.uri = currentURI;
                event.line = currentLine;
                return state;
            }
        };
    }

    public int next() {
        if (currentState == AT_END) {
            return AT_END;
        } else {
            try {
                currentState = parser.parse();
                if (currentState == DOC_END)
                    currentState = AT_END;
            } catch (ParseException e) {
                throw new StreamingException(e);
            } catch (IOException e) {
                throw new StreamingException(e);
            }

            currentName = parser.getCurName();
            currentValue = parser.getCurValue();

            currentURI = parser.getCurURI();

            /*
            // not needed any more
            if (currentURI != null && currentURI.length() == 0)
                currentURI = null;
            */

            currentLine = parser.getLineNumber();

            return currentState;
        }
    }

    public int getState() {
        if (currentState == EMPTY)
            throw new IllegalStateException("parser not started");
        if (currentState < EXCEPTION)
            throw new InternalError();
        return currentState;
    }

    public String getName() {
        return currentName;
    }

    public String getValue() {
        return currentValue;
    }

    public String getURI() {
        return currentURI;
    }
}
