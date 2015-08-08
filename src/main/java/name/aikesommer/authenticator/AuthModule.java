/**
 *    Copyright (C) 2015 OmniBene
 *	  Parts (C) 2007-2010 Aike J Sommer (http://aikesommer.name/)
 *
 *    This file is part of AuthenticRoast.
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
 *
 *    You can reach the author and get more information about this
 *    project at: http://aikesommer.name/
 */
package name.aikesommer.authenticator;

import java.io.IOException;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import name.aikesommer.authenticator.AuthenticationRequest.ManageAction;
import name.aikesommer.authenticator.AuthenticationRequest.Status;


/**
 * This is the main class called by the container. You probably don't want to call this class directly.
 * 
 * @author Aike J Sommer
 */
public abstract class AuthModule extends AuthenticationManagerBase implements ServerAuthModule,
        PluggableAuthenticator.AuthenticationManager {
	
	private static final String IS_MANDATORY = "javax.security.auth.message.MessagePolicy.isMandatory";

    private CallbackHandler handler;

    private Map options;

    private MessagePolicy responsePolicy;

    private MessagePolicy requestPolicy;


	@Override
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy,
            CallbackHandler handler, Map options) throws AuthException {
        this.requestPolicy = requestPolicy;
        this.responsePolicy = responsePolicy;
        this.handler = handler;
        this.options = options;
    }

	@Override
    public Class[] getSupportedMessageTypes() {
        return null;
    }
	
	/**
	 * Retrieves the BeanManager associated with the current ServletContext and then a reference to an @ApplicationScoped
	 * PluggableAuthenticator.
	 * @return 
	 */
	protected abstract PluggableAuthenticator getPrimaryAuthenticator();
	
	@Override
	public AuthStatus validateRequest(MessageInfo info, Subject clientSubject, Subject serviceSubject) throws AuthException {

		PluggableAuthenticator authenticator = getPrimaryAuthenticator();
		
		AuthStatus result = AuthStatus.FAILURE;
		// Reject requests, if there's no authenticator defined
		if(authenticator != null) {
			result = requestValidator(info, clientSubject, serviceSubject, authenticator);
		}

		return result;
	}
	
	
    protected AuthStatus requestValidator(MessageInfo info, Subject clientSubject,
            Subject serviceSubject, PluggableAuthenticator authenticator) throws AuthException {
		
        HttpServletRequest request = (HttpServletRequest) info.getRequestMessage();
        HttpServletResponse response = (HttpServletResponse) info.getResponseMessage();
		
		
		boolean mandatory = isProtectedResource(info);
        JSR196Request authReq = new AuthenticationRequestImpl.JSR196(request, response,
                clientSubject, mandatory);

        boolean finished = false;
        try {
            authenticator.begin(this, authReq);

            /**
             * Check whether we already authenticated the user. In that case we
             * saved our Principal in the session and can just load it from 
             * there.
             * We will call manage() in our authenticator to be able to logout
             * and such things.
             */
            SimplePrincipal simplePrincipal = principalStore.getPrincipal();
            if (simplePrincipal != null) {
                ManageAction action = authenticator.manage(this, authReq);
                switch (action) {
                    case None:
                        createPrincipal(simplePrincipal, clientSubject);
                        return AuthStatus.SUCCESS;
                    case Clear:
						principalStore.invalidate();
                        return AuthStatus.SEND_CONTINUE;
                }
            }

            /**
             * The user hasn't been authenticated before, so we'll try to do
             * that now. The actual process of authentication will be done 
             * by the authenticator class in our web-app.
             */
            Status status = authenticator.validateAuthenticationInfo(this, authReq);

            switch (status) {
                case Success:
                    return AuthStatus.SUCCESS;
                case None:
                    if (!mandatory) {
                        return AuthStatus.SUCCESS;
                    }
                    status = authenticator.initiateAuthentication(this, authReq);
                    if (status == Status.Success) {
                        return AuthStatus.SUCCESS;
                    }
                case Continue:
                case Failure:
                    response.setStatus(response.SC_UNAUTHORIZED);
                    return AuthStatus.SEND_CONTINUE;
                default:
                    throw new IllegalArgumentException("dont know how to handle " + status);
            }
        } catch (Exception ex) {
            finished = true;
            authenticator.abort(this, authReq, ex);
            ex.printStackTrace();
            try {
                response.sendError(response.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            } catch (IOException ex1) {
                ex1.printStackTrace();
            }

            return AuthStatus.FAILURE;
        } finally {
            if (!finished) {
                authenticator.finish(this, authReq);
            }
        }
    }

	@Override
    public AuthStatus secureResponse(MessageInfo info, Subject serviceSubject) throws AuthException {
		HttpServletResponse response = (HttpServletResponse) info.getResponseMessage();
		if(response.getStatus() > 400) {
			return AuthStatus.SEND_CONTINUE;
		} else {
			return AuthStatus.SEND_SUCCESS;
		}
//        return success ? AuthStatus.SEND_SUCCESS : AuthStatus.SEND_CONTINUE;
    }

	@Override
    public void cleanSubject(MessageInfo info, Subject subject) throws AuthException {
    }

    /**
     * This is just to create the principal as needed by jsr 196 from our little
     * helper-class SimplePrincipal.
     * 
     * @param simplePrincipal The SimplePrincipal representing the authenticated
     *          user.
     */
    private void createPrincipal(SimplePrincipal simplePrincipal, Subject clientSubject) throws
            IOException, UnsupportedCallbackException {
        clientSubject.getPrincipals().add(simplePrincipal);

        CallerPrincipalCallback callerCallback = new CallerPrincipalCallback(clientSubject,
                simplePrincipal);
        GroupPrincipalCallback groupCallback = new GroupPrincipalCallback(clientSubject, simplePrincipal.getGroups().toArray(
                new String[0]));

        handler.handle(new Callback[]{callerCallback, groupCallback});
    }

    @Override
    public void register(AuthenticationRequest request, SimplePrincipal simplePrincipal) {
        try {
            createPrincipal(simplePrincipal, ((JSR196Request) request).getClientSubject());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        super.register(request, simplePrincipal);
    }
	
	public static boolean isProtectedResource(MessageInfo messageInfo) {
		return Boolean.valueOf((String) messageInfo.getMap().get(IS_MANDATORY));
	}
}
