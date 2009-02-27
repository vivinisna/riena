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
package org.eclipse.riena.core.exception;

import org.eclipse.riena.core.exception.IExceptionHandlerManager.Action;

import org.eclipse.equinox.log.Logger;

/**
 * ExceptionHandler can handle exceptions
 */
public interface IExceptionHandler {

	/**
	 * Check if the exception passed can be handled and return an {@link Action}
	 * how might to process. This method should be used anywhere where an
	 * exceptions is catched directly.
	 * 
	 * @pre t != null
	 * @post result != null
	 * 
	 * @param t
	 *            exception to be handled
	 * @param msg
	 *            an optional message
	 * @param logger
	 *            to be used for logging
	 * 
	 * @return the Action how to process
	 * 
	 */
	Action handleException(Throwable t, String msg, Logger logger);
}
