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
package org.eclipse.riena.internal.security.authenticationservice;

import java.security.Principal;
import java.util.Set;

import javax.security.auth.callback.Callback;
import javax.security.auth.login.LoginException;

import org.eclipse.equinox.security.auth.ILoginContext;
import org.eclipse.equinox.security.auth.LoginContextFactory;

import org.eclipse.riena.security.common.ISubjectHolder;
import org.eclipse.riena.security.common.authentication.AuthenticationFailure;
import org.eclipse.riena.security.common.authentication.AuthenticationTicket;
import org.eclipse.riena.security.common.authentication.Callback2CredentialConverter;
import org.eclipse.riena.security.common.authentication.IAuthenticationService;
import org.eclipse.riena.security.common.authentication.credentials.AbstractCredential;
import org.eclipse.riena.security.common.session.ISessionHolder;
import org.eclipse.riena.security.common.session.Session;
import org.eclipse.riena.security.server.session.ISessionService;

/**
 * The <code>AuthenticationService</code> will perform the authentication
 * operations. This class contains the logic implementation of the methods
 * defined in the <code>IAuthenticationService</code> Interface. The real
 * authentication operations are linked into by a dynamically loaded
 * authentication module. The AuthenticationService itself just passes the
 * authentication attributes this module, makes some generic checks to the
 * credentials passed by the client and synchronizes the operations with the
 * session service.
 * 
 */
public class AuthenticationService implements IAuthenticationService {

	/**
	 * version ID (controlled by CVS)
	 */
	public static final String VERSION_ID = "$Id$"; //$NON-NLS-1$

	// private Properties properties;
	//	private final static Logger LOGGER = Log4r.getLogger(Activator.getDefault(), AuthenticationService.class);

	// private IAuthenticationModule authenticationModule;
	private ISessionService sessionService;
	private ISubjectHolder subjectHolder;
	private ISessionHolder sessionHolder;

	/**
	 * default constructor
	 * 
	 */
	public AuthenticationService() {
		super();
	}

	public void bind(ISessionService sessionService) {
		this.sessionService = sessionService;
	}

	public void unbind(ISessionService sessionService) {
		if (this.sessionService == sessionService) {
			this.sessionService = null;
		}
	}

	public void bind(ISubjectHolder subjectHolder) {
		this.subjectHolder = subjectHolder;
	}

	public void unbind(ISubjectHolder subjectHolder) {
		if (this.subjectHolder == subjectHolder) {
			this.subjectHolder = null;
		}
	}

	// public void bind(IAuthenticationModule authenticationModule) {
	// this.authenticationModule = authenticationModule;
	// }
	//
	// public void unbind(IAuthenticationModule authenticationModule) {
	// if (this.authenticationModule == authenticationModule) {
	// this.authenticationModule = null;
	// }
	// }

	public void bind(ISessionHolder sessionHolder) {
		this.sessionHolder = sessionHolder;
	}

	public void unbind(ISessionHolder sessionHolder) {
		if (this.sessionHolder == sessionHolder) {
			this.sessionHolder = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.riena.security.common.authentication.IAuthenticationService
	 * #login(java.lang.String,
	 * org.eclipse.riena.security.common.authentication.
	 * credentials.AbstractCredential[])
	 */
	public AuthenticationTicket login(String loginContext, AbstractCredential[] credentials)
			throws AuthenticationFailure {
		try {
			Callback[] callbacks = Callback2CredentialConverter.credentials2Callbacks(credentials);
			// create login context and login
			//			LoginContext lc = new LoginContext(loginContext, new MyCallbackHandler(callbacks));
			//			lc.login();
			ILoginContext secureContext = LoginContextFactory.createContext(loginContext);
			AuthenticationServiceCallbackHandler.setCallbacks(callbacks);

			secureContext.login();
			// if login was successful, create session and add principals to
			// session
			Set<Principal> principals = secureContext.getSubject().getPrincipals();
			// add only new principals to ticket (not the ones that might exist
			// in the session)
			AuthenticationTicket ticket = new AuthenticationTicket();
			Session session = sessionHolder.getSession();
			if (session != null) {
				sessionService.invalidateSession(session);
			}
			Principal[] pArray = principals.toArray(new Principal[principals.size()]);
			session = sessionService.generateSession(pArray);
			sessionHolder.setSession(session);
			for (Principal p : principals) {
				ticket.getPrincipals().add(p);
			}
			// add session object to authentication ticket
			ticket.setSession(session);
			return ticket;
		} catch (LoginException e) {
			throw new AuthenticationFailure("AuthenticationService login failed", e); //$NON-NLS-1$
		} finally {
			AuthenticationServiceCallbackHandler.setCallbacks(null);
		}
	}

	public void logout() throws AuthenticationFailure {
		Session session = sessionHolder.getSession();
		if (session == null) {
			throw new AuthenticationFailure("no valid session"); //$NON-NLS-1$
		}
		sessionService.invalidateSession(session);
		sessionHolder.setSession(null);
	}
}