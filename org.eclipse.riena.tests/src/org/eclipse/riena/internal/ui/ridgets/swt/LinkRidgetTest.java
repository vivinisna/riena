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
package org.eclipse.riena.internal.ui.ridgets.swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Widget;

import org.eclipse.riena.beans.common.StringBean;
import org.eclipse.riena.internal.core.test.collect.UITestCase;
import org.eclipse.riena.internal.ui.swt.test.UITestHelper;
import org.eclipse.riena.ui.ridgets.ILinkRidget;
import org.eclipse.riena.ui.ridgets.listener.ISelectionListener;
import org.eclipse.riena.ui.ridgets.listener.SelectionEvent;
import org.eclipse.riena.ui.ridgets.swt.uibinding.SwtControlRidgetMapper;

/**
 * Tests for {@link LinkRidget}
 */
@UITestCase
public class LinkRidgetTest extends AbstractSWTRidgetTest {

	@Override
	protected Widget createWidget(Composite parent) {
		return new Link(parent, SWT.NONE);
	}

	@Override
	protected ILinkRidget createRidget() {
		return new LinkRidget();
	}

	@Override
	protected Link getWidget() {
		return (Link) super.getWidget();
	}

	@Override
	protected ILinkRidget getRidget() {
		return (ILinkRidget) super.getRidget();
	}

	public void testRidgetMapping() {
		SwtControlRidgetMapper mapper = SwtControlRidgetMapper.getInstance();
		assertSame(LinkRidget.class, mapper.getRidgetClass(getWidget()));
	}

	public void testSetText() {
		ILinkRidget ridget = getRidget();
		Link control = getWidget();

		ridget.setText("text");

		assertEquals("text", ridget.getText());
		assertEquals("text", control.getText());

		ridget.setText("<a>text</a>");

		assertEquals("<a>text</a>", ridget.getText());
		assertEquals("<a>text</a>", control.getText());

		ridget.setText("Click <a>here</a> or <a>there</a>");

		assertEquals("Click <a>here</a> or <a>there</a>", ridget.getText());
		assertEquals("Click <a>here</a> or <a>there</a>", control.getText());

		ridget.setText("<a href=\"link\">text</a>");

		assertEquals("<a href=\"link\">text</a>", ridget.getText());
		assertEquals("<a href=\"link\">text</a>", control.getText());

		ridget.setText("<a></a>");

		assertEquals("<a></a>", ridget.getText());
		assertEquals("<a></a>", control.getText());

		ridget.setText("");

		assertEquals("", ridget.getText());
		assertEquals("", control.getText());

		ridget.setText("test");
		ridget.setText(null);

		assertEquals(null, ridget.getText());
		assertEquals("", control.getText());
	}

	public void testSetTextAndLink() {
		ILinkRidget ridget = getRidget();
		Link control = getWidget();

		ridget.setText("text", "link");

		assertEquals("<a href=\"link\">text</a>", ridget.getText());
		assertEquals("<a href=\"link\">text</a>", control.getText());

		ridget.setText("text", null);

		assertEquals("<a>text</a>", ridget.getText());
		assertEquals("<a>text</a>", control.getText());

		ridget.setText(null, "link");

		assertEquals("<a href=\"link\"></a>", ridget.getText());
		assertEquals("<a href=\"link\"></a>", control.getText());

		ridget.setText("text", "");

		assertEquals("<a>text</a>", ridget.getText());
		assertEquals("<a>text</a>", control.getText());

		ridget.setText("", "link");

		assertEquals("<a href=\"link\"></a>", ridget.getText());
		assertEquals("<a href=\"link\"></a>", control.getText());

		ridget.setText(null, null);

		assertEquals("", ridget.getText());
		assertEquals("", control.getText());

		ridget.setText("", "");

		assertEquals("", ridget.getText());
		assertEquals("", control.getText());
	}

	public void testSetTextFiresEvents() {
		ILinkRidget ridget = getRidget();

		ridget.setText("textA");

		expectPropertyChangeEvent(ILinkRidget.PROPERTY_TEXT, "textA", "textB");
		ridget.setText("textB");
		verifyPropertyChangeEvents();

		expectNoPropertyChangeEvent();
		ridget.setText("textB");
		verifyPropertyChangeEvents();
	}

	public void testBindToModelOneArg() {
		ILinkRidget ridget = getRidget();
		ridget.setText("<a>alpha</a>");
		StringBean bean = new StringBean("<a>beta</a>");

		ridget.bindToModel(BeansObservables.observeValue(bean, StringBean.PROP_VALUE));

		assertEquals("<a>alpha</a>", ridget.getText());

		ridget.updateFromModel();

		assertEquals("<a>beta</a>", ridget.getText());

		ridget.setText("<a>gamma</a>");

		assertEquals("<a>gamma</a>", bean.getValue());

		bean.setValue(null);
		ridget.updateFromModel();

		assertEquals(null, ridget.getText());
	}

