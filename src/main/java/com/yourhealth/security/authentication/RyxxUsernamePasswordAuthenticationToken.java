package com.yourhealth.security.authentication;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.yourhealth.orgnization.domain.Ryxx;

public class RyxxUsernamePasswordAuthenticationToken extends
		UsernamePasswordAuthenticationToken {

	private static final long serialVersionUID = 1L;
	
	// used for attempting authentication
	public RyxxUsernamePasswordAuthenticationToken(String principal, String credentials) {
		super(principal, credentials);
	}

	// used for returning to Spring Security after being authenticated
	public RyxxUsernamePasswordAuthenticationToken(Ryxx principal,
			String credentials, Collection<? extends GrantedAuthority> authorities) {
		super(principal, credentials, authorities);
	}

	public RyxxUsernamePasswordAuthenticationToken(Ryxx ryxx,
			String password, Set<GrantedAuthority> authorities) {
		super(ryxx, password, authorities);		
	}

}
