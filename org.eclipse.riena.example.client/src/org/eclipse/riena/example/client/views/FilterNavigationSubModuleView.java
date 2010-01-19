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
package org.eclipse.riena.example.client.views;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.riena.example.client.controllers.FilterNavigationSubModuleController;
import org.eclipse.riena.navigation.ui.swt.views.SubModuleView;
import org.eclipse.riena.ui.swt.ChoiceComposite;
import org.eclipse.riena.ui.swt.utils.UIControlsFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * View of the sub module that demonstrates UI filters for navigation nodes.
 */
public class FilterNavigationSubModuleView extends SubModuleView<FilterNavigationSubModuleController> {

	@Override
	protected void basicCreatePartControl(Composite parent) {

		parent.setLayout(new GridLayout(1, false));

		Group group1 = createFiltersGroup(parent);
		GridDataFactory.fillDefaults().grab(false, false).applyTo(group1);

	}

	private Group createFiltersGroup(Composite parent) {

		Group group = UIControlsFactory.createGroup(parent, "UI-Filters (Navigation)"); //$NON-NLS-1$

		int defaultVSpacing = new GridLayout().verticalSpacing;
		GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).margins(10, 20).spacing(10, defaultVSpacing)
				.applyTo(group);

		Label label1 = UIControlsFactory.createLabel(group, "Node ID"); //$NON-NLS-1$
		GridDataFactory.fillDefaults().grab(false, false).span(1, 1).applyTo(label1);
		Text nodeId = UIControlsFactory.createText(group);
		addUIControl(nodeId, "nodeId"); //$NON-NLS-1$
		GridDataFactory.fillDefaults().grab(true, false).span(1, 1).applyTo(nodeId);

		ChoiceComposite filterType = new ChoiceComposite(group, SWT.NONE, false);
		filterType.setOrientation(SWT.HORIZONTAL);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(filterType);
		addUIControl(filterType, "filterType"); //$NON-NLS-1$

		Combo filterTypeValues = UIControlsFactory.createCombo(group);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(filterTypeValues);
		addUIControl(filterTypeValues, "filterTypeValues"); //$NON-NLS-1$

		Button addFilter = UIControlsFactory.createButton(group);
		addFilter.setText("Add Filter"); //$NON-NLS-1$
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(addFilter);
		addUIControl(addFilter, "addFilter"); //$NON-NLS-1$

		Button removeFilters = UIControlsFactory.createButton(group);
		removeFilters.setText("Remove All Filters"); //$NON-NLS-1$
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(removeFilters);
		addUIControl(removeFilters, "removeFilters"); //$NON-NLS-1$

		return group;

	}

}