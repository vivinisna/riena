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
package org.eclipse.riena.ui.filter.extension;

import org.eclipse.riena.core.injector.extension.ExtensionInterface;

/**
 * The rule to mark a ridget.
 */
@ExtensionInterface
public interface IRuleMarkerRidget {

	/**
	 * Returns the ID of a ridget.
	 * 
	 * @return ID
	 */
	String getRidgetId();

	/**
	 * Returns the type of a marker (e.g. hidden).
	 * 
	 * @return a string that represent the type of a marker
	 */
	String getMarker();

}