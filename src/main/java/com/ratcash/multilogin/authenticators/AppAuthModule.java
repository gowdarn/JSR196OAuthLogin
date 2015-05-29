/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ratcash.multilogin.authenticators;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import name.aikesommer.authenticator.AuthModule;
import name.aikesommer.authenticator.PluggableAuthenticator;
import name.aikesommer.authenticator.Primary;
import name.aikesommer.authenticator.PrincipalStore;


@ApplicationScoped
public class AppAuthModule extends AuthModule {
	@Inject
	@Primary
	SampleOAuth oauth;
	
	@Inject
	@SessionScoped
	PrincipalStore store;

	@Override
	protected PluggableAuthenticator getPrimaryAuthenticator() {
		return oauth;
	}
	
	@Override
	protected PrincipalStore getPrincipalStore() {
		return store;
	}
	
}
