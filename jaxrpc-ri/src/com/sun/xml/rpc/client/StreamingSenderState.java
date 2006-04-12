/*
 * $Id: StreamingSenderState.java,v 1.1 2006-04-12 20:35:21 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.client;

import javax.xml.rpc.handler.HandlerChain;

import com.sun.xml.rpc.client.dii.BasicCall;
import com.sun.xml.rpc.soap.message.InternalSOAPMessage;
import com.sun.xml.rpc.soap.message.SOAPMessageContext;

/**
 * <p> The internal state of an otherwise stateless StreamingSender. </p>
 *
 * @author JAX-RPC Development Team
 */
public class StreamingSenderState {

    public StreamingSenderState(SOAPMessageContext context,
        HandlerChain handlerChain, boolean useFastInfoset, 
        boolean acceptFastInfoset) 
    {
        _context = context;
        _context.setMessage(_context.createMessage(useFastInfoset, acceptFastInfoset));
        _handlerChain = handlerChain;
    }

    public SOAPMessageContext getMessageContext() {
        return _context;
    }

    public boolean isFailure() {
        return _context.isFailure();
    }

    public InternalSOAPMessage getRequest() {
        if (_request == null) {
            _request = new InternalSOAPMessage(_context.getMessage());
        }

        return _request;
    }

    public InternalSOAPMessage getResponse() {
        if (_response == null) {
            _response = new InternalSOAPMessage(_context.getMessage());
            _response.setOperationCode(getRequest().getOperationCode());
        }

        return _response;
    }

    public HandlerChain getHandlerChain() {
        return _handlerChain;
    }

    public BasicCall getCall() {
        return _call;
    }
    
    public void setCall(BasicCall call) {
        _call = call;
    }
    
    private SOAPMessageContext _context;
    private InternalSOAPMessage _request;
    private InternalSOAPMessage _response;
    private HandlerChain _handlerChain;
    private BasicCall _call;
}
