/*
 * $Id: InternalSOAPMessage.java,v 1.2 2006-04-13 01:32:22 ofung Exp $
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

package com.sun.xml.rpc.soap.message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.soap.SOAPMessage;

import com.sun.xml.rpc.util.NullIterator;

/**
 * The internal representation of a SOAP message.
 * 
 * @author JAX-RPC Development Team
 */
public class InternalSOAPMessage {

	public static final int NO_OPERATION = -1;

	public InternalSOAPMessage(SOAPMessage message) {
		_message = message;
		_operationCode = NO_OPERATION;
	}

	public SOAPMessage getMessage() {
		return _message;
	}

	public void add(SOAPHeaderBlockInfo headerInfo) {
		if (headerInfo != null) {
			if (_headers == null) {
				_headers = new ArrayList();
			}
			_headers.add(headerInfo);
		}
	}

	public Iterator headers() {
		if (_headers == null) {
			return NullIterator.getInstance();
		} else {
			return _headers.iterator();
		}
	}

	public SOAPBlockInfo getBody() {
		return _body;
	}

	public void setBody(SOAPBlockInfo body) {
		_body = body;
	}

	public int getOperationCode() {
		return _operationCode;
	}

	public void setOperationCode(int i) {
		_operationCode = i;
	}

	public boolean isHeaderNotUnderstood() {
		return _headerNotUnderstood;
	}

	public void setHeaderNotUnderstood(boolean b) {
		_headerNotUnderstood = b;
	}

	public boolean isFailure() {
		return _failure;
	}

	public void setFailure(boolean b) {
		_failure = b;
	}

	private SOAPMessage _message;
	private List _headers;
	private SOAPBlockInfo _body;
	private int _operationCode;
	private boolean _failure;
	private boolean _headerNotUnderstood;
}