	public void testBindToModelTwoArg() {
		ILinkRidget ridget = getRidget();
		ridget.setText("<a>alpha</a>");
		StringBean bean = new StringBean("<a>beta</a>");

		ridget.bindToModel(bean, StringBean.PROP_VALUE);

		assertEquals("<a>alpha</a>", ridget.getText());

		ridget.updateFromModel();

		assertEquals("<a>beta</a>", ridget.getText());

		ridget.setText("<a>gamma</a>");

		assertEquals("<a>gamma</a>", bean.getValue());

		bean.setValue(null);
		ridget.updateFromModel();

		assertEquals(null, ridget.getText());
	}

	public void testSetModelToUIControlConverter() {
		ILinkRidget ridget = getRidget();
		Link control = getWidget();
		StringBean bean = new StringBean("desrever");

		Reverser converter = new Reverser();
		ridget.setModelToUIControlConverter(converter);
		ridget.bindToModel(BeansObservables.observeValue(bean, StringBean.PROP_VALUE));
		ridget.updateFromModel();

		assertSame(converter, ridget.getModelToUIControlConverter());
		assertEquals("reversed", ridget.getText());
		assertEquals("reversed", control.getText());

		ridget.setModelToUIControlConverter(null);
		ridget.bindToModel(BeansObservables.observeValue(bean, StringBean.PROP_VALUE));
		ridget.updateFromModel();

		assertNull(ridget.getModelToUIControlConverter());
		assertEquals("desrever", ridget.getText());
		assertEquals("desrever", control.getText());
	}

	public void testAddListenerInvalid() {
		try {
			getRidget().addSelectionListener(null);
			fail();
		} catch (RuntimeException rex) {
			ok();
		}
	}

	public void testAddListener() {
		ILinkRidget ridget = getRidget();
		Link control = getWidget();

		FTSelectionListener listener1 = new FTSelectionListener();
		FTSelectionListener listener2 = new FTSelectionListener();

		ridget.addSelectionListener(listener1);
		ridget.addSelectionListener(listener2);
		// listener2 will not be added again, if the same instance is already added
		ridget.addSelectionListener(listener2);

		fireSelectionEvent(control);

		assertEquals(1, listener1.getCount());
		assertEquals(1, listener2.getCount());

		ridget.removeSelectionListener(listener1);
		fireSelectionEvent(control);

		assertEquals(1, listener1.getCount());
		assertEquals(2, listener2.getCount());

		ridget.removeSelectionListener(listener2);
		fireSelectionEvent(control);

		assertEquals(1, listener1.getCount());
		assertEquals(2, listener2.getCount());

		ridget.removeSelectionListener(listener2);
		fireSelectionEvent(control);

		assertEquals(1, listener1.getCount());
		assertEquals(2, listener2.getCount());
	}

	public void testSelectionContainsLink() {
		ILinkRidget ridget = getRidget();
		Link control = getWidget();
		ridget.setText("<a href=\"link1\">text1</a> or <a>text2</a>");
		FTSelectionListener listener = new FTSelectionListener();
		ridget.addSelectionListener(listener);

		getShell().layout();
		control.setFocus();
		UITestHelper.sendString(control.getDisplay(), "\r");

		assertEquals(1, listener.getCount());
		listener.verifyData(new String[] { "link1" });

		UITestHelper.sendString(control.getDisplay(), "\t\r");

		assertEquals(2, listener.getCount());
		listener.verifyData(new String[] { "link1", "text2" });
	}

	@Override
	public void testSetFocusable() {
		// the Link widget needs text before it can receive focus
		getRidget().setText("<a>need text to receive focus</a>");
		getShell().layout();
		super.testSetFocusable();
	}

	// helping methods
	// ////////////////

	private void fireSelectionEvent(Control control) {
		Event event = new Event();
		event.type = SWT.Selection;
		event.widget = control;
		control.notifyListeners(SWT.Selection, event);
	}

	// helping classes
	//////////////////

	/**
	 * Converts from String to String. Will reverse the source value.
	 */
	private static final class Reverser implements IConverter {
		public Object convert(Object fromObject) {
			String s = (String) fromObject;
			String result = null;
			if (s != null) {
				char[] chars = s.toCharArray();
				for (int i = 0, j = chars.length / 2; i < j; i++) {
					int flip = chars.length - i - 1;
					char ch = chars[i];
					chars[i] = chars[flip];
					chars[flip] = ch;
				}
				result = String.valueOf(chars);
			}
			return result;
		}

		public Object getFromType() {
			return String.class;
		}

		public Object getToType() {
			return String.class;
		}
	}

	/**
	 * ISelectionListener used for testing.
	 */
	private static final class FTSelectionListener implements ISelectionListener {

		private int count;
		private List<String> values = new ArrayList<String>();

		public int getCount() {
			return count;
		}

		public void verifyData(String[] expected) {
			String[] actual = values.toArray(new String[0]);
			assertEquals(expected.length, actual.length);
			for (int i = 0; i < expected.length; i++) {
				assertEquals(expected[i], actual[i]);
			}
		}

		public void ridgetSelected(SelectionEvent event) {
			count++;
			String linkValue = (String) event.getNewSelection().get(0);
			// System.out.println("clicked: " + linkValue);
			values.add(linkValue);
		}
	}

}