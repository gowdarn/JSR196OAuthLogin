/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ratcash.multilogin.authenticators;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import name.aikesommer.authenticator.AuthenticationRequest;
import name.aikesommer.authenticator.PluggableAuthenticator;
import name.aikesommer.authenticator.SimplePrincipal;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

/**
 * taken from: http://stackoverflow.com/questions/21626277/how-to-integrate-a-login-with-facebook-button-in-java-ee-project
 */
@ApplicationScoped
public class FacebookAuthenticator extends PluggableAuthenticator {

    private static final String PRINCIPAL_NOTE = PluggableAuthenticator.class.getName() + ".PRINCIPAL";
    public static final String LOGIN_ACTION = "/j_facebook_login";
    public static final String REDIRECT_ACTION = "/j_facebook_redirect";
    public static final String CODE = "code";
    OAuthService service = null;
	
	
	public static final String FACEBOOK_APPID = "facebook.appid";
	public static final String FACEBOOK_APPID_DEFAULT = "1601430796738818";
	
	public static final String FACEBOOK_APP_SECRET = "facebook.appsecret";
	public static final String FACEBOOK_APP_SECRET_DEFAULT = "2a310d9cdfe1b00fc9db94d2f5d22859";
	
   
    /**
     * Overwrite this to specify a different error-page.
	 * @return 
     */
    protected String getErrorPage() {
        return "/login-error.jsp";
    }

    protected String getRegisterPage() {
        return "/register.xhtml";
    }

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
    protected String getNextPath() {
        return "/";
    }

    @Override
    public AuthenticationRequest.Status tryAuthenticate(PluggableAuthenticator.AuthenticationManager manager, AuthenticationRequest request) {
        if (manager.matchesRequest(request) && request.getSessionMap().containsKey(PRINCIPAL_NOTE)) {
            manager.register(request, (SimplePrincipal) request.getSessionMap().get(PRINCIPAL_NOTE));
            request.getSessionMap().remove(PRINCIPAL_NOTE);
            manager.restoreRequest(request);
            return AuthenticationRequest.Status.Success;
        }

        String requestURI = request.getRequestPath();
        boolean loginAction = requestURI.endsWith(LOGIN_ACTION);
        if (loginAction) {

            String facebookId = checkCredentials(manager, request);
            if (facebookId != null) {
                request.getSessionMap().put(PRINCIPAL_NOTE, loadPrincipal(manager, request, facebookId));
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

            manager.forward(request, getRegisterPage() + "?facebook=facebook");
            return AuthenticationRequest.Status.Continue;
        } else {
            boolean redirectAction = requestURI.endsWith(REDIRECT_ACTION);
            if (redirectAction) {
                redirect(manager, request);
                return AuthenticationRequest.Status.Continue;
            }
        }

        return AuthenticationRequest.Status.None;
    }

    @Override
    public AuthenticationRequest.Status authenticate(PluggableAuthenticator.AuthenticationManager manager, AuthenticationRequest request) {
//        manager.saveRequest(request);
//        manager.forward(request, getLoginPage());
        return AuthenticationRequest.Status.None;
    }

    @Override
    public AuthenticationRequest.ManageAction manage(PluggableAuthenticator.AuthenticationManager manager, AuthenticationRequest request) {
        return AuthenticationRequest.ManageAction.None;
    }

    public void redirect(PluggableAuthenticator.AuthenticationManager manager, AuthenticationRequest request) {
        try {
            service = new ServiceBuilder()
                    .provider(FacebookApi.class)
                    .apiKey(FACEBOOK_APPID_DEFAULT)
                    .apiSecret(FACEBOOK_APP_SECRET_DEFAULT)
					.callback("http://localhost:8080/oauth/#j_facebook_login")
                    .debug()
                    .build();

            // Obtain the Authorization URL
            String url = service.getAuthorizationUrl(null);


            manager.forward(request, getRedirectPage(url));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FacebookAuthenticator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String checkCredentials(PluggableAuthenticator.AuthenticationManager manager, AuthenticationRequest request) {
		System.out.println("Path=" + request.getRequestPath());
        String code = request.getParameter(CODE);
        Verifier verifier = new Verifier(code);

        // Trade the Request Token and Verfier for the Access Token
        Token accessToken = service.getAccessToken(null, verifier);
        System.out.println("Got the Access Token!");
        System.out.println("(if your curious it looks like this: " + accessToken + " )");
        System.out.println();

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        OAuthRequest orequest = new OAuthRequest(Verb.GET, "https://graph.facebook.com/me");
        orequest.addBodyParameter("get", "name");

        service.signRequest(accessToken, orequest);
        Response response = orequest.send();
        System.out.println("Got it! Lets see what we found...");
        System.out.println();
        System.out.println(response.getBody());
		
		String facebookId = null;
		JsonReader jsonReader = Json.createReader(new StringReader(response.getBody()));
		JsonObject object = jsonReader.readObject();
		jsonReader.close();
		facebookId = object.getString("id");
		
		return facebookId;
    }

    protected SimplePrincipal loadPrincipal(AuthenticationManager manager, AuthenticationRequest request, String facebookId) {
//        Profile p = DAOFactory.getProfileDAO().findByFacebookId(facebookId);
		String username="facebook", role="users";
        return new SimplePrincipal(username, role);
    }
}
