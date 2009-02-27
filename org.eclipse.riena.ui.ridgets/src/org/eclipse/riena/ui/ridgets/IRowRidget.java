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
package org.eclipse.riena.ui.ridgets;

/**
 * TODO [ev] docs
 * <p>
 * Implemementors should subclass {@link AbstractCompositeRidget}
 * 
 * @see AbstractCompositeRidget
 */
public interface IRowRidget extends IComplexRidget {

	/**
	 * Injects the data for this row.
	 * 
	 * @param rowData
	 *            an object with the data for this row
	 */
	void setData(Object rowData);
}
