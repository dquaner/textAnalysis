package xmu.springBootMybatis.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import xmu.springBootMybatis.config.WebSecurityConfig;
import xmu.springBootMybatis.entity.Project;
import xmu.springBootMybatis.entity.User;
import xmu.springBootMybatis.service.AsyncTaskService;
import xmu.springBootMybatis.service.ProjectService;
import xmu.springBootMybatis.service.UserService;

@Controller  
@RequestMapping(value="/textAnalysis/project")
public class ProjectController {

	@Autowired
	ProjectService projectService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	AsyncTaskService asyncTaskService;
	
	@RequestMapping(value="/finishTable")
	@ResponseBody
    public ModelAndView finishTable(HttpSession session){
		//获取存储在session中的用户标识
		String userCredit = (String) session.getAttribute(WebSecurityConfig.SESSION_KEY);
		
		//获取用户
		User user = userService.getUser(userCredit);
		
        ModelAndView mav = new ModelAndView();  
        mav.setViewName("html/finishTable"); 
        List<Project> projects = projectService.getProjectByType(1, user.getId());
		mav.addObject("projects", projects);
        return mav;    
	} 
	
	@RequestMapping(value="/unfinishTable")    
    public ModelAndView unfinishTable(HttpSession session){ 
		//获取存储在session中的用户标识
		String userCredit = (String) session.getAttribute(WebSecurityConfig.SESSION_KEY);
		
		//获取用户
		User user = userService.getUser(userCredit);
		
        ModelAndView mav = new ModelAndView();  
        mav.setViewName("html/unfinishTable"); 
        List<Project> projects = projectService.getProjectByType(0, user.getId());
		mav.addObject("projects", projects);
        return mav; 
	}
	
	//
	@PostMapping(value="/createProjectFile")
	@ResponseBody
    public Map<String,String> createProjectFile(            
    		@RequestParam(value = "files[]") MultipartFile[] files,  //这样接收文件 
    		@RequestParam(value = "choice") int choice,
            @RequestParam(value = "projectName") String projectName,            //接收其他参数  
            @RequestParam(value = "projectInstruction") String projectInstruction,  
            @RequestParam(value = "smartChineseAnalyzer") boolean smartChineseAnalyzer, 
            @RequestParam(value = "IK") boolean IK, 
            @RequestParam(value = "ansj") boolean ansj, 
            @RequestParam(value = "jieba") boolean jieba, 
            @RequestParam(value = "hanlp") boolean hanlp,
            HttpServletRequest request){ 
		
		//从request中读取session,从session中读取用户身份信息
		HttpSession session = request.getSession();
		String userCredit = (String) session.getAttribute(WebSecurityConfig.SESSION_KEY);
		//查询用户
    	User user = userService.getUser(userCredit);
		Map<String, String> map = new HashMap<>();
		map = projectService.createProjectFile(files,projectName,projectInstruction, smartChineseAnalyzer, IK, ansj, jieba, hanlp,user);
        
		if(choice == 1) {
			map.put("result","1");//如果是1的话，则说明需要下一步
		}
		else {
			map.put("result","2");//如果是2的话，则说明不需要下一步文件,创建完成
			
			/*异步调用函数,解压缩文件分词工具进行分词*/
			long projectId =Long.parseLong(map.get("projectId"));
			projectService.dealFile(projectId);

		}
		
		return map;    
	}  
	
	

	
	//
	@PostMapping(value="/createProjectText")
	@ResponseBody
    public Map<String,String> createProjectText(            
    		@RequestParam(value = "files") String files,  //这样接收文件 
    		@RequestParam(value = "choice") int choice,
            @RequestParam(value = "projectName") String projectName,            //接收其他参数  
            @RequestParam(value = "projectInstruction") String projectInstruction,  
            @RequestParam(value = "smartChineseAnalyzer") boolean smartChineseAnalyzer, 
            @RequestParam(value = "IK") boolean IK, 
            @RequestParam(value = "ansj") boolean ansj, 
            @RequestParam(value = "jieba") boolean jieba, 
            @RequestParam(value = "hanlp") boolean hanlp, 
            HttpServletRequest request ){ 
		
		HttpSession session = request.getSession();
		String userCredit = (String) session.getAttribute(WebSecurityConfig.SESSION_KEY);
		
		//查询用户
    	User user = userService.getUser(userCredit);
		Map<String, String> map = new HashMap<>();
		map = projectService.createProjectText(files,projectName,projectInstruction, smartChineseAnalyzer, IK, ansj, jieba, hanlp,user);
		
		/*异步调用函数,调用分词工具进行分词*/
		long projectId =Long.parseLong(map.get("projectId"));
		projectService.wordSegmentation(projectId);

		
		return map;
	}  
	
