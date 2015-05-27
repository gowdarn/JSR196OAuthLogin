/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ratcash.multilogin.boundary.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

@Path("test")
public class TestService {
	@Context
	SecurityContext context;

	@GET
	@Path("secure/oauth")
	@Produces(MediaType.TEXT_HTML)
	public String accessResource() {
		return "OAuth resource for: " + context.getUserPrincipal();
	}
	
	@GET
	@Path("public")
	public String publicResoure() {
		return "Public resource";
	}
	
}
