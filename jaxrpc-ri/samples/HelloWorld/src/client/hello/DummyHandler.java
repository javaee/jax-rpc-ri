package hello;

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

public class DummyHandler extends GenericHandler {

	public boolean handleRequest(MessageContext context) {
		return true;
	}

	public boolean handleResponse(MessageContext context) {
		return true;
	}

	public boolean handleFault(MessageContext context) {
		return true;
	}

     	public QName[] getHeaders() { return new QName[0]; }

}

