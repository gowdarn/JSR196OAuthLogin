/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ratcash.multilogin.auth.boundary;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named
@RequestScoped
public class SignUpCtrl {
	
	public boolean isEmailAvailable(String email) {
		return true;
	}

	public String register(Credentials model) {
		try {
			// TODO store the user
			// redirect to the dashboard
			return "restricted/setup";
		} catch (Exception ex) {
			Logger.getLogger(Credentials.class.getName()).log(Level.SEVERE, null, ex);
			
			 // invalid
            FacesMessage message = new FacesMessage("Unexpected error. Please notify the administrators.");
            addMessage(message);
			return "register";
		}
	}
	
	private void addMessage(FacesMessage msg) {
		FacesContext context = FacesContext.getCurrentInstance();
		UIComponent comp = context.getViewRoot().findComponent("registration-form");
		context.addMessage(comp.getClientId(context), msg);
	}
    
    public void useFacebook() {
		
	}
     
    public void useTwitter() {
		
	}
	
	public void useGooglePlus() {
		
	}
	
	public void useLinkedIn() {
		
	}
}
