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

import org.eclipse.riena.ui.ridgets.AbstractMarkerSupport;
import org.eclipse.riena.ui.ridgets.IToolItemRidget;
import org.eclipse.riena.ui.swt.utils.ImageState;
import org.eclipse.riena.ui.swt.utils.ImageStore;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Ridget of a tool item.
 */
public class ToolItemRidget extends AbstractItemRidget implements IToolItemRidget {

	@Override
	protected void bindUIControl() {
		super.bindUIControl();
		ToolItem toolItem = getUIControl();
		if (toolItem != null) {
			toolItem.addSelectionListener(getActionObserver());
		}
	}

	@Override
	protected void unbindUIControl() {
		ToolItem toolItem = getUIControl();
		if ((toolItem != null) && !toolItem.isDisposed()) {
			toolItem.removeSelectionListener(getActionObserver());
		}
		super.unbindUIControl();
	}

	@Override
	protected void checkUIControl(Object uiControl) {
		assertType(uiControl, ToolItem.class);
	}

	@Override
	public ToolItem getUIControl() {
		return (ToolItem) super.getUIControl();
	}

	@Override
	protected AbstractMarkerSupport createMarkerSupport() {
		return new ToolItemMarkerSupport(this, propertyChangeSupport);
	}

	@Override
	AbstractItemProperties createProperties() {
		return new ToolItemProperties(this);
	}

	/**
	 * Updates (sets) the icon of the tool item and also the <i>hot/hover</i>
	 * icon (image).
	 * 
	 * @return {@code true} if icon (image) of control was set; otherwise
	 *         {@code false} .
	 * @see org.eclipse.riena.internal.ui.ridgets.swt.AbstractItemRidget#updateUIIcon()
	 */
	@Override
	protected boolean updateUIIcon() {

		boolean newImage = super.updateUIIcon();
		if (newImage) {
			ToolItem control = getUIControl();
			if (getIcon() != null) {
				Image image = ImageStore.getInstance().getImage(getIcon(), ImageState.HOVER);
				control.setHotImage(image);
			}
		}

		return newImage;
	}

}
