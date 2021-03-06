package com.efounder.JEnterprise.controller;

import com.core.common.ISSPReturnObject;
import com.core.common.util.CurrentUserUtils;
import com.core.config.qxpz.IpOnOffConfig;
import com.efounder.JEnterprise.common.constants.Constants;
import com.efounder.JEnterprise.common.util.DateUtil;
import com.efounder.JEnterprise.common.util.UUIDUtil;
import com.efounder.JEnterprise.common.util.salt.Digests;
import com.efounder.JEnterprise.common.util.salt.Encodes;
import com.efounder.JEnterprise.config.shiro.vo.Principal;
import com.efounder.JEnterprise.initializer.SubRealTimeDataCache;
import com.efounder.JEnterprise.model.basedatamanage.zzjg.ESZzjg;
import com.efounder.JEnterprise.model.systemcenter.logmanage.ESSysLoginLog;
import com.efounder.JEnterprise.model.usercenter.ESRole;
import com.efounder.JEnterprise.model.usercenter.ESUser;
import com.efounder.JEnterprise.service.basedatamanage.zzjg.ESZzjgService;
import com.efounder.JEnterprise.service.systemcenter.logmanage.ESSysLoginLogService;
import com.efounder.JEnterprise.service.usercenter.ESRoleService;
import com.efounder.JEnterprise.service.usercenter.ESUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import static com.efounder.JEnterprise.service.usercenter.impl.ESUserServiceImpl.HASH_INTERATIONS;
import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@Controller
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
	@Autowired
	private ESUserService esUserService;
	@Autowired
	private ESSysLoginLogService esSysLoginLogService;
	@Autowired
	private ESZzjgService esZzjgService;
	@Autowired
	private IpOnOffConfig ipConfig;

	@Autowired
	private ESRoleService roleService;

	@Resource
	private SubRealTimeDataCache subRealTimeDataCache;

	@Value("${comm.websocket.port.mapping}")
	private int port;
	@Autowired
	private ESUserService userService;

	@Value("${comm.websocket.path}")
	private String path;

	@Value("${comm.websocket.password}")
	private String password;

	@Value("${comm.websocket.heartbeat.interval}")
	private int interval;
	
    @RequestMapping(value = "login", method = RequestMethod.GET)
    String login(Model model) {
        model.addAttribute("user", new ESUser());
        log.info("#?????????");
        return "login";
    }

	// ??????????????????
	@RequestMapping(value = "/npsLogin", method = RequestMethod.GET)
	String npsLogin(@ModelAttribute("userForm") ESUser user,RedirectAttributes redirectAttributes) {
		log.info("#???????????????????????????");
		log.info("?????????????????????:"+user.getF_yhbh());
		log.info("?????????????????????:"+user.getF_pass());
		//ESUser user = new ESUser();
		//user.setF_yhbh("admin");
		//user.setF_pass("123456");
		if (null == user || StringUtils.isBlank(user.getF_yhbh()) || StringUtils.isBlank(user.getF_pass())) {
			log.error("# ?????????????????????");
			return "redirect:/login";
		}

		String username = user.getF_yhbh();
		UsernamePasswordToken token = new UsernamePasswordToken(user.getF_yhbh(), user.getF_pass());
		// ???????????????Subject
		Subject currentUser = SecurityUtils.getSubject();
		try {
			// ????????????login?????????,SecurityManager?????????AuthenticationToken,??????????????????????????????Realm???????????????????????????
			// ??????Realm??????????????????????????????AuthenticationTokens????????????
			// ????????????????????????login(token)?????????,????????????MyRealm.doGetAuthenticationInfo()?????????,?????????????????????????????????
			log.info("?????????[" + username + "]??????????????????..????????????");
			currentUser.login(token);
			log.info("?????????[" + username + "]??????????????????..????????????");
		} catch (UnknownAccountException uae) {
			log.error("?????????[" + username + "]??????????????????..???????????????,????????????");
			redirectAttributes.addFlashAttribute("message", "????????????");
		} catch (IncorrectCredentialsException ice) {
			log.error("?????????[" + username + "]??????????????????..???????????????,???????????????");
			redirectAttributes.addFlashAttribute("message", "???????????????");
		} catch (LockedAccountException lae) {
			log.error("?????????[" + username + "]??????????????????..???????????????,???????????????");
			redirectAttributes.addFlashAttribute("message", "???????????????");
		} catch (ExcessiveAttemptsException eae) {
			log.error("?????????[" + username + "]??????????????????..???????????????,??????????????????");
			redirectAttributes.addFlashAttribute("message", "????????????????????????????????????");
		} catch (AuthenticationException ae) {
			// ????????????Shiro????????????AuthenticationException????????????????????????????????????????????????????????????
			log.error("?????????[" + username + "]??????????????????..???????????????,??????????????????");
			ae.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "???????????????????????????");
		}
		// ????????????????????????
		if (currentUser.isAuthenticated()) {
			Session session = currentUser.getSession(true);
			SecurityUtils.getSubject().getSession().setAttribute(Constants.SESSION_KEY, session.getId());
			log.info("??????[" + username + "]??????????????????(???????????????????????????????????????????????????????????????????????????)");
			// ????????????????????? ????????????????????? ?????????????????????
			addSysLoginLog(username, "0");
			String ipConfigMark = ipConfig.getSysParameterIpable();
			if(ipConfigMark.equals("0")){
				return "redirect:/index";
			}else{
				return "redirect:/interceptor?username="+username;
			}
		} else {
			token.clear();
			return "redirect:/login";
		}
	}

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@ModelAttribute("userForm") ESUser user, RedirectAttributes redirectAttributes) {
        log.info("# ????????? ");
        if (null == user || StringUtils.isBlank(user.getF_yhbh()) || StringUtils.isBlank(user.getF_pass())) {
            log.error("# ?????????????????????");
			redirectAttributes.addFlashAttribute("message", "???????????????????????????");
            return "redirect:/login";
        }

		//???????????????????????????????????????????????????????????????
		//??????salt????????????
		ESUser realUser = userService.findUserById(user.getF_yhbh());
        if(realUser == null){
			redirectAttributes.addFlashAttribute("message", "?????????????????????");
			return "redirect:/login";
		}
		byte[] salt = Encodes.decodeHex(realUser.getSalt());
		byte[] hashPassword = Digests.sha1((user.getF_yhbh()+user.getF_pass()).getBytes(), salt, HASH_INTERATIONS);
		//??????????????????salt?????????????????????????????????shiro????????????
		user.setF_pass(Encodes.encodeHex(hashPassword));
        String username = user.getF_yhbh();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getF_yhbh(), user.getF_pass());
        // ???????????????Subject
        Subject currentUser = SecurityUtils.getSubject();
        try {
            // ????????????login?????????,SecurityManager?????????AuthenticationToken,??????????????????????????????Realm???????????????????????????
            // ??????Realm??????????????????????????????AuthenticationTokens????????????
            // ????????????????????????login(token)?????????,????????????MyRealm.doGetAuthenticationInfo()?????????,?????????????????????????????????
            log.info("?????????[" + username + "]??????????????????..????????????");
            currentUser.login(token);
            log.info("?????????[" + username + "]??????????????????..????????????");
        } catch (UnknownAccountException uae) {
            log.error("?????????[" + username + "]??????????????????..???????????????,????????????");
            redirectAttributes.addFlashAttribute("message", "????????????");
        } catch (IncorrectCredentialsException ice) {
            log.error("?????????[" + username + "]??????????????????..???????????????,???????????????");
            redirectAttributes.addFlashAttribute("message", "???????????????");
        } catch (LockedAccountException lae) {
            log.error("?????????[" + username + "]??????????????????..???????????????,???????????????");
            redirectAttributes.addFlashAttribute("message", "???????????????");
        } catch (ExcessiveAttemptsException eae) {
            log.error("?????????[" + username + "]??????????????????..???????????????,??????????????????");
            redirectAttributes.addFlashAttribute("message", "????????????????????????????????????");
        } catch (AuthenticationException ae) {
            // ????????????Shiro????????????AuthenticationException????????????????????????????????????????????????????????????
            log.error("?????????[" + username + "]??????????????????..???????????????,??????????????????");
            ae.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "???????????????????????????");
        }
        // ????????????????????????
		if (currentUser.isAuthenticated()) {
			Session session = currentUser.getSession(true);
			SecurityUtils.getSubject().getSession().setAttribute(Constants.SESSION_KEY, session.getId());
			SecurityUtils.getSubject().getSession().setAttribute(Constants.USERNAME, username);
			log.info("??????[" + username + "]??????????????????(???????????????????????????????????????????????????????????????????????????)");
			// ????????????????????? ????????????????????? ?????????????????????
			addSysLoginLog(username, "0");
            Principal principal = (Principal) currentUser.getPrincipal();
            CurrentUserUtils.setUserId(principal.getUser().getF_id());
            String ipConfigMark = ipConfig.getSysParameterIpable();

            // ??????????????? session
			Set<String> roleCodes = new HashSet<>();

			List<ESRole> esRoles = principal.getRoles();

			if (esRoles != null && !esRoles.isEmpty())
			{

				for (ESRole role : esRoles) {
					roleCodes.add(role.getF_rolebh());
				}
				session.setAttribute(Constants.ROLE_CODE, roleCodes);

			}

			// ??????????????? session
			session.setAttribute(Constants.GROUP_CODE, principal.getUser().getF_zzjgbh());

			if(ipConfigMark.equals("0")){
				return "redirect:/index";
			}else{
				return "redirect:/interceptor?username="+username;
			}
		} else {
            token.clear();
            return "redirect:/login";
        }
    }

    //IP?????????
    @RequestMapping("/interceptor")
	public String getIP(String username) throws UnknownHostException{
		InetAddress address = InetAddress.getLocalHost();
		String ip= address.getHostAddress(); //??????IP??????
		boolean findip =esUserService.interceptIP(ip);
		if(findip){
		//IP?????????????????????????????????
			return "redirect:/interceptorUser?username="+username;
		}else{
		//IP???????????????????????????????????????
			return "redirect:/login";
		}
	}
    
    //???????????????
    @RequestMapping("/interceptorUser")
    public String username(String username) throws UnknownHostException{
    	InetAddress address = InetAddress.getLocalHost();
		String ip= address.getHostAddress(); //??????IP??????
		String login=esUserService.username(ip); 
		String name=username;
		//???????????????????????????
		boolean status = login.contains(name);        
		if(status){ 
			//?????????????????????
				return "redirect:/index";
			}else{   
			//???????????????????????????
				return "redirect:/login";
			}
    }

    @RequestMapping("/logout")
	public String logout() {
    	Principal principal = (Principal) SecurityUtils.getSubject().getPrincipal();

		// ?????? sessionId ?????????????????????
		subRealTimeDataCache.unsubscribeCacheBySessionId(SecurityUtils.getSubject().getSession().getId().toString());
    	// ?????????????????????????????????????????????
    	addSysLoginLog(principal.getUser().getF_yhbh(), "1");
		// ??????
		SecurityUtils.getSubject().logout();
		CurrentUserUtils.clear();
		return "redirect:/login";
	}
    
    @RequestMapping(value ="/abnormallogout")
	public void abnormallogout() {
    	Principal principal = (Principal) SecurityUtils.getSubject().getPrincipal();
    	// ?????????????????????????????????????????????
    	addSysLoginLog(principal.getUser().getF_yhbh(), "2");
}
    
	/**
	 * ?????????????????????????????????????????????
	 * @param username ?????????
	 * @param type ??????????????????
	 * @return
	 */
	private void addSysLoginLog(String username, String type){
		List<ESUser> list = esUserService.findUserByusername(username);
		ESSysLoginLog esSysLoginLog = new ESSysLoginLog();
		esSysLoginLog.setF_yhbh(list.get(0).getF_yhbh());
		esSysLoginLog.setF_username(list.get(0).getF_name());
		// ??????????????????
		String rolebh = "";
		String rolemc = "";
		for (ESUser euser : list) {
			if (euser.getF_rolebh() != null)
				rolebh = rolebh + "/" + euser.getF_rolebh();
			if (euser.getF_rolemc() != null)
				rolemc = rolemc + "/" + euser.getF_rolemc();
		}
		String bh = "".equals(rolebh) ? null : rolebh.substring(2, rolebh.length() - 1);
		String mc = "".equals(rolemc) ? null : rolemc.substring(1, rolemc.length());
		esSysLoginLog.setF_rolebh(bh);
		esSysLoginLog.setF_rolemc(mc);
		// ????????????guid
		esSysLoginLog.setF_id(UUIDUtil.getRandom32BeginTimePK());
		// 0????????????
		esSysLoginLog.setF_type(type);
		esSysLoginLog.setF_logintime(DateUtil.getCurrTime());
		// ??????????????????????????????zzjgid
		List<ESZzjg> zzjglist = esZzjgService.getId(list.get(0).getF_zzjgbh());
		if(zzjglist != null && zzjglist.size() > 0){
			esSysLoginLog.setF_zzjgid(zzjglist.get(0).getF_id());
		}
		// ?????????????????????????????????????????????
		esSysLoginLogService.addSysLoginLogid(esSysLoginLog);
	}
	
    @RequestMapping("/403")
    public String unauthorizedRole() {
        log.info("------????????????-------");
        return "403";
    }
    
    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "issp_left/{menuId}", method = RequestMethod.GET)
    public String leftMenu(@PathVariable String menuId, ModelMap map) {
    	log.info("???????????????");
    	Map attribute = (Map) SecurityUtils.getSubject().getSession().getAttribute(Constants.PERMISSION_MENU_SESSION);
    	map.addAttribute("menu_session", (List) attribute.get(menuId));
		return "issp_left";
    }

	@RequestMapping(value = "/issp_left", method = RequestMethod.GET)
	public String getLeftMenu(ModelMap map) {
		log.info("???????????????");
		List attribute = (ArrayList) SecurityUtils.getSubject().getSession().getAttribute(Constants.PERMISSION_MENU_SESSION);
		map.addAttribute("menu_session", attribute);
		return "issp_left";
    }
    
    @RequestMapping(value = "issperror", method = RequestMethod.GET)
    public String isspErrorPage() {
    	return "common/error";
    }

    /**
     * webSocket ????????????
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/wsLoginInfo", method = RequestMethod.GET)
    public ISSPReturnObject wsLoginInfo() throws UnknownHostException
	{

		ISSPReturnObject result  = new ISSPReturnObject();

		Map data = new HashMap();

		Session session = SecurityUtils.getSubject().getSession();
		String userId = (String) session.getAttribute(Constants.USERNAME);

		InetAddress inetAddress = InetAddress.getLocalHost();
		String ip = inetAddress.getHostAddress();

		data.put("sessionId", session.getId());
		data.put("userId", userId);
		data.put("host", ip);
		data.put("port", port);
		data.put("path", path);
		data.put("password", password);
		data.put("interval", interval);

		result.setStatus("1");
		result.setData(data);

    	return result;
    }

}