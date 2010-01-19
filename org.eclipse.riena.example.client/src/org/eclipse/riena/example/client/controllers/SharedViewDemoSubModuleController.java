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
package org.eclipse.riena.example.client.controllers;

import org.eclipse.riena.beans.common.Person;
import org.eclipse.riena.example.client.views.SharedViewDemoSubModuleView;
import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.ui.controllers.SubModuleController;
import org.eclipse.riena.ui.ridgets.ITextRidget;

/**
 * Controller for {@link SharedViewDemoSubModuleView}.
 */
public class SharedViewDemoSubModuleController extends SubModuleController {

	private Person personBean;

	public SharedViewDemoSubModuleController() {
		this(null);
	}

	public SharedViewDemoSubModuleController(ISubModuleNode navigationNode) {
		super(navigationNode);
		personBean = new Person("Max", "Muster"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void configureRidgets() {
		ITextRidget txtFirst = (ITextRidget) getRidget("txtFirst"); //$NON-NLS-1$
		txtFirst.bindToModel(personBean, Person.PROPERTY_FIRSTNAME);

		ITextRidget txtLast = (ITextRidget) getRidget("txtLast"); //$NON-NLS-1$
		txtLast.bindToModel(personBean, Person.PROPERTY_LASTNAME);
	}

}