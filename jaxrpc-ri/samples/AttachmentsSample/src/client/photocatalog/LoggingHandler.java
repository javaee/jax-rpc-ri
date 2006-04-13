/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
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

