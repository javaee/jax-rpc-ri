/*
 * $Id: StreamingHandlerState.java,v 1.1 2006-04-12 20:35:24 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.server;

import com.sun.xml.rpc.soap.message.InternalSOAPMessage;
import com.sun.xml.rpc.soap.message.SOAPMessageContext;

import com.sun.xml.rpc.client.StubPropertyConstants;

/**
 * The internal state of an otherwise stateless StreamingHandler. 
 *
 * @author JAX-RPC Development Team
 */
public class StreamingHandlerState {
        
    public StreamingHandlerState(SOAPMessageContext context) {
        _context = context;
        _request = new InternalSOAPMessage(_context.getMessage());
        _response = null;
    }

    public boolean isFastInfoset() {
        return _context.isFastInfoset();
    }
    
    public boolean acceptFastInfoset() {
        return _context.acceptFastInfoset();
    }
    
    public SOAPMessageContext getMessageContext() {
        return _context;
    }

    public boolean isFailure() {
        if (_response == null) {
            return false;
        } else {
            return _response.isFailure();
        }
    }

    public InternalSOAPMessage getRequest() {
        return _request;
    }

    public InternalSOAPMessage getResponse() {
        if (_response == null) {
            // Create FI response if accepted by client
            _response = new InternalSOAPMessage(
                _context.createMessage(_context.acceptFastInfoset(), 
                                       _context.acceptFastInfoset())); 
        }

        return _response;
    }

    public void setResponse(InternalSOAPMessage msg) {
        _response = msg;
    }

    public InternalSOAPMessage resetResponse() {
        _response = null;
        return getResponse();
    }

    private SOAPMessageContext _context;
    private InternalSOAPMessage _request;
    private InternalSOAPMessage _response;

    public static final int CALL_NO_HANDLERS = -1;
    public static final int CALL_FAULT_HANDLERS = 0;
    public static final int CALL_RESPONSE_HANDLERS = 1;

    int handlerFlag = CALL_RESPONSE_HANDLERS;
    
    public void setHandlerFlag(int handlerFlag) {
        this.handlerFlag = handlerFlag;
    }

    public int getHandlerFlag() {
        return handlerFlag;
    }
    
}
