package xmu.springBootMybatis.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import xmu.springBootMybatis.service.ProjectService;

@Controller
@RequestMapping(value="/textAnalysis/system")
public class PageController {
	
	@Autowired
	ProjectService projectService;
	
	@RequestMapping(value="/main")    
    public String main(){    
        return "html/main";    
	}   
	
	@RequestMapping(value="/createProject") 
    public String createProject(){    
        return "html/createProject";    
	}  
	

	
	@RequestMapping(value="/adminFuction")
	public ModelAndView adminFuction(HttpServletRequest request) {
		//获取原路径，返回到前端页面
		String path = projectService.getPath();
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("path", path);
		modelAndView.setViewName("html/adminFuction");
		return modelAndView;
	}
	
}
