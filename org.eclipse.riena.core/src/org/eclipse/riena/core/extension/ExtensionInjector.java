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
package org.eclipse.riena.core.extension;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.equinox.log.Logger;
import org.eclipse.riena.core.logging.ConsoleLogger;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

/**
 * This is the extension injector.<br>
 * See {@link ExtensionDescriptor} for explanation and usage.
 */
public class ExtensionInjector {

	private final ExtensionDescriptor extensionDesc;
	private final Object target;

	private BundleContext context;
	private boolean started;
	private boolean track = true;
	private boolean symbolReplacement = true;
	private String updateMethodName = "update"; //$NON-NLS-1$
	private Method updateMethod;
	private IRegistryEventListener injectorListener;
	private boolean isArray;
	private Class<?> componentType;

	private final static Logger LOGGER = new ConsoleLogger(ExtensionInjector.class.getName());

	/**
	 * @param extensionDesc
	 * @param target
	 */
	ExtensionInjector(final ExtensionDescriptor extensionDesc, final Object target) {
		this.extensionDesc = extensionDesc;
		this.target = target;
	}

	/**
	 * Start the extension injector.<br>
	 * 
	 * @param context
	 * @return itself
	 */
	public ExtensionInjector andStart(final BundleContext context) {
		Assert.isTrue(!started, "ExtensionInjector already started.");
		started = true;
		this.context = context;
		updateMethod = findUpdateMethod();
		Class<?> paramaterType = updateMethod.getParameterTypes()[0];
		isArray = paramaterType.isArray();
		// if the interface type is given explicitly it will be used; otherwise
		// the formal parameter type of the update method will be used.
		componentType = extensionDesc.getInterfaceType() != null ? extensionDesc.getInterfaceType()
				: isArray ? paramaterType.getComponentType() : paramaterType;
		populateInterfaceBeans();

		if (track) {
			IExtensionRegistry extensionRegistry = RegistryFactory.getRegistry();
			if (extensionRegistry == null)
				// TODO Is this an error for that we should throw an exception?
				LOGGER.log(LogService.LOG_ERROR,
						"For some reason the extension registry has not been created. Tracking is not possible.");
			else {
				injectorListener = new InjectorListener();
				extensionRegistry.addListener(injectorListener, extensionDesc.getExtensionPointId());
			}
		}
		return this;
	}

	/**
	 * Define the bind method name.<br>
	 * If not given 'update' will be assumed.
	 * 
	 * @param bindMethodName
	 * @return itself
	 */
	public ExtensionInjector bind(final String bindMethodName) {
		Assert.isNotNull(bindMethodName, "Bind method name must not be null");
		Assert.isTrue(!started, "ExtensionInjector already started.");
		this.updateMethodName = bindMethodName;
		return this;
	}

	/**
	 * Explicitly forbid tracking of changes of the extensions.
	 * 
	 * @return itself
	 */
	public ExtensionInjector doNotTrack() {
		Assert.isTrue(!started, "ExtensionInjector already started.");
		track = false;
		return this;
	}

	/**
	 * Modify the values with ConfigurationPlugin.
	 * 
	 * @return itself
	 */
	public ExtensionInjector doNotReplaceSymbols() {
		Assert.isTrue(!started, "ExtensionInjector already started.");
		symbolReplacement = false;
		return this;
	}

	/**
	 * Stop the extension injector.
	 */
	public void stop() {
		if (!started)
			return;

		if (track) {
			IExtensionRegistry extensionRegistry = RegistryFactory.getRegistry();
			if (extensionRegistry == null)
				// TODO Is this an error for that we should throw an exception?
				LOGGER.log(LogService.LOG_ERROR, "For some reason the extension registry has not been created.");
			else
				extensionRegistry.removeListener(injectorListener);
		}
		injectorListener = null;
	}

	/**
	 */
	private Method findUpdateMethod() {
		return extensionDesc.getInterfaceType() == null ? findUpdateMethodForUnkownType()
				: findUpdateMethodForKownType();
	}

	/**
	 * Determine method from update method name and known type
	 * 
	 * @return
	 */
	private Method findUpdateMethodForKownType() {
		try {
			if (extensionDesc.requiresArrayUpdateMethod())
				return seekMatchingUpdateMethod(extensionDesc.getInterfaceType(), true);

			try {
				return seekMatchingUpdateMethod(extensionDesc.getInterfaceType(), false);
			} catch (NoSuchMethodException e) {
				// retry with array
				return seekMatchingUpdateMethod(extensionDesc.getInterfaceType(), true);
			}

		} catch (SecurityException e) {
			throw new IllegalStateException("Could not find 'bind' method.", e);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("Could not find 'bind' method.", e);
		}
	}

