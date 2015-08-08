/**
 * Copyright (C) 2015 OmniBene
 *
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package com.ratcash.multilogin.oauth;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author rex
 */
@ApplicationScoped
public class FlowState {
	// TODO: replace with Hazelcast map
	Map<Key, FlowData> map = new ConcurrentHashMap<>();

	public FlowState() {
	}
	
	
	
	public static class Key {
		String code;
		String clientId;

		public Key(FlowData h) {
			this.code = h.getCode();
			this.clientId = h.getClientId();
		}

		public Key(String code, String clientId) {
			this.code = code;
			this.clientId = clientId;
		}

		@Override
		public int hashCode() {
			int hash = 5;
			hash = 79 * hash + Objects.hashCode(this.code);
			hash = 79 * hash + Objects.hashCode(this.clientId);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Key other = (Key) obj;
			if (!Objects.equals(this.code, other.code)) {
				return false;
			}
			if (!Objects.equals(this.clientId, other.clientId)) {
				return false;
			}
			return true;
		}
		
	}
	
	public void addFlowData(FlowData fd) {
		map.put(new Key(fd), fd);
	}
	
	public FlowData getFlowData(String code, String clientId) {
		Key k = new Key(code, clientId);
		FlowData fd = map.get(k);
		
		if(fd != null && fd.hasExpired()) {
			map.remove(k);
			fd = null;
		}
		return fd;
	}
	
}
