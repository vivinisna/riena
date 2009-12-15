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
package org.eclipse.riena.client.controller.test;

import java.util.Comparator;

import org.easymock.EasyMock;
import org.easymock.LogicalOperator;

import org.eclipse.riena.beans.common.Person;
import org.eclipse.riena.example.client.controllers.NavigateSubModuleController;
import org.eclipse.riena.internal.core.test.collect.NonUITestCase;
import org.eclipse.riena.internal.example.client.beans.PersonModificationBean;
import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.NavigationArgument;
import org.eclipse.riena.navigation.NavigationNodeId;
import org.eclipse.riena.ui.ridgets.IActionRidget;

/**
 * Tests for the NavigateSubModuleController.
 */
@SuppressWarnings("restriction")
@NonUITestCase
public class NavigateSubModuleControllerTest extends AbstractSubModuleControllerTest<NavigateSubModuleController> {

	@Override
	protected NavigateSubModuleController createController(ISubModuleNode node) {
		NavigateSubModuleController newInst = new NavigateSubModuleController();
		node.setNodeId(new NavigationNodeId("org.eclipse.riena.example.navigate"));
		newInst.setNavigationNode(node);

		return newInst;
	}

	public void testNavigateCombo() {
		getMockNavigationProcessor().navigate(EasyMock.eq(controller.getNavigationNode()),
				EasyMock.eq(new NavigationNodeId("org.eclipse.riena.example.navigate.comboAndList")),
				(NavigationArgument) EasyMock.isNull());

		EasyMock.replay(getMockNavigationProcessor());

		IActionRidget navigateToComboButton = controller.getRidget(IActionRidget.class, "comboAndList");
		navigateToComboButton.fireAction();

		EasyMock.verify(getMockNavigationProcessor());
	}

	/**
	 * Tests whether the method <code>INavigationProcessor#navigate</code> is
	 * called with the proper parameters: <br>
	 * - a NavigationNode<br>
	 * - a NavigationNodeId and<br>
	 * - a <code>NavigationArgument</code> that has the ridgetId "textFirst" and
	 * a parameter that is compared by a custom compare-method. This
	 * compare-method returns 0, if the first- and lastName of the
	 * <code>PersonModificationBean</code> match.
	 */
	public void testNavigateToRidgetWithCompare() {
		PersonModificationBean bean = new PersonModificationBean();
		bean.setPerson(new Person("Doe", "Jane"));

		getMockNavigationProcessor().navigate(EasyMock.eq(controller.getNavigationNode()),
				EasyMock.eq(new NavigationNodeId("org.eclipse.riena.example.combo")),
				EasyMock.cmp(new NavigationArgument(bean, "textFirst"), new Comparator<NavigationArgument>() {

					public int compare(NavigationArgument o1, NavigationArgument o2) {
						if (o1.getParameter() instanceof PersonModificationBean
								&& o2.getParameter() instanceof PersonModificationBean) {
							return comparePersonModificationBeans((PersonModificationBean) o1.getParameter(),
									(PersonModificationBean) o2.getParameter());
						} else {
							return -1;
						}
					}

				}, LogicalOperator.EQUAL));

		EasyMock.replay(getMockNavigationProcessor());
		IActionRidget navigateToNavigateRidget = controller.getRidget(IActionRidget.class, "btnNavigateToRidget");
		navigateToNavigateRidget.fireAction();
		EasyMock.verify(getMockNavigationProcessor());
	}

	/**
	 * Tests whether the method <code>INavigationProcessor#navigate</code> is
	 * called with the proper parameters: <br>
	 * - a NavigationNode<br>
	 * - a NavigationNodeId and<br>
	 * - a <code>NavigationArgument</code> that has the ridgetId "textFirst" and
	 * a parameter that is not null
	 */
	public void testNavigateToRidgetWithNotNull() {
		getMockNavigationProcessor().navigate(EasyMock.eq(controller.getNavigationNode()),
				EasyMock.eq(new NavigationNodeId("org.eclipse.riena.example.combo")),
				new NavigationArgument(EasyMock.notNull(), "textFirst"));

		EasyMock.replay(getMockNavigationProcessor());
		IActionRidget navigateToNavigateRidget = controller.getRidget(IActionRidget.class, "btnNavigateToRidget");
		navigateToNavigateRidget.fireAction();
		EasyMock.verify(getMockNavigationProcessor());
	}

	/**
	 * Tests whether the method <code>INavigationProcessor#navigate</code> is
	 * called with the proper parameters: <br>
	 * - a NavigationNode<br>
	 * - a NavigationNodeId and<br>
	 * - a <code>NavigationArgument</code> that has the ridgetId "textFirst" and
	 * a parameter that compared by the equals methods in the specific classes.
	 */
	public void testNavigateToRidgetWithEquals() {
		PersonModificationBean bean = new PersonModificationBean();
		bean.setPerson(new Person("Doe", "Jane"));

		getMockNavigationProcessor().navigate(EasyMock.eq(controller.getNavigationNode()),
				EasyMock.eq(new NavigationNodeId("org.eclipse.riena.example.combo")),
				EasyMock.eq(new NavigationArgument(bean, "textFirst")));

		EasyMock.replay(getMockNavigationProcessor());
		IActionRidget navigateToNavigateRidget = controller.getRidget(IActionRidget.class, "btnNavigateToRidget");
		navigateToNavigateRidget.fireAction();
		EasyMock.verify(getMockNavigationProcessor());
	}

	public void testNavigateTableTextAndTree() {
		getMockNavigationProcessor().navigate(EasyMock.eq(controller.getNavigationNode()),
				EasyMock.eq(new NavigationNodeId("org.eclipse.riena.example.navigate.tableTextAndTree")),
				(NavigationArgument) EasyMock.isNull());

		EasyMock.replay(getMockNavigationProcessor());
		IActionRidget navigateToTableTextAndTree = controller.getRidget(IActionRidget.class, "tableTextAndTree");
		navigateToTableTextAndTree.fireAction();
		EasyMock.verify(getMockNavigationProcessor());
	}

	private int comparePersonModificationBeans(PersonModificationBean p1, PersonModificationBean p2) {
		if (p1.getFirstName().equals(p2.getFirstName()) && p1.getLastName().equals(p2.getLastName())) {
			return 0;
		} else {
			return -1;
		}
	}
}
