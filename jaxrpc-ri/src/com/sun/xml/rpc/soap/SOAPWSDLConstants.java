/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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

/*
 *$Id: SOAPWSDLConstants.java,v 1.3 2007-07-13 23:36:25 ofung Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.xml.rpc.soap;

import javax.xml.namespace.QName;

/**
 * @author JAX-RPC Development Team
 */

public interface SOAPWSDLConstants {
	/** NS_WSDL_SOAP */
	public String getWSDLSOAPNamespace();

	/** NS_SOAP_ENCODING */
	public String getSOAPEncodingNamespace();

	/** URI_SOAP_TRANSPORT_HTTP */
	public String getSOAPTransportHttpURI();

	/** QNames */
	public QName getQNameAddress();
	public QName getQNameBinding();
	public QName getQNameBody();
	public QName getQNameFault();
	public QName getQNameHeader();
	public QName getQNameHeaderFault();
	public QName getQNameOperation();

	/** SOAP encoding QNames */
	public QName getQNameTypeArray();
	public QName getQNameAttrGroupCommonAttributes();
	public QName getQNameAttrArrayType();
	//soap12
	public QName getQNameAttrItemType();
	public QName getQNameAttrArraySize();

	public QName getQNameAttrOffset();
	public QName getQNameAttrPosition();

	public QName getQNameTypeBase64();

	public QName getQNameElementString();
	public QName getQNameElementNormalizedString();
	public QName getQNameElementToken();
	public QName getQNameElementByte();
	public QName getQNameElementUnsignedByte();
	public QName getQNameElementBase64Binary();
	public QName getQNameElementHexBinary();
	public QName getQNameElementInteger();
	public QName getQNameElementPositiveInteger();
	public QName getQNameElementNegativeInteger();
	public QName getQNameElementNonNegativeInteger();
	public QName getQNameElementNonPositiveInteger();
	public QName getQNameElementInt();
	public QName getQNameElementUnsignedInt();
	public QName getQNameElementLong();
	public QName getQNameElementUnsignedLong();
	public QName getQNameElementShort();
	public QName getQNameElementUnsignedShort();
	public QName getQNameElementDecimal();
	public QName getQNameElementFloat();
	public QName getQNameElementDouble();
	public QName getQNameElementBoolean();
	public QName getQNameElementTime();
	public QName getQNameElementDateTime();
	public QName getQNameElementDuration();
	public QName getQNameElementDate();
	public QName getQNameElementGMonth();
	public QName getQNameElementGYear();
	public QName getQNameElementGYearMonth();
	public QName getQNameElementGDay();
	public QName getQNameElementGMonthDay();
	public QName getQNameElementName();
	public QName getQNameElementQName();
	public QName getQNameElementNCNAME();
	public QName getQNameElementAnyURI();
	public QName getQNameElementID();
	public QName getQNameElementIDREF();
	public QName getQNameElementIDREFS();
	public QName getQNameElementEntity();
	public QName getQNameElementEntities();
	public QName getQNameElementNotation();
	public QName getQNameElementNMTOKEN();
	public QName getQNameElementNMTOKENS();

	public QName getQNameTypeString();
	public QName getQNameTypeNormalizedString();
	public QName getQNameTypeToken();
	public QName getQNameTypeByte();
	public QName getQNameTypeUnsignedByte();
	public QName getQNameTypeBase64Binary();
	public QName getQNameTypeHexBinary();
	public QName getQNameTypeInteger();
	public QName getQNameTypePositiveInteger();
	public QName getQNameTypeNegativeInteger();
	public QName getQNameTypeNonNegativeInteger();
	public QName getQNameTypeNonPositiveInteger();
	public QName getQNameTypeInt();
	public QName getQNameTypeUnsignedInt();
	public QName getQNameTypeLong();
	public QName getQNameTypeUnsignedLong();
	public QName getQNameTypeShort();
	public QName getQNameTypeUnsignedShort();
	public QName getQNameTypeDecimal();
	public QName getQNameTypeFloat();
	public QName getQNameTypeDouble();
	public QName getQNameTypeBoolean();
	public QName getQNameTypeTime();
	public QName getQNameTypeDateTime();
	public QName getQNameTypeDuration();
	public QName getQNameTypeDate();
	public QName getQNameTypeGMonth();
	public QName getQNameTypeGYear();
	public QName getQNameTypeGYearMonth();
	public QName getQNameTypeGDay();
	public QName getQNameTypeGMonthDay();
	public QName getQNameTypeName();
	public QName getQNameTypeQName();
	public QName getQNameTypeNCNAME();
	public QName getQNameTypeAnyURI();
	public QName getQNameTypeID();
	public QName getQNameTypeIDREF();
	public QName getQNameTypeIDREFS();
	public QName getQNameTypeENTITY();
	public QName getQNameTypeENTITIES();
	public QName getQNameTypeNotation();
	public QName getQNameTypeNMTOKEN();
	public QName getQNameTypeNMTOKENS();
	public QName getQNameTypeLanguage();

	// SOAP attributes with non-colonized names
	public QName getQNameAttrID();
	public QName getQNameAttrHREF();

	/** SOAP Version used */
	public SOAPVersion getSOAPVersion();
}
