/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ratcash.multilogin.authenticators;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.javaee7.jaspic.common.JaspicUtils;

/**
 * This is not used. It was replaced by active CDI bean lookup from within AuthenticRoast
 */
@WebListener
public class AuthenticatorRegistrar implements ServletContextListener {
	
	@Inject
	AppAuthModule authModule;
	
	String regId;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		regId = JaspicUtils.registerSAM(sce.getServletContext(), authModule);
//		ServletContext sc = null;
//		sc = sce.getServletContext();
		// register AuthenticRoast authenticator
		System.out.println("registration returned: " + regId);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("RegId = " + regId);
		boolean result = JaspicUtils.unregisterSAM(sce.getServletContext(), regId);
		System.out.println("result = " + result);
		// do nothing...
//		ServletContext sc = null;
//		sc = sce.getServletContext();
//		// register AuthenticRoast authenticator
//		
//		Registry.forContext( sc ).register( (PluggableAuthenticator) null );
	}
	
}
