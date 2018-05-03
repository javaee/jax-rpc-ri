@echo off


REM  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
REM
REM  Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
REM
REM  The contents of this file are subject to the terms of either the GNU
REM  General Public License Version 2 only ("GPL") or the Common Development
REM  and Distribution License("CDDL") (collectively, the "License").  You
REM  may not use this file except in compliance with the License.  You can
REM  obtain a copy of the License at
REM  https://oss.oracle.com/licenses/CDDL+GPL-1.1
REM  or LICENSE.txt.  See the License for the specific
REM  language governing permissions and limitations under the License.
REM
REM  When distributing the software, include this License Header Notice in each
REM  file and include the License file at LICENSE.txt.
REM
REM  GPL Classpath Exception:
REM  Oracle designates this particular file as subject to the "Classpath"
REM  exception as provided by Oracle in the GPL Version 2 section of the License
REM  file that accompanied this code.
REM
REM  Modifications:
REM  If applicable, add the following below the License Header, with the fields
REM  enclosed by brackets [] replaced by your own identifying information:
REM  "Portions Copyright [year] [name of copyright owner]"
REM
REM  Contributor(s):
REM  If you wish your version of this file to be governed by only the CDDL or
REM  only the GPL Version 2, indicate your decision by adding "[Contributor]
REM  elects to include this software in this distribution under the [CDDL or GPL
REM  Version 2] license."  If you don't indicate a single choice of license, a
REM  recipient has the option to distribute your version of this file under
REM  either the CDDL, the GPL Version 2 or to extend the choice of license to
REM  its licensees as provided above.  However, if you add GPL Version 2 code
REM  and therefore, elected the GPL Version 2 license, then the option applies
REM  only if the new code is made subject to such option by the copyright
REM  holder.

if defined JAVA_HOME goto CONTA
echo ERROR: Set JAVA_HOME to the path where the J2SE (JDK) is installed (e.g., D:\jdk1.4)
goto END
:CONTA

set PRG=%0
set WEBSERVICES_LIB=%PRG%\..\..\lib

rem Get command line arguments and save them
set CMD_LINE_ARGS=
:setArgs
if ""%1""=="""" goto doneSetArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setArgs
:doneSetArgs

setlocal

set CLASSPATH=%WEBSERVICES_LIB%\jaxrpc-impl.jar;%WEBSERVICES_LIB%\jaxrpc-api.jar;%WEBSERVICES_LIB%\jaxrpc-spi.jar;%WEBSERVICES_LIB%\activation.jar;%WEBSERVICES_LIB%\saaj-api.jar;%WEBSERVICES_LIB%\saaj-impl.jar;%WEBSERVICES_LIB%\mail.jar;%WEBSERVICES_LIB%\endorsed\jaxp-api.jar;%WEBSERVICES_LIB%\endorsed\dom.jar;%WEBSERVICES_LIB%\endorsed\sax.jar;%WEBSERVICES_LIB%\endorsed\xalan.jar;%WEBSERVICES_LIB%\endorsed\xercesImpl.jar;%WEBSERVICES_LIB%\relaxngDatatype.jar;%JAVA_HOME%\lib\tools.jar

"%JAVA_HOME%\bin\java" %WSDEPLOY_OPTS% -cp "%CLASSPATH%" com.sun.xml.rpc.tools.wsdeploy.Main %CMD_LINE_ARGS% 

endlocal

:END
