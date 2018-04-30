/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2018 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
