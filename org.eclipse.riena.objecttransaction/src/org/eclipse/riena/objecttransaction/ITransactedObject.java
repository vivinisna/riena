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
package org.eclipse.riena.objecttransaction;

/**
 * Interface that must be implemented by all transaction enabled Business
 * Objects. It has method to retrieve a unique id and a version for any object
 * 
 */
public interface ITransactedObject {

	/**
	 * Returns the object id of the transacted object
	 * 
	 * @return IObjectId
	 */
	IObjectId getObjectId();

	/**
	 * changes the object id of the transacted object to the new object id
	 * 
	 * @param objectId
	 */
	void setObjectId(IObjectId objectId);

	/**
	 * Returns the current version of the transacted object
	 * 
	 * @return String version String
	 */
	String getVersion();

	/**
	 * Sets a new version string for a transacted object
	 * 
	 * @param versionString
	 *            version String
	 */
	void setVersion(String versionString);

}