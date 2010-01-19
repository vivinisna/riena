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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.riena.example.client.controllers.ValidationSubModuleController;
import org.eclipse.riena.navigation.ui.swt.views.SubModuleView;
import org.eclipse.riena.ui.swt.lnf.LnfKeyConstants;
import org.eclipse.riena.ui.swt.lnf.LnfManager;
import org.eclipse.riena.ui.swt.utils.UIControlsFactory;

/**
 * Sample for validation rules.
 */
public class ValidationSubModuleView extends SubModuleView<ValidationSubModuleController> {

	public static final String ID = ValidationSubModuleView.class.getName();

	@Override
	protected void basicCreatePartControl(Composite parent) {
		parent.setBackground(LnfManager.getLnf().getColor(LnfKeyConstants.SUB_MODULE_BACKGROUND));
		parent.setLayout(new GridLayout(1, false));

		GridDataFactory fillFactory = GridDataFactory.fillDefaults();

		Group group1 = createGroupOnEditValidation(parent);
		fillFactory.applyTo(group1);
	}

	// helping methods
	// ////////////////

	private Group createGroupOnEditValidation(Composite parent) {
		Group group = UIControlsFactory.createGroup(parent, "Validation Rules:"); //$NON-NLS-1$
		GridLayoutFactory.fillDefaults().margins(20, 20).numColumns(3).applyTo(group);

		UIControlsFactory.createLabel(group, ""); //$NON-NLS-1$
		UIControlsFactory.createLabel(group, "Control Value"); //$NON-NLS-1$
		UIControlsFactory.createLabel(group, "Ridget Value"); //$NON-NLS-1$

		GridDataFactory fillFactory = GridDataFactory.fillDefaults().grab(true, false);

		UIControlsFactory.createLabel(group, "Numbers only:"); //$NON-NLS-1$
		Text txtNumbersOnly = UIControlsFactory.createText(group);
		addUIControl(txtNumbersOnly, "txtNumbersOnly"); //$NON-NLS-1$
		fillFactory.applyTo(txtNumbersOnly);
		Text lblNumbersOnly = UIControlsFactory.createText(group);
		addUIControl(lblNumbersOnly, "lblNumbersOnly"); //$NON-NLS-1$
		fillFactory.applyTo(lblNumbersOnly);

		UIControlsFactory.createLabel(group, "Numbers only (direct writing):"); //$NON-NLS-1$
		Text txtNumbersOnlyDW = UIControlsFactory.createText(group);
		addUIControl(txtNumbersOnlyDW, "txtNumbersOnlyDW"); //$NON-NLS-1$
		fillFactory.applyTo(txtNumbersOnlyDW);
		Text lblNumbersOnlyDW = UIControlsFactory.createText(group);
		addUIControl(lblNumbersOnlyDW, "lblNumbersOnlyDW"); //$NON-NLS-1$
		fillFactory.applyTo(lblNumbersOnlyDW);

		UIControlsFactory.createLabel(group, "Characters only:"); //$NON-NLS-1$
		Text txtCharactersOnly = UIControlsFactory.createText(group);
		addUIControl(txtCharactersOnly, "txtCharactersOnly"); //$NON-NLS-1$
		fillFactory.applyTo(txtCharactersOnly);
		Text lblCharactersOnly = UIControlsFactory.createText(group);
		addUIControl(lblCharactersOnly, "lblCharactersOnly"); //$NON-NLS-1$
		fillFactory.applyTo(lblCharactersOnly);

		UIControlsFactory.createLabel(group, "Expression (PDX##):"); //$NON-NLS-1$
		Text txtExpression = UIControlsFactory.createText(group);
		addUIControl(txtExpression, "txtExpression"); //$NON-NLS-1$
		fillFactory.applyTo(txtExpression);
		Text lblExpression = UIControlsFactory.createText(group);
		addUIControl(lblExpression, "lblExpression"); //$NON-NLS-1$
		fillFactory.applyTo(lblExpression);

		UIControlsFactory.createLabel(group, "Length < 5:"); //$NON-NLS-1$
		Text txtLengthLessThan5 = UIControlsFactory.createText(group);
		addUIControl(txtLengthLessThan5, "txtLengthLessThan5"); //$NON-NLS-1$
		fillFactory.applyTo(txtLengthLessThan5);
		Text lblLengthLessThan5 = UIControlsFactory.createText(group);
		addUIControl(lblLengthLessThan5, "lblLengthLessThan5"); //$NON-NLS-1$
		fillFactory.applyTo(lblLengthLessThan5);

		UIControlsFactory.createLabel(group, "Required and Lowercase:"); //$NON-NLS-1$
		Text txtRequiredLowercase = UIControlsFactory.createText(group);
		addUIControl(txtRequiredLowercase, "txtRequiredLowercase"); //$NON-NLS-1$
		fillFactory.applyTo(txtRequiredLowercase);
		Text lblRequiredLowercase = UIControlsFactory.createText(group);
		addUIControl(lblRequiredLowercase, "lblRequiredLowercase"); //$NON-NLS-1$
		fillFactory.applyTo(lblRequiredLowercase);

		UIControlsFactory.createLabel(group, "Numeric Range 18 to 80:"); //$NON-NLS-1$
		Text txtRange18to80 = UIControlsFactory.createTextNumeric(group);
		addUIControl(txtRange18to80, "txtRange18to80"); //$NON-NLS-1$
		fillFactory.applyTo(txtRange18to80);
		Text lblRange18to80 = UIControlsFactory.createText(group);
		addUIControl(lblRange18to80, "lblRange18to80"); //$NON-NLS-1$
		fillFactory.applyTo(lblRange18to80);

		UIControlsFactory.createLabel(group, "Length between 5 and 10:"); //$NON-NLS-1$
		Text txtLength5to10 = UIControlsFactory.createText(group);
		addUIControl(txtLength5to10, "txtLength5to10"); //$NON-NLS-1$
		fillFactory.applyTo(txtLength5to10);
		Text lblLength5to10 = UIControlsFactory.createText(group);
		addUIControl(lblLength5to10, "lblLength5to10"); //$NON-NLS-1$
		fillFactory.applyTo(lblLength5to10);

		UIControlsFactory.createLabel(group, "Valid Date (dd.MM.yyyy):"); //$NON-NLS-1$
		Text txtDate = UIControlsFactory.createText(group);
		addUIControl(txtDate, "txtDate"); //$NON-NLS-1$
		fillFactory.applyTo(txtDate);
		Text lblDate = UIControlsFactory.createText(group);
		addUIControl(lblDate, "lblDate"); //$NON-NLS-1$
		fillFactory.applyTo(lblDate);

		Label lbl = UIControlsFactory.createLabel(group, "Valid eMail:"); //$NON-NLS-1$
		Text txtEmail = UIControlsFactory.createText(group);
		addUIControl(txtEmail, "txtEmail"); //$NON-NLS-1$
		fillFactory.applyTo(txtEmail);
		Text lblEmail = UIControlsFactory.createText(group);
		addUIControl(lblEmail, "lblEmail"); //$NON-NLS-1$
		fillFactory.applyTo(lblEmail);

		GridDataFactory.fillDefaults().grab(true, false).applyTo(lbl);

		return group;
	}

}