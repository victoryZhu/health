package com.yourhealth.regmgmt.member_info.controller;
 
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourhealth.common.service.ChbcUtils;
import com.yourhealth.foundation.domain.JqgridDataModel;
import com.yourhealth.foundation.domain.SpecificationByJqgridFilters;
//import com.yourhealth.orgnization.domain.Bmxx;
//import com.yourhealth.orgnization.domain.Ryxx;
//import com.yourhealth.orgnization.service.BmxxService;
//import com.yourhealth.orgnization.service.RyxxService;
import com.yourhealth.regmgmt.domain.MemberInfo;
import com.yourhealth.regmgmt.service.MemberInfoService;
/**
 * 会员个人基本信息控制类
 * @author zzm
 *
 */
@Controller
@RequestMapping("/regmgmt/member_info")
public class DataMemberInfoController {

	@Autowired
	private MemberInfoService memberinfoService = null;
	//@Autowired
	//private RyxxService ryxxService = null;	
	@Autowired
	private ChbcUtils chbcUtils;

	/**
	 * 主框架
	 * @param httpSession
	 * @param clear
	 * @param model
	 * @return
	 */	@RequestMapping(method = RequestMethod.GET)
	//@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	//@PreAuthorize("hasAnyRole('YH0010102')")
	public String main(HttpSession httpSession,
			@RequestParam(value = "clear", defaultValue = "true") String clear,
			Map<String, Object> model) {
		return "regmgmt/member_info/main";
	}
	