	private Method seekMatchingUpdateMethod(final Class<?> interfaceType, final boolean isArray)
			throws SecurityException, NoSuchMethodException {
		try {
			Class<?> seeking = isArray ? Array.newInstance(interfaceType, 0).getClass() : interfaceType;
			return target.getClass().getMethod(updateMethodName, seeking);
		} catch (NoSuchMethodException e) {
			for (Class<?> superInterfaceType : interfaceType.getInterfaces()) {
				Method attempt = seekMatchingUpdateMethod(superInterfaceType, isArray);
				if (attempt != null)
					return attempt;
			}
		}
		throw new NoSuchMethodError("In class " + target.getClass() + " is no method matching " + updateMethodName
				+ "(" + interfaceType + " )");
	}

	/**
	 * Determine method from update method name and also determine interface
	 * type (array?)
	 * 
	 * @return
	 */
	private Method findUpdateMethodForUnkownType() {
		List<Method> candidates = new ArrayList<Method>();
		Method[] methods = target.getClass().getMethods();
		for (Method method : methods)
			if (method.getName().equals(updateMethodName) && method.getParameterTypes().length == 1
					&& isInterface(method.getParameterTypes()[0]))
				candidates.add(method);

		if (candidates.size() == 0)
			throw new IllegalStateException("No suitable 'bind' method found.");

		if (candidates.size() == 1)
			if (matchesExtensionPointConstraint(candidates.get(0).getParameterTypes()[0]))
				return candidates.get(0);
			else
				throw new IllegalStateException("Found method " + candidates.get(0)
						+ " does not match extension point constraints.");

		if (candidates.size() > 2)
			throw new IllegalStateException("Too much (>2) candidates (" + candidates + ") for 'bind' method.");

		if (matchesExtensionPointConstraint(candidates.get(0).getParameterTypes()[0]))
			return candidates.get(0);

		if (matchesExtensionPointConstraint(candidates.get(1).getParameterTypes()[0]))
			return candidates.get(1);

		throw new IllegalStateException("No suitable candidate from (" + candidates + ") found for 'bind' method.");
	}

	/**
	 * @param type
	 * @return
	 */
	private boolean isInterface(Class<?> type) {
		type = type.isArray() ? type.getComponentType() : type;
		return type.isInterface();
	}

	/**
	 * @param type
	 * @return
	 */
	private boolean matchesExtensionPointConstraint(final Class<?> type) {
		return !extensionDesc.requiresArrayUpdateMethod() || type.isArray();
	}

	void populateInterfaceBeans() {
		final Object[] beans = ExtensionMapper.read(symbolReplacement ? context : null, extensionDesc, componentType);
		if (!matchesExtensionPointConstraint(beans.length))
			LOGGER
					.log(LogService.LOG_ERROR,
							"Number of extensions does not fullfil the extension point's constraints.");
		try {
			if (isArray) {
				updateMethod.invoke(target, new Object[] { beans });
			} else {
				updateMethod.invoke(target, new Object[] { beans.length > 0 ? beans[0] : null });
			}
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("Calling 'bind' method fails.", e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Calling 'bind' method fails.", e);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException("Calling 'bind' method fails.", e);
		}
	}

	private boolean matchesExtensionPointConstraint(int occurence) {
		return occurence >= extensionDesc.getMinOccurences() && occurence <= extensionDesc.getMaxOccurences();
	}

	/**
	 * Listen to extension registry events.
	 */
	private class InjectorListener implements IRegistryEventListener {

		/*
		 * @see
		 * org.eclipse.core.runtime.IRegistryEventListener#added(org.eclipse
		 * .core. runtime.IExtension[])
		 */
		public void added(IExtension[] extensions) {
			populateInterfaceBeans();
		}

		/*
		 * @see
		 * org.eclipse.core.runtime.IRegistryEventListener#added(org.eclipse
		 * .core. runtime.IExtensionPoint[])
		 */
		public void added(IExtensionPoint[] extensionPoints) {
			// We don�t care about other extension points. We only listen to the
			// extensions for the id <code>extensionDesc</code>!
		}

		/*
		 * @see
		 * org.eclipse.core.runtime.IRegistryEventListener#removed(org.eclipse
		 * .core. runtime.IExtension[])
		 */
		public void removed(IExtension[] extensions) {
			populateInterfaceBeans();
		}

		/*
		 * @see
		 * org.eclipse.core.runtime.IRegistryEventListener#removed(org.eclipse
		 * .core. runtime.IExtensionPoint[])
		 */
		public void removed(IExtensionPoint[] extensionPoints) {
			// We don�t care about other extension points. We only listen to the
			// extensions for the id <code>extensionDesc</code>!
		}

	}

}
