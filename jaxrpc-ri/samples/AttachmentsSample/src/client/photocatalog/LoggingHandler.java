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