	/**
	 * 返回分页部门列表，json格式数据的方式
	 * @param pageable
	 * @param sidx
	 * @param sord
	 * @param search
	 * @param filters
	 * @return
	 * @throws Exception 
	 */
	//@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@RequestMapping(value = "/memberinfo", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public JqgridDataModel<MemberInfo> getMemberinfos(
			Pageable pageable,
			@RequestParam(value = "sidx", defaultValue = "id", required = false) String sidx,
			@RequestParam(value = "sord", defaultValue = "desc", required = false) String sord,
			@RequestParam("_search") boolean search,			
			@RequestParam(value = "filters", required = false) String filters) throws Exception {
		
		PageRequest pageRequest = (PageRequest) pageable.previousOrFirst();		
		if (!sidx.equals("") && !sord.equals("")) {
			Sort sort = chbcUtils.getSortBySidxAndSord(sidx, sord);
			pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
		}			
				
		Specification<MemberInfo> spec = null;		
		if (search && filters != null) {			
			spec = new SpecificationByJqgridFilters<>(filters);
		}	
				
		return new JqgridDataModel<>(memberinfoService.findAll(spec, pageRequest));		
	}	

	/**
	 * 新增部门
	 * @param bmxx
	 * @param bindingResult
	 * @return
	 */
	//@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@RequestMapping(value = "/memberinfo", method = RequestMethod.POST, produces = "application/json")	    
	@ResponseBody		  
	public Map<String, Object> addMemberInfo(@Valid MemberInfo memberinfo,
			BindingResult bindingResult) {
		Map<String, Object> model = new HashMap<String, Object>();

		validateMemberInfo("ADD", memberinfo, bindingResult);
				
		if (bindingResult.hasErrors()) {
			model.put("success", false);
			model.put("message", bindingResult.getFieldErrors());		
			System.out.println("error : " + bindingResult.getFieldErrors());
			return model;
		}
		//设置部门状态为正常
		//memberinfo.setUsed(true);
		memberinfoService.save(memberinfo);
		
		model.put("success", true);
		model.put("message", "ok");
		return model;
	}
	
	/**
	 * 修改部门
	 * @param bmxx
	 * @param bindingResult
	 * @return
	 */
	//@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@RequestMapping(value = "/memberinfo/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public Map<String, Object> updMemberInfo(@Valid MemberInfo memberinfo, BindingResult bindingResult) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("key_id", memberinfo.getMemberid());
		
		validateMemberInfo("UPD", memberinfo, bindingResult);
		if (bindingResult.hasErrors()) {
			model.put("success", false);
			model.put("message", bindingResult.getFieldErrors());
			return model;
		}

		memberinfoService.save(memberinfo);
		model.put("success", true);
		model.put("message", "ok");
		return model;
	}

	private void validateMemberInfo(String opeType, MemberInfo memberinfo, BindingResult bindingResult) {
		if (memberinfoService.checkMemberid(opeType, memberinfo)) {
			bindingResult.rejectValue("memberid", "validate.memberinfo.memberid.repeat",
					"会员号不可重复，请重新输入！");
		}
		
		/*if (bmxxService.checkBmmc(opeType, bmxx)) {
			bindingResult.rejectValue("bmmc", "validate.bmxx.bmmc.repeat",
					"部门名称不可重复，请重新输入！");
		}		

		if("UPD".equalsIgnoreCase(opeType) && !bmxx.isUsed() && !bmxxService.canStopBmxx(bmxx.getId())){
			bindingResult.rejectValue("used", "validate.bmxx.used.not",
					"部门下有在职人员不可注销部门，请重新输入！");
		}*/
	}	

	/**
	 * 返回人员列表，json格式数据的方式
	 * @param pageable
	 * @param sortBy
	 * @param order
	 * @param id
	 * @return
	 */
	//@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	/*@RequestMapping(value = "/ryxx", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public JqgridDataModel<Ryxx> getRyxxs(
			Pageable pageable,
			@RequestParam(value = "sidx", defaultValue = "rybh", required = false) String sidx,
			@RequestParam(value = "sord", defaultValue = "asc", required = false) String sord,
			@RequestParam(value = "id", required = true) int id) {
		
		PageRequest pageRequest = (PageRequest) pageable.previousOrFirst();			
		if (!sidx.equals("") && !sord.equals("")) {
			Sort sort = chbcUtils.getSortBySidxAndSord(sidx, sord);
			pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
		}
		
		return new JqgridDataModel<>(ryxxService.findAllByBmxxId(id, pageRequest));
	}*/

	/**
	 * 新增人员
	 * @param ryxx
	 * @param bindingResult
	 * @return
	 */
	//@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	/*@RequestMapping(value = "/ryxx", method = RequestMethod.POST, produces = "application/json")	    
	@ResponseBody		  
	public Map<String, Object> addRyxx(@Valid Ryxx ryxx,
			BindingResult bindingResult) {
		Map<String, Object> model = new HashMap<String, Object>();

		validateRyxx("ADD", ryxx, bindingResult);
				
		if (bindingResult.hasErrors()) {
			model.put("success", false);
			model.put("message", bindingResult.getFieldErrors());			
			return model;
		}
		ryxxService.add(ryxx);
		
		model.put("success", true);
		model.put("message", "ok");
		return model;
	}

	private void validateRyxx(String opeType, Ryxx ryxx, BindingResult bindingResult) {
		if (ryxxService.checkRybh(opeType, ryxx)) {
			bindingResult.rejectValue("rybh", "validate.ryxx.rybh.repeat", "员工编号不可重复，请重新输入！");
		}
		if (ryxxService.checkUsername(opeType, ryxx)) {
			bindingResult.rejectValue("username", "validate.ryxx.username.repeat", "用户名已经存在，请重新输入！");
		}
	}

	/**
	 * 修改人员
	 * @param ryxx
	 * @param bindingResult
	 * @return
	 */
	//@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	/*@RequestMapping(value = "/ryxx/{id}", method = RequestMethod.PUT, produces = "application/json")	
	@ResponseBody
	public Map<String, Object> updRyxx(@Valid Ryxx ryxx,
			BindingResult bindingResult) { 
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("key_id", ryxx.getId());

		validateRyxx("UPD", ryxx, bindingResult);
		
		if (bindingResult.hasErrors()) {
			model.put("success", false);
			model.put("message", bindingResult.getFieldErrors());
			return model;
		}
		ryxxService.update(ryxx);
		model.put("success", true);
		model.put("message", "ok");
		return model;
	}*/
	
	/**
	  * 修改密码
	  * @param id
	  * @param password
	  * @return
	  */
	//@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	/*@RequestMapping(value = "/ryxx/password/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public Map<String, Object> chgPasswd(@PathVariable int id,
			@RequestParam(value = "password", required = false) String password) {		
				
		ryxxService.updPassword(id, password);
				
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("key_id", id);	
		model.put("success", true);
		model.put("message", "ok");
		return model;			
	}*/
       
}