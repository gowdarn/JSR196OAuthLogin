/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package name.aikesommer.authenticator;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;

@SessionScoped
public class PrincipalHolder implements Serializable {
	private static final long serialVersionUID = 7038272345049751682L;
	
	SimplePrincipal principal;

	public void setPrincipal(SimplePrincipal principal) {
		this.principal = principal;
	}
	
	public void invalidate() {
		setPrincipal(null);
	}
	
	public SimplePrincipal getPrincipal() {
		return principal;
	};
}
