#!/bin/sh

#
# The contents of this file are subject to the terms
# of the Common Development and Distribution License
# (the License).  You may not use this file except in
# compliance with the License.
# 
# You can obtain a copy of the license at
# https://glassfish.dev.java.net/public/CDDLv1.0.html.
# See the License for the specific language governing
# permissions and limitations under the License.
# 
# When distributing Covered Code, include this CDDL
# Header Notice in each file and include the License file
# at https://glassfish.dev.java.net/public/CDDLv1.0.html.
# If applicable, add the following below the CDDL Header,
# with the fields enclosed by brackets [] replaced by
# you own identifying information:
# "Portions Copyrighted [year] [name of copyright owner]"
# 
# Copyright 2006 Sun Microsystems Inc. All Rights Reserved
#

if [ -z "$JAVA_HOME" ]; then
	echo "ERROR: Set JAVA_HOME to the path where the J2SE (JDK) is installed (e.g., /usr/java/jdk1.3)"
	exit 1
fi

if [ -z "$JAXRPC_HOME" ]; then
	echo "ERROR: Set JAXRPC_HOME to the root of a JAXRPC-RI distribution (e.g., /usr/bin/jaxrpc-ri/build)"
	exit 1
fi


CLASSPATH=.:$JAXRPC_HOME/lib/jaxrpc-impl.jar:$JAXRPC_HOME/lib/jaxrpc-api.jar:$JAXRPC_HOME/lib/jaxrpc-spi.jar:$JAXRPC_HOME/lib/activation.jar:$JAXRPC_HOME/lib/saaj-api.jar:$JAXRPC_HOME/lib/saaj-impl.jar:$JAXRPC_HOME/lib/mail.jar:$JAXRPC_HOME/lib/jaxp-api.jar:$JAXRPC_HOME/lib/dom.jar:$JAXRPC_HOME/lib/sax.jar:$JAXRPC_HOME/lib/xalan.jar:$JAXRPC_HOME/lib/xercesImpl.jar:$JAXRPC_HOME/lib/jcert.jar:$JAXRPC_HOME/lib/jnet.jar:$JAXRPC_HOME/lib/jsse.jar:$JAXRPC_HOME/lib/jax-qname.jar:$JAXRPC_HOME/lib/relaxngDatatype.jar:$JAXRPC_HOME/lib/xsdlib.jar:$JAVA_HOME/lib/tools.jar

$JAVA_HOME/bin/java -cp "$CLASSPATH" com.sun.xml.rpc.tools.wsdeploy.Main "$@"


