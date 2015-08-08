/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ratcash.multilogin.authenticators;

import com.ratcash.multilogin.oauth.ResourceAuthenticator;
import javax.enterprise.context.ApplicationScoped;
import name.aikesommer.authenticator.AuthenticationRequest;
import name.aikesommer.authenticator.SimplePrincipal;

@ApplicationScoped
public class SampleOAuth extends ResourceAuthenticator {


	@Override
	public SimplePrincipal isTokenValid(String access_token, AuthenticationRequest request) {
		//some JAX-RS call to the Authorization Server (and cache it using Hazelcast)
		
		System.out.println("Validating OAuth token=" + access_token);
		String expectedToken = "mytoken";
		if(access_token != null && expectedToken.contentEquals(access_token)) {
			SimplePrincipal user = new SimplePrincipal("api", "api");
			return user;
		}
		return null;
	}

	@Override
	protected String getRealmName() {
		return "ACME";
	}
	
	/**
     * Overwrite this to specify a different login-page.
     */
    protected String getLoginPage() {
        return "/api/oauth/auth";
    }

	@Override
	public int getPriority() {
		return 10;
	}
	
}
