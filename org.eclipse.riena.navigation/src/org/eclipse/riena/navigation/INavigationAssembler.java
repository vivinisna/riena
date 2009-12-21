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
package org.eclipse.riena.navigation;

import org.eclipse.riena.navigation.model.SimpleNavigationNodeProvider;

/**
 * Creates a node or a subtree for the application model tree.
 */
public interface INavigationAssembler {

	/**
	 * @return The extension interface representing the definition of the
	 *         navigation node extension point
	 */
	INavigationAssemblyExtension getAssembly();

	/**
	 * Set the extension interface representing the definition of the navigation
	 * node extension point. In the current implementation this extension
	 * interface would be injected by the {@link SimpleNavigationNodeProvider}
	 * 
	 * @param nodeDefinition
	 */
	void setAssembly(INavigationAssemblyExtension nodeDefinition);

	/**
	 * Creates a node or an application model subtree. The node (or in the case
	 * of a subtree the root node) created should have the specified node ID. If
	 * the assembler sets a different node ID (e.g. with a consecutively
	 * numbered instance ID) then the node will not be found during later
	 * navigate(..) or create(..)-calls and a new node will be build every time.
	 * This may be desired though.
	 * 
	 * @see INavigationNode#navigate(NavigationNodeId)
	 * @see INavigationNode#create(NavigationNodeId)
	 * @param nodeId
	 *            The ID of the node to create.
	 * @param navigationArgument
	 *            Optional argument passed on from the navigate(..) method. May
	 *            be null.
	 * @return The created node or subtree root node.
	 */
	INavigationNode<?> buildNode(NavigationNodeId nodeId, NavigationArgument navigationArgument);

	/**
	 * @param nodeId
	 *            The ID of the node to create.
	 * @param argument
	 *            The navigation argument. May provide additional information
	 *            for node creation.
	 * @return <code>true</code> if this assembler knows how to build the node
	 *         with the provided id, <code>false</code> otherwise.
	 */
	boolean acceptsToBuildNode(NavigationNodeId nodeId, NavigationArgument argument);
}
