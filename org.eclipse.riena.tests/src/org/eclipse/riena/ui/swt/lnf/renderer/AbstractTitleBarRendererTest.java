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
package org.eclipse.riena.ui.swt.lnf.renderer;

import org.eclipse.riena.core.util.ReflectionUtils;
import org.eclipse.riena.tests.RienaTestCase;
import org.eclipse.riena.ui.swt.lnf.ImageLnfResource;
import org.eclipse.riena.ui.swt.lnf.LnfManager;
import org.eclipse.riena.ui.swt.lnf.rienadefault.RienaDefaultLnf;
import org.eclipse.riena.ui.swt.utils.ImageUtil;
import org.eclipse.riena.ui.swt.utils.SwtUtilities;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;

/**
 * Tests of the class {@link AbstractTitleBarRenderer}.
 */
public class AbstractTitleBarRendererTest extends RienaTestCase {

	private final static String PLUGIN_ID = "org.eclipse.riena.tests:";
	private final static String ICON_ECLIPSE = PLUGIN_ID + "/icons/eclipse.gif";

	private TitleBarRenderer renderer;
	private GC gc;
	private Shell shell;

	@Override
	protected void setUp() throws Exception {
		shell = new Shell();
		gc = new GC(shell);
		renderer = new TitleBarRenderer();
		renderer.setShell(shell);
		renderer.setBounds(0, 0, 100, 100);
	}

	@Override
	protected void tearDown() throws Exception {
		SwtUtilities.disposeWidget(shell);
		gc = null;
		renderer.dispose();
		renderer = null;
	}

	/**
	 * Tests the <i>private</i> method {@code resetBounds()}.
	 */
	public void testResetBounds() {

		renderer.setCloseable(true);
		renderer.setMaximizable(true);
		renderer.setMinimizable(true);
		renderer.paint(gc, null);
		assertEquals(new Rectangle(1, 1, 1, 1), renderer.getImageBounds());
		Rectangle[] btnBounds = renderer.getButtonsBounds();
		for (Rectangle rectangle : btnBounds) {
			assertFalse(new Rectangle(0, 0, 0, 0).equals(rectangle));
		}

		ReflectionUtils.invokeHidden(renderer, "resetBounds");
		assertEquals(new Rectangle(0, 0, 0, 0), renderer.getImageBounds());
		btnBounds = renderer.getButtonsBounds();
		for (Rectangle rectangle : btnBounds) {
			assertEquals(new Rectangle(0, 0, 0, 0), rectangle);
		}

	}

	/**
	 * Tests the method {@code paintButton}.
	 */
	public void testPaintButton() {

		Image image = ImageUtil.getImage(ICON_ECLIPSE);
		ImageData imageData = image.getImageData();

		LnfManager.setLnf(new TitleBarLnf());
		renderer.setCloseable(true);
		renderer.setMaximizable(true);
		renderer.setMinimizable(true);

		ReflectionUtils.invokeHidden(renderer, "resetBounds");
		renderer.paintButton(gc, 0);
		Rectangle[] btnBounds = renderer.getButtonsBounds();
		assertTrue(btnBounds[0].x < 100);
		assertTrue(btnBounds[0].x > 0);
		assertTrue(btnBounds[0].y > 0);
		assertEquals(imageData.width, btnBounds[0].width);
		assertEquals(imageData.height, btnBounds[0].height);
		for (int i = 1; i < btnBounds.length; i++) {
			assertEquals(new Rectangle(0, 0, 0, 0), btnBounds[i]);
		}
		renderer.paintButton(gc, 1);
		btnBounds = renderer.getButtonsBounds();
		assertTrue(btnBounds[1].x < 100);
		assertTrue(btnBounds[1].x > 0);
		assertTrue(btnBounds[1].x < btnBounds[0].x);
		assertEquals(btnBounds[0].y, btnBounds[1].y);
		assertEquals(imageData.width, btnBounds[1].width);
		assertEquals(imageData.height, btnBounds[1].height);
		for (int i = 2; i < btnBounds.length; i++) {
			assertEquals(new Rectangle(0, 0, 0, 0), btnBounds[i]);
		}

	}

	/**
	 * This class changes the visibility of some method for testing and
	 * implements the abstract methods.
	 */
	private class TitleBarRenderer extends AbstractTitleBarRenderer {

		@Override
		protected String[] getBtnHoverImageKeys() {
			return null;
		}

		@Override
		protected String[] getBtnHoverSelectedImageKeys() {
			return null;
		}

		@Override
		protected String[] getBtnImageKeys() {
			return new String[] { "Image1", "Image2", "Image3" };
		}

		@Override
		protected String[] getBtnInactiveImageKeys() {
			return new String[] { "InactiveImage1", "InactiveImage2", "InactiveImage3" };
		}

		@Override
		protected void paintBackground(GC gc) {
		}

		@Override
		protected Rectangle paintImage(GC gc) {
			return new Rectangle(1, 1, 1, 1);
		}

		@Override
		protected Rectangle paintTitle(GC gc) {
			return new Rectangle(1, 1, 1, 1);
		}

		@Override
		public Rectangle getImageBounds() {
			return super.getImageBounds();
		}

		@Override
		public Rectangle[] getButtonsBounds() {
			return super.getButtonsBounds();
		}

		@Override
		public void paintButton(GC gc, int btnIndex) {
			super.paintButton(gc, btnIndex);
		}

	}

	/**
	 * This Look and Feel adds additional images. This images are used for
	 * testing.
	 */
	private class TitleBarLnf extends RienaDefaultLnf {

		@Override
		protected void initImageDefaults() {
			super.initImageDefaults();
			getResourceTable().put("Image1", new ImageLnfResource(ICON_ECLIPSE));
			getResourceTable().put("Image2", new ImageLnfResource(ICON_ECLIPSE));
			getResourceTable().put("Image3", new ImageLnfResource(ICON_ECLIPSE));
			getResourceTable().put("InactiveImage1", new ImageLnfResource(ICON_ECLIPSE));
			getResourceTable().put("InactiveImage2", new ImageLnfResource(ICON_ECLIPSE));
			getResourceTable().put("InactiveImage3", new ImageLnfResource(ICON_ECLIPSE));
		}

	}

}
