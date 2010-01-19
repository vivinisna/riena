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
package org.eclipse.riena.ui.ridgets.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.riena.ui.core.context.IContext;
import org.eclipse.riena.ui.ridgets.IRidget;
import org.eclipse.riena.ui.ridgets.IWindowRidget;

/**
 * Controller for a view that is or has a window.
 * 
 */
public abstract class AbstractWindowController implements IController, IContext {

	/**
	 * The ridget id to use for the window ridget.
	 */
	public static final String RIDGET_ID_WINDOW = "windowRidget"; //$NON-NLS-1$

	/**
	 * TODO [ev] docs -- provisional, where is the best location for this?
	 * 
	 * @since 1.2
	 */
	public static final int OK = 0;

	/**
	 * TODO [ev] docs -- provisional, where is the best location for this
	 * 
	 * @since 1.2
	 */
	public static final int CANCEL = 1;

	private final Map<String, IRidget> ridgets;
	private final Map<String, Object> context;
	private IWindowRidget windowRidget;
	private boolean blocked;
	private int returnCode;

	public AbstractWindowController() {
		super();
		ridgets = new HashMap<String, IRidget>();
		context = new HashMap<String, Object>();
	}

	public void addRidget(String id, IRidget ridget) {
		ridgets.put(id, ridget);
	}

	public void afterBind() {
		returnCode = OK;
		getWindowRidget().updateFromModel();
	}

	public void configureRidgets() {
		setWindowRidget((IWindowRidget) getRidget(RIDGET_ID_WINDOW));
	}

	/**
	 * @since 1.2
	 */
	public Object getContext(String key) {
		return context.get(key);
	}

	public IRidget getRidget(String id) {
		return ridgets.get(id);
	}

	@SuppressWarnings("unchecked")
	public <R extends IRidget> R getRidget(Class<R> ridgetClazz, String id) {
		return (R) getRidget(id);
	}

	public Collection<? extends IRidget> getRidgets() {
		return ridgets.values();
	}

	/**
	 * @return The window ridget.
	 */
	public IWindowRidget getWindowRidget() {
		return windowRidget;
	}

	/**
	 * @since 1.2
	 */
	// TODO [ev] javadoc
	public int getReturnCode() {
		return returnCode;
	}

	public boolean isBlocked() {
		return this.blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
		// TODO: implement
	}

	/**
	 * @since 1.2
	 */
	public void setContext(String key, Object value) {
		context.put(key, value);
	}

	/**
	 * @since 1.2
	 */
	// TODO [ev] javadoc
	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	/**
	 * Sets the window ridget.
	 * 
	 * @param windowRidget
	 *            The window ridget.
	 */
	public void setWindowRidget(IWindowRidget windowRidget) {
		this.windowRidget = windowRidget;
	}
}