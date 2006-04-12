/*
 * $Id: SOAPNamespaceConstants.java,v 1.1 2006-04-12 20:34:38 kohlert Exp $
 */
/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.soap;

/**
 * @author JAX-RPC Development Team
 */
public interface SOAPNamespaceConstants {

	public String getEnvelope();

	public String getEncoding();

	// SOAP 1.2
	public String getSOAPRpc();

	public String getXSD();
	public String getXSI();

	public String getTransportHTTP();

	public String getActorNext();

	public String getTagEnvelope();
	public String getTagHeader();
	public String getTagBody();
	public String getAttrActor();
	public String getAttrEncodingStyle();
	public String getAttrMustUnderstand();

	// SOAP 1.2
	public String getTagResult();
	public String getAttrMisunderstood();
	public String getSOAPUpgrade();

	public SOAPVersion getSOAPVersion();
}
