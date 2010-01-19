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
package org.eclipse.riena.navigation.ui.swt.component;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.riena.core.util.ReflectionUtils;
import org.eclipse.riena.internal.core.test.collect.UITestCase;
import org.eclipse.riena.ui.swt.utils.SWTBindingPropertyLocator;
import org.eclipse.riena.ui.swt.utils.SwtUtilities;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Tests of the class {@link MenuCoolBarComposite}.
 */
@UITestCase
public class MenuCoolBarCompositeTest extends TestCase {

	private Shell shell;

	@Override
	protected void setUp() throws Exception {
		shell = new Shell();
	}

	@Override
	protected void tearDown() throws Exception {
		SwtUtilities.disposeWidget(shell);
	}

	/**
	 * Tests the constructor of {@code MenuCoolBarComposite} and also the
	 * <i>private</i> method {@code create()}.
	 */
	public void testMenuCoolBarComposite() {

		MenuCoolBarComposite composite = new MenuCoolBarComposite(shell, SWT.NONE);
		ToolBar toolBar = ReflectionUtils.getHidden(composite, "toolBar");
		assertNotNull(toolBar);
		Listener[] listeners = toolBar.getListeners(SWT.MouseMove);
		assertEquals(1, listeners.length);

		CoolBar coolBar = (CoolBar) toolBar.getParent();
		assertEquals(1, coolBar.getItemCount());
		CoolItem item = coolBar.getItem(0);
		assertSame(toolBar, item.getControl());

	}

	/**
	 * Tests the method {@code createAndAddMenu}.
	 */
	public void testCreateAndAddMenu() {

		MenuCoolBarComposite composite = new MenuCoolBarComposite(shell, SWT.NONE);

		MenuManager manager = new MenuManager("TestMenu", "0815");
		ToolItem topItem = composite.createAndAddMenu(manager);
		SWTBindingPropertyLocator locator = SWTBindingPropertyLocator.getInstance();
		assertEquals("0815", locator.locateBindingProperty(topItem));
		assertEquals("TestMenu", topItem.getText());

	}

	/**
	 * Tests the method {@code getTopLevelItems()}.
	 */
	public void testGetTopLevelItems() {

		MenuCoolBarComposite composite = new MenuCoolBarComposite(shell, SWT.NONE);

		MenuManager manager = new MenuManager("TestMenu", "0815");
		ToolItem topItem = composite.createAndAddMenu(manager);
		List<ToolItem> items = composite.getTopLevelItems();
		assertEquals(1, items.size());
		assertTrue(items.contains(topItem));

	}

}