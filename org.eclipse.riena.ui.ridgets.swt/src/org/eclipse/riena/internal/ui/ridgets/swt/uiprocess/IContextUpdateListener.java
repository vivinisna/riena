/*******************************************************************************
 * Copyright (c) 2007, 2008 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.internal.ui.ridgets.swt.uiprocess;

/**
 * Implementations can be used to observe changes of visual contexts registered
 * at the {@link IVisualContextManager}.
 * 
 */
public interface IContextUpdateListener {

	/**
	 * gets called before the state of the observed context changes
	 * 
	 * @param context
	 *            - the observed context
	 */
	void beforeContextUpdate(Object context);

	/**
	 * notification for context updates
	 * 
	 * @param context
	 *            - the observed context
	 */
	void contextUpdated(Object context);

}
