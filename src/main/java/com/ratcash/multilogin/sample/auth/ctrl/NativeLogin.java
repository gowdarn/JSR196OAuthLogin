/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ratcash.multilogin.sample.auth.ctrl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import name.aikesommer.authenticator.AuthenticationRequest;
import name.aikesommer.authenticator.PluggableAuthenticator.AuthenticationManager;
import name.aikesommer.authenticator.PrincipalHolder;
import name.aikesommer.authenticator.SimplePrincipal;
import name.aikesommer.authenticator.modules.FormAuthenticator;

@ApplicationScoped
public class NativeLogin extends FormAuthenticator {

	@Inject
	PrincipalHolder principalStore;

	@Override
	protected Object checkCredentials(AuthenticationManager manager, AuthenticationRequest request, String username, String password) {
		boolean result = false;

		// TODO: put here some code that validates the account against the DB records
		if (username != null && ("user@nowhere.org".equalsIgnoreCase(username) 
				|| "api@nowhere.org".equalsIgnoreCase(username))) {
			result = true;
		}
		return result ? username : null;
	}

	@Override
	protected SimplePrincipal loadPrincipal(AuthenticationManager manager, AuthenticationRequest request, Object userAccount) {
		SimplePrincipal simplePrincipal;
		if(((String) userAccount).startsWith("user"))
			simplePrincipal = new SimplePrincipal(userAccount.toString(), "user");
		else {
			System.out.println("granting API");
			simplePrincipal = new SimplePrincipal(userAccount.toString(), "api");
		}
		
		simplePrincipal.setUserAccount(userAccount);
		return simplePrincipal;
	}

	@Override
	protected String getLoginPage() {
		System.out.println("Native Login sending to 'signin.xhtml'");
		return "/signin.xhtml";
	}

	@Override
	protected String getErrorPage() {
		System.out.println("Native Login sending to error page: 'signin.xhtml'");
		return "/signin.xhtml";
	}

	@Override
	protected String getNextPath() {
		return "/restricted/";
	}

	@Override
	public int getPriority() {
		return 9999;
	}
	
	

}
