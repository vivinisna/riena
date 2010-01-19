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

/**
 * Contains additional navigation information that is passed on to the opened
 * node during its creation.
 * 
 * @see INavigationNode#navigate(INavigationNodeId, NavigationArgument)
 */
public class NavigationArgument {

	/**
	 * this key is used in INavigationNode.getContext to address THIS (the
	 * NavigationArgument)
	 */
	public static final String CONTEXTKEY_ARGUMENT = "riena.navigation.argument"; //$NON-NLS-1$

	public static final String CONTEXTKEY_PARAMETER = "riena.navigation.parameter"; //$NON-NLS-1$

	private Object parameter;
	private NavigationNodeId parentNodeId;
	private IUpdateListener updateListener = null;
	private String ridgetId;

	/**
	 */
	public NavigationArgument() {
		super();
	}

	/**
	 * @param parameter
	 *            parameter object that is passed to the opened node.
	 */
	public NavigationArgument(Object parameter) {
		super();
		this.parameter = parameter;
	}

	/**
	 * @param parameter
	 *            parameter object that is passed to the opened node.
	 * @param ridgetId
	 *            ID of the ridget that will get the initial focus in the view
	 *            associated with the opened node
	 * @since 1.2
	 */
	public NavigationArgument(Object parameter, String ridgetId) {
		this(parameter);
		this.ridgetId = ridgetId;
	}

	/**
	 * @param parameter
	 *            parameter object that is passed to the opened node.
	 * @param parentNodeId
	 *            overrides the parentTypeId specified for the containing
	 *            assembly extension. The type of the specified parent node has
	 *            to be identical to the type of the original node.
	 */
	public NavigationArgument(Object parameter, NavigationNodeId parentNodeId) {
		super();
		this.parameter = parameter;
		this.parentNodeId = parentNodeId;
	}

	/**
	 * @param parameter
	 *            parameter object that is passed to the opened node.
	 * @param parentNodeId
	 *            overrides the parentTypeId specified for the containing
	 *            assembly extension. The type of the specified parent node has
	 *            to be identical to the type of the original node.
	 * @param ridgetId
	 *            ID of the ridget that will get the initial focus in the view
	 *            associated with the opened node
	 * @since 1.2
	 */
	public NavigationArgument(Object parameter, NavigationNodeId parentNodeId, String ridgetId) {
		super();
		this.parameter = parameter;
		this.parentNodeId = parentNodeId;
		this.ridgetId = ridgetId;
	}

	/**
	 * @param parameter
	 *            parameter object that is passed to the opened node.
	 * @param updateListener
	 *            the specified updateListener is informed about update changes
	 *            using this NavigationArgument. The opened node can use
	 *            fireValueChanged() to inform the caller about changes in the
	 *            opened node on which the caller can react.
	 * @param parentNodeId
	 *            overrides the parentTypeId specified for the containing
	 *            assembly extension. The type of the specified parent node has
	 *            to be identical to the type of the original node.
	 */
	public NavigationArgument(Object parameter, IUpdateListener updateListener, NavigationNodeId parentNodeId) {
		super();
		this.parameter = parameter;
		this.parentNodeId = parentNodeId;
		this.updateListener = updateListener;
	}

	/**
	 * @return ID of the ridget that will get the initial focus in the view
	 *         associated with the opened node
	 * @since 1.2
	 */
	public String getRidgetId() {
		return ridgetId;
	}

	/**
	 * @return the parameter object that is passed to the opened node using this
	 *         NavigationArgument.
	 */
	public Object getParameter() {
		return parameter;
	}

	/**
	 * @return the parentNodeId.
	 */
	public NavigationNodeId getParentNodeId() {
		return parentNodeId;
	}

	/**
	 * @return the update listener that will be informed about changes in the
	 *         opened node when fireValueChanged is called.
	 */
	public IUpdateListener getUpdateListener() {
		return updateListener;
	}

	/**
	 * Notify the update listener of this NavigationArgument about changes in
	 * the opened node.
	 */
	public void fireValueChanged(Object parameter) {
		if (updateListener == null) {
			return;
		}
		updateListener.handleUpdate(parameter);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parameter == null) ? 0 : parameter.hashCode());
		result = prime * result + ((ridgetId == null) ? 0 : ridgetId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		NavigationArgument other = (NavigationArgument) obj;
		if (parameter == null) {
			if (other.parameter != null) {
				return false;
			}
		} else if (!parameter.equals(other.parameter)) {
			return false;
		}
		if (ridgetId == null) {
			if (other.ridgetId != null) {
				return false;
			}
		} else if (!ridgetId.equals(other.ridgetId)) {
			return false;
		}
		return true;
	}

}