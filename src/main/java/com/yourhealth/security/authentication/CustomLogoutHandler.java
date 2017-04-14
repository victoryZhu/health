package com.yourhealth.security.authentication;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.yourhealth.orgnization.domain.Ryxx;
import com.yourhealth.security.domain.SysLog;
import com.yourhealth.security.service.SysLogService;

public class CustomLogoutHandler implements LogoutHandler {
	@Autowired
	SysLogService sysLogService;

	@Override
	public void logout(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication) {
		
		Ryxx ryxx = (Ryxx) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		//记录登出日志，发生异常时输出异常信息，不抛出异常
		try{			
			SysLog sysLog = new SysLog();
			sysLog.setRyxx(ryxx);
			sysLog.setOpeType("退出系统");
			sysLog.setOpeTime(Calendar.getInstance());
			sysLog.setIp(request.getRemoteAddr());
			sysLogService.save(sysLog );		
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
