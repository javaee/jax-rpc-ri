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
