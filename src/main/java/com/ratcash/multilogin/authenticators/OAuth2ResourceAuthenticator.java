/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ratcash.multilogin.authenticators;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import name.aikesommer.authenticator.AuthenticationRequest;
import name.aikesommer.authenticator.PluggableAuthenticator;
import name.aikesommer.authenticator.SimplePrincipal;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.apache.oltu.oauth2.rs.response.OAuthRSResponse;

public abstract class OAuth2ResourceAuthenticator extends PluggableAuthenticator {
	
	/**
	 * Implement this to do the access_token validation
	 * @param access_token
	 * @param request
	 * @return 
	 */
	public abstract boolean isTokenValid(String access_token, AuthenticationRequest request);
	
	/**
     * Return the realm-name used for basic authentication.
     *
     * @return The realm name shown at the browser popup dialog.
     */
    protected abstract String getRealmName();

	@Override
	public AuthenticationRequest.Status tryAuthenticate(AuthenticationManager manager, AuthenticationRequest request) {
		try {
			// Make the OAuth Request out of this request and validate it
			// Specify where you expect OAuth access token (request header, body or query string)
			OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(request.getHttpServletRequest(), ParameterStyle.HEADER);
			// Get the access token
            String accessToken = oauthRequest.getAccessToken();
			
			if(isTokenValid(accessToken, request)) {
				System.out.println("Success ...");
				SimplePrincipal user = new SimplePrincipal("api", "api");
				manager.register(request, user);
				return AuthenticationRequest.Status.Success;
			}
			else
				return AuthenticationRequest.Status.Failure;
		} catch (OAuthSystemException|OAuthProblemException ex) {
			return AuthenticationRequest.Status.None;
		}
	}

	@Override
	public AuthenticationRequest.Status authenticate(AuthenticationManager manager, AuthenticationRequest request) {
		HttpServletResponse resp = request.getHttpServletResponse();
		
		try {
			OAuthResponse oauthResponse = OAuthRSResponse
					.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
					.setRealm(getRealmName())
					.buildHeaderMessage();
			resp.setHeader(OAuth.HeaderType.WWW_AUTHENTICATE, oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (OAuthSystemException ex) {
			Logger.getLogger(OAuth2ResourceAuthenticator.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(OAuth2ResourceAuthenticator.class.getName()).log(Level.SEVERE, null, ex);
		}
 
		return AuthenticationRequest.Status.Continue;
	}

	@Override
	public AuthenticationRequest.ManageAction manage(AuthenticationManager manager, AuthenticationRequest request) {
		 return AuthenticationRequest.ManageAction.None;
	}
	
}
