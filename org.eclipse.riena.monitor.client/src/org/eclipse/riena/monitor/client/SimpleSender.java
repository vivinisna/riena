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
package org.eclipse.riena.monitor.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.riena.core.injector.Inject;
import org.eclipse.riena.core.util.Literal;
import org.eclipse.riena.core.util.Millis;
import org.eclipse.riena.core.util.PropertiesUtils;
import org.eclipse.riena.internal.monitor.client.Activator;
import org.eclipse.riena.monitor.common.Collectible;
import org.eclipse.riena.monitor.common.IReceiver;

/**
 * This simple sender implements {@code ISender} that uses riena�s (remote)
 * services to communicate with the �server�.
 * <p>
 * The simple sender expects the following configuration that can be passed with
 * its definition in an extension:
 * <ul>
 * <li>retryTime - defines the time in minutes that will be waited for a retry
 * when a send has failed. (default value is 15 minutes if not defined)</li>
 * </ul>
 * Periods of time can be specified as a string conforming to
 * {@link Millis#valueOf(String)}.<br>
 * Example extension:
 * 
 * <pre>
 * &lt;extension point=&quot;org.eclipse.riena.monitor.sender&quot;&gt;
 *       &lt;sender
 *             name=&quot;SimpleSender&quot;
 *             class=&quot;org.eclipse.riena.internal.monitor.client.SimpleSender:retryTime=20 m&quot;&gt;
 *       &lt;/sender&gt;
 * &lt;/extension&gt;
 * </pre>
 */
public class SimpleSender implements ISender, IExecutableExtension {

	private IStore store;
	private IReceiver receiver;
	private boolean started;
	private long retryTime;
	private Map<String, Category> categories = new HashMap<String, Category>();
	private final Map<String, Sender> senders = new HashMap<String, Sender>();
	private static final String RETRY_TIME = "retryTime"; //$NON-NLS-1$
	private static final String RETRY_TIME_DEFAULT = "15 m"; //$NON-NLS-1$

	private static final boolean TRACE = true;

	/**
	 * Creates the {@code SimpleSender}.
	 * 
	 * @throws CoreException
	 */
	public SimpleSender() throws CoreException {
		this(true);
		// perform default initialization - maybe redone by the {@code IConfigurationElement.createExecutableExtension(String)}
		setInitializationData(null, null, null);
	}

	/**
	 * Constructor that should only be used while testing
	 * 
	 * @param autoConfig
	 *            true perform configuration; otherwise do not configure
	 */
	public SimpleSender(boolean autoConfig) {
		if (autoConfig) {
			Inject.service(IReceiver.class).useRanking().into(this).andStart(Activator.getDefault().getContext());
		}
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {

		Map<String, String> properties = null;
		try {
			properties = PropertiesUtils.asMap(data, Literal.map(RETRY_TIME, RETRY_TIME_DEFAULT));
			retryTime = Millis.valueOf(properties.get(RETRY_TIME));
			Assert.isLegal(retryTime > 0, "retryTime must be greater than 0."); //$NON-NLS-1$
		} catch (IllegalArgumentException e) {
			throw configurationException("Bad configuration.", e); //$NON-NLS-1$
		}
	}

	private CoreException configurationException(String message, Exception e) {
		return new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, e));
	}

	public void bind(IReceiver receiver) {
		this.receiver = receiver;
	}

	public void unbind(IReceiver receiver) {
		this.receiver = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.riena.monitor.client.ISender#configureStore(org
	 * .eclipse.riena.monitor.client.ICollectibleStore)
	 */
	public void setStore(IStore store) {
		Assert.isNotNull(store, "store must not be null");
		this.store = store;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.riena.monitor.client.ISender#addCategory(java.
	 * lang.String )
	 */
	public void setCategories(Map<String, Category> categories) {
		Assert.isNotNull(categories, "categories must not be null"); //$NON-NLS-1$
		this.categories = categories;
	}

	public void start() {
		if (started) {
			return;
		}
		started = true;

		for (String categoryName : categories.keySet()) {
			Sender sender = new Sender(categoryName);
			senders.put(categoryName, sender);
			// check if there are remaining collectibles, if so trigger transfer in the background with a slight delay
			sender.tryIt(Millis.seconds(5));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.riena.monitor.client.ISender#stop()
	 */
	public void stop() {
		if (!started) {
			return;
		}
		started = false;
		for (Sender sender : senders.values()) {
			sender.cancel();
		}
		senders.clear();
	}

	public synchronized void triggerTransfer(String categoryName) {
		if (!started) {
			return;
		}
		Sender sender = senders.get(categoryName);
		if (sender == null) {
			return;
		}
		sender.tryIt(0);
	}

	private final class Sender extends Job {

		private final String category;
		private boolean retrying;

		private Sender(String category) {
			super("SimpleSender"); //$NON-NLS-1$
			this.category = category;
		}

		private void tryIt(long delay) {
			if (retrying) {
				trace("Retry already scheduled.");
				return;
			}
			schedule(delay);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
		 * IProgressMonitor)
		 */
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			trace("Sender retrying: " + retrying);
			trace("sender start - " + category);
			if (receiver == null) {
				trace("sender ended(no receiver) - " + category);
				return Status.OK_STATUS;
			}
			List<Collectible<?>> transferables = store.retrieveTransferables(category);
			if (transferables.size() == 0) {
				trace("sender ended(nothing to send) - " + category);
				return Status.OK_STATUS;
			}
			transfer(transferables);
			trace("sender ended - " + category);
			return Status.OK_STATUS;
		}

		/**
		 * 
		 */
		private void transfer(List<Collectible<?>> transferables) {
			trace("sender transfer " + transferables.size() + " transferables:");
			for (Collectible<?> transferable : transferables) {
				trace(" - " + transferable);
			}
			try {
				if (receiver.take(System.currentTimeMillis(), transferables)) {
					store.commitTransferred(transferables);
					retrying = false;
				} else {
					throw new RuntimeException("Retry sending later because receiver rejected it."); //$NON-NLS-1$
				}
			} catch (Throwable t) {
				trace("sending failed with: " + t);
				trace("retrying in " + retryTime + " milli seconds");
				retrying = true;
				schedule(retryTime);
			}
		}

	}

	private static void trace(String line) {
		if (TRACE) {
			System.out.println(line);
		}
	}
}
