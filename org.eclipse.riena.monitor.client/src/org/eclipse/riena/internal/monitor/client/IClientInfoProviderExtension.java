/*******************************************************************************
 * Copyright (c) 2007, 2013 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.internal.monitor.client;

import org.eclipse.riena.core.injector.extension.ExtensionInterface;
import org.eclipse.riena.core.injector.extension.MapName;
import org.eclipse.riena.monitor.client.IClientInfoProvider;

/**
 * Extension interface for the {@code IClientInfoProvider} definition.
 */
@ExtensionInterface(id = "clientInfoProvider")
public interface IClientInfoProviderExtension {

	/**
	 * Return the descriptive name of the client info provider.
	 * 
	 * @return the client info provider
	 */
	String getName();

	/**
	 * Create the configured {@code IClientInfoProvider}.
	 * 
	 * @return the client info provider
	 */
	@MapName("class")
	IClientInfoProvider createClientInfoProvider();

}
