/*
 * $Id: PostDeserializationAction.java,v 1.2 2006-04-13 01:27:15 ofung Exp $
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

package com.sun.xml.rpc.encoding;

/**
 *
 * The action object. Implemented by an inner class from IDREF serializer and 
 * added to deserilization context in a list and latter can be executed, for 
 * example to resolve the xsd:ID.
 *
 * @author JAX-RPC Development Team
 *
 */

public interface PostDeserializationAction {
    public void run(SOAPDeserializationContext deserContext);
}
