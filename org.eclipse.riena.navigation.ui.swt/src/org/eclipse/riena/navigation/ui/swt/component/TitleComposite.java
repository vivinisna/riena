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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.riena.navigation.IApplicationNode;
import org.eclipse.riena.navigation.listener.ApplicationNodeListener;
import org.eclipse.riena.navigation.listener.IApplicationNodeListener;
import org.eclipse.riena.navigation.model.ApplicationNode;
import org.eclipse.riena.navigation.ui.swt.lnf.renderer.ShellBorderRenderer;
import org.eclipse.riena.navigation.ui.swt.lnf.renderer.ShellRenderer;
import org.eclipse.riena.ui.swt.AbstractTitleBarMouseListener;
import org.eclipse.riena.ui.swt.lnf.LnfKeyConstants;
import org.eclipse.riena.ui.swt.lnf.LnfManager;
import org.eclipse.riena.ui.swt.lnf.renderer.AbstractTitleBarRenderer;
import org.eclipse.riena.ui.swt.lnf.rienadefault.RienaDefaultLnf;

/**
 * This composite contains parts of the title of a Riena application: e.g. logo
 * or sub-application switcher. Also the renderer of this composite draws some
 * other parts: e.g. minimize, maximize and close button
 */
public class TitleComposite extends Composite {

	private TitlelessShellMouseListener mouseListener;
	private ApplicationNode node;
	private IApplicationNodeListener appNodeListener;

	/**
	 * Creates a new instance of {@code TitleComposite} and initializes it.
	 * 
	 * @param parentShell
	 *            - a shell which will be the parent of the new instance (cannot
	 *            be null)
	 * @param node
	 *            - node of the application
	 */
	public TitleComposite(Shell parentShell, ApplicationNode node) {

		super(parentShell, SWT.NONE);

		this.node = node;
		appNodeListener = new ApplicationLabelListener();
		node.addListener(appNodeListener);
		init(parentShell);

	}

	/**
	 * Sets colors and images. Adds logo and switcher.
	 * 
	 * @param parentShell
	 */
	private void init(Shell parentShell) {

		// force result's background into logo and switcher
		setBackgroundMode(SWT.INHERIT_FORCE);
		// sets the background of the composite
		Image image = LnfManager.getLnf().getImage(LnfKeyConstants.TITLELESS_SHELL_BACKGROUND_IMAGE);
		setBackgroundImage(image);

		setLayout(new FormLayout());
		ShellBorderRenderer borderRenderer = (ShellBorderRenderer) LnfManager.getLnf().getRenderer(
				LnfKeyConstants.TITLELESS_SHELL_BORDER_RENDERER);
		int borderWidth = borderRenderer.getBorderWidth();
		FormData data = new FormData();
		data.top = new FormAttachment(parentShell, borderWidth);
		data.left = new FormAttachment(0, borderWidth);
		data.right = new FormAttachment(100, -borderWidth);
		setLayoutData(data);

		new LogoComposite(this, SWT.NONE);
		new SwitcherComposite(this, node);

		addListeners();

	}

	/**
	 * Adds a paint and a mouse listener to this composite.
	 */
	private void addListeners() {

		addPaintListener(new TitlelessPaintListener());

		mouseListener = new TitlelessShellMouseListener();
		addMouseListener(mouseListener);
		addMouseMoveListener(mouseListener);
		addMouseTrackListener(mouseListener);

	}

	/**
	 * Returns the renderer of the shell.
	 * 
	 * @return renderer
	 */
	private ShellRenderer getShellRenderer() {
		ShellRenderer shellRenderer = (ShellRenderer) LnfManager.getLnf().getRenderer(
				LnfKeyConstants.TITLELESS_SHELL_RENDERER);
		return shellRenderer;
	}

	/**
	 * Removes and disposes all added listeners.
	 * 
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	@Override
	public void dispose() {

		if (mouseListener != null) {
			removeMouseListener(mouseListener);
			removeMouseMoveListener(mouseListener);
			removeMouseTrackListener(mouseListener);
			mouseListener.dispose();
			mouseListener = null;
		}
		if (node != null) {
			node.removeListener(appNodeListener);
			appNodeListener = null;
		}

		super.dispose();

	}

	/**
	 * If the label of the application has changed, redraw the composite so that
	 * the correct label is displayed.
	 */
	private class ApplicationLabelListener extends ApplicationNodeListener {

		@Override
		public void labelChanged(IApplicationNode source) {
			super.labelChanged(source);
			redraw();
		}

	}

	/**
	 * This listener paints (top part of) the shell.
	 */
	private class TitlelessPaintListener implements PaintListener {

		/**
		 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
		 */
		public void paintControl(PaintEvent e) {
			onPaint(e);
		}

		/**
		 * Paints the title, buttons and background of the (titleless) shell.
		 * 
		 * @param e
		 *            - event
		 */
		private void onPaint(PaintEvent e) {

			if (e.getSource() instanceof Control) {

				Control shell = (Control) e.getSource();

				ShellRenderer shellRenderer = getShellRenderer();
				Rectangle shellBounds = shell.getBounds();
				Rectangle bounds = new Rectangle(0, 0, shellBounds.width, shellRenderer.getHeight());
				shellRenderer.setBounds(bounds);
				RienaDefaultLnf lnf = LnfManager.getLnf();
				shellRenderer.setCloseable(lnf.getBooleanSetting(LnfKeyConstants.TITLELESS_SHELL_SHOW_CLOSE));
				shellRenderer.setMaximizable(lnf.getBooleanSetting(LnfKeyConstants.TITLELESS_SHELL_SHOW_MAX));
				shellRenderer.setMinimizable(lnf.getBooleanSetting(LnfKeyConstants.TITLELESS_SHELL_SHOW_MIN));
				shellRenderer.paint(e.gc, shell);

				// idea: set background image here !?!
				//				if (shellBounds.width > 1000) {
				//					Image image = LnfManager.getLnf().getImage(LnfKeyConstants.TITLELESS_SHELL_LOGO);
				//					shell.setBackgroundImage(image);
				//				} else {
				//					Image image = LnfManager.getLnf().getImage(LnfKeyConstants.TITLELESS_SHELL_BACKGROUND_IMAGE);
				//					shell.setBackgroundImage(image);
				//				}

			}

		}
	}

	/**
	 * After any mouse operation a method of this listener is called.
	 */
	private class TitlelessShellMouseListener extends AbstractTitleBarMouseListener {

		@Override
		protected AbstractTitleBarRenderer getTitleBarRenderer() {
			return getShellRenderer();
		}

	}

}