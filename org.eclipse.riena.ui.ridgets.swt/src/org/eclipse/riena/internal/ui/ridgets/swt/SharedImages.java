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
package org.eclipse.riena.internal.ui.ridgets.swt;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Provides access to a set of shared images. The images can be accessed using
 * one of the predifined string constants.
 * <p>
 * Example:
 * 
 * <pre>
 * Image image = Activator.getSharedImage(SharedImages.IMG_LEAF);
 * </pre>
 * 
 * @see Activator
 */
public final class SharedImages {

	public static final String IMG_NODE_COLLAPSED = "IMG_NODE_COLLAPSED"; //$NON-NLS-1$
	public static final String IMG_NODE_EXPANDED = "IMG_NODE_EXPANDED"; //$NON-NLS-1$
	public static final String IMG_LEAF = "IMG_LEAF"; //$NON-NLS-1$

	static void initializeImageRegistry(ImageRegistry reg) {
		doPut(reg, IMG_NODE_COLLAPSED, "/icons/obj16/node_collapsed.gif"); //$NON-NLS-1$
		doPut(reg, IMG_NODE_EXPANDED, "/icons/obj16/node_expanded.gif"); //$NON-NLS-1$
		doPut(reg, IMG_LEAF, "/icons/obj16/leaf.gif"); //$NON-NLS-1$
	}

	// helping methods
	// ////////////////

	private static void doPut(ImageRegistry reg, String key, String path) {
		ImageDescriptor descr = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, path);
		if (descr == null) {
			descr = ImageDescriptor.getMissingImageDescriptor();
		}
		reg.put(key, descr);
	}

}
