/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ratcash.multilogin.sample.auth.ctrl;

import com.ratcash.oauth.provider.ctrl.OAuthConstants;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import com.ratcash.authenticator.AuthenticationException;
import name.aikesommer.authenticator.AuthenticationRequest;
import name.aikesommer.authenticator.SimplePrincipal;
import com.ratcash.authenticator.TicketAuthenticator;

@ApplicationScoped
public class SampleOAuth extends TicketAuthenticator {

	@Override
	protected SimplePrincipal loadPrincipal(AuthenticationManager manager, AuthenticationRequest request, String ticket) {
		//If the server is not local,
        // make a JAX-RS call to the Authorization Server (and cache it using Hazelcast)
		
		System.out.println("Validating OAuth token=" + ticket);
		String expectedToken = "mytoken";
		if(ticket != null && expectedToken.contentEquals(ticket)) {
			SimplePrincipal user = new SimplePrincipal("api", "api");
			return user;
		}
		return null;
	}
    
    @Override
	public AuthenticationRequest.Status initiateAuthentication(AuthenticationManager manager, AuthenticationRequest request) {
		HttpServletResponse resp = request.getHttpServletResponse();
		resp.setHeader(HttpHeaders.WWW_AUTHENTICATE, OAuthConstants.OAUTH_HEADER_NAME + " realm=\"" + getRealmName() + "\"");
 
		return AuthenticationRequest.Status.Continue;
	}

	protected String getRealmName() {
		return "ACME";
	}

	/**
     * Overwrite this to specify a different login-page.
     * @return 
     */
    @Override
    protected String getLoginPage() {
        return "/api/oauth/auth";
    }

	@Override
	public int getPriority() {
		return 10	;
	}
	
	@Override
	public boolean isApplicable(AuthenticationRequest request) {
		String requestURI = request.getRequestPath();
		return requestURI.startsWith("/api");
	}
	
    @Override
    protected String extractTicket(AuthenticationRequest request) throws AuthenticationException {
        return getHeaderParam(request.getHttpServletRequest(), OAuthConstants.OAUTH_HEADER_NAME);
    }
}
