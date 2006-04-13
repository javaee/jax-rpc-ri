/*
 * $Id: CombinedSerializer.java,v 1.2 2006-04-13 01:27:01 ofung Exp $
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

import javax.xml.namespace.QName;

/**
 * All serializers in the JAX-RPC SI extend this interface.
 * 
 * @author JAX-RPC Development Team
 */
public interface CombinedSerializer
    extends JAXRPCSerializer, JAXRPCDeserializer {
    /** Returns the XML schema type processed by this serializer. 
     * 
     *  @return Returns the XML schema type processed by this serializer.
    **/
    public QName getXmlType();

    /** Returns whether xsi:type information will be encoded 
     * 
     * @return  Returns whether xsi:type information will be encoded
    **/
    public boolean getEncodeType();

    /** Returns whether serializer allows null values
     * 
     * @return Returns whether serializer allows null values
     */
    public boolean isNullable();

    /** Returns the encodingStyle of this serializer
     * 
     * @return Returns the encodingStyle of this serializer
     */
    public String getEncodingStyle();

    /** Returns the serializer that actually does the serialization
     * 
     * @return Returns the serializer that actually does the serialization
     */
    public CombinedSerializer getInnermostSerializer();
}
