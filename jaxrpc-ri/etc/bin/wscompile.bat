@echo off


REM
REM The contents of this file are subject to the terms
REM of the Common Development and Distribution License
REM (the License).  You may not use this file except in
REM compliance with the License.
REM 
REM You can obtain a copy of the license at
REM https://glassfish.dev.java.net/public/CDDLv1.0.html.
REM See the License for the specific language governing
REM permissions and limitations under the License.
REM 
REM When distributing Covered Code, include this CDDL
REM Header Notice in each file and include the License file
REM at https://glassfish.dev.java.net/public/CDDLv1.0.html.
REM If applicable, add the following below the CDDL Header,
REM with the fields enclosed by brackets [] replaced by
REM you own identifying information:
REM "Portions Copyrighted [year] [name of copyright owner]"
REM 
REM Copyright 2006 Sun Microsystems Inc. All Rights Reserved
REM

if defined JAVA_HOME goto CONTA
echo ERROR: Set JAVA_HOME to the path where the J2SE (JDK) is installed (e.g., D:\jdk1.3)
goto END
:CONTA

if defined JAXRPC_HOME goto CONTB
echo ERROR: Set JAXRPC_HOME to the root of a JAXRPC-RI distribution (e.g., D:\ws\jaxrpc-ri\build)
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

set CLASSPATH=.;%JAXRPC_HOME%\lib\jaxrpc-impl.jar;%JAXRPC_HOME%\lib\jaxrpc-api.jar;%JAXRPC_HOME%\lib\jaxrpc-spi.jar;%JAXRPC_HOME%\lib\activation.jar;%JAXRPC_HOME%\lib\saaj-api.jar;%JAXRPC_HOME%\lib\saaj-impl.jar;%JAXRPC_HOME%\lib\mail.jar;%JAXRPC_HOME%\lib\jaxp-api.jar;%JAXRPC_HOME%\lib\dom.jar;%JAXRPC_HOME%\lib\sax.jar;%JAXRPC_HOME%\lib\xalan.jar;%JAXRPC_HOME%\lib\xercesImpl.jar;%JAXRPC_HOME%\lib\jax-qname.jar;%JAXRPC_HOME%\lib\relaxngDatatype.jar;%JAXRPC_HOME%\lib\xsdlib.jar;%JAXRPC_HOME%\lib\jcert.jar;%JAXRPC_HOME%\lib\jnet.jar;%JAXRPC_HOME%\lib\jsse.jar;%JAVA_HOME%\lib\tools.jar

%JAVA_HOME%\bin\java -cp "%CLASSPATH%" com.sun.xml.rpc.tools.wscompile.Main %CMD_LINE_ARGS%

endlocal

:END
