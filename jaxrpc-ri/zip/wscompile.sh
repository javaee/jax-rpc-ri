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

# Script to run WSCompile
#

if [ -z "$JAVA_HOME" ]; then
	echo "ERROR: Set JAVA_HOME to the path where the J2SE (JDK) is installed (e.g., /usr/java/jdk1.4)"
	exit 1
fi

# Resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

PRGDIR=`dirname "$PRG"`
WEBSERVICES_LIB=$PRGDIR/../..

# Set the default -Djava.endorsed.dirs argument
JAVA_ENDORSED_DIRS="$JAVA_HOME"/lib:"$WEBSERVICES_LIB"/jaxp/lib/endorsed

# Set CLASSPATH
CLASSPATH=$WEBSERVICES_LIB/jaxrpc/lib/jaxrpc-impl.jar:$WEBSERVICES_LIB/jaxrpc/lib/jaxrpc-api.jar:$WEBSERVICES_LIB/jaxrpc/lib/jaxrpc-spi.jar:$WEBSERVICES_LIB/saaj/lib/activation.jar:$WEBSERVICES_LIB/saaj/lib/saaj-api.jar:$WEBSERVICES_LIB/saaj/lib/saaj-impl.jar:$WEBSERVICES_LIB/saaj/lib/mail.jar:$WEBSERVICES_LIB/jaxp/lib/jaxp-api.jar:$WEBSERVICES_LIB/jaxp/endorsed/lib/dom.jar:$WEBSERVICES_LIB/jaxp/endorsed/lib/sax.jar:$WEBSERVICES_LIB/jaxp/endorsed/lib/xalan.jar:$WEBSERVICES_LIB/jaxp/endorsed/lib/xercesImpl.jar:$JAVA_HOME/lib/tools.jar:$WEBSERVICES_LIB/jwsdp-shared/lib/jax-qname.jar:$WEBSERVICES_LIB/jwsdp-shared/lib/relaxngDatatype.jar:$WEBSERVICES_LIB/jwsdp-shared/lib/xsdlib.jar

cygwin=false;
case "`uname`" in
    CYGWIN*) cygwin=true ;;
esac

if $cygwin; then
  JAVA_HOME=`cygpath --windows "$JAVA_HOME"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  JAVA_ENDORSED_DIRS=`cygpath --path --windows "$JAVA_ENDORSED_DIRS"`
fi

$JAVA_HOME/bin/java -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" -classpath "$CLASSPATH" com.sun.xml.rpc.tools.wscompile.Main "$@"
