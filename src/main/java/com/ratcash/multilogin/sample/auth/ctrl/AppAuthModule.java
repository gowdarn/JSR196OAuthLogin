/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ratcash.multilogin.sample.auth.ctrl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import name.aikesommer.authenticator.AuthModule;
import name.aikesommer.authenticator.PluggableAuthenticator;

@ApplicationScoped
public class AppAuthModule extends AuthModule {
//	@Inject
//	SampleOAuth oauth;

	@Inject
	@Primary
	AuthWalker walker;

	@Override
	protected PluggableAuthenticator getPrimaryAuthenticator() {
		return walker;
	}
}
