#! /bin/sh

#
# $Id: build.sh,v 1.1 2006-04-12 20:35:23 kohlert Exp $
#

#
# Copyright 2004 Sun Microsystems, Inc. All rights reserved.
# SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
