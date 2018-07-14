package xmu.springBootMybatis.service.Imp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xmu.springBootMybatis.entity.Project;
import xmu.springBootMybatis.mapper.ProjectMapper;
import xmu.springBootMybatis.service.AsyncTaskService;
import xmu.springBootMybatis.tools.AnsjTest;
import xmu.springBootMybatis.tools.HanlpTest;
import xmu.springBootMybatis.tools.JiebaTest;
import xmu.springBootMybatis.tools.SmartcnTest;

@Service
public class AsyncTaskServiceImpl implements AsyncTaskService{
	
	@Autowired
	ProjectMapper projectMapper;
	
	@Autowired
	private AnsjTest ansjTest;
	
	@Autowired
	private HanlpTest hanlpTest;
	
	@Autowired
	private JiebaTest jiebaTest;
	
	@Autowired
	private SmartcnTest smartcnTest;

	@Override
	public String executeAsyncTask(long i) {
		// TODO Auto-generated method stub
		
		//获取project
		Project project = projectMapper.getProjectById(i);
		
		//先进行分词
		try {
			wordSegmentation(i);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//项目是否执行完毕
		short finish_or_not = project.getFinish_or_not();
		
		//获取方法
		String method = "";
		
		//项目未执行完毕
		if(finish_or_not == 0) {
			
			//获取文件存储路径
			String path = project.getProject_location();
			
			//获取要执行的分类和聚类函数
			method = getClassMethod(project);
			
			//获取是否需要进行统计分析
			method = getClusterMethod(method,project);
			
			//判断是否需要进行统计分析
			boolean analysis = getAnalysis(project);
			
			//将这些信息发送到后台
			
		}
		projectMapper.changeFinishOrNotFinish(i);
		
		return "true";
	}
	
	//决定要执行的分类算法和聚类算法以及是否要进行统计分析
	public String getClassMethod(Project project) {
		//获取要执行的方法
		String method = "";
	
		//获取项目要执行的分类算法和参数
		String classification = project.getClassification_algorithm();
		
		if(classification!=null) {
			String classifications[] = classification.split(",");
			for(int k=0;k<classifications.length;k++) {
				
				int classificationValue = Integer.parseInt(classifications[k]);
				if(projectMapper.getClassification(classificationValue).equals("KNN")) {
					method = method+"KNN,";
				}
				else if(projectMapper.getClassification(classificationValue).equals("SVM")) {
					String value = project.getSVM_value();
					String values [] = value.split(",");
					method = method + "SVM_";
					for(int m=0;m<values.length;m++) {
						method = method + values[m]+"_";
					}
					
					//method去掉_
					method = method.substring(0,method.length()-1);
					//method增加，
					method = method+",";
				}
				else if(projectMapper.getClassification(classificationValue).equals("Linear programming")) {
					method = method+"Linear programming"+",";
				}
				else if(projectMapper.getClassification(classificationValue).equals("Deep learning")) {
					method = method+"Deep learning"+",";
				}
				else {
					//method = method+"Deep learning";
				}
			}
			
		}		
		
		return method;
	}
	
	//获取聚类算法
	public String getClusterMethod(String method,Project project) {
		//获取项目要执行的聚类算法
		String cluster = project.getClustering_algorithm();
		//如果有需要执行的聚类算法
		if(cluster!=null) {
			method = method+"Kmeans_"+project.getKmeans_value()+",";
		}
				
		if(method!="")
			method = method.substring(0,method.length()-1);
				
		return method;
	}
	
	//判断是否需要进行统计分析
	public boolean getAnalysis(Project project) {
		//获取项目是否需要进行文本分析
		return project.isStatistical_analysis();
	}
	
	//分词
	public String wordSegmentation(long i) throws IOException {
		
		//获取project
		Project project = projectMapper.getProjectById(i);
		
		//获取路径
		String path = project.getProject_location();
		String tempPath = path +"/原始数据集";
		
		//结果集路径
		String resultPath = path.replaceAll("dataSet", "resultSet");
		resultPath = resultPath+"/wordSegmentation/";
		
		
		//获取要执行的分析方法
		String method = project.getWord_segmentation();
		String methods[] = method.split(",");
		
		//根据要执行的分析方法分词
		for(String tempMethod:methods) {
			
			String temp = resultPath;
			
			//将String转化为int
			int methodIntValue = Integer.parseInt(tempMethod);
			
			//根据int来确定methodName
			String methodName = projectMapper.getWordSegmentationTool(methodIntValue);
			temp = temp + methodName;
			
			//根据文件生成结果集
			getDataFromPath(tempPath,temp,methodName);	
			
		}
		projectMapper.changeFinishOrNotFinish(i);
		//返回结果
		return "true";
	}
	
	//获取路径下面的文件
	public String getDataFromPath(String file_location,String resultPath,String tools) throws IOException{
		
		//获得某一特征提取下面的文件，或者文件夹的集合。
		File file = new File(file_location);
		File files[] = file.listFiles();
		String tempResultPath1 = resultPath;
		
		//判断下面是否有文件,如果没有文件则return "false"
		if(files.length<=0) {
			//map2.put("result", "no file found");
			return "false";
		}
		else {
			//遍历分词工具下面的文件，或者文件夹
			for(File f : files) {
				
				//获得文件名，或者文件夹名
				String fileName = f.getName();
				String filePath = file_location+"/"+fileName;
				resultPath= tempResultPath1+"/"+fileName;
				String tempResultPath2 = resultPath;
						
				System.out.println(resultPath);
				
				//如果只有txt文件,则将文件名设置为空，将文件内容保存下来，并结束。
				if(f.isFile()) {
					String text="";
					createDirectory(resultPath);
			        //读取文件
					text = readFile(filePath);
					//选择工具和方法分词
					chooseTools(tools,text,resultPath);
				}
				//如果是文件夹，那么就保存文件夹名，以及文件夹下面的文件名和文件内容
				else {			
					//获得某一特征提取下面的文件，或者文件夹的集合。
					File file_2 = new File(filePath);			
					File files_2[] = file_2.listFiles();
					
					for(File f1 : files_2) {
						String fileName_2 = f1.getName();
						String filePath_2 = filePath+"/"+fileName_2;
						resultPath = tempResultPath2+"/"+fileName_2;
						createDirectory(resultPath);
						//读取文件
						String text = readFile(filePath_2);
						chooseTools(tools,text,resultPath);
					}
				}
				
			}
		}

		return "true";
	}
	
	
	//根据工具名选择分词算法
	public void chooseTools(String tools,String text,String resultPath) throws IOException {
		//分词并输出
		if(tools.equals("smartChineseAnalyzer")) {
			smartcnTest.smartcnTools(text, resultPath);
		}
		else if(tools.equals("ansj")) {
			ansjTest.ansjTools(text, resultPath);
		}
		else if(tools.equals("jieba")) {
			jiebaTest.jiebaTools(text, resultPath);
		}
		else {
			hanlpTest.hanlpTools(text, resultPath);
		}
		
	}
	
	//根据路径创建文件夹
	public void createDirectory(String resultPath) throws IOException {
		//结果集的路径
		File tempFile = new File(resultPath);
		//创建结果集文件夹
		int i = 1;
        if (!tempFile.getParentFile().exists()) {    //创建文件夹  
            tempFile.getParentFile().mkdirs();
        }  
        if (!tempFile.exists()) {  
			tempFile.createNewFile();
        }  
	}
	
	//读取txt中的文件
	public String readFile(String filePath) throws IOException{
		String result = "";
		File sFile = new File(filePath);
		Reader r=new FileReader(sFile);//读文件
		BufferedReader br=new BufferedReader(r);//缓冲机制
		String str="";
		while((str=br.readLine())!=null){
			result = result+str;
		}//(str=br.readLine())很重要，不能写在上面，否则死循环
		r.close();
		br.close();
		return result;
	}
	
}
