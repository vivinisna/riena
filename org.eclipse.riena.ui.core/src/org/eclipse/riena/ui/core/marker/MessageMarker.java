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
package org.eclipse.riena.ui.core.marker;

import org.eclipse.core.runtime.Assert;

import org.eclipse.riena.core.marker.AbstractMarker;

/**
 * Marks an adapter, with messages. The messages can be viewed by an
 * IMessageMarkerViewer
 */
public class MessageMarker extends AbstractMarker implements IMessageMarker {

	public MessageMarker() {
		super(false);
	}

	/**
	 * @return the Message in this Marker
	 */
	public String getMessage() {
		return (String) super.getAttribute(MESSAGE);
	}

	/**
	 * Basic constructor for the Message marker
	 * 
	 * @param message
	 *            - the Message of the Marker; never null
	 */
	public MessageMarker(String message) {
		super();
		Assert.isNotNull(message, "The message of the message marker must not be null"); //$NON-NLS-1$
		setAttribute(MESSAGE, message);
	}

}
