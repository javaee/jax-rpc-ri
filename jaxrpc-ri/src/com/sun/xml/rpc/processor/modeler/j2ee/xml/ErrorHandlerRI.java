/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#) 1.2 jsr109ri/src/java/com/ibm/webservices/ri/xml/util/ErrorHandlerRI.java, jsr109ri, jsr10911, b0240.03 9/26/02 21:08:03 [10/7/02 11:54:37]
/*************************************************************************
   Licensed Materials - Property of IBM
   5639-D57, 5630-A36, 5630-A37, 5724-D18 (C) COPYRIGHT International Business Machines Corp. 2002
   All Rights Reserved
   US Government Users Restricted Rights - Use, duplication, or
   disclosure restricted by GSA ADP Schedule Contract  with
   IBM Corp.
**************************************************************************/
/*********************************************************************
Change History
Date     user       defect    purpose
---------------------------------------------------------------------------
08/21/02 mcheng     143206    new code to enable validation
*********************************************************************/
package com.sun.xml.rpc.processor.modeler.j2ee.xml;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * SAX Error handler
 * @author Michael Cheng
 */
public class ErrorHandlerRI implements ErrorHandler {
    public void error(SAXParseException ex) throws SAXException {
        throw ex;
    }

    public void fatalError(SAXParseException ex) throws SAXException {
        throw ex;
    }

    public void warning(SAXParseException ex) throws SAXException {
        System.out.println("warning: " + ex.toString());
    }
};
