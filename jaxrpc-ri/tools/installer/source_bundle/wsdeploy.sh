#!/bin/sh

#
# Copyright 2003 Sun Microsystems, Inc. All rights reserved.
# SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
#

if [ -z "$JAVA_HOME" ]; then
	echo "ERROR: Set JAVA_HOME to the path where the J2SE (JDK) is installed (e.g., /usr/java/jdk1.3)"
	exit 1
fi

if [ -z "$JAXRPC_HOME" ]; then
	echo "ERROR: Set JAXRPC_HOME to the root of a JAXRPC-RI distribution (e.g., the directory above this bin directory)"
	exit 1
fi


CLASSPATH=.:$JAXRPC_HOME/build:$JAXRPC_HOME/src:$JAXRPC_HOME/lib/jaxrpc-api.jar:$JAXRPC_HOME/lib/jaxrpc-spi.jar:$JAXRPC_HOME/lib/activation.jar:$JAXRPC_HOME/lib/saaj-api.jar:$JAXRPC_HOME/lib/saaj-impl.jar:$JAXRPC_HOME/lib/mail.jar:$JAXRPC_HOME/lib/jaxp-api.jar:$JAXRPC_HOME/lib/dom.jar:$JAXRPC_HOME/lib/sax.jar:$JAXRPC_HOME/lib/xalan.jar:$JAXRPC_HOME/lib/xercesImpl.jar:$JAXRPC_HOME/lib/jcert.jar:$JAXRPC_HOME/lib/jnet.jar:$JAXRPC_HOME/lib/jsse.jar:$JAXRPC_HOME/lib/jax-qname.jar:$JAXRPC_HOME/lib/relaxngDatatype.jar:$JAXRPC_HOME/lib/xsdlib.jar:$JAVA_HOME/lib/tools.jar

$JAVA_HOME/bin/java -cp "$CLASSPATH" com.sun.xml.rpc.tools.wsdeploy.Main "$@"


