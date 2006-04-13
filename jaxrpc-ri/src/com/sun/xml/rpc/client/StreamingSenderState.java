/*
 * $Id: StreamingSenderState.java,v 1.2 2006-04-13 01:26:37 ofung Exp $
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
