/*
 * $Id: StreamingParser.java,v 1.2 2006-04-13 01:33:17 ofung Exp $
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
