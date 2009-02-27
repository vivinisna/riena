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
package org.eclipse.riena.core.util;

import java.util.Arrays;

import org.eclipse.riena.tests.RienaTestCase;
import org.eclipse.riena.tests.collect.NonUITestCase;

/**
 * Test the {@code ArraysUtil} class.
 */
@NonUITestCase
public class ArraysUtilTest extends RienaTestCase {

	public void testCopy() {
		String[] source = new String[] { "1", "2", "3", "4" };
		assertTrue(Arrays.equals(new String[] {}, ArraysUtil.copyRange(source, 0, 0)));
		assertTrue(Arrays.equals(new String[] { "1" }, ArraysUtil.copyRange(source, 0, 1)));
		assertTrue(Arrays.equals(new String[] { "1", "2" }, ArraysUtil.copyRange(source, 0, 2)));
		assertTrue(Arrays.equals(new String[] { "2" }, ArraysUtil.copyRange(source, 1, 2)));
		assertTrue(Arrays.equals(new String[] { "3", "4" }, ArraysUtil.copyRange(source, 2, 4)));
		assertTrue(Arrays.equals(new String[] { "1", "2", "3", "4" }, ArraysUtil.copyRange(source, 0, 4)));
	}

	public void testCopyFailNullArray() {
		try {
			ArraysUtil.copyRange(null, 0, 2);
			fail();
		} catch (IllegalArgumentException e) {
			ok();
		}
	}

	public void testCopyFailNegativeSize() {
		try {
			String[] source = new String[] { "1", "2", "3", "4" };
			ArraysUtil.copyRange(source, 0, 5);
			fail();
		} catch (IllegalArgumentException e) {
			ok();
		}
	}

	public void testCopyFailSizeTooBig() {
		try {
			ArraysUtil.copyRange(null, 2, 0);
			fail();
		} catch (IllegalArgumentException e) {
			ok();
		}
	}
}
