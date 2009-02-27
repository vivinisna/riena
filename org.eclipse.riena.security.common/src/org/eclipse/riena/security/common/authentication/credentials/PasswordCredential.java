/*******************************************************************************
 * Copyright (c) 2007, 2009 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.security.common.authentication.credentials;

/**
 * @author campo
 * 
 */
public class PasswordCredential extends AbstractCredential {

	private char[] password;
	private boolean echoOn;

	/**
	 * @param prompt
	 */
	public PasswordCredential(String prompt, boolean echoOn) {
		super(prompt);
		this.echoOn = echoOn;
	}

	public char[] getPassword() {
		if (password == null) {
			return null;
		}
		return password.clone();
	}

	public void setPassword(char[] password) {
		if (password != null) {
			this.password = password.clone();
		} else {
			this.password = null;
		}
	}

	public boolean isEchoOn() {
		return echoOn;
	}

}
