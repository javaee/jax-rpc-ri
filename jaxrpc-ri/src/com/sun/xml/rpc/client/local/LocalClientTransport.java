/*
 * $Id: LocalClientTransport.java,v 1.2 2006-04-13 01:26:59 ofung Exp $
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

package com.sun.xml.rpc.client.local;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;

import com.sun.xml.rpc.client.ClientTransport;
import com.sun.xml.rpc.client.ClientTransportException;
import com.sun.xml.rpc.soap.message.Handler;
import com.sun.xml.rpc.soap.message.SOAPMessageContext;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 * @author JAX-RPC Development Team
 */
public class LocalClientTransport implements ClientTransport {

    //this class is used primarily for debugging purposes
    public LocalClientTransport(Handler handler) {
        _handler = handler;
    }

    public LocalClientTransport(Handler handler, OutputStream logStream) {
        _handler = handler;
        _logStream = logStream;
    }

    public void invoke(String endpoint, SOAPMessageContext context) {
        try {
            if (_logStream != null) {
                String s = "\n******************\nRequest\n";
                _logStream.write(s.getBytes());
                context.getMessage().writeTo(_logStream);
            }

            setSOAPMessageFromSAAJ(context);

            _handler.handle(context);

            setSOAPMessageFromSAAJ(context);            

            // set this because a sender cannot rely on it being set
            // automatically
            context.setFailure(false);

            if (_logStream != null) {
                String s = "\nResponse\n";
                _logStream.write(s.getBytes());
                context.getMessage().writeTo(_logStream);
                s = "\n******************\n\n";
                _logStream.write(s.getBytes());
            }
        } catch (Exception e) {
            if (e instanceof Localizable) {
                throw new ClientTransportException(
                    "local.client.failed",
                    (Localizable) e);
            } else {
                throw new ClientTransportException(
                    "local.client.failed",
                    new LocalizableExceptionAdapter(e));
            }
        }
    }

    /**
     * Set the SOAPMessage in the context using stream thru SAAJ, this is 
     * to closely match http behaviour
     * 
     * @param context
     */
    private void setSOAPMessageFromSAAJ(SOAPMessageContext context) throws Exception{
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        context.getMessage().writeTo(os);

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

        SOAPMessage message =
            MessageFactory.newInstance().createMessage(
                context.getMessage().getMimeHeaders(),
                is);
        context.setMessage(message);
    }

    public void invokeOneWay(String endpoint, SOAPMessageContext context) {
        try {
            if (_logStream != null) {
                String s = "\n******************\nRequest\n";
                _logStream.write(s.getBytes());
                context.getMessage().writeTo(_logStream);
            }

            _handler.handle(context);

            // set this because a sender cannot rely on it being set
            // automatically
            context.setFailure(false);

        } catch (Exception e) {
            if (e instanceof Localizable) {
                throw new ClientTransportException(
                    "local.client.failed",
                    (Localizable) e);
            } else {
                throw new ClientTransportException(
                    "local.client.failed",
                    new LocalizableExceptionAdapter(e));
            }
        }
    }

    private Handler _handler;
    private OutputStream _logStream;
}
