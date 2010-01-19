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
package org.eclipse.riena.internal.ui.workarea.registry;

import org.eclipse.riena.navigation.INavigationNode;
import org.eclipse.riena.ui.workarea.IWorkareaDefinition;
import org.eclipse.riena.ui.workarea.spi.AbstractWorkareaDefinitionRegistry;

final class ExplicitWorkareaDefinitionRegistry extends AbstractWorkareaDefinitionRegistry {

	private static final ExplicitWorkareaDefinitionRegistry INSTANCE = new ExplicitWorkareaDefinitionRegistry();

	static ExplicitWorkareaDefinitionRegistry getInstance() {
		return INSTANCE;
	}

	private ExplicitWorkareaDefinitionRegistry() {
	}

	@Override
	public IWorkareaDefinition register(Object id, IWorkareaDefinition definition) {

		if (id instanceof INavigationNode<?>) {
			return registerDefinition((INavigationNode<?>) id, definition);
		} else {
			return super.register(id, definition);
		}
	}

	private IWorkareaDefinition registerDefinition(INavigationNode<?> node, IWorkareaDefinition definition) {

		// this may be specific - register with submodule
		workareas.put(node, definition);

		if (node.getNodeId() != null && node.getNodeId().getTypeId() != null) {
			internalRegister(node.getNodeId().getTypeId(), definition);
		}

		return definition;
	}
}