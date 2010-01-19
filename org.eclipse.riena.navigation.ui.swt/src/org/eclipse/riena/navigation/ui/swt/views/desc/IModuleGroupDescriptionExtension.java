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
package org.eclipse.riena.navigation.ui.swt.views.desc;

import org.eclipse.riena.core.injector.extension.ExtensionInterface;
import org.eclipse.riena.navigation.ui.controllers.ModuleGroupController;
import org.eclipse.riena.navigation.ui.swt.views.ModuleGroupView;

/**
 * interfaces for injecting org.eclipse.riena.navigation.ui.swt.moduleGroupView
 */
@ExtensionInterface(id = "moduleGroupView")
public interface IModuleGroupDescriptionExtension {

	Class<ModuleGroupView> getView();

	Class<ModuleGroupController> getController();

	String getName();

}