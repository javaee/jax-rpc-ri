/*
 * $Id: Event.java,v 1.2 2006-04-13 01:33:11 ofung Exp $
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

/**
 * An event contains all the current state of a StreamingParser.
 *
 * @author JAX-RPC Development Team
 */

public final class Event {
    public int state = StreamingParser.AT_END;
    public String name;
    public String value;
    public String uri;
    public int line;

    public Event() {
    }

    public Event(int s, String n, String v, String u) {
        this(s, n, v, u, -1);
    }

    public Event(int s, String n, String v, String u, int i) {
        state = s;
        name = n;
        value = v;
        uri = u;
        line = i;
    }

    public Event(Event e) {
        from(e);
    }

    public void from(Event e) {
        this.state = e.state;
        this.name = e.name;
        this.value = e.value;
        this.uri = e.uri;
        this.line = e.line;
    }

    public String toString() {
        // intended for debug only
        return "Event("
            + getStateName()
            + ", "
            + name
            + ", "
            + value
            + ", "
            + uri
            + ", "
            + line
            + ")";
    }

    protected String getStateName() {
        switch (state) {
            case StreamingParser.START :
                return "start";
            case StreamingParser.END :
                return "end";
            case StreamingParser.ATTR :
                return "attr";
            case StreamingParser.CHARS :
                return "chars";
            case StreamingParser.IWS :
                return "iws";
            case StreamingParser.PI :
                return "pi";
            case StreamingParser.AT_END :
                return "at_end";
            default :
                return "unknown";
        }
    }
}
