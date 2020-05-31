package com.jewel.member.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.jewel.common.CommandMap;
import com.jewel.member.service.LoginService;
import com.jewel.member.service.MailService;


@Controller
public class LoginController {
	
	String sendEmailId="nmlkj66@gmail.com";
	
	//�α��� ���� ����
	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name = "loginService")
	private LoginService loginService;
	
	
    @Resource(name="mailService")
    private MailService mailService;

	
	
	@RequestMapping(value="/login/loginForm") 
	public String loginForm() throws Exception{
		return "loginForm";
}
	//�α��� ó��
	@RequestMapping(value = "/login/login", method = RequestMethod.POST)
	public ModelAndView login(CommandMap commandMap, HttpServletRequest request) throws Exception {
		ModelAndView mav = new ModelAndView("login");
		HttpSession session = request.getSession(true);
		String message="";
		String url="";
		Map<String,Object> result = loginService.loginCheck(commandMap.getMap());
		if(result == null) { //���̵� �ִ��� Ȯ��
			message="�ش� ���̵� �������� �ʽ��ϴ�.";
			
		} else { 
		if(result.get("MEM_PWD").equals(commandMap.get("MEM_PWD"))){ //��й�ȣ�� ���ٸ�
			session.setAttribute("MEM_ID", commandMap.get("MEM_ID")); 
			session.setAttribute("MEM_RANK", result.get("MEM_RANK"));
		}
		else {//��й�ȣ�� ��ġ�������� ��
			message="��й�ȣ�� ���� �ʽ��ϴ�.";
		}
		}
		mav.addObject("message",message);
		
		
		
		return mav;
}
	
	@RequestMapping(value="/login/logout")//�α׾ƿ�
	public ModelAndView logout(HttpServletRequest request,CommandMap commandMap) throws Exception {
		HttpSession session = request.getSession(false);
		if (session != null)
			session.invalidate();
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/main/main");
		return mav;
	}
	
	@RequestMapping(value = "/login/findId") // ���̵� ã�� ���� �����ִ� �޼ҵ�
	public ModelAndView findId(CommandMap commandMap) throws Exception {
		ModelAndView mv = new ModelAndView("findId");
		return mv;
	}

	@RequestMapping(value = "/login/findIdResult", method = RequestMethod.POST) // �Է��� ������ ���缭 ���̵� ã���ִ� ��
	public ModelAndView findIdResult(CommandMap commandMap) throws Exception {
			ModelAndView mv = new ModelAndView("findIdResult");
		
			List<Map<String, Object>> list = loginService.findIdWithEmail(commandMap.getMap());
			mv.addObject("list", list);
			return mv;
		}
	

		

	@RequestMapping(value = "/login/findPw") // ��й�ȣ ã�� ���� �����ִ� �޼ҵ�
	public ModelAndView findPw(CommandMap commandMap) throws Exception {
		ModelAndView mav = new ModelAndView("findPw");
		return mav;
	}

	
	@RequestMapping(value="/login/findPwConfirm", method=RequestMethod.GET)
	@ResponseBody
	public boolean sendNewPw(CommandMap commandMap) throws Exception{
		   //�ӽú�й�ȣ �̸��Ϸ� ������ 
		System.out.println("ddd");
		int count=loginService.PwdEmailCheck(commandMap.getMap());
		if(count>0) {
			Random rnd =new Random();

			StringBuffer buf =new StringBuffer();

			for(int i=0;i<8;i++){
			    // rnd.nextBoolean() �� �������� true, false �� ����. true�� �� ���� �� �ҹ��ڸ�, false �� �� ���� �� ���ڸ� StringBuffer �� append �Ѵ�.
			    if(rnd.nextBoolean()){
			        buf.append((char)((int)(rnd.nextInt(26))+97));
			    }else{
			        buf.append((rnd.nextInt(10)));
			    }
			}
			
			String subject = "[JEWELS]�ӽú�й�ȣ�Դϴ�.";
			StringBuilder sb = new StringBuilder();
			sb.append("������ �ӽ� ��й�ȣ ��  <span style=\"color:red;font-weight:bold;font-size:15px; \">" + buf.toString() + " </span>�Դϴ�.");
			if( mailService.send(subject, sb.toString(), sendEmailId,(String)commandMap.get("MEM_EMAIL"), null)) {
				
				commandMap.getMap().put("MEM_PWD", buf.toString());
				
				loginService.updateTempPw(commandMap.getMap());
				return true;
			}else {
				return false;
			}
		}
		
		return false;

	}
		 
			
		
	
	@RequestMapping("/needLogin")
	//�α��� ���ͼ���
	public ModelAndView needLogin(CommandMap commandMap) throws Exception {
		ModelAndView mv = new ModelAndView("/login");
		String message = "�α����� �ʿ��� �����Դϴ�.";
		String url = "/loginForm";
		mv.addObject("message",message);
		mv.addObject("url",url);
		return mv;
	}
}
