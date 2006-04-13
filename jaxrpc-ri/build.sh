#! /bin/sh

#
# $Id: build.sh,v 1.2 2006-04-13 01:26:02 ofung Exp $
#

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

if [ -z "$JAVA_HOME" ]
then
JAVACMD=`which java`
if [ -z "$JAVACMD" ]
then
echo "Cannot find JAVA. Please set your PATH."
exit 1
fi
JAVA_BINDIR=`dirname $JAVACMD`
JAVA_HOME=$JAVA_BINDIR/..
fi

JAVACMD=$JAVA_HOME/bin/java

cp=$JAVA_HOME/lib/tools.jar:./lib/jaxp-api.jar:./lib/dom.jar:./lib/sax.jar:./lib/xalan.jar:./lib/xercesImpl.jar:./lib/jnet.jar:./lib/jsse.jar:./lib/jcert.jar:./lib/ant.jar:./lib/optional.jar:./lib/junit.jar

$JAVACMD -classpath $cp:$CLASSPATH org.apache.tools.ant.Main "$@"
