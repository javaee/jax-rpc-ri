/*
 * $Id: StubHooksIf.java,v 1.1 2006-04-12 20:33:17 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.tools.wscompile;

import java.io.IOException;

import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.util.IndentingWriter;

/**
 * @author JAX-RPC Development Team
 *
 */
public interface StubHooksIf {
    public void writeStubStatic(Model model, Port port, IndentingWriter p) throws IOException;
    public void writeStubStatic(Model model, IndentingWriter p) throws IOException;
    public void _preHandlingHook(Model model, IndentingWriter p,
        StubHooksState state) throws IOException;
    public void _preRequestSendingHook(Model model, IndentingWriter p,
        StubHooksState state) throws IOException;
    public class StubHooksState {
        public boolean superDone;
    }
}
