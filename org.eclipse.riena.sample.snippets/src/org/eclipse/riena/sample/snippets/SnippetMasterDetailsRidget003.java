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
package org.eclipse.riena.sample.snippets;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.eclipse.riena.beans.common.Person;
import org.eclipse.riena.beans.common.PersonFactory;
import org.eclipse.riena.ui.core.marker.ValidationTime;
import org.eclipse.riena.ui.ridgets.AbstractMasterDetailsDelegate;
import org.eclipse.riena.ui.ridgets.IMasterDetailsRidget;
import org.eclipse.riena.ui.ridgets.IRidgetContainer;
import org.eclipse.riena.ui.ridgets.ITextRidget;
import org.eclipse.riena.ui.ridgets.swt.SwtRidgetFactory;
import org.eclipse.riena.ui.ridgets.validation.NotEmpty;
import org.eclipse.riena.ui.swt.MasterDetailsComposite;
import org.eclipse.riena.ui.swt.utils.UIControlsFactory;

/**
 * Demonstrates a master details widget that asks for confirmation before
 * removal.
 */
public final class SnippetMasterDetailsRidget003 {

	private SnippetMasterDetailsRidget003() {
		// "utility class"
	}

	/**
	 * A master details widget with a text fields for renaming a person.
	 */
	private static final class PersonMasterDetails extends MasterDetailsComposite {

		PersonMasterDetails(Composite parent, int style) {
			super(parent, style, SWT.BOTTOM);
		}

		@Override
		protected void createDetails(Composite parent) {
			GridLayoutFactory.fillDefaults().numColumns(2).margins(20, 20).spacing(10, 10).equalWidth(false).applyTo(
					parent);
			GridDataFactory hFill = GridDataFactory.fillDefaults().grab(true, false);

			UIControlsFactory.createLabel(parent, "Last Name:"); //$NON-NLS-1$
			Text txtLast = UIControlsFactory.createText(parent);
			hFill.applyTo(txtLast);
			addUIControl(txtLast, "txtLast"); //$NON-NLS-1$

			UIControlsFactory.createLabel(parent, "First Name:"); //$NON-NLS-1$
			Text txtFirst = UIControlsFactory.createText(parent);
			hFill.applyTo(txtFirst);
			addUIControl(txtFirst, "txtFirst"); //$NON-NLS-1$
		}

		/**
		 * This method creates a confirmation dialog, to be shown on before
		 * removing. Return {@code false} to veto removal.
		 */
		public boolean confirmRemove(Object item) {
			String title = "Confirm Remove"; //$NON-NLS-1$
			String message = String.format("Delete '%s' ?", item.toString()); //$NON-NLS-1$
			boolean result = MessageDialog.openQuestion(getShell(), title, message);
			return result;
		}
	}

	/**
	 * A IMasterDetailsDelegate that renames a person.
	 */
	private static final class PersonDelegate extends AbstractMasterDetailsDelegate {

		private final Person workingCopy = createWorkingCopy();

		public void configureRidgets(IRidgetContainer container) {
			ITextRidget txtLast = (ITextRidget) container.getRidget("txtLast"); //$NON-NLS-1$
			txtLast.bindToModel(workingCopy, Person.PROPERTY_LASTNAME);
			txtLast.addValidationRule(new NotEmpty(), ValidationTime.ON_UI_CONTROL_EDIT);
			txtLast.updateFromModel();

			ITextRidget txtFirst = (ITextRidget) container.getRidget("txtFirst"); //$NON-NLS-1$
			txtFirst.bindToModel(workingCopy, Person.PROPERTY_FIRSTNAME);
			txtFirst.updateFromModel();
		}

		public Person copyBean(Object source, Object target) {
			Person from = source != null ? (Person) source : createWorkingCopy();
			Person to = target != null ? (Person) target : createWorkingCopy();
			to.setFirstname(from.getFirstname());
			to.setLastname(from.getLastname());
			return to;
		}

		public Person createWorkingCopy() {
			return new Person("", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		public Person getWorkingCopy() {
			return workingCopy;
		}

		@Override
		public boolean isChanged(Object source, Object target) {
			Person p1 = (Person) source;
			Person p2 = (Person) target;
			boolean equal = p1.getFirstname().equals(p2.getFirstname()) && p1.getLastname().equals(p2.getLastname());
			return !equal;
		}
	}

	public static void main(String[] args) {
		Display display = Display.getDefault();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		PersonMasterDetails details = new PersonMasterDetails(shell, SWT.NONE);

		final IMasterDetailsRidget ridget = (IMasterDetailsRidget) SwtRidgetFactory.createRidget(details);
		ridget.setDelegate(new PersonDelegate());
		WritableList input = new WritableList(PersonFactory.createPersonList(), Person.class);
		String[] properties = { Person.PROPERTY_LASTNAME, Person.PROPERTY_FIRSTNAME };
		String[] headers = { "Last Name", "First Name" }; //$NON-NLS-1$ //$NON-NLS-2$
		ridget.bindToModel(input, Person.class, properties, headers);
		ridget.updateFromModel();

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
	}
}