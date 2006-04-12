@echo off  

REM
REM The contents of this file are subject to the terms 
REM of the Common Development and Distribution License 
REM (the License).  You may not use this file except in
REM compliance with the License.
REM 
REM You can obtain a copy of the license at 
REM https://glassfish.dev.java.net/public/CDDLv1.0.html or
REM glassfish/bootstrap/legal/CDDLv1.0.txt.
REM See the License for the specific language governing 
REM permissions and limitations under the License.
REM 
REM When distributing Covered Code, include this CDDL 
REM Header Notice in each file and include the License file 
REM at glassfish/bootstrap/legal/CDDLv1.0.txt.  
REM If applicable, add the following below the CDDL Header, 
REM with the fields enclosed by brackets [] replaced by
REM you own identifying information: 
REM "Portions Copyrighted [year] [name of copyright owner]"
REM 
REM Copyright 2006 Sun Microsystems, Inc. All rights reserved.
REM

echo JAX-RPC-RI Builder
echo -------------------

if "%JAVA_HOME%" == "" goto error

set LOCALCLASSPATH=%JAVA_HOME%\lib\tools.jar;.\lib\jaxp-api.jar;.\lib\dom.jar;.\lib\sax.jar;.\lib\xalan.jar;.\lib\xercesImpl.jar;.\lib\jsse.jar;.\lib\jnet.jar;.\lib\jcert.jar;.\lib\ant.jar;.\lib\optional.jar;.\lib\junit.jar;%ADDITIONALCLASSPATH%
set ANT_HOME=./lib

echo Building with classpath %LOCALCLASSPATH%

echo Starting Ant...

%JAVA_HOME%\bin\java.exe -Dant.home="%ANT_HOME%" -classpath "%LOCALCLASSPATH%" org.apache.tools.ant.Main %1 %2 %3 %4 %5

goto end

:error

echo ERROR: JAVA_HOME not found in your environment.
echo Please, set the JAVA_HOME variable in your environment to match the
echo location of the Java Virtual Machine you want to use.

:end

set LOCALCLASSPATH=

