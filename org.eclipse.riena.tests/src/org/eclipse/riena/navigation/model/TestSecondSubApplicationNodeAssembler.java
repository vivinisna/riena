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
package org.eclipse.riena.navigation.model;

import org.eclipse.riena.navigation.AbstractNavigationAssembler;
import org.eclipse.riena.navigation.INavigationNode;
import org.eclipse.riena.navigation.ISubApplicationNode;
import org.eclipse.riena.navigation.NavigationArgument;
import org.eclipse.riena.navigation.NavigationNodeId;

public class TestSecondSubApplicationNodeAssembler extends AbstractNavigationAssembler {

	/**
	 * @see org.eclipse.riena.navigation.INavigationAssembler#buildNode(org.eclipse.riena.navigation.NavigationNodeId,
	 *      org.eclipse.riena.navigation.NavigationArgument)
	 */
	public INavigationNode<?>[] buildNode(final NavigationNodeId navigationNodeId,
			final NavigationArgument navigationArgument) {
		final ISubApplicationNode subApplication = new SubApplicationNode(navigationNodeId);
		return new ISubApplicationNode[] { subApplication };
	}

	/**
	 * @see org.eclipse.riena.navigation.INavigationAssembler#acceptsTargetId(String)
	 */
	public boolean acceptsToBuildNode(final NavigationNodeId nodeId, final NavigationArgument argument) {

		return nodeId.getTypeId().equals("org.eclipse.riena.navigation.model.test.secondSubApplication");
	}

	@Override
	public String getParentNodeId() {
		return "org.eclipse.riena.navigation.model.test.application";
	}
}
