/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ratcash.multilogin.auth.boundary;

import java.io.IOException;
import javax.enterprise.inject.Model;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.validation.constraints.Size;

@Model
public class LoginForm {
	//@Email
	String email;
	
	@Size(min = 6, max=32)
	String password;
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	public void facebookLogin() throws IOException {
		FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getApplicationContextPath()
				+ "/j_facebook_redirect");
	}
	
	public void twitterLogin() {
		
	}
	
	public void googleLogin() {
		
	}
	
	public void linkedinLogin() {
		
	}
	
	public void checkAuthentication() throws IOException {
//		FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getApplicationContextPath()
//				+ "j_facebook_redirect");
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		if (externalContext.getUserPrincipal() != null) {
			externalContext.redirect(externalContext.getRequestContextPath() + "/restricted/index.xhtml");
		}
	}
	
}
