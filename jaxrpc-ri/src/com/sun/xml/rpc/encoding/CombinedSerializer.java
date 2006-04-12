/*
 * $Id: CombinedSerializer.java,v 1.1 2006-04-12 20:33:15 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
