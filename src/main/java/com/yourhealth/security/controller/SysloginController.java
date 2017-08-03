package com.yourhealth.security.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yourhealth.foundation.domain.SystemMenuDataModel;
import com.yourhealth.orgnization.domain.Ryxx;
import com.yourhealth.orgnization.service.RyxxService;
import com.yourhealth.security.service.SysLogService;

/**
 * 系统登录控制类
 * @author zzm
 *
 */
@Controller
@Transactional(propagation = Propagation.REQUIRED)
public class SysloginController {

	@Autowired
	private RyxxService ryxxService;	
	@Autowired
	private SysLogService sysLogService;
	
	protected final Log logger = LogFactory.getLog(getClass());
		
	/**
	 * 返回登录页面
	 * @param httpSession
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = {"/","/login"}, method = RequestMethod.GET)	
	public String login(HttpSession httpSession, HttpServletRequest request, HttpServletResponse response,		
			Map<String, Object> model) {
		return "login";
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET, headers = "X-Requested-With=XMLHttpRequest")
	public void loginViaAjax(HttpServletRequest request, HttpServletResponse response) {
		String url = request.getRequestURI() + (request.getQueryString() == null ? "" : ("?"+request.getQueryString()));
		PrintWriter out;
		try {
			out = response.getWriter();
			out.println("<script>");
			out.println("document.location.href='" + url + "'");
			out.println("</script>");
			out.flush();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
		
	/**
	 * 到主界面  
	 * 当用户有多个身份或多个公司权限时，选择登录公司和系统后进入系统主界面
	 * @param httpSession
	 * @param request
	 * @param model
	 * @param attr
	 * @return
	 */
	@RequestMapping(value = {"/main" }, method = {RequestMethod.POST, RequestMethod.GET})
	public String main(HttpSession httpSession, HttpServletRequest request,					
			Map<String, Object> model, RedirectAttributes attr) {				  
		try {			
			httpSession.setMaxInactiveInterval((int) (0.5 * 3600));			
			return "main";			
		} catch (AuthenticationException e) {
			e.printStackTrace();
			// 如果forward到login页面，保存异常信息在request中
			// request.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, e);
			request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, e);
			attr.addAttribute("error", "");
			return "redirect:/login";
		}		
	}		

	/**
	 * 系统主界面的top
	 * @param httpSession
	 * @param model
	 * @return
	 */
	@RequestMapping("/top")
	public String showTop(HttpSession httpSession,Map<String, Object> model){
		Ryxx ryxx = (Ryxx) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		model.put("name", ryxx.getName());
		return "top";
	}
	
	/**
	 * 系统主界面的menu
	 * @return
	 */
	@RequestMapping("/menu")
	public String showMenu(HttpServletRequest request,	Map<String, Object> model){		
		StringBuffer menuString = new StringBuffer("");
		Ryxx ryxx = (Ryxx) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<SystemMenuDataModel> menu = ryxxService.getRyxxMenu(ryxx.getId());
		int i=0;
		for(SystemMenuDataModel sm : menu){
			i=i+1;
			System.out.println("id=" + sm.getId() + ", text="+sm.getText() + ", type="+sm.getType() + ", url="+sm.getUrl() + ",pId="+sm.getParentId());
			if(sm.getType()==SystemMenuDataModel.Type.CATEGORY){				
				menuString.append(i==1?"":"</ul>").append("<h3>").append(sm.getText()).append("</h3><ul>");
			}
			if(sm.getType()==SystemMenuDataModel.Type.MODULE){
				menuString.append("<li><a id=\"").append(sm.getId()).append("\" href=\"javascript:void(0)\" onclick=\"clickMenu('" + request.getContextPath() + sm.getUrl()).append("')\" >").append(sm.getText()).append("</a></li>");
				//menuString.append("<li><a id=\"").append(sm.getId()).append("\" href=\"javascript:void(0)\" onclick=\"clickMenu('" + sm.getUrl()).append("')\" >").append(sm.getText()).append("</a></li>");
			}			
		}
		menuString.append("</ul>");
		model.put("menuString", menuString.toString());
		return "menu";
	}
	
	/**
	  * 修改管理员密码
	  * @param oldyhkl
	  * @param yhkl
	  * @return
	  */
	@RequestMapping(value = "/ryxx/updpassword", method = RequestMethod.PUT)
	@ResponseBody
	public Map<String, Object> chgMyPasswd(
			@RequestParam(value = "oldyhkl", required = false) String oldyhkl,
			@RequestParam(value = "yhkl", required = false) String yhkl) {		
		
		Ryxx ryxx = (Ryxx) SecurityContextHolder.getContext().getAuthentication().getPrincipal();		
	
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("key_id", ryxx.getId());	
		
		//检查旧密码是否正确		
		if(!ryxxService.checkPassword(ryxx.getId(), oldyhkl)){			
			model.put("success", false);
			model.put("message", "输入密码错误，请重新输入！");			
			return model;			
		}			
				
		ryxxService.updPassword(ryxx.getId(), yhkl);
					
		model.put("success", true);
		model.put("message", "ok");
		return model;			
	}
	
}