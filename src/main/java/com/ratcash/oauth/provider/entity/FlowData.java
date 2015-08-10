/**
 *    Copyright (C) 2015 OmniBene
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General
 *    Public License along with this library; if not, write to the
 *    Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 *    Boston, MA 02110-1301 USA
 */
package com.ratcash.oauth.provider.entity;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class FlowData implements Serializable {
	private static final long serialVersionUID = -2351411168875412652L;
	
	String code;
	String redirectUri;
	String clientId;
	@XmlElement(name = "access_token")
	String token;
	String scope;
	Instant created;

	public FlowData() {
	}

	public FlowData(String code, String redirectUri, String clientId, String token, String scope) {
		this.code = code;
		this.redirectUri = redirectUri;
		this.clientId = clientId;
		this.token = token;
		this.scope = scope;
		this.created = Instant.now();
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public Instant getCreated() {
		return created;
	}
	
	public boolean hasExpired() {
		return created.isBefore(Instant.now().minus(10, ChronoUnit.MINUTES));
	}
	
}
