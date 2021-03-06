/**
 *    Copyright (C) 2007-2010 Aike J Sommer (http://aikesommer.name/)
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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletContext;


/**
 *
 * @author Aike J Sommer
 */
public abstract class AuthenticationManagerBase implements PluggableAuthenticator.AuthenticationManager {
	
	@Inject
	PrincipalHolder principalStore;

    protected static final Logger log = Logger.getLogger(AuthenticationManagerBase.class.getName());

    private RequestHandler requestHandler = new RequestHandler();

	@Override
    public boolean hasRequest(AuthenticationRequest request) {
        return requestHandler.getPathForRequest(request) != null;
    }

	@Override
    public void saveRequest(AuthenticationRequest request) {
        requestHandler.saveRequest((ModifiableRequest) request);
    }

	@Override
    public void saveRequest(AuthenticationRequest request, String path) {
        requestHandler.saveRequest((ModifiableRequest) request, path);
    }

	@Override
    public void clearRequest(AuthenticationRequest request) {
        requestHandler.clearRequest(request);
    }

	@Override
    public void forward(AuthenticationRequest authRequest, String path) {
        ServletContext sc = authRequest.getServletContext();
        forward(authRequest, sc.getContextPath(), path);
    }

    public void forward(AuthenticationRequest authRequest, String context, String path) {
        try {
            String to;
            if (path.startsWith("http://") || path.startsWith("https://")) {
                to = path;
            } else {
                to = context + path;
            }
            authRequest.getHttpServletResponse().sendRedirect(to);
            ((ModifiableRequest) authRequest).setForwarded(true);
        } catch (Throwable t) {
            log.severe("unexpected error forwarding or redirecting to " + path + ": " + t);
            log.log(Level.FINE, "unexpected error forwarding or redirecting to " + path, t);
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else {
                throw new RuntimeException(t);
            }
        }
    }

	@Override
    public void register(AuthenticationRequest request, SimplePrincipal simplePrincipal) {
		principalStore.setPrincipal(simplePrincipal);
    }

	@Override
    public void restoreRequest(AuthenticationRequest request) {
        requestHandler.restoreRequest(request);
    }

	@Override
    public void redirectToRequest(AuthenticationRequest request) {
        String path = requestHandler.getPathForRequest(request);
        String context = requestHandler.getContextForRequest(request);

        if (path != null) {
            forward(request, context, path);
        } else {
            throw new IllegalStateException();
        }
    }

	@Override
    public boolean matchesRequest(AuthenticationRequest request) {
        return requestHandler.matchesRequest((ModifiableRequest) request);
    }

	@Override
    public void addQueryString(AuthenticationRequest request, String queryString) {
        requestHandler.addQueryString(request, queryString);
    }
}
