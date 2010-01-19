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
package org.eclipse.riena.communication.publisher;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import org.eclipse.core.runtime.Assert;

import org.eclipse.riena.communication.core.publisher.IServicePublishBinder;
import org.eclipse.riena.core.injector.Inject;
import org.eclipse.riena.internal.communication.publisher.Activator;

/**
 * 
 */
public class SingleServicePublisher {

	private String serviceName;
	private String filter;
	private BundleContext context;
	private String path;
	private String protocol;

	private IServicePublishBinder binder;

	// public static final String FILTER_REMOTE = "(&(" + PROP_IS_REMOTE +
	// "=true)("
	// + PROP_REMOTE_PROTOCOL + "=*)" + ")";

	public SingleServicePublisher(String name) {
		super();
		this.serviceName = name;
		Inject.service(IServicePublishBinder.class).useRanking().into(this).andStart(
				Activator.getDefault().getContext());
	}

	public SingleServicePublisher useFilter(String filter) {
		Assert.isNotNull(filter);
		this.filter = filter;
		return this;
	}

	public SingleServicePublisher usingPath(String path) {
		Assert.isNotNull(path);
		this.path = path;
		return this;
	}

	public SingleServicePublisher withProtocol(String protocol) {
		Assert.isNotNull(protocol);
		this.protocol = protocol;
		return this;
	}

	public void andStart(BundleContext context) {
		this.context = context;
		Assert.isNotNull(path);
		Assert.isNotNull(protocol);

		try {
			ServiceReference[] refs = this.context.getServiceReferences(serviceName, filter);
			if (refs != null) {
				for (ServiceReference ref : refs) {
					publish(ref);
				}
			}
		} catch (InvalidSyntaxException e1) {
			e1.printStackTrace();
		}

		ServiceListener listener = new ServiceListener() {
			public void serviceChanged(ServiceEvent event) {
				String[] serviceInterfaces = (String[]) event.getServiceReference().getProperty(Constants.OBJECTCLASS);
				for (String serviceInterf : serviceInterfaces) {
					if (serviceInterf.equals(serviceName)) {
						if (event.getType() == ServiceEvent.REGISTERED) {
							publish(event.getServiceReference());
						} else {
							if (event.getType() == ServiceEvent.UNREGISTERING) {
								unpublish(event.getServiceReference());
							}
						}
					}
				}
			}
		};
		try {
			Activator.getDefault().getContext().addServiceListener(listener, filter);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}

		return;
	}

	public void bind(IServicePublishBinder binder) {
		this.binder = binder;
	}

	public void unbind(IServicePublishBinder binder) {
		this.binder = null;
	}

	private void publish(ServiceReference serviceReference) {
		binder.publish(serviceReference, path, protocol);
	}

	private void unpublish(ServiceReference serviceReference) {
		binder.unpublish(serviceReference);
	}

}