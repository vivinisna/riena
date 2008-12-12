/*******************************************************************************
 * Copyright (c) 2007, 2008 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.internal.navigation.ui.filter;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.riena.core.util.StringMatcher;
import org.eclipse.riena.ui.core.marker.ValidationTime;
import org.eclipse.riena.ui.filter.IUIFilterRuleValidatorRidget;
import org.eclipse.riena.ui.filter.impl.AbstractUIFilterRuleValidator;
import org.eclipse.riena.ui.ridgets.IEditableRidget;

/**
 * Filter rule to provide a validator for a ridget.
 */
public class UIFilterRuleRidgetValidator extends AbstractUIFilterRuleValidator implements IUIFilterRuleValidatorRidget {

	private RidgetMatcher matcher;
	private String idPattern;

	/**
	 * Creates a new instance of {@code UIFilterRuleRidgetValidator}.
	 */
	public UIFilterRuleRidgetValidator() {
		super();
	}

	/**
	 * Creates a new instance of {@code UIFilterRuleRidgetValidator}.
	 * 
	 * @param idPattern
	 *            - pattern ({@link StringMatcher}) for ridget IDs
	 * @param validator
	 *            - validator
	 * @param validationTime
	 *            - time of validation
	 */
	public UIFilterRuleRidgetValidator(String idPattern, IValidator validator, ValidationTime validationTime) {
		super(validator, validationTime);
		this.idPattern = idPattern;
	}

	/**
	 * Returns and - if necessary - creates the matcher.
	 * 
	 * @return matcher
	 */
	private RidgetMatcher getMatcher() {
		if (matcher == null) {
			matcher = new RidgetMatcher(idPattern);
		}
		return matcher;
	}

	/**
	 * This method compares the ID of this rule and the given ID of a ridget.
	 * 
	 * @see org.eclipse.riena.ui.internal.IUIFilterRule.IUIFilterAttribute#matches(java.lang.Object)
	 */
	public boolean matches(Object... args) {
		if ((args == null) || (args.length <= 0)) {
			return false;
		}
		return getMatcher().matches(args);
	}

	/**
	 * Adds the validator of this rule to the given object (if the object is an
	 * editable ridget).
	 * 
	 * @see org.eclipse.riena.ui.internal.IUIFilterRule.IUIFilterAttribute#apply(java.lang.Object)
	 */
	public void apply(Object object) {

		if (object instanceof IEditableRidget) {
			IEditableRidget editableRidget = (IEditableRidget) object;
			editableRidget.addValidationRule(getValidator(), getValidationTime());
			editableRidget.updateFromModel();
		}

	}

	/**
	 * Removes the validator of this rule from the given object (if the object
	 * is an editable ridget).
	 * 
	 * @see org.eclipse.riena.ui.internal.IUIFilterRule.IUIFilterAttribute#remove(java.lang.Object)
	 */
	public void remove(Object object) {

		if (object instanceof IEditableRidget) {
			IEditableRidget editableRidget = (IEditableRidget) object;
			editableRidget.removeValidationRule(getValidator());
			editableRidget.updateFromModel();
		}

	}

	public void setId(String idPattern) {
		this.idPattern = idPattern;
		matcher = null;
	}

}