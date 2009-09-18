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

import org.eclipse.core.databinding.observable.list.WritableList;

import org.eclipse.riena.beans.common.Person;
import org.eclipse.riena.beans.common.PersonFactory;
import org.eclipse.riena.example.client.views.ContextMenuSubModuleView;
import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.ui.controllers.SubModuleController;
import org.eclipse.riena.ui.ridgets.IActionListener;
import org.eclipse.riena.ui.ridgets.IMenuItemRidget;
import org.eclipse.riena.ui.ridgets.ITableRidget;
import org.eclipse.riena.ui.ridgets.ITextRidget;
import org.eclipse.riena.ui.ridgets.IToggleButtonRidget;

/**
 * Controller for the {@link ContextMenuSubModuleView} example.
 */
public class ContextMenuSubModuleController extends SubModuleController {

	public ContextMenuSubModuleController() {
		this(null);
	}

	public ContextMenuSubModuleController(ISubModuleNode navigationNode) {
		super(navigationNode);
	}

	/**
	 * Binds and updates the ridgets.
	 */
	@Override
	public void configureRidgets() {
		ITextRidget textField = (ITextRidget) getRidget("textField"); //$NON-NLS-1$
		textField.updateFromModel();

		final IMenuItemRidget textSelectAll = (IMenuItemRidget) getRidget("textSelectAll"); //$NON-NLS-1$

		final IToggleButtonRidget markerButton = (IToggleButtonRidget) getRidget("markerButton"); //$NON-NLS-1$
		markerButton.addListener(new IActionListener() {
			public void callback() {
				boolean state = markerButton.isSelected();
				textSelectAll.setVisible(!state);
			}
		});

		final WritableList personList = new WritableList(PersonFactory.createPersonList(), Person.class);
		final ITableRidget table = (ITableRidget) getRidget("table"); //$NON-NLS-1$

		String[] columnPropertyNames = { Person.PROPERTY_FIRSTNAME, Person.PROPERTY_LASTNAME };
		String[] columnHeaders = { "FirstName", "LastName" }; //$NON-NLS-1$ //$NON-NLS-2$ 
		table.bindToModel(new WritableList(personList, Person.class), Person.class, columnPropertyNames, columnHeaders);
		table.updateFromModel();

		IMenuItemRidget tableRemoveSelected = (IMenuItemRidget) getRidget("tableRemove"); //$NON-NLS-1$
		tableRemoveSelected.addListener(new IActionListener() {
			public void callback() {
				int sel = table.getSelectionIndex();
				if (sel > -1) {
					personList.remove(sel);
					table.updateFromModel();
				}
			}
		});
	}
}
