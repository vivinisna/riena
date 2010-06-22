/*******************************************************************************
 * Copyright (c) 2007, 2010 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.objecttransaction;

/**
 * This failure is thrown when the transaction is not in the right state for
 * this method
 * 
 */
public class InvalidTransactionFailure extends ObjectTransactionFailure {

	private static final long serialVersionUID = -1932517137846817105L;

	/**
	 * @param message
	 */
	public InvalidTransactionFailure(final String message) {
		super(message);
	}

}