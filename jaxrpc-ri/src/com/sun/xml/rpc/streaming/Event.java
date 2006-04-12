/*
 * $Id: Event.java,v 1.1 2006-04-12 20:32:51 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
