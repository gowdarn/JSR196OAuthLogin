/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ratcash.multilogin.authenticators;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import name.aikesommer.authenticator.Registry;

/**
 * This is not used. It was replaced by active CDI bean lookup from within AuthenticRoast
 */
//@WebListener
public class AuthenticatorRegistrar implements ServletContextListener {
//	@Inject 
	SampleOAuth oauth = new SampleOAuth();

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext sc = null;
		sc = sce.getServletContext();
		// register AuthenticRoast authenticator
		System.out.println("registration");
		Registry.forContext( sc ).register( oauth );
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// do nothing...
//		ServletContext sc = null;
//		sc = sce.getServletContext();
//		// register AuthenticRoast authenticator
//		
//		Registry.forContext( sc ).register( (PluggableAuthenticator) null );
	}
	
}
