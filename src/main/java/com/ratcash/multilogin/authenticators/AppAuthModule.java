/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ratcash.multilogin.authenticators;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import name.aikesommer.authenticator.AuthModule;
import name.aikesommer.authenticator.PluggableAuthenticator;


@ApplicationScoped
public class AppAuthModule extends AuthModule {
	@Inject
	SampleOAuth oauth;

	@Override
	protected PluggableAuthenticator getPrimaryAuthenticator() {
		return oauth;
	}
}
