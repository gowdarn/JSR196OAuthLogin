/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package name.aikesommer.authenticator.modules;

import com.ratcash.multilogin.sample.auth.ctrl.FacebookAuthenticator;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import name.aikesommer.authenticator.AuthenticationRequest;
import name.aikesommer.authenticator.PluggableAuthenticator;
import name.aikesommer.authenticator.SimplePrincipal;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public abstract class SocialAuthenticator extends PluggableAuthenticator {
    protected static final String PRINCIPAL_NOTE = PluggableAuthenticator.class.getName() + ".PRINCIPAL";
    
    protected static final String CODE = "code";

    /**
     * Overwrite this to specify a different error-page.
     * @return
     */
    protected abstract String getErrorPage();

    protected abstract String getRegisterPage();
    
    protected abstract String getLoginAction();
    protected abstract String getCallbackAction();

    protected String getRedirectPage(String url) throws UnsupportedEncodingException {
        String encode = "ISO-8859-1";
        String data = URLEncoder.encode(url, encode);
        return url;
        //        return "/ui/social-login.xhtml?redirectto=" + data;
    }

    /**
     * Overwrite this to specify a different path to direct to, if there is no
     * original request.
     * @return
     */
    protected abstract String getNextPath();

    @Override
    public AuthenticationRequest.Status validateAuthenticationInfo(PluggableAuthenticator.AuthenticationManager manager, AuthenticationRequest request) {
        if (manager.matchesRequest(request) && request.getSessionMap().containsKey(PRINCIPAL_NOTE)) {
            manager.register(request, (SimplePrincipal) request.getSessionMap().get(PRINCIPAL_NOTE));
            request.getSessionMap().remove(PRINCIPAL_NOTE);
            manager.restoreRequest(request);
            return AuthenticationRequest.Status.Success;
        }
        String requestURI = request.getRequestPath();
        boolean callbackAction = requestURI.endsWith(getCallbackAction());
        if (callbackAction) {
            SocialUserData facebookData = checkCredentials(manager, request);
            if (facebookData != null) {
                request.getSessionMap().put(PRINCIPAL_NOTE, loadPrincipal(manager, request, facebookData));
                String queryString = request.getHttpServletRequest().getQueryString();
                if (queryString != null && queryString.length() > 0) {
                    manager.addQueryString(request, queryString);
                }
                if (manager.hasRequest(request)) {
                    manager.redirectToRequest(request);
                } else {
                    manager.saveRequest(request, getNextPath());
                    manager.forward(request, getNextPath());
                }
                return AuthenticationRequest.Status.Continue;
            }
            manager.forward(request, getRegisterPage());
            return AuthenticationRequest.Status.Continue;
        } else {
            boolean loginAction = requestURI.endsWith(getLoginAction());
            if (loginAction) {
                redirectToFacebook(manager, request);
                return AuthenticationRequest.Status.Continue;
            }
        }
        return AuthenticationRequest.Status.None;
    }

    @Override
    public AuthenticationRequest.Status initiateAuthentication(PluggableAuthenticator.AuthenticationManager manager, AuthenticationRequest request) {
        //        manager.saveRequest(request);
        //        manager.forward(request, getLoginPage());
        return AuthenticationRequest.Status.None;
    }

    @Override
    public AuthenticationRequest.ManageAction manage(PluggableAuthenticator.AuthenticationManager manager, AuthenticationRequest request) {
        return AuthenticationRequest.ManageAction.None;
    }

    public void redirectToFacebook(PluggableAuthenticator.AuthenticationManager manager, AuthenticationRequest request) {
        try {
            OAuthService service = getScribeService(request);
            // Obtain the Authorization URL
            String url = service.getAuthorizationUrl(null);
            manager.forward(request, getRedirectPage(url));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FacebookAuthenticator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public SocialUserData checkCredentials(PluggableAuthenticator.AuthenticationManager manager, AuthenticationRequest request) {
        String code = request.getParameter(CODE);
        Verifier verifier = new Verifier(code);
        OAuthService service = getScribeService(request);
        // Trade the Request Token and Verfier for the Access Token
        Token accessToken = service.getAccessToken(null, verifier);
        System.out.println("Got the Access Token!");
        System.out.println("(if your curious it looks like this: " + accessToken + " )");
        System.out.println();
        return getSocialUserData(service, accessToken);
    }

    protected abstract SocialUserData getSocialUserData(OAuthService service, Token accessToken);

    protected abstract SimplePrincipal loadPrincipal(AuthenticationManager manager, AuthenticationRequest request, SocialUserData socialData);

    protected abstract OAuthService getScribeService(AuthenticationRequest request);
    
}
