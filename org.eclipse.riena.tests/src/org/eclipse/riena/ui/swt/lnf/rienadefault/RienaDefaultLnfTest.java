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
package org.eclipse.riena.ui.swt.lnf.rienadefault;

import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.swt.graphics.Color;

import org.eclipse.riena.tests.collect.NonUITestCase;
import org.eclipse.riena.ui.swt.lnf.ILnfResource;
import org.eclipse.riena.ui.swt.lnf.ILnfTheme;
import org.eclipse.riena.ui.swt.lnf.LnfKeyConstants;
import org.eclipse.riena.ui.swt.lnf.LnfManager;

/**
 * Tests of the class <code>RienaDefaultLnf</code>.
 */
@NonUITestCase
public class RienaDefaultLnfTest extends TestCase {

	private RienaDefaultLnf lnf;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		lnf = LnfManager.getLnf();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		lnf.setTheme(null);
		LnfManager.dispose();
		lnf = null;
	}

	/**
	 * Test of the method <code>initialize()</code>.
	 * 
	 * @throws Exception
	 *             - handled by JUnit
	 */
	public void testInitialize() throws Exception {

		lnf.uninitialize();

		assertNull(lnf.getRenderer(LnfKeyConstants.SUB_MODULE_VIEW_BORDER_RENDERER));
		assertNull(lnf.getColor(LnfKeyConstants.EMBEDDED_TITLEBAR_ACTIVE_FOREGROUND));

		lnf.initialize();

		assertNotNull(lnf.getColor(LnfKeyConstants.EMBEDDED_TITLEBAR_ACTIVE_FOREGROUND));

	}

	/**
	 * Test of the method <code>uninitialize()</code>.
	 * 
	 * @throws Exception
	 *             - handled by JUnit
	 */
	public void testUninitialize() throws Exception {

		Color color = lnf.getColor(LnfKeyConstants.EMBEDDED_TITLEBAR_ACTIVE_FOREGROUND);
		assertNotNull(color);

		lnf.uninitialize();

		assertTrue(color.isDisposed());
		assertNull(lnf.getColor(LnfKeyConstants.EMBEDDED_TITLEBAR_ACTIVE_FOREGROUND));

	}

	/**
	 * Test of the method <code>getColor(String)</code>.
	 * 
	 * @throws Exception
	 *             - handled by JUnit
	 */
	public void testGetColor() throws Exception {

		lnf.initialize();
		assertNotNull(lnf.getColor(LnfKeyConstants.EMBEDDED_TITLEBAR_ACTIVE_FOREGROUND));
		assertNull(lnf.getColor(LnfKeyConstants.EMBEDDED_TITLEBAR_FONT));
		assertNull(lnf.getColor("dummy"));

	}

	/**
	 * Test of the method <code>getFont(String)</code>.
	 * 
	 * @throws Exception
	 *             - handled by JUnit
	 */
	public void testGetFont() throws Exception {

		lnf.initialize();
		assertNull(lnf.getFont(LnfKeyConstants.EMBEDDED_TITLEBAR_ACTIVE_FOREGROUND));
		assertNotNull(lnf.getFont(LnfKeyConstants.EMBEDDED_TITLEBAR_FONT));
		assertNull(lnf.getFont("dummy"));

	}

	/**
	 * Test of the method <code>getTheme()</code>.
	 * 
	 * @throws Exception
	 *             - handled by JUnit
	 */
	public void testGetTheme() throws Exception {

		assertEquals(RienaDefaultTheme.class, lnf.getTheme().getClass());

		lnf.setTheme(new DummyTheme());
		assertEquals(DummyTheme.class, lnf.getTheme().getClass());

	}

	/**
	 * Test of the method <code>setTheme()</code>.
	 * 
	 * @throws Exception
	 *             - handled by JUnit
	 */
	public void testSetTheme() throws Exception {

		Color color = lnf.getColor(LnfKeyConstants.EMBEDDED_TITLEBAR_ACTIVE_FOREGROUND);
		assertNotNull(color);

		lnf.setTheme(new DummyTheme());
		lnf = LnfManager.getLnf();
		assertTrue(color.isDisposed());

	}

	private static class DummyTheme implements ILnfTheme {

		/**
		 * @see org.eclipse.riena.ui.swt.lnf.ILnfTheme#addCustomColors(java.util.Map)
		 */
		public void addCustomColors(Map<String, ILnfResource> table) {
		}

		/**
		 * @see org.eclipse.riena.ui.swt.lnf.ILnfTheme#addCustomFonts(java.util.Map)
		 */
		public void addCustomFonts(Map<String, ILnfResource> table) {
		}

		/**
		 * @see org.eclipse.riena.ui.swt.lnf.ILnfTheme#addCustomImages(java.util.Map)
		 */
		public void addCustomImages(Map<String, ILnfResource> table) {
		}

		/**
		 * @see org.eclipse.riena.ui.swt.lnf.ILnfTheme#addCustomSettings(java.util.Map)
		 */
		public void addCustomSettings(Map<String, Object> table) {
		}

	}

}
