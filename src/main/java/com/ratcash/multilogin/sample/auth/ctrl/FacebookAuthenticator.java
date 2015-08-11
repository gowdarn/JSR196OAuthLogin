/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ratcash.multilogin.sample.auth.ctrl;

import com.ratcash.authenticator.SocialAuthenticator;
import com.ratcash.authenticator.SocialUserData;
import java.io.StringReader;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import name.aikesommer.authenticator.AuthenticationRequest;
import name.aikesommer.authenticator.SimplePrincipal;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

/**
 * taken from: http://stackoverflow.com/questions/21626277/how-to-integrate-a-login-with-facebook-button-in-java-ee-project
 */
@ApplicationScoped
public class FacebookAuthenticator extends SocialAuthenticator {

    public static final String LOGIN_ACTION = "/j_facebook_login";
    private static final String CALLBACK_ACTION = "/j_facebook_callback";
    
	private static final String FACEBOOK_APPID = "facebook.appid";
	private static final String FACEBOOK_APPID_DEFAULT = "1601430796738818";
	
	private static final String FACEBOOK_APP_SECRET = "facebook.appsecret";
	private static final String FACEBOOK_APP_SECRET_DEFAULT = "2a310d9cdfe1b00fc9db94d2f5d22859";
	
   
    /**
     * Overwrite this to specify a different error-page.
	 * @return 
     */
    @Override
    protected String getErrorPage() {
        return "/login-error.jsp";
    }

    @Override
    protected String getRegisterPage() {
        return "/register.xhtml?social=facebook";
    }

    @Override
    protected String getLoginAction() {
        return LOGIN_ACTION;
    }

    @Override
    protected String getCallbackAction() {
        return CALLBACK_ACTION;
    }

    /**
     * Overwrite this to specify a different path to direct to, if there is no
     * original request.
	 * @return 
     */
    @Override
    protected String getNextPath() {
        return "/";
    }

    
    @Override
    protected SocialUserData getSocialUserData(OAuthService service, Token accessToken) {
        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        OAuthRequest orequest = new OAuthRequest(Verb.GET, "https://graph.facebook.com/me");
        orequest.addBodyParameter("get", "name");

        service.signRequest(accessToken, orequest);
        Response response = orequest.send();
        System.out.println("Got it! Lets see what we found...");
        System.out.println();
        System.out.println(response.getBody());
		
		JsonReader jsonReader = Json.createReader(new StringReader(response.getBody()));
		JsonObject object = jsonReader.readObject();
		jsonReader.close();
        
        SocialUserData data = SocialUserData.forFacebook(object);
		data.token = accessToken.getToken();
        return data;
    }

    @Override
    protected SimplePrincipal loadPrincipal(AuthenticationManager manager, AuthenticationRequest request, SocialUserData socialData) {
        // TODO persist data to the DB
//        Profile p = DAOFactory.getProfileDAO().findByFacebookId(facebookId);
        
		String username=socialData.name, role="user";
        return new SimplePrincipal(username, socialData, role);
    }
    
    @Override
    protected OAuthService getScribeService(AuthenticationRequest request) {
        String callbackUrl = request.getHttpServletRequest().getScheme() + "://" 
                    + request.getHttpServletRequest().getServerName() + ":"
                    + request.getHttpServletRequest().getServerPort() + request.getContextPath() + CALLBACK_ACTION;
            OAuthService service = new ServiceBuilder()
                    .provider(FacebookApi.class)
                    .apiKey(FACEBOOK_APPID_DEFAULT)
                    .apiSecret(FACEBOOK_APP_SECRET_DEFAULT)
					.callback(callbackUrl)
                    .build();
            return service;
    }
}
