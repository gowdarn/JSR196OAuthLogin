/**
 *    Copyright (c) 2015 OmniBene
 *	  Copyright (C) 2007-2010 Aike J Sommer (http://aikesommer.name/)
 *
 *    This file is part of AuthenticRoast.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General
 *    Public License along with this library; if not, write to the
 *    Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 *    Boston, MA 02110-1301 USA
 *
 *    You can reach the author and get more information about this
 *    project at: http://aikesommer.name/
 */
package name.aikesommer.authenticator.modules;

import name.aikesommer.authenticator.PluggableAuthenticator;
import name.aikesommer.authenticator.AuthenticationRequest;
import java.util.Iterator;
import name.aikesommer.authenticator.AuthenticationRequest.ManageAction;
import name.aikesommer.authenticator.AuthenticationRequest.Status;

/**
 * This class allows for easy combination of different Authenticators, for example to allow a combination of ticket- and form-login. Just implement
 * getAuthenticators() and return the authenticators you want to use. If you need a specific order in which they are used make sure to return a list and not a
 * set or similar.
 *
 * @author Aike J Sommer
 */
public abstract class CompositeAuthenticator extends PluggableAuthenticator {

	public CompositeAuthenticator() {
	}

	/**
	 * Returns the currently stored authenticators in this instance.
	 *
	 * @return The currently stored authenticators in this instance or null if they have not been created yet.
	 */
	protected abstract Iterator<PluggableAuthenticator> getAuthenticators();

	@Override
	public Status validateAuthenticationInfo(AuthenticationManager manager,
			AuthenticationRequest request) {

		Iterator<PluggableAuthenticator> iterator = getAuthenticators();
		while (iterator.hasNext()) {
			PluggableAuthenticator pa = iterator.next();
			Status status = pa.validateAuthenticationInfo(manager, request);
			System.out.println("--- TryAuthenticate: PA = " + pa + " status= " + status);
			if (status != null && status != Status.None) {
				return status;
			}
		}

		return Status.None;
	}

	@Override
	public Status initiateAuthentication(AuthenticationManager manager,
			AuthenticationRequest request) {

		Iterator<PluggableAuthenticator> iterator = getAuthenticators();
		while (iterator.hasNext()) {
			PluggableAuthenticator pa = iterator.next();
			Status status = pa.initiateAuthentication(manager, request);
			System.out.println("--- Authenticate: PA = " + pa + " status= " + status);
			if (status != null && status != Status.None) {
				return status;
			}
		}

		return Status.None;
	}

	@Override
	public ManageAction manage(AuthenticationManager manager,
			AuthenticationRequest request) {

		Iterator<PluggableAuthenticator> iterator = getAuthenticators();
		while (iterator.hasNext()) {
			PluggableAuthenticator pa = iterator.next();
			ManageAction action = pa.manage(manager, request);
			System.out.println("--- Manage: PA = " + pa + " action= " + action);
			if (action != null && action != ManageAction.None) {
				return action;
			}
		}

		return ManageAction.None;
	}
}
