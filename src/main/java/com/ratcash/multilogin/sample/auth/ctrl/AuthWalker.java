/**
 *    Copyright (C) 2015 OmniBene
 *
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
 */
package com.ratcash.multilogin.sample.auth.ctrl;

import java.util.Iterator;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import name.aikesommer.authenticator.PluggableAuthenticator;
import name.aikesommer.authenticator.modules.CompositeAuthenticator;

/**
 *
 * @author rex
 */
@ApplicationScoped
@Primary
public class AuthWalker extends CompositeAuthenticator {

	@Inject
	@Default
	Instance<PluggableAuthenticator> authenticators;

	@Override
	protected Iterator<PluggableAuthenticator> getAuthenticators() {
		return authenticators.iterator();
	}
}
