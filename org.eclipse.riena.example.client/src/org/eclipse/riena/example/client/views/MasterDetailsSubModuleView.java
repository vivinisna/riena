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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import org.eclipse.riena.example.client.controllers.MasterDetailsSubModuleController;
import org.eclipse.riena.navigation.ui.swt.views.SubModuleView;
import org.eclipse.riena.ui.ridgets.IMasterDetailsRidget;
import org.eclipse.riena.ui.swt.ChoiceComposite;
import org.eclipse.riena.ui.swt.MasterDetailsComposite;
import org.eclipse.riena.ui.swt.lnf.LnfKeyConstants;
import org.eclipse.riena.ui.swt.lnf.LnfManager;
import org.eclipse.riena.ui.swt.utils.UIControlsFactory;

/**
 * Demonstrates use of a master/details ridget.
 * 
 * @see IMasterDetailsRidget
 * @see MasterDetailsSubModuleController
 */
public class MasterDetailsSubModuleView extends SubModuleView<MasterDetailsSubModuleController> {

	public static final String ID = MasterDetailsSubModuleView.class.getName();

	public MasterDetailsSubModuleView() {
	}

	@Override
	protected void basicCreatePartControl(Composite parent) {
		parent.setBackground(LnfManager.getLnf().getColor(LnfKeyConstants.SUB_MODULE_BACKGROUND));
		FillLayout layout = new FillLayout(SWT.HORIZONTAL);
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		parent.setLayout(layout);
		createMasterDetails(parent);
	}

	// helping methods
	//////////////////

	private Group createMasterDetails(Composite parent) {
		Group result = UIControlsFactory.createGroup(parent, "Master/Details:"); //$NON-NLS-1$
		FillLayout layout = new FillLayout(SWT.HORIZONTAL);
		layout.marginHeight = 20;
		layout.marginWidth = 20;
		result.setLayout(layout);

		MasterDetailsComposite mdComposite = new MasterDetailsComposite(result, SWT.NONE, SWT.BOTTOM);
		Composite details = mdComposite.getDetails();
		details.setLayout(new GridLayout(2, false));

		UIControlsFactory.createLabel(details, "First Name:"); //$NON-NLS-1$
		Text txtFirst = UIControlsFactory.createText(details, SWT.BORDER, "first"); //$NON-NLS-1$
		txtFirst.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		UIControlsFactory.createLabel(details, "Last Name:"); //$NON-NLS-1$
		Text txtLast = UIControlsFactory.createText(details, SWT.BORDER, "last"); //$NON-NLS-1$
		txtLast.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		UIControlsFactory.createLabel(details, "Gender:"); //$NON-NLS-1$
		ChoiceComposite ccGender = new ChoiceComposite(details, SWT.NONE, false);
		ccGender.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		ccGender.setOrientation(SWT.HORIZONTAL);
		mdComposite.addUIControl(ccGender, "gender"); //$NON-NLS-1$

		UIControlsFactory.createLabel(details, "Pets:"); //$NON-NLS-1$
		ChoiceComposite ccPets = new ChoiceComposite(details, SWT.NONE, true);
		ccPets.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		ccPets.setOrientation(SWT.HORIZONTAL);
		mdComposite.addUIControl(ccPets, "pets"); //$NON-NLS-1$

		this.addUIControl(mdComposite, "master"); //$NON-NLS-1$

		return result;
	}
}