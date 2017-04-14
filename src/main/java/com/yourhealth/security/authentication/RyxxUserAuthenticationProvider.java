package com.yourhealth.security.authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.yourhealth.orgnization.domain.Ryxx;
import com.yourhealth.orgnization.service.RyxxService;

public class RyxxUserAuthenticationProvider implements
		AuthenticationProvider, MessageSourceAware {
	protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
		
	@Autowired
	private RyxxService ryxxService;	   
	@Autowired
	private PasswordEncoder passwordEncoder;	              
	@Resource(name="appProperties")
	private Properties appProperties;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		RyxxUsernamePasswordAuthenticationToken token = (RyxxUsernamePasswordAuthenticationToken) authentication;
		String userName = token.getName();
		
		Ryxx ryxx = null;
		if(userName != null) {
			ryxx = ryxxService.findOneByUsername(userName);
		}
		if(ryxx == null) {
			throw new UsernameNotFoundException(messages.getMessage("BindAuthenticator.badCredentials",	"Bad credentials"));
		}
				
		String password = ryxx.getPassword();
		if(!passwordEncoder.matches(token.getCredentials().toString(), password)) {
			throw new BadCredentialsException(messages.getMessage("BindAuthenticator.badCredentials",	"Bad credentials"));
		}
		
		if(!ryxx.isEmployed())
			throw new BadCredentialsException("员工已经离职，不可登录系统！");
				
		//查询用户权限
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();	
		authorities.add(new SimpleGrantedAuthority("ROLE_TEST"));		
		return new RyxxUsernamePasswordAuthenticationToken(ryxx, password, authorities);
		
		//List<GrantedAuthority> authorities = ryxxService.loadAuthorities(ryxx.getId());
		//return new RyxxUsernamePasswordAuthenticationToken(ryxx, password, ryxxService.loadAuthorities(ryxx.getId()));
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return RyxxUsernamePasswordAuthenticationToken.class.equals(authentication);
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messages = new MessageSourceAccessor(messageSource);
	}

}
