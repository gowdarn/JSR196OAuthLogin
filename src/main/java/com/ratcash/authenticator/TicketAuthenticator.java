package com.ratcash.authenticator;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import com.ratcash.authenticator.AuthenticationException;
import name.aikesommer.authenticator.PluggableAuthenticator;
import name.aikesommer.authenticator.SimplePrincipal;
import name.aikesommer.authenticator.AuthenticationRequest;
import name.aikesommer.authenticator.AuthenticationRequest.ManageAction;
import name.aikesommer.authenticator.AuthenticationRequest.Status;

public abstract class TicketAuthenticator extends PluggableAuthenticator {

    /**
     * Override this to do some quick-checks to the ticket received. If this fails, other, potentially time-consuming checks
     * like loadPrincipal won't be executed.
     * @param manager
     * @param request
     * @param ticket
     * @return 
     */
    protected boolean checkTicket(AuthenticationManager manager, AuthenticationRequest request, String ticket) {
        return true;
    }
     /**
     * Validates the given ticket and loads the principal for the ticket
     * 
     * @param ticket The ticket from the request.
     * @return A SimplePrincipal instance representing the user.
     */
    protected abstract SimplePrincipal loadPrincipal(AuthenticationManager manager, AuthenticationRequest request, String ticket);
    
	
	/**
     * Overwrite this to specify a different login-page.
     */
    protected abstract String getLoginPage();

    @Override
    public Status validateAuthenticationInfo(AuthenticationManager manager, AuthenticationRequest request) {
        String ticket;
        try {
			ticket = extractTicket(request);
		} catch (AuthenticationException ex) {
			HttpServletResponse resp = request.getHttpServletResponse();
			resp.setHeader("Error", ex.getMessage());
			return AuthenticationRequest.Status.Failure;
		}
        
        if(ticket == null) {
			// ticket was not found
			return AuthenticationRequest.Status.None;
		}
        
        if(!checkTicket(manager, request, ticket))
            return AuthenticationRequest.Status.Failure;
        
        SimplePrincipal p = loadPrincipal(manager, request, ticket);
        if (p != null) {
            manager.register(request, p);
            return Status.Success;
        } else
            return Status.Failure;
    }
    
    protected abstract String extractTicket(AuthenticationRequest request) throws AuthenticationException;

    @Override
    public Status initiateAuthentication(AuthenticationManager manager, AuthenticationRequest request) {
        return Status.None;
    }

    @Override
    public ManageAction manage(AuthenticationManager manager, AuthenticationRequest request) {
        return ManageAction.None;
    }
    
    public static String getHeaderParam(HttpServletRequest request, String paramName) throws AuthenticationException {
		Enumeration<String> authHeaders = request.getHeaders(HttpHeaders.AUTHORIZATION);
		
		String paramValue = null;
		while(authHeaders.hasMoreElements()) {
			String headerValue = authHeaders.nextElement();
			for(String headerParam : headerValue.split(",")) {
				String[] pair = headerParam.trim().split("[ :]");
				if(pair.length != 2)
					continue;

				if(pair[0].equalsIgnoreCase(paramName)) {
					if(paramValue == null)
						paramValue = pair[1];
					else
						throw new AuthenticationException(HttpHeaders.AUTHORIZATION + " header(s) containing multiple " 
								+ paramName + " tokens");
				}
			}
			
		}
		return paramValue;
	}
}
