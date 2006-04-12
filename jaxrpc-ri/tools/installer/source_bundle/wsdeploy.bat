@echo off

REM
REM Copyright 2003 Sun Microsystems, Inc. All rights reserved.
REM SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
REM

if defined JAVA_HOME goto CONTA
echo ERROR: Set JAVA_HOME to the path where the J2SE (JDK) is installed (e.g., D:\jdk1.3)
goto END
:CONTA

if defined JAXRPC_HOME goto CONTB
echo ERROR: Set JAXRPC_HOME to the root of a JAXRPC-RI distribution (e.g., the directory above this bin directory)
goto END
:CONTB

rem Get command line arguments and save them
set CMD_LINE_ARGS=
:setArgs
if ""%1""=="""" goto doneSetArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setArgs
:doneSetArgs

setlocal

set CLASSPATH=.;%JAXRPC_HOME%\build;%JAXRPC_HOME%\src;%JAXRPC_HOME%\lib\jaxrpc-api.jar;%JAXRPC_HOME%\lib\jaxrpc-spi.jar;%JAXRPC_HOME%\lib\activation.jar;%JAXRPC_HOME%\lib\saaj-api.jar;%JAXRPC_HOME%\lib\saaj-impl.jar;%JAXRPC_HOME%\lib\mail.jar;%JAXRPC_HOME%\lib\jaxp-api.jar;%JAXRPC_HOME%\lib\dom.jar;%JAXRPC_HOME%\lib\sax.jar;%JAXRPC_HOME%\lib\xalan.jar;%JAXRPC_HOME%\lib\xercesImpl.jar;%JAXRPC_HOME%\lib\jcert.jar;%JAXRPC_HOME%\lib\jnet.jar;%JAXRPC_HOME%\lib\jsse.jar;%JAXRPC_HOME%\lib\jax-qname.jar;%JAXRPC_HOME%\lib\relaxngDatatype.jar;%JAXRPC_HOME%\lib\xsdlib.jar;%JAVA_HOME%\lib\tools.jar

%JAVA_HOME%\bin\java -cp "%CLASSPATH%" com.sun.xml.rpc.tools.wsdeploy.Main %CMD_LINE_ARGS% 

endlocal

:END