	@RequestMapping(value="/ProjectMoreFunction")    
    public ModelAndView ProjectMoreFunction(@PathParam("projectId") long projectId){    
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("projectId", projectId);
		modelAndView.setViewName("html/createProjectMoreFunction");
        return modelAndView;    
	}  
	
	@RequestMapping(value="/createProjectMoreFunction")    
	@ResponseBody
    public Map<String, String> createProjectMoreFunction(@RequestParam(value = "chooseNode") boolean chooseNode,
    		@RequestParam(value = "KNN") boolean KNN,
    		@RequestParam(value = "SVM") boolean SVM,
    		@RequestParam(value = "Linear_programming") boolean Linear_programming,
    		@RequestParam(value = "Deep_learning") boolean Deep_learning,
    		@RequestParam(value = "Knowledge") boolean Knowledge,
    		@RequestParam(value = "Kmeans") boolean Kmeans,
    		@RequestParam(value = "projectId") String projectId,
    		@RequestParam(value = "SVM_c") String SVM_c,
    		@RequestParam(value = "SVM_toler") String SVM_toler,
    		@RequestParam(value = "SVM_max") String SVM_max,
    		@RequestParam(value = "SVM_select") String SVM_select,
    		@RequestParam(value = "Kmeans_class") String Kmeans_class){ 
		
		long id = Long.parseLong(projectId);
		Map<String, String> map = new HashMap<>();
		map = projectService.updateProjectAnotherMethod(chooseNode,KNN,SVM,Linear_programming,Deep_learning,Knowledge,Kmeans,id,SVM_c,SVM_toler,SVM_max,SVM_select,
				Kmeans_class);
		
		/*异步调用函数，调用分类算法，聚类算法*/
		projectService.dealMoreFunctionFile(id);

		return map;    
	}  
	
	
	
	@RequestMapping(value="/detailedInformationOfProject") 
	@ResponseBody
    public ModelAndView detailedInformationOfProject(@PathParam(value="projectId") long projectId ){  
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("html/detailedInformationOfProject");
		Map<String,Object> model = projectService.getProjectData(projectId);
		
		modelAndView.addObject("model",model);
        return modelAndView;    
	} 
	
	@RequestMapping(value="/papersummary") 
	@ResponseBody
    public ModelAndView papersummary(@PathParam(value="projectId") long projectId ){  
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("html/papersummary");
		Map<String, Object> infoMap = projectService.getInfo(projectId);
		
		String[] method = (String[]) infoMap.get("method");
		modelAndView.addObject("method", method);
		modelAndView.addObject("method_num", (int[]) infoMap.get("method_num"));
		if (method[0].equals("block")) {
			modelAndView.addObject("ws", (String[]) infoMap.get("ws"));
			modelAndView.addObject("ws_num", (int[]) infoMap.get("ws_num"));
		}
		if (method[1].equals("block")) {
			modelAndView.addObject("classify", (String[]) infoMap.get("classify"));
			modelAndView.addObject("classify_num", (int[]) infoMap.get("classify_num"));
		}
		if (method[2].equals("block")) {
			modelAndView.addObject("cluster", (String[]) infoMap.get("cluster"));
			modelAndView.addObject("cluster_num", (int[]) infoMap.get("cluster_num"));
		}
		if (method[3].equals("block")) {
			modelAndView.addObject("m_improve", (String[]) infoMap.get("m_improve"));
			modelAndView.addObject("m_improve_num", (int[]) infoMap.get("m_improve_num"));
		}

		modelAndView.addObject("dsnum", (String) infoMap.get("dsnum"));
		modelAndView.addObject("datasets", (List<String>) infoMap.get("datasets"));
		modelAndView.addObject("eStrings", (String[]) infoMap.get("eStrings"));
		modelAndView.addObject("e_num", (int[]) infoMap.get("e_num"));

		modelAndView.addObject("wsList", (List<String[]>) infoMap.get("wsList"));
		modelAndView.addObject("classifierList", (List<String[]>) infoMap.get("classifierList"));
		modelAndView.addObject("clusterList", (List<String[]>) infoMap.get("clusterList"));

		modelAndView.addObject("ws_figure", (String) infoMap.get("ws_figure"));
		modelAndView.addObject("ws_table", (String) infoMap.get("ws_table"));
		modelAndView.addObject("best_ws", (String) infoMap.get("best_ws"));

		modelAndView.addObject("classify_figure", (String) infoMap.get("classify_figure"));
		modelAndView.addObject("classify_table", (String) infoMap.get("classify_table"));
		modelAndView.addObject("best_classifier", (String) infoMap.get("best_classifier"));

		modelAndView.addObject("cluster_figure", (String) infoMap.get("cluster_figure"));
		modelAndView.addObject("cluster_table", (String) infoMap.get("cluster_table"));
		modelAndView.addObject("best_cluster", (String) infoMap.get("best_cluster"));

		modelAndView.addObject("sa_figure", (String) infoMap.get("sa_figure"));
		modelAndView.addObject("sa_table", (String) infoMap.get("sa_table"));
        return modelAndView;    
	}
	
