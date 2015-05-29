package org.javaee7.jaspic.common;

import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.ServletContext;

/**
 * 
 * @author Arjan Tijms
 * 
 */
public final class JaspicUtils {

    private JaspicUtils() {
    }

    /**
     * Registers the given SAM using the standard JASPIC {@link AuthConfigFactory} but using a small set of wrappers that just
     * pass the calls through to the SAM.
     * 
	 * @param context
     * @param serverAuthModule
	 * @return 
     */
    public static String registerSAM(ServletContext context, ServerAuthModule serverAuthModule) {
        return AuthConfigFactory.getFactory().registerConfigProvider(new TestAuthConfigProvider(serverAuthModule), "HttpServlet",
            getAppContextID(context), "Test authentication config provider");
    }
	
	public static boolean unregisterSAM(ServletContext context, String regId) {
		return AuthConfigFactory.getFactory().removeRegistration(regId);
	}

    public static String getAppContextID(ServletContext context) {
        return context.getVirtualServerName() + " " + context.getContextPath();
    }

}
