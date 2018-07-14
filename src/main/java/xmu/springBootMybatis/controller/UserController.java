package xmu.springBootMybatis.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

import xmu.springBootMybatis.config.StaticValue;
import xmu.springBootMybatis.entity.User;
import xmu.springBootMybatis.service.UserService;

@Controller    
@RequestMapping("/textAnalysis/user")    
public class UserController {    
    @Autowired  
    private UserService userService;  
      

    @RequestMapping("/PageUserSelect")  
    public PageInfo<User> PageUserSelect(  
                @RequestParam(value="pageNum",defaultValue="1")int pageNum,  
                @RequestParam(value="Size",defaultValue="5")int Size  
            ){  
        Page<User> persons = userService.findByPage(pageNum, Size);  
        PageInfo<User> pageInfo = new PageInfo<User>(persons);  
                return pageInfo;  
    } 
    
	@RequestMapping(value="/selfInformation" )    
    public ModelAndView selfInformation(){   
		Map<String, Object> map = new HashMap<>();
		User user = userService.getUser(StaticValue.USER);
		
		System.out.println(user.getName());
		System.out.println(user.getMail());
		System.out.println(user.getId());
		map.put("user", user);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("html/selfInformation");
		modelAndView.addObject("map",map);
        return modelAndView;    
	}
	
	@RequestMapping(value="changeUserName")
	@ResponseBody
	public Map<String, String> changeUserName(@RequestParam(value= "userId") long userId,
			@RequestParam(value= "userName") String userName){
		
		Map<String, String> map = new HashMap<>();
		map = userService.changeUserName(userId,userName);
		return map;
		
	}
	
	@RequestMapping(value="changePassword")
	@ResponseBody
	public Map<String, String> changePassword(@RequestParam(value= "userId") long userId,
			@RequestParam(value= "password", required=true) String password,
			@RequestParam(value= "password2",required=true) String password2){
		
		Map<String, String> map = new HashMap<>();
		map = userService.changePassword(userId,password,password2);
		return map;
		
	}
	
	@RequestMapping(value="changePhoneNumber")
	@ResponseBody
	public Map<String, String> changePhoneNumber(@RequestParam(value= "userId") long userId,@RequestParam(value= "phoneNumber") String phoneNumber){
		
		Map<String, String> map = new HashMap<>();
		map = userService.changePhoneNumber(userId,phoneNumber);
		return map;
		
	}
	
	@RequestMapping(value="changeMail")
	@ResponseBody
	public Map<String, String> changeMail(@RequestParam(value= "userId") long userId,@RequestParam(value= "mail") String mail){
		
		Map<String, String> map = new HashMap<>();
		map = userService.changeMail(userId,mail);
		return map;
		
	}
      
}    