	@RequestMapping(value="/experiment") 
	@ResponseBody
    public ModelAndView experiment(@PathParam(value="projectId") long projectId ){  
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("html/experiment");
		Map<String, Object> infoMap = projectService.getInfo(projectId);

		modelAndView.addObject("dsnum", (String) infoMap.get("dsnum"));
		modelAndView.addObject("datasets", (List<String>) infoMap.get("datasets"));
		modelAndView.addObject("eStrings", (String[]) infoMap.get("eStrings"));
		modelAndView.addObject("e_num", (int[]) infoMap.get("e_num"));

		modelAndView.addObject("wsList", (List<String[]>) infoMap.get("wsList"));
		modelAndView.addObject("classifierList", (List<String[]>) infoMap.get("classifierList"));
		modelAndView.addObject("clusterList", (List<String[]>) infoMap.get("clusterList"));

		modelAndView.addObject("ws_figure", (String) infoMap.get("ws_figure"));
		modelAndView.addObject("ws_table", (String) infoMap.get("ws_table"));
		modelAndView.addObject("best_ws", (String) infoMap.get("best_ws"));

		modelAndView.addObject("classify_figure", (String) infoMap.get("classify_figure"));
		modelAndView.addObject("classify_table", (String) infoMap.get("classify_table"));
		modelAndView.addObject("best_classifier", (String) infoMap.get("best_classifier"));

		modelAndView.addObject("cluster_figure", (String) infoMap.get("cluster_figure"));
		modelAndView.addObject("cluster_table", (String) infoMap.get("cluster_table"));
		modelAndView.addObject("best_cluster", (String) infoMap.get("best_cluster"));

		modelAndView.addObject("sa_figure", (String) infoMap.get("sa_figure"));
		modelAndView.addObject("sa_table", (String) infoMap.get("sa_table"));
        return modelAndView;    
	}
	
	
	@RequestMapping(value="/changePath")
	@ResponseBody
	public Map<String, String> changePath(@PathParam(value = "path") String path) {
		
		int result = projectService.changePath(path);
		Map<String,String> map = new HashMap<>();
		if(result!=0) {
			map.put("path", path);
			map.put("result", "success");
		}
		else {
			map.put("result", "fail");
		}
		return map;
	}
	
	 @RequestMapping("/image")  
	 @ResponseBody  
	 public void getImage(HttpServletRequest request, HttpServletResponse response,@PathParam(value = "filePath") String filePath) throws Exception{  
		 
	     String JPG="image/jpeg;charset=GB2312";  
	     File file = new File(filePath);    
	     // 获取输出流  
	     OutputStream outputStream = response.getOutputStream();  
	     FileInputStream fileInputStream = new FileInputStream(file);  
	         
	     // 读数据      
	     byte[] data = new byte[fileInputStream.available()];  
	     fileInputStream.read(data);  
	     fileInputStream.close();  
	         
	     // 回写  
	     response.setContentType(JPG);  
	         outputStream.write(data);  
	         outputStream.flush();  
	         outputStream.close(); 
	 }
}
