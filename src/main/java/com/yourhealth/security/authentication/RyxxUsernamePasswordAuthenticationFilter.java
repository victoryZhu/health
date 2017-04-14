package com.yourhealth.security.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class RyxxUsernamePasswordAuthenticationFilter extends
		UsernamePasswordAuthenticationFilter {
	private boolean postOnly = true;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {
		
		//只处理post提交的请求，其他请求抛出异常
		if (postOnly && !request.getMethod().equals("POST")) {		
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}		
				
		//从request中获取用户名、密码和公司编号
		String username = obtainUsername(request);
		String password = obtainPassword(request);
				
		if (username == null) 
			username = "";
		username = username.trim();

		if (password == null) 
			password = "";		
				
		// authRequest.isAuthenticated() = false since no authorities are specified
		//构造未认证的RyxxUsernamePasswordAuthenticationToken
		RyxxUsernamePasswordAuthenticationToken authRequest = new RyxxUsernamePasswordAuthenticationToken(username, password);			
		
		// 允许子类设置"details"属性
		setDetails(request, authRequest);
		
		//通过AuthenticationManager:ProviderManager完成认证任务
		return this.getAuthenticationManager().authenticate(authRequest);
	}

}
