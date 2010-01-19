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
package org.eclipse.riena.ui.ridgets.databinding;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.riena.internal.core.test.collect.NonGatherableTestCase;

/**
 * All tests of the package org.eclipse.riena.ui.ridgets.databinding.
 */
@NonGatherableTestCase("This is not a �TestCase�!")
public final class AllTests {

	private AllTests() {
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.riena.ui.ridgets.databinding");
		// $JUnit-BEGIN$
		suite.addTestSuite(DateToStringConverterTest.class);
		suite.addTestSuite(GregorianCalendarToStringConverterTest.class);
		suite.addTestSuite(RidgetUpdateValueStrategyTest.class);
		suite.addTestSuite(StringToGregorianCalendarConverterTest.class);
		// $JUnit-END$
		return suite;
	}

}