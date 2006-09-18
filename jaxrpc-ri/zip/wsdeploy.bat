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
echo ERROR: Set JAVA_HOME to the path where the J2SE (JDK) is installed (e.g., D:\jdk1.4)
goto END
:CONTA

set PRG=%0
set WEBSERVICES_LIB=%PRG%\..\..\..

rem Get command line arguments and save them
set CMD_LINE_ARGS=
:setArgs
if ""%1""=="""" goto doneSetArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setArgs
:doneSetArgs

setlocal

set CLASSPATH=%WEBSERVICES_LIB%\jaxrpc\lib\jaxrpc-impl.jar;%WEBSERVICES_LIB%\jaxrpc\lib\jaxrpc-api.jar;%WEBSERVICES_LIB%\jaxrpc\lib\jaxrpc-spi.jar;%WEBSERVICES_LIB%\jaf\lib\activation.jar;%WEBSERVICES_LIB%\saaj\lib\saaj-api.jar;%WEBSERVICES_LIB%\saaj\lib\saaj-impl.jar;%WEBSERVICES_LIB%\javamail\lib\mail.jar;%WEBSERVICES_LIB%\jaxp\lib\endorsed\jaxp-api.jar;%WEBSERVICES_LIB%\jaxp\endorsed\lib\dom.jar;%WEBSERVICES_LIB%\jaxp\endorsed\lib\sax.jar;%WEBSERVICES_LIB%\jaxp\endorsed\lib\xalan.jar;%WEBSERVICES_LIB%\jaxp\endorsed\lib\xercesImpl.jar;%WEBSERVICES_LIB%\wscl\lib\relaxngDatatype.jar;%WEBSERVICES_LIB%\wscl\lib\xsdlib.jar;%JAVA_HOME%\lib\tools.jar

"%JAVA_HOME%\bin\java" "%WSDEPLOY_OPTS%" -cp "%CLASSPATH%" com.sun.xml.rpc.tools.wsdeploy.Main %CMD_LINE_ARGS% 

endlocal

:END
