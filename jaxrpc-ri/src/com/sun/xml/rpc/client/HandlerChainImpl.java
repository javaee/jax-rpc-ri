/*
 * $Id: HandlerChainImpl.java,v 1.2 2006-04-13 01:26:32 ofung Exp $
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerChain;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.soap.SOAPFaultException;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import com.sun.xml.rpc.soap.message.SOAPMessageContext;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 * @author JAX-RPC Development Team
 */
public class HandlerChainImpl extends Vector implements HandlerChain {
    protected List handlerInfos;

    String[] roles = null;

    public HandlerChainImpl(List handlerInfos) {
        this.handlerInfos = handlerInfos;
        createHandlerInstances();
    }

    private void createHandlerInstances() {
        for (int i = 0; i < handlerInfos.size(); i++)
            add(newHandler(getHandlerInfo(i)));
    }

    public boolean handleFault(MessageContext _context) {
        SOAPMessageContext context = (SOAPMessageContext) _context;

        int n = context.getCurrentHandler();

        for (int i = n; i >= 0; i--) {
            context.setCurrentHandler(i);

            try {
                if (getHandlerInstance(i).handleFault(context) == false) {
                    return false;
                }
            } catch (SOAPFaultException sfe) {
                throw sfe;
            } catch (RuntimeException re) {
                deleteHandlerInstance(i);
                setElementAt(newHandler(getHandlerInfo(i)), i);
                throw re;
            }
        }

        context.setCurrentHandler(-1);
        return true;
    }

    public boolean handleRequest(MessageContext _context) {

        SOAPMessageContext context = (SOAPMessageContext) _context;
        context.setRoles(roles);

        for (int i = 0; i < size(); i++) {
            Handler currentHandler = getHandlerInstance(i);
            context.setCurrentHandler(i);

            try {
                if (currentHandler.handleRequest(context) == false) {
                    return false;
                }
            } catch (SOAPFaultException sfe) {
                throw sfe;
            } catch (RuntimeException re) {
                deleteHandlerInstance(i);
                setElementAt(newHandler(getHandlerInfo(i)), i);
                throw re;
            }
        }
        context.setCurrentHandler(-1);
        return true;
    }

    public boolean handleResponse(MessageContext _context) {
        if (size() > 0) {
            SOAPMessageContext context = (SOAPMessageContext) _context;

            int n = context.getCurrentHandler();

            if (n == -1)
                n = size() - 1;

            for (int i = n; i >= 0; i--) {
                context.setCurrentHandler(i);

                try {
                    if (getHandlerInstance(i).handleResponse(context)
                        == false) {
                        context.setCurrentHandler(-1);
                        return false;
                    }
                } catch (SOAPFaultException sfe) {
                    throw sfe;
                } catch (RuntimeException re) {
                    deleteHandlerInstance(i);
                    setElementAt(newHandler(getHandlerInfo(i)), i);
                    throw re;
                }
            }

            context.setCurrentHandler(-1);
        }
        return true;
    }

    boolean initialized = false;

    public void init(java.util.Map config) {
        // TODO: How to implement this?
    }

    public void destroy() {
        for (int i = 0; i < size(); i++)
            deleteHandlerInstance(i);
        clear();
    }

    protected void deleteHandlerInstance(int index) {
        Handler h = getHandlerInstance(index);
        h.destroy();
        removeHandlerFromPool(h.getClass());
    }

    /*
     * Allow handlers to be added so that handler and handler infos
     * lists are kept in sync.
     */
    public void addHandlerInfo(int index, HandlerInfo handlerInfo) {
        handlerInfos.add(index, handlerInfo);
        add(index, newHandler(handlerInfo));
    }
    
    /*
     * Allow handlers to be added so that handler and handler infos
     * lists are kept in sync. This version of method simply appends
     * handler.
     */
    public void addHandlerInfo(HandlerInfo handlerInfo) {
        addHandlerInfo(handlerInfos.size(), handlerInfo);
    }

    protected Handler getHandlerInstance(int index) {
        return (Handler) castToHandler(get(index));
    }

    protected HandlerInfo getHandlerInfo(int index) {
        return (HandlerInfo) handlerInfos.get(index);
    }

    Hashtable handlerPool = new Hashtable();

    protected void removeHandlerFromPool(Class clz) {
        handlerPool.remove(clz.getName());
    }

    protected Handler getHandlerFromPool(HandlerInfo handlerInfo) {
        Class clz = handlerInfo.getHandlerClass();
        Handler h = (Handler) handlerPool.get(clz.getName());
        if (h == null)
            try {
                h = (Handler) clz.newInstance();
                h.init(handlerInfo);
                addUnderstoodHeaders(h.getHeaders());
                handlerPool.put(clz.getName(), h);
            } catch (Exception ex) {
                throw new HandlerException(
                    "Unable to instantiate handler: ",
                    new Object[] {
                        handlerInfo.getHandlerClass(),
                        new LocalizableExceptionAdapter(ex)});
            }
        return h;
    }

    protected Handler newHandler(HandlerInfo handlerInfo) {
        return getHandlerFromPool(handlerInfo);
    }

    public void setRoles(String[] soapActorNames) {
        this.roles = soapActorNames;
    }

    public String[] getRoles() {
        return roles;
    }

    protected Handler castToHandler(Object o) {
        if (!(o instanceof Handler)) {
            throw new HandlerException(
                "handler.chain.contains.handler.only",
                new Object[] { o.getClass().getName()});
        }
        return (Handler) o;
    }

    List understoodHeaders = new ArrayList();

    public void addUnderstoodHeaders(QName[] ignoredHeaders) {
        if (ignoredHeaders != null)
            for (int i = 0; i < ignoredHeaders.length; i++)
                understoodHeaders.add(ignoredHeaders[i]);
    }

    public boolean checkMustUnderstand(MessageContext mc)
        throws SOAPException {
        if (roles != null && !isEmpty()) {
            SOAPMessage soapMessage = ((SOAPMessageContext) mc).getMessage();
            SOAPHeader header =
                soapMessage.getSOAPPart().getEnvelope().getHeader();
            if (header == null) {
                return true;
            }
            for (int i = 0; i < roles.length; i++) {
                String actor = roles[i];
                Iterator it = header.examineMustUnderstandHeaderElements(actor);

                while (it.hasNext()) {
                    SOAPHeaderElement element = (SOAPHeaderElement) it.next();
                    Name saajName = element.getElementName();
                    QName qname =
                        new QName(saajName.getURI(), saajName.getLocalName());
                    if (!understoodHeaders.contains(qname)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}