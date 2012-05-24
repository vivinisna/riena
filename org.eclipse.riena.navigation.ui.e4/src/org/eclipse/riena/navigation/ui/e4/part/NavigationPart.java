package org.eclipse.riena.navigation.ui.e4.part;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.riena.navigation.ISubApplicationNode;
import org.eclipse.riena.navigation.ui.swt.presentation.SwtViewProvider;
import org.eclipse.riena.navigation.ui.swt.views.NavigationViewPart;

public class NavigationPart {
	@Inject
	MWindow window;

	@Inject
	EModelService modelService;

	@Inject
	public void createUI(final Composite parent) {
		new NavigationViewPart() {
			@Override
			public ISubApplicationNode getSubApplicationNode() {
				final MPerspective activePerspective = modelService.getActivePerspective(window);
				final String perspectiveId = activePerspective.getElementId();
				return SwtViewProvider.getInstance().getNavigationNode(perspectiveId, ISubApplicationNode.class);
			};
		}.createPartControl(parent);
	}
}
