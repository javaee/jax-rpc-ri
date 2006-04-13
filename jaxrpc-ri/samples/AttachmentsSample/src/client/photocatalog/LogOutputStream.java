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
import java.io.PrintStream;
import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class LogOutputStream extends PrintStream {
	boolean autoFlush = false;

	public LogOutputStream(OutputStream out) {
		super(out);
		this.out = out;
	}

	public LogOutputStream(OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
		this.out = out;
		this.autoFlush = autoFlush;
	}

	public LogOutputStream reopen() throws FileNotFoundException {
		out = new FileOutputStream(System.getProperty("log.dir") +
				System.getProperty("file.separator") +
				System.getProperty("soap.msgs.file"), true);
		LogOutputStream log = new LogOutputStream(out, autoFlush);

		return log;
	}

	public void close() {
		super.close();
	}

}

