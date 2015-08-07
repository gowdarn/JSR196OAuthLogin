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
package com.ratcash.multilogin.oauth;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import name.aikesommer.authenticator.AuthenticationRequest;
import name.aikesommer.authenticator.PluggableAuthenticator;
import name.aikesommer.authenticator.SimplePrincipal;

public abstract class ResourceAuthenticator extends PluggableAuthenticator {
	
	/**
	 * Implement this to do the access_token validation
	 * @param access_token
	 * @param request
	 * @return null if the token is not valid
	 */
	public abstract SimplePrincipal isTokenValid(String access_token, AuthenticationRequest request);
	
	/**
     * Return the realm-name used for basic authentication.
     *
     * @return The realm name shown at the browser popup dialog.
     */
    protected abstract String getRealmName();

	@Override
	public AuthenticationRequest.Status tryAuthenticate(AuthenticationManager manager, AuthenticationRequest request) {
		// Make the OAuth Request out of this request and validate it
		// Specify where you expect OAuth access token (request header, body or query string)
		String requestURI = request.getRequestPath();
//        boolean isApiUrl = requestURI.startsWith("/api");
//		if(!isApiUrl) {
//			return AuthenticationRequest.Status.None;
//		}
		
		String accessToken;
		try {
			accessToken = getOAuthToken(request.getHttpServletRequest());
		} catch (OAuthException ex) {
			HttpServletResponse resp = request.getHttpServletResponse();
			resp.setHeader("Error", ex.getMessage());
			return AuthenticationRequest.Status.None;
		}

		if(accessToken == null) {
			// token was not found
			return AuthenticationRequest.Status.None;
		}
			
		SimplePrincipal p = isTokenValid(accessToken, request);
		if(p != null) {
			manager.register(request, p);
			return AuthenticationRequest.Status.Success;
		}
		else
			return AuthenticationRequest.Status.Failure;
	}

	@Override
	public AuthenticationRequest.Status authenticate(AuthenticationManager manager, AuthenticationRequest request) {
		return AuthenticationRequest.Status.None;
	}

	@Override
	public AuthenticationRequest.ManageAction manage(AuthenticationManager manager, AuthenticationRequest request) {
		 return AuthenticationRequest.ManageAction.None;
	}
	
	public String getOAuthToken(HttpServletRequest request) throws OAuthException {
		Enumeration<String> authHeaders = request.getHeaders(HttpHeaders.AUTHORIZATION);
		
		String token = null;
		while(authHeaders.hasMoreElements()) {
			String headerValue = authHeaders.nextElement();
			for(String headerParam : headerValue.split(",")) {
				String[] pair = headerParam.trim().split("[ :]");
				if(pair.length != 2)
					continue;

				if(pair[0].equalsIgnoreCase(OAuthConstants.OAUTH_HEADER_NAME)) {
					if(token == null)
						token = pair[1];
					else
						throw new OAuthException(HttpHeaders.AUTHORIZATION + " header(s) containing multiple " 
								+ OAuthConstants.OAUTH_HEADER_NAME + " tokens");
				}
			}
			
		}
		return token;
	}
	
}
