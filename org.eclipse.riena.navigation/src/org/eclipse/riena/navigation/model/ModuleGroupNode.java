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
package org.eclipse.riena.navigation.model;

import java.util.List;

import org.eclipse.riena.navigation.IModuleGroupNode;
import org.eclipse.riena.navigation.IModuleNode;
import org.eclipse.riena.navigation.NavigationNodeId;
import org.eclipse.riena.navigation.listener.IModuleGroupNodeListener;

/**
 * Default implementation for the module group node
 */
public class ModuleGroupNode extends NavigationNode<IModuleGroupNode, IModuleNode, IModuleGroupNodeListener> implements
		IModuleGroupNode {

	private boolean presentWithSingleModule;

	/**
	 * Creates a ModuleGroupNode
	 */
	public ModuleGroupNode() {
		this(null);
	}

	public Class<IModuleNode> getValidChildType() {
		return IModuleNode.class;
	}

	/**
	 * Creates a ModuleGroupNode.
	 * 
	 * @param nodeId
	 *            Identifies the node in the application model tree.
	 */
	public ModuleGroupNode(NavigationNodeId nodeId) {
		super(nodeId);
		presentWithSingleModule = true;
	}

	public boolean isPresentWithSingleModule() {
		return presentWithSingleModule;
	}

	public void setPresentWithSingleModule(boolean pPresentWithSingleModule) {
		presentWithSingleModule = pPresentWithSingleModule;
		notifyPresentWithSingleModule();
	}

	private void notifyPresentWithSingleModule() {
		for (IModuleGroupNodeListener next : getListeners()) {
			next.presentWithSingleModuleChanged(this);
		}
	}

	public boolean isPresentGroupNode() {
		return isPresentWithSingleModule() || getChildren().size() > 1;
	}

	@Override
	public final boolean isVisible() {
		boolean visible = super.isVisible();
		if (visible) {
			visible = false;
			List<IModuleNode> children = getChildren();
			for (IModuleNode moduleNode : children) {
				if (moduleNode.isVisible()) {
					visible = true;
					break;
				}
			}
		}
		return visible;
	}

}