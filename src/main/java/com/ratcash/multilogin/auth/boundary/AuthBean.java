/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ratcash.multilogin.auth.boundary;

import com.ratcash.multilogin.oauth.FlowData;
import com.ratcash.multilogin.oauth.FlowState;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;
import javax.enterprise.inject.Model;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

@Model
public class AuthBean {
    String appName;
    String developer;
    
    String clientId;
    String responseType;
	String redirectUri;
    String scope;
    
    @Inject
	FlowState flowState;
    
    public void allow() throws IOException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        if("code".equalsIgnoreCase(responseType)) {
			String code = UUID.randomUUID().toString();
			String token = issueToken(clientId, "rex"); //principal.getName());   // needs to be written somewhere (DB)
			FlowData fd = new FlowData(code, redirectUri, clientId, token, scope);
			flowState.addFlowData(fd);
			// do some checks
			URI uri = UriBuilder.fromUri(redirectUri).queryParam("code",  fd.getCode()).build((Object) null);
			
			externalContext.redirect(uri.toString());
		} else if ("token".equalsIgnoreCase(responseType)) {
			String token = UUID.randomUUID().toString();
			URI uri = UriBuilder.fromUri(redirectUri).queryParam("token", token).build((Object) null);
			externalContext.redirect(uri.toString());
		} else {
            URI uri = UriBuilder.fromUri(redirectUri).queryParam("error", "Unspecified error").build((Object) null);
			externalContext.redirect(uri.toString());
        }
    }
    
    public void reject() throws IOException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        URI uri = UriBuilder.fromUri(redirectUri).queryParam("error", "Access rejected").build((Object) null);
        externalContext.redirect(uri.toString());
    }

    public String getAppName() {
        return appName;
    }

    public String getDeveloper() {
        return developer;
    }

    
    public void checkParams() throws IOException {
        // TODO 
        // 1. validate and convert params to Obejcts the necessary param combinations (Use JSR310)
        // 2. load everything from the DB (the appName based on clientId, validate the redirectURI, 
        
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        if(scope == null) 
            scope = "Profile";
        
		if (redirectUri == null || clientId == null || responseType == null) {
			//externalContext.redirect(externalContext.getRequestContextPath() + "/error.xhtml");
		}
        
        appName = "Sample Oauth Client App";
        developer = "Pro Developers, inc.";
    }
    
    protected String issueToken(String clientId, String userId) {
		// look up, validate
		String token = UUID.randomUUID().toString();
		// write-back the token to the DB
		return token;
	}

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
