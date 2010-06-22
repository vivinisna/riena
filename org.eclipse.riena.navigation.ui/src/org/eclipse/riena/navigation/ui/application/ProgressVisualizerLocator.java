/*******************************************************************************
 * Copyright (c) 2007, 2010 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.navigation.ui.application;

import org.eclipse.riena.internal.navigation.ui.marker.UIProcessFinishedObserver;
import org.eclipse.riena.navigation.IApplicationNode;
import org.eclipse.riena.navigation.INavigationNode;
import org.eclipse.riena.navigation.ISubApplicationNode;
import org.eclipse.riena.navigation.ui.controllers.ApplicationController;
import org.eclipse.riena.navigation.ui.controllers.SubApplicationController;
import org.eclipse.riena.ui.core.uiprocess.IProgressVisualizer;
import org.eclipse.riena.ui.core.uiprocess.IProgressVisualizerLocator;
import org.eclipse.riena.ui.core.uiprocess.IProgressVisualizerObserver;
import org.eclipse.riena.ui.core.uiprocess.ProgressVisualizer;
import org.eclipse.riena.ui.ridgets.IStatuslineUIProcessRidget;

public class ProgressVisualizerLocator implements IProgressVisualizerLocator {

	@SuppressWarnings("unchecked")
	public IProgressVisualizer getProgressVisualizer(final Object context) {
		final IProgressVisualizer aVisualizer = new ProgressVisualizer();
		if (context != null && INavigationNode.class.isAssignableFrom(context.getClass())) {
			final INavigationNode node = INavigationNode.class.cast(context);
			final IStatuslineUIProcessRidget statuslineUIProcessRidget = ((ApplicationController) node.getParentOfType(
					IApplicationNode.class).getNavigationNodeController()).getStatusline()
					.getStatuslineUIProcessRidget();
			if (statuslineUIProcessRidget != null) {
				aVisualizer.addObserver(statuslineUIProcessRidget);
			}
			ISubApplicationNode subApp = (ISubApplicationNode) node.getParentOfType(ISubApplicationNode.class);
			if (subApp == null && context instanceof ISubApplicationNode) {
				subApp = (ISubApplicationNode) context;
			}
			if (subApp != null) {
				aVisualizer.addObserver(getUIProcessRidget(subApp));
				aVisualizer.addObserver(createObserver(node));
			}
		}
		return aVisualizer;
	}

	private UIProcessFinishedObserver createObserver(final INavigationNode<?> node) {
		return new UIProcessFinishedObserver(node);
	}

	private IProgressVisualizerObserver getUIProcessRidget(final ISubApplicationNode subApp) {
		return ((SubApplicationController) subApp.getNavigationNodeController()).getUiProcessRidget();
	}
}
