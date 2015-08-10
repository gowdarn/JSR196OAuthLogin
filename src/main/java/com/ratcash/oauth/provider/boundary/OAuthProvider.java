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
package com.ratcash.oauth.provider.boundary;

import com.ratcash.oauth.provider.entity.FlowData;
import com.ratcash.oauth.provider.entity.FlowData;
import com.ratcash.oauth.provider.ctrl.FlowState;
import com.ratcash.oauth.provider.ctrl.FlowState;
import com.ratcash.oauth.provider.ctrl.OAuthConstants;
import com.ratcash.oauth.provider.ctrl.OAuthConstants;
import java.security.Principal;
import java.util.UUID;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Inspired by http://aaronparecki.com/articles/2012/07/29/1/oauth2-simplified
 * and http://tav.espians.com/oauth-3.0-the-sane-and-simple-way-to-do-it.html
 * @author rex
 */
@Path("oauth")
@RequestScoped
public class OAuthProvider {
	@Inject
	FlowState flowState;
	
	@Context
	Principal principal;
    
    // See the /auth endpoint in auth.xhtml JSF page and the corresponding backing bean
	

	//	POST https://api.oauth2server.com/token
	//    grant_type=authorization_code&
	//    code=AUTH_CODE_HERE&
	//    redirect_uri=REDIRECT_URI&
	//    client_id=CLIENT_ID&
	//    client_secret=CLIENT_SECRET
	/**
	 * PASSWORD or ACCESS_CODE grant type
	 * @param grantType
	 * @param code
	 * @param redirectUri
	 * @param clientId
	 * @param clientSecret 
	 * @param username 
	 * @param password 
	 */
	@Path("token")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public FlowData provideToken(@QueryParam("grant_type") OAuthConstants.GRANT_TYPE grantType,
			@QueryParam("code") String code, @QueryParam("redirect_uri") String redirectUri, 
			@QueryParam("client_id") String clientId, @QueryParam("client_secret") String clientSecret,
			@QueryParam("username") String username, @QueryParam("password") String password
			) {
		FlowData fd;
		switch (grantType) {
		case AUTHORIZATION_CODE:
			fd = flowState.getFlowData(code, clientId);
			// validate client secret
			boolean validClientSecret = true;
			if(fd != null && validClientSecret) {
				return fd;
			}
			break;
		case PASSWORD:
			// validate username and password
			boolean credentialsValid = true;
			if(credentialsValid) {
				String token = issueToken(clientId, "rex"); //principal.getName());
				fd = new FlowData(null, redirectUri, clientId, token, null);
				return fd;
			}
			break;
		case CLIENT_CREDENTIALS:
			break;
		default:
			// nothing
		}
		return null;
	}
	
	@Path("register")
	@POST
	public String registerApp() {
		return "success";
	}

	protected String issueToken(String clientId, String userId) {
		// look up, validate
		String token = UUID.randomUUID().toString();
		// write-back the token to the DB
		return token;
	}

}
