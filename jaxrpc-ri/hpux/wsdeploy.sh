#!/bin/sh
#
# Copyright 2002 Sun Microsystems, Inc.  All rights reserved.
# Use is subject to license terms.
#

#
# Script to run WSDeploy

if [ -z "$JAVA_HOME" ]; then
	echo "ERROR: Set JAVA_HOME to the path where the J2SE (JDK) is installed (e.g., /usr/java/jdk1.4)"
	exit 1
fi

bin_dir=`dirname $0`
WEBSERVICES_LIB=`cd $bin_dir/../share/lib; pwd`
WEBSERVICES_PRIVATE_LIB=`cd $bin_dir/../private/share/lib; pwd`

# Set CLASSPATH
CLASSPATH=.:$WEBSERVICES_LIB/jaxrpc-impl.jar:$WEBSERVICES_LIB/jaxrpc-api.jar:$WEBSERVICES_PRIVATE_LIB/jaxrpc-spi.jar:$WEBSERVICES_LIB/activation.jar:$WEBSERVICES_LIB/saaj-api.jar:$WEBSERVICES_LIB/saaj-impl.jar:$WEBSERVICES_LIB/mail.jar:$WEBSERVICES_LIB/endorsed/dom.jar:$WEBSERVICES_LIB/endorsed/xalan.jar:$WEBSERVICES_LIB/endorsed/xercesImpl.jar:$WEBSERVICES_LIB/endorsed/lib/jaxp-api.jar:$WEBSERVICES_LIB/endorsed/sax.jar:$WEBSERVICES_PRIVATE_LIB/relaxngDatatype.jar:$WEBSERVICES_PRIVATE_LIB/xsdlib.jar:$JAVA_HOME/lib/tools.jar

$JAVA_HOME/bin/java $WSDEPLOY_OPTS -classpath "$CLASSPATH" com.sun.xml.rpc.tools.wsdeploy.Main "$@"
