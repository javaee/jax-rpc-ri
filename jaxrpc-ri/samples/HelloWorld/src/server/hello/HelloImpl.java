/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package hello;

public class HelloImpl implements hello.HelloIF, java.rmi.Remote {

    public void sayHelloBackOneWay(java.lang.String str) {
    }

    public String sayHelloBack(java.lang.String str) {
	     String result = " Hi " + str + " From Server for wsdl client";
	     System.out.println("In sayHello for WSDL : " + str);
        return result;
    }
}
