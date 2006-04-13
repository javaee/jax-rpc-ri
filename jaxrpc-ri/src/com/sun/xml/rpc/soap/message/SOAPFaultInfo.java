/*
 * $Id: SOAPFaultInfo.java,v 1.2 2006-04-13 01:32:23 ofung Exp $
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

import javax.xml.namespace.QName;

/**
 * @author JAX-RPC Development Team
 */
public class SOAPFaultInfo {

	// called SOAPFaultInfo to avoid clashes with the SOAPFault in JAXM
	public SOAPFaultInfo(QName code, String string, String actor) {
		this(code, string, actor, null);
	}

	public SOAPFaultInfo(
		QName code,
		String string,
		String actor,
		Object detail) {
		this.code = code;
		this.string = string;
		this.actor = actor;
		this.detail = detail;
	}

	public QName getCode() {
		return code;
	}

	public void setCode(QName code) {
		this.code = code;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public Object getDetail() {
		return detail;
	}

	public void setDetail(Object detail) {
		this.detail = detail;
	}

	private QName code;
	private String string;
	private String actor;
	private Object detail;
}
