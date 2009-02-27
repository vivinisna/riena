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
package org.eclipse.riena.ui.ridgets.validation.tests;

import org.eclipse.riena.tests.RienaTestCase;
import org.eclipse.riena.tests.collect.NonUITestCase;
import org.eclipse.riena.ui.ridgets.validation.MinLength;
import org.eclipse.riena.ui.ridgets.validation.ValidationFailure;

/**
 * Tests for the MinLength rule.
 */
@NonUITestCase
public class MinLengthTest extends RienaTestCase {

	/**
	 * @throws Exception
	 *             Handled by JUnit.
	 */
	public void testLength() throws Exception {

		final MinLength rule = new MinLength(10);
		try {
			rule.validate(new Object());
			fail("expected a thrown ValidationFailure");
		} catch (final ValidationFailure f) {
			ok("ValidationFailure expected");
		}

		assertTrue(rule.validate("0123456789ab").isOK());
		assertTrue(rule.validate("0123456789a").isOK());
		// whitespace
		assertTrue(rule.validate("01 3 56 89a").isOK());
		assertTrue(rule.validate("0123456789").isOK());

		assertFalse(rule.validate("012345678").isOK());
		assertFalse(rule.validate("abcde").isOK());
		// null is treated like a blank string
		assertFalse(rule.validate(null).isOK());

		final MinLength zeroLengthRule = new MinLength(0);
		assertTrue(zeroLengthRule.validate("abc").isOK());
		assertTrue(zeroLengthRule.validate("a").isOK());
		// whitespace
		assertTrue(zeroLengthRule.validate(" ").isOK());
		assertTrue(zeroLengthRule.validate("").isOK());
		assertTrue(zeroLengthRule.validate(null).isOK());

	}

	/**
	 * Tests the method {@code setInitializationData}.
	 * 
	 * @throws Exception
	 *             - Handled by JUnit.
	 */
	public void testSetInitializationData() throws Exception {

		MinLength rule = new MinLength();
		assertTrue(rule.validate("").isOK());

		rule = new MinLength();
		rule.setInitializationData(null, null, "5");
		assertFalse(rule.validate("1").isOK());
		assertTrue(rule.validate("12345").isOK());

		rule = new MinLength();
		rule.setInitializationData(null, null, "6,7");
		assertFalse(rule.validate("12345").isOK());
		assertTrue(rule.validate("123456").isOK());

	}

}
