/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ratcash.multilogin.authenticators;

import javax.enterprise.context.ApplicationScoped;
import name.aikesommer.authenticator.AuthenticationRequest;
import name.aikesommer.authenticator.Primary;

@ApplicationScoped
@Primary
public class SampleOAuth extends OAuth2ResourceAuthenticator {

	public SampleOAuth() {
		System.out.println("Cr eated");
	}

	@Override
	public boolean isTokenValid(String access_token, AuthenticationRequest request) {
		//some JAX-RS call to the Authorization Server (and cache it using Hazelcast)
		
		System.out.println("Called. Token=" + access_token);
		return access_token.contentEquals("mytoken");
	}

	@Override
	protected String getRealmName() {
		return "ACME";
	}
	
}
