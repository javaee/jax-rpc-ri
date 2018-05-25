/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package photocatalog;
import java.util.Iterator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.soap.SOAPMessage;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.soap.SOAPMessageContext;

public class LoggingHandler extends GenericHandler {

    String[] MIME_HEADERS = { "Server", "Date", "Content-length", "Content-type", "SOAPAction" };

	public boolean handleRequest(MessageContext context) {
		logMessage("request", context);
		return true;
	}

	public boolean handleResponse(MessageContext context) {
		logMessage("response", context);
		return true;
	}


	public boolean handleFault(MessageContext context) {
		logMessage("fault", context);
		return true;
	}

	private void logMessage(String method, MessageContext context) {
		FileOutputStream fout = createFile();
		LogOutputStream out = new LogOutputStream(fout, true);
		try {
			out.println("<" + method + ">");
			//Uncomment this statement to log the SOAP messages.
			out.println(logSOAPMessage(context));
			out.println("</" + method + ">");
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			throw new JAXRPCException("LoggingHandler: Unable to log the " +
				" message in " + method + " - {" + ex.getClass().getName() +
				"}" + ex.getMessage());
		} finally {
			out.flush();
		}
	}

	
	String logSOAPMessage(MessageContext context) {
		StringBuffer stringBuffer = new StringBuffer();
		SOAPMessageContext smc = (SOAPMessageContext) context;
		SOAPMessage soapMessage = smc.getMessage();

		ByteArrayOutputStream bout= new ByteArrayOutputStream();
		try {
			soapMessage.writeTo(bout);
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}
		stringBuffer.append(bout.toString() + "\n");

		return stringBuffer.toString();
	} 

	public FileOutputStream createFile() {

		FileOutputStream fout = null;
		try {
			String logfile = System.getProperty("log.dir") +
				System.getProperty("file.separator") +
				System.getProperty("soap.msgs.file");
			File file = new File(logfile); 

			fout = new FileOutputStream(logfile, true); //append
		} catch (IOException ex) {
			ex.printStackTrace(System.out);
			throw new JAXRPCException("Unable to initialize the log file: " +
				ex.getClass().getName() + " - " + ex.getMessage());
		}
		return fout;
	}
     	public QName[] getHeaders() { return new QName[0]; }

}

