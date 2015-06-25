/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ratcash.multilogin.authenticators;

import name.aikesommer.authenticator.modules.OAuth2ResourceAuthenticator;
import javax.enterprise.context.ApplicationScoped;
import name.aikesommer.authenticator.AuthenticationRequest;
import name.aikesommer.authenticator.SimplePrincipal;

@ApplicationScoped
public class SampleOAuth extends OAuth2ResourceAuthenticator {


	@Override
	public SimplePrincipal isTokenValid(String access_token, AuthenticationRequest request) {
		//some JAX-RS call to the Authorization Server (and cache it using Hazelcast)
		
		System.out.println("Validating OAuth token=" + access_token);
		if(access_token.contentEquals("mytoken")) {
			SimplePrincipal user = new SimplePrincipal("api", "api");
			return user;
		}
		return null;
	}

	@Override
	protected String getRealmName() {
		return "ACME";
	}
	
}
