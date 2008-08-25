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
package org.eclipse.riena.core.extension;

import java.lang.reflect.Proxy;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Factory for interface beans.
 */
final class InterfaceBeanFactory {

	private InterfaceBeanFactory() {
		// utility
	}

	static Object newInstance(boolean symbolReplace, Class<?> interfaceType, IConfigurationElement configurationElement) {
		return Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class[] { interfaceType },
				new InterfaceBeanHandler(interfaceType, symbolReplace, configurationElement));
	}
}
