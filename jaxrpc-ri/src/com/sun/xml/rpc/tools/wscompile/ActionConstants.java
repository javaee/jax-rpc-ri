/*
 * $Id: ActionConstants.java,v 1.2 2006-04-13 01:33:30 ofung Exp $
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


package com.sun.xml.rpc.tools.wscompile;

/**
 *
 * @author JAX-RPC Development Team
 */
public interface ActionConstants {
    
    public static final String ACTION_REMOTE_INTERFACE_GENERATOR          = "remote.interface.generator";
    public static final String ACTION_REMOTE_INTERFACE_IMPL_GENERATOR     = "remote.interface.impl.generator";
    public static final String ACTION_CUSTOM_CLASS_GENERATOR              = "custom.class.generator";
    public static final String ACTION_SOAP_OBJECT_SERIALIZER_GENERATOR    = "soap.object.serializer.generator";
    public static final String ACTION_INTERFACE_SERIALIZER_GENERATOR      = "interface.serializer.generator";
    public static final String ACTION_SOAP_OBJECT_BUILDER_GENERATOR       = "soap.object.builder.generator";
    public static final String ACTION_LITERAL_OBJECT_SERIALIZER_GENERATOR = "literal.object.serializer.generator";
    public static final String ACTION_STUB_GENERATOR                      = "stub.generator";
    public static final String ACTION_TIE_GENERATOR                       = "tie.generator";
    public static final String ACTION_SERVLET_CONFIG_GENERATOR            = "servlet.config.generator";
    public static final String ACTION_WSDL_GENERATOR                      = "wsdl.generator";
    public static final String ACTION_HOLDER_GENERATOR                    = "holder.generator";
    public static final String ACTION_SERVICE_INTERFACE_GENERATOR         = "service.interface.generator";
    public static final String ACTION_SERVICE_GENERATOR                   = "service.generator";
    public static final String ACTION_SERIALIZER_REGISTRY_GENERATOR       = "serializer.registry.generator";
    public static final String ACTION_CUSTOM_EXCEPTION_GENERATOR          = "custom.exception.generator";
    public static final String ACTION_SOAP_FAULT_SERIALIZER_GENERATOR     = "soap.fault.serializer.generator";
    public static final String ACTION_FAULT_EXCEPTION_BUILDER_GENERATOR   = "fault.exception.builder.generator";
    public static final String ACTION_ENUMERATION_GENERATOR               = "enumeration.generator";
    public static final String ACTION_ENUMERATION_ENCODER_GENERATOR       = "enumeration.encoder.generator";
}