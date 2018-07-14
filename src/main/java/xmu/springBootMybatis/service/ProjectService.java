package xmu.springBootMybatis.service;

import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import xmu.springBootMybatis.entity.Project;
import xmu.springBootMybatis.entity.User;

public interface ProjectService {
	
	public List<Project> getProjectByType(int finish_or_not,long user_id);
	
	public Map<String,Object> getInfo(long id);
	
	public String getPath();
	
	public int changePath(String location);
	
	public Map<String,String> createProjectText(String files,
			String projectName,String projectInstruction,boolean smartChineseAnalyzer,
			boolean IK,boolean ansj,boolean jieba,boolean hanlp,User user);

	public Map<String,String> createProjectFile(MultipartFile[] files,String projectName,String projectInstruction, 
			boolean smartChineseAnalyzer,boolean IK,boolean ansj, boolean jieba, boolean hanlp,User user);
	
	public Map<String, String> updateProjectAnotherMethod( boolean chooseNode,
    		 boolean KNN,
    		 boolean SVM,
    		 boolean Linear_programming,
    		 boolean Deep_learning,
    		 boolean Knowledge,
    		 boolean Kmeans,
    		 long projectId,
    		 String SVM_c,
    		 String SVM_toler,
    		 String SVM_max,
    		 String SVM_select,
    		 String KNN_class);
	
	public Map<String,Object> getProjectData(long projectId);
	
	public int updateProjectStatusFinish(long projectId);
	
	public int updateProjectStatusHalfFinish(long projectId);
	
	public void wordSegmentation(long projectId);
	
	public String dealMoreFunctionFile(long projectId);
	
	public String dealFile(long projectId);
}
