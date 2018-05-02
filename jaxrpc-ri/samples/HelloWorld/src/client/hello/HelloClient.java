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

package hello;  

import javax.xml.rpc.Call;
import javax.xml.rpc.ParameterMode;
import javax.xml.namespace.QName;

import com.sun.xml.rpc.client.StubPropertyConstants;
import com.sun.xml.rpc.client.dii.CallPropertyConstants;
                 
/**
 * This sample demonstrates how to enable content negotiation
 * for Fast Infoset. By default, content negotiation is turned
 * off but can be enabled by setting a property on a Stub or
 * Call. Once Fast Infoset is negotiated, any subsequent 
 * exchange between the client and the service will be in
 * "optimistic" mode (i.e., both the request and reply will
 * be encoded using Fast Infoset). 
 *
 * @author Santiago.PericasGeertsen@sun.com
 */
public class HelloClient {      

    HelloIF_Stub stub;
    HelloWorldService helloWorldService;
    Call call;
    
    public static void main(String[] args) { 
        try {
            // Set endpoint URL
            System.setProperty(
                "endpoint", 
                "http://localhost:8080/jaxrpc-HelloWorld/hello");
            
            HelloClient hc = new HelloClient();
            hc.setUp();

            hc.testStubInitialState();
            hc.testPessimisticContentNegotiation();

            hc.testCallInitialState();
            hc.testPessimisticContentNegotiationDII();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void setUp() {
        try {
            helloWorldService = new HelloWorldService_Impl();
            stub = (HelloIF_Stub) (helloWorldService.getHelloIFPort());           
            stub._setProperty(  
                javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY,   
                System.getProperty("endpoint"));
                        
            // Create a DII call to invoke sayHelloBack
            call = helloWorldService.createCall(
                new QName("http://hello.org/wsdl", "HelloIFPort"),
                new QName("http://hello.org/wsdl", "sayHelloBack"));
            call.setTargetEndpointAddress(System.getProperty("endpoint"));
            call.setProperty(
                "javax.xml.rpc.soap.operation.style",
                "rpc");
            call.setProperty(
                "javax.xml.rpc.encodingstyle.namespace.uri",
                "http://schemas.xmlsoap.org/soap/encoding/");
            call.addParameter(
                "String_1", 
                new QName("http://www.w3.org/2001/XMLSchema", "string"),
                ParameterMode.IN);
            call.setReturnType(
                new QName("http://www.w3.org/2001/XMLSchema", "string"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    void testStubInitialState() {
        assertTrue(getContentNegotiation() == "none");
    }
    
    void testPessimisticContentNegotiation() {
        // Set initial state
        setContentNegotiation("pessimistic");
        
        // Precondition
        assertTrue(getContentNegotiation() == "pessimistic");
        
        // XML request - FI reply
        try {
            System.out.println(stub.sayHelloBack("JAXRPC Sample")); 
        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }

        // Postcondition - FI is negotiated
        assertTrue(getContentNegotiation() == "optimistic");
    }
        
    // DII
    
    void testCallInitialState() {
        assertTrue(getContentNegotiationDII() == "none");
    }
    
    void testPessimisticContentNegotiationDII() {
        // Set initial state
        setContentNegotiationDII("pessimistic");
        
        // Precondition
        assertTrue(getContentNegotiationDII() == "pessimistic");
        
        // XML request - FI reply
        try {
            Object[] in = new Object[] { "JAXRPC Sample" };
            System.out.println((String) call.invoke(in));
        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }

        // Postcondition - FI is negotiated
        assertTrue(getContentNegotiationDII() == "optimistic");
    }
        
    // --- Utility Methods -----------------------------------------------
    
    private void setContentNegotiation(String value) {
        stub._setProperty(StubPropertyConstants.CONTENT_NEGOTIATION_PROPERTY,
                          value);
    }
    
    private String getContentNegotiation() {
        return (String) stub._getProperty(StubPropertyConstants.CONTENT_NEGOTIATION_PROPERTY);
    }
        
    // DII
    
    private void setContentNegotiationDII(String value) {
        call.setProperty(CallPropertyConstants.CONTENT_NEGOTIATION_PROPERTY,
                          value);
    }
    
    private String getContentNegotiationDII() {
        return (String) call.getProperty(CallPropertyConstants.CONTENT_NEGOTIATION_PROPERTY);
    }
    
    private void assertTrue(boolean value) {
        if (!value) throw new RuntimeException("Assertion failed.");
    }
}
