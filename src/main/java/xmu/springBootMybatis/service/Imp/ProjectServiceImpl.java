package xmu.springBootMybatis.service.Imp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import net.sf.json.JSONArray;
import xmu.springBootMybatis.config.StaticValue;
import xmu.springBootMybatis.entity.Info;
import xmu.springBootMybatis.entity.Project;
import xmu.springBootMybatis.entity.User;
import xmu.springBootMybatis.mapper.InfoMapper;
import xmu.springBootMybatis.mapper.ProjectMapper;
import xmu.springBootMybatis.service.AsyncTaskService;
import xmu.springBootMybatis.service.ProjectService;
import xmu.springBootMybatis.tools.DecompressionRar;
import xmu.springBootMybatis.tools.DecompressionZip;

@Service
public class ProjectServiceImpl implements ProjectService{

	@Autowired
	private ProjectMapper projectMapper;
	
	@Autowired
	private InfoMapper infoMapper;
	
	@Autowired
	private AsyncTaskService asyncTaskService;
	
	@Autowired
	private DecompressionRar decompressionRar;
	
	@Autowired
	private DecompressionZip decompressionZip;
	
	//根据类型（已完成未完成）和用户ID来获取工程
	@Override
	public List<Project> getProjectByType(int finish_or_not,long user_id) {
		// TODO Auto-generated method stub
		List<Project> projects= projectMapper.getProjectByType(finish_or_not,user_id);
		
		//将时间戳转换成时间
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年-MM月dd日-HH时mm分ss秒");
		
		for(Project project: projects) {
			long time = Long.parseLong(project.getCreate_time());
			Date date = new Date(time);
			project.setCreate_time(simpleDateFormat.format(date));
		}
		
		return projects;
	}

	@Override
	public Map<String, Object> getInfo(long id) {
		Map<String, Object> infoMap = new HashMap<>();
    	Info info = infoMapper.getInfoByProjectId(id);
    	
    	String[] mStrings = {"none", "none", "none", "none"};
    	int[] method_num = {0,0,0,0};
    	int mn=1;
    	
    	String[] m_improve = {"none", "none", "none"};
    	int[] m_improve_num = {0,0,0};
    	int m_improven=2;
    	
    	String[] eStrings = {"none", "none", "none"};
    	int[] e_num = {0,0,0};
    	int en=1;
    	
    	String[][] wordsegmentations = {{"SmartChineseAnalyzer","A tool provided by a text retrieval system called Lucene. It uses a large number of corpuses in the Hidden Markov Model (HMM) to calculate the word statistics. Then, the statistical results are used to calculate the optimal segmentation results for the text."},
    			{"IKAnalyzer","A lightweight Chinese word segmentation toolkit that developed an ambiguity analysis algorithm to optimize the query arrangement and combination of query keywords, which can greatly improve the hit rate of Lucene search."},
    			{"Ansj","A java implementation of Chinese word segmentation based on n-Gram, CRF and HMM. The speed of word segmentation reaches about 2 million words per second, and the accuracy rate can reach over 96%."},
    			{"Jieba","Jieba segmentation uses dynamic programming to find the maximum probability path, and then finds the largest segmentation based on word frequency. For the unregistered words, the HMM and the Viterbi algorithm are used."},
    			{"HanLP","HanLP is a Java toolkit consisting of a series of models with different solutions. The goal is to popularize the application of natural language processing in the production environment."},
    			{"Word Segmentation Based on Knowledge Graphs","Choose the most appropriate segmentation result based on the correlation between the word and the topic of the text which is computed by semantic similarity using knowledge graph."}};
    	
    	String[][] classifiers = {{"KNN","K nearest neighbors is a simple algorithm that stores all available cases and classifies new cases based on a similarity measure (e.g., distance functions)."},
    			{"SVM","An SVM model is a representation of the examples as points in space, mapped so that the examples of the separate categories are divided by a clear gap that is as wide as possible."},
    			{"LR","The probabilities describing the possible outcomes of a single trial are modeled using a logistic function. "},
    			{"Deep Learning","Nonlinear models, which makes them flexible in modeling real world complex relationships. Be able to estimate the posterior probabilities, which provide the basis for establishing classification rule and performing statistical analysis."},
    			{"Algorithm Based on Knowledge Graphs","Improved KNN and SVM based on Knowledge Graphs."}};
    	
    	String[][] clusters = {{"DBSCAN","The DBSCAN algorithm views clusters as areas of high density separated by areas of low density. Due to this rather generic view, clusters found by DBSCAN can be any shape, as opposed to k-means which assumes that clusters are convex shaped."},
    			{"BIRCH","The Birch builds a tree called the Characteristic Feature Tree (CFT) for the given data. The data is essentially lossy compressed to a set of Characteristic Feature nodes (CF Nodes)."},
    			{"K-means","The KMeans algorithm clusters data by trying to separate samples in n groups of equal variance, minimizing a criterion known as the inertia or within-cluster sum-of-squares. This algorithm requires the number of clusters to be specified. It scales well to large number of samples and has been used across a large range of application areas in many different fields."},
    			{"K-means Based on Knowledge Graphs","We use a new distance as we call the concept distance. Based on the related knowledge graph, we query the shortest length of path among words of two documents."}};
    	
    	String ws_string = info.getWord_segmentation();
    	String classifier_string = info.getClassifier();
    	String cluster_string = info.getCluster();
    	
    	if(ws_string==null || ws_string=="") {
    		mStrings[0] = "none";
    	}else {
    		List<String> ws_str = Arrays.asList(ws_string.split(","));
    		//method3.1
        	if(ws_str.size()==1 && ws_str.get(0).equals("5")) {
        		mStrings[0] = "none";
        	}else {
        		mStrings[0] = "block";
        		method_num[0] = mn;
        		mn++;
        	}
        	//method3.4
        	if(ws_str.contains("5")) {
        		mStrings[3] = "block";
        		m_improve[0] = "block";
        		m_improve_num[0] = m_improven;
        		m_improven++;
        	}
        	
        	String[] ws = {"none","none","none","none","none"};
        	int[] ws_num = {0,0,0,0,0};
        	int wsn = 1;
        	for( int i=0; i<5; i++) {
        		if(ws_str.contains(String.valueOf(i))) {
        			ws[i] = "block";
        			ws_num[i] = wsn;
        			wsn++;
        		}
        	}
        	infoMap.put("ws", ws);
        	infoMap.put("ws_num", ws_num);
        	
        	//experiment
        	eStrings[0] = "block";
        	e_num[0] = en;
        	en++;
        	
        	List<String[]> wsList = new ArrayList<String[]>();
        	for( int i=0; i<6; i++) {
        		if(ws_str.contains(String.valueOf(i))) {
        			wsList.add(wordsegmentations[i]);
        		}
        	}
        	infoMap.put("wsList", wsList);
        	infoMap.put("ws_figure", info.getWs_figure());
        	infoMap.put("ws_table", info.getWs_table());
        	infoMap.put("best_ws", info.getBest_segmention());
    	}
    	en++;
    	
    	if(classifier_string==null || ws_string=="") {
    		mStrings[1] = "none";
    	}else {
    		List<String> classifier = Arrays.asList(classifier_string.split(","));
    		if(classifier.size()==1 && classifier.get(0).equals("4")) {
        		mStrings[1] = "none";
        	}else {
        		mStrings[1] = "block";
        		method_num[1] = mn;
        		mn++;
        	}
        	//method3.4
        	if(classifier.contains("4")) {
        		mStrings[3] = "block";
        		m_improve[1] = "block";
        		m_improve_num[1] = m_improven;
        		m_improven++;
        	}
        	
        	String[] classify = {"none","none","none","none"};
        	int[] classify_num = {0,0,0,0};
        	int classifyn = 1;
        	for( int i=0; i<4; i++) {
        		if(classifier.contains(String.valueOf(i))) {
        			classify[i] = "block";
        			classify_num[i] = classifyn;
        			classifyn++;
        		}
        	}
        	infoMap.put("classify", classify);
        	infoMap.put("classify_num", classify_num);
        	
        	//
        	eStrings[1] = "block";
        	e_num[1] = en;
        	en++;
        	
        	List<String[]> classifierList = new ArrayList<String[]>();
        	for( int i=0; i<5; i++) {
        		if(classifier.contains(String.valueOf(i))) {
        			classifierList.add(classifiers[i]);
        		}
        	}
        	infoMap.put("classifierList", classifierList);
        	infoMap.put("classify_figure", info.getClassify_figure());
        	infoMap.put("classify_table", info.getClassify_table());
        	infoMap.put("best_classifier", info.getBest_classifier());

    	}
    	
    	if(cluster_string==null || cluster_string=="") {
    		mStrings[2] = "none";
    	}else {
    		List<String> cluster_str = Arrays.asList(cluster_string.split(","));
    		if(cluster_str.size()==1 && cluster_str.get(0).equals("3")) {
        		mStrings[2] = "none";
        	}else {
        		mStrings[2] = "block";
        		method_num[2] = mn;
        		mn++;
        	}
        	//method3.4
        	if(cluster_str.contains("3")) {
        		mStrings[3] = "block";
        		m_improve[2] = "block";
        		m_improve_num[2] = m_improven;
        		m_improven++;
        	}
        	
        	String[] cluster = {"none","none","none"};
        	int[] cluster_num = {0,0,0};
        	int clustern = 1;
        	for( int i=0; i<3; i++) {
        		if(cluster_str.contains(String.valueOf(i))) {
        			cluster[i] = "block";
        			cluster_num[i] = clustern;
        			clustern++;
        		}
        	}
        	infoMap.put("cluster", cluster);
        	infoMap.put("cluster_num", cluster_num);
        	
        	//
        	eStrings[2] = "block";
        	e_num[2] = en;
        	en++;
        	List<String[]> clusterList = new ArrayList<String[]>();
        	for( int i=0; i<4; i++) {
        		if(cluster_str.contains(String.valueOf(i))) {
        			clusterList.add(clusters[i]);
        		}
        	}
        	infoMap.put("clusterList", clusterList);
    		infoMap.put("cluster_figure", info.getCluster_figure());
        	infoMap.put("cluster_table", info.getCluster_table());
        	infoMap.put("best_cluster", info.getBest_cluster());
    	}
    	
    	infoMap.put("method", mStrings);
    	if(mStrings[3].equals("block")) {
    		method_num[3]=mn;
    		infoMap.put("m_improve", m_improve);
    		infoMap.put("m_improve_num", m_improve_num);
    	}
    	infoMap.put("method_num", method_num);
    	
    	int dsnum = info.getDataset_num();
    	if(dsnum>1) {
    		infoMap.put("dsnum", dsnum+" real datasets");
    	}else {
    		infoMap.put("dsnum", dsnum+" real dataset");
    	}
    	
    	infoMap.put("datasets", info.getDatasets());
    	infoMap.put("eStrings", eStrings);
    	infoMap.put("e_num", e_num);
    	
    	infoMap.put("sa_figure", info.getSa_figure());
    	infoMap.put("sa_table", info.getSa_table());
        return infoMap;
	}
	
	//获取文件的存储路径
	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return projectMapper.getPath();
	}
	
	//修改文件的存储路径
	public int changePath(String location) {
		return projectMapper.changePath(location);
	}

	//存储txt类型的文件
	@Override
	public Map<String, String> createProjectText(String files, String projectName,
			String projectInstruction, boolean smartChineseAnalyzer, boolean IK, boolean ansj, boolean jieba,
			boolean hanlp,User user) {
		// TODO Auto-generated method stub
		String path = saveTxt(files,projectName,user.getId());//返回文件存储路径
		Map<String, String> map = new HashMap<>();
		map = createProject(path, projectName, projectInstruction, smartChineseAnalyzer, IK, ansj, jieba, hanlp, user);
		return map;
	}

	/*
	 * 存储txt文档
	 * */
	public String saveTxt(String files,String projectName, long userId) {

		//根据存储路径获取directory
		String directory = createDirectory(projectName,userId);		
		//获取时间戳
		long timeStamp = System.currentTimeMillis();		
		//加上时间戳，成为文件名
		String fileName =timeStamp+".txt";		
		File file = new File(directory,fileName);	
		FileOutputStream fos =null;	
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] array = files.getBytes();
		
		try {
			fos.write(array);
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return directory;
	}

	//存储file类型的文件，当然file类型只允许rar和zip类型
	@Override
	public Map<String, String> createProjectFile(MultipartFile[] files, String projectName,
			String projectInstruction, boolean smartChineseAnalyzer, boolean IK, boolean ansj, boolean jieba,
			boolean hanlp,User user) {
		// TODO Auto-generated method stub
		
		String path = "";
		
        try {  
            for (MultipartFile file : files) {    //循环保存文件
                path = uploadFile(file,projectName,user.getId());  
            }  
        } catch (Exception e) {  
            e.printStackTrace();   
        }  
        
		Map<String, String> map = new HashMap<>();
		map = createProject(path, projectName, projectInstruction, smartChineseAnalyzer, IK, ansj, jieba, hanlp, user);
        return map;    
	}
	
	/*
	 * 下载文件
	 * */
	
    public String uploadFile(MultipartFile file,String projectName,long userId) throws IOException {  
     
        String directory = createDirectory(projectName,userId);            //设置文件保存路径   
        File tempFile = new File(directory, String.valueOf(file.getOriginalFilename()));  
        
        file.transferTo(tempFile);  
        
//        //获取压缩包的后缀
//        String fileName = file.getOriginalFilename();
//        String fileType = fileName.substring(fileName.length()-3, fileName.length());
        
        
		//根据后缀名解压缩文件
        return directory;
    } 
    


    //创建路径 因为从前台传过来的路径不一定存在，所以我们要判断路径合不合适，然后再创建路径供保存文件使用,路径为：固定前缀 + 用户名 + 项目名
    public String createDirectory(String projectName,long userId) {
		//从数据库中读取path
    	String path = getPath();
    	//用tempPath逐层判断文件夹是不是存在
    	String tempPath = "";
    	//根据'/'将路径分隔开
    	String directories[] = path.split("/");
    	
    	//windows系统的方法
    	for(int i=0;i<directories.length;i++) {
    		tempPath = tempPath+directories[i];
    		File file = new File(tempPath);
    		if(!file.exists())
    			file.mkdirs();
    		tempPath = tempPath+"/";
    	}
    	
//    	//linux系统的方法
//    	for(int i=0;i<directories.length;i++) {
//    		tempPath = tempPath+"/"+directories[i];
//    		File file = new File(tempPath);
//    		if(!file.exists())
//    			file.mkdirs();
//    	}
    	tempPath = tempPath+userId;
    	File userFile = new File(tempPath);
    	userFile.mkdirs();
    	
    	tempPath = tempPath+"/"+projectName;
    	File userProjectFile = new File(tempPath);
    	userProjectFile.mkdirs();
    	
    	tempPath = tempPath+"/dataSet";
    	File dataSetFile = new File(tempPath);
    	dataSetFile.mkdirs();
    	
    	return tempPath;
    }
    
    public Map<String, String> createProject(String path,String projectName,
			String projectInstruction, boolean smartChineseAnalyzer, boolean IK, boolean ansj, boolean jieba,
			boolean hanlp,User user){
		Map<String, String> map = new HashMap<>();
		
		//看看选取了哪些分词工具
		String word_segment_tool = "";
		if(smartChineseAnalyzer==true)
			word_segment_tool = "1,";
		if(IK == true)
			word_segment_tool = word_segment_tool+"2,";
		if(ansj == true)
			word_segment_tool = word_segment_tool+"3,";
		if(jieba ==true)
			word_segment_tool = word_segment_tool+"4,";
		if(hanlp == true)
			word_segment_tool = word_segment_tool+"5,";
		
		//去掉最后一个字符","
		word_segment_tool = word_segment_tool.substring(0, word_segment_tool.length()-1);
		
		Project project = new Project();
		
		project.setCreate_time(System.currentTimeMillis()+"");//设置文件创建时间
		project.setFinish_or_not((short) 0);//设置文件尚未创建成功
		project.setProject_instruction(projectInstruction);//设置文件简介
		project.setProject_location(path);//设置文件存储路径
		project.setProject_name(projectName);//设置文件名
		project.setUser_id(user.getId());//设置用户Id
		project.setWord_segmentation(word_segment_tool);
		projectMapper.createProject(project);
		
		long result1 = project.getId();
		
		if(result1!=0) {
			map.put("saveResult","success");//表示存储文件成功
			map.put("projectId", result1+"");
		}
		else
			map.put("saveResult","fail");//表示存储文件成功
		return map;
    }

	/**
	 * 用户选择分类和聚类算法以及是否进行数据分析，把结果存到数据库中
	 * @return Map<String, String> 
	 */
	@Override
	public Map<String, String> updateProjectAnotherMethod(boolean chooseNode, boolean KNN, boolean SVM,
			boolean Linear_programming, boolean Deep_learning, boolean Knowledge, boolean Kmeans,long projectId,
   		 String SVM_c,
   		 String SVM_toler,
   		 String SVM_max,
   		 String SVM_select,
   		 String Kmeans_class) {
		// TODO Auto-generated method stub
		Map<String, String> map = new HashMap<>();
		
		String classification_algorithm = "";
		String clustering_algorithm = "";
		
		//根据用户的选择，将用户选中的分类算法保存到数据库中
		if(KNN == true)
			classification_algorithm = classification_algorithm+"1,";
		
		if(SVM == true)
			classification_algorithm = classification_algorithm+"2,";
		
		if(Linear_programming == true)
			classification_algorithm = classification_algorithm+"3,";
		
		if(Deep_learning == true)
			classification_algorithm = classification_algorithm+"4,";
		
		if(Knowledge == true)
			classification_algorithm = classification_algorithm+"5,";
		
		classification_algorithm = classification_algorithm.substring(0,classification_algorithm.length()-1);
		
		//根据用户的选择，将用户选中的聚类算法保存到数据库中
		if(Kmeans == true) {
			clustering_algorithm = clustering_algorithm+"1,";
			clustering_algorithm = clustering_algorithm.substring(0,clustering_algorithm.length()-1);
		}

		String Kmeans_value = Kmeans_class;
		String SVM_value = SVM_c+","+SVM_toler+","+SVM_max+","+SVM_select;
		
		int result = projectMapper.updateProjectAnotherMethod(chooseNode,classification_algorithm,clustering_algorithm,projectId,Kmeans_value,SVM_value);
		
		if(result!=0) {
			map.put("saveResult","success");//表示更新文件成功
		}
		else
			map.put("saveResult","fail");//表示更新文件失败
		
		return map;
	}
	
	public Map<String,Object> getProjectData(long projectId) {
		
		//根据项目ID查找项目	
		Project project = projectMapper.getProjectById(projectId);
		
		//全局变量记录下，projectID和projectName，方便后面使用
		StaticValue.PROJECTID = projectId;
		StaticValue.PROJECTNAME = project.getProject_name();
		
		Map<String, Object> map = new HashMap<>();
		
		//获得项目的存储地址
		String project_location = project.getProject_location();
		
		//获得项目所用的分词工具
		String projectWordSegmentation = project.getWord_segmentation();
		String wordSegmentations[];
		Map<String,Object> mapWordSegmentation = new HashMap<>();
		
		
		//循环遍历，根据分词工具获得结果
		if(projectWordSegmentation!=null) {
			wordSegmentations = projectWordSegmentation.split(",");
			try {
				map.put("wordSegmentation", getWordSegmentResult(wordSegmentations,project_location));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		//获得项目所用的分类算法
		String classification = project.getClassification_algorithm();
		String classifications[];
		//根据分类算法获取结果
		if(classification!=null) {
			classifications = classification.split(",");
			map.put("classifications",getClassification(classifications,project_location));
		}else {
			map.put("classifications", "false");
		}
	
		//获得项目所用的聚类算法
		String clustering = project.getClustering_algorithm();
		String clusterings[];
		if(clustering!=null) {
			clusterings = clustering.split(",");
			map.put("clustering",getClusterResult(clusterings,project_location));
		}
		else {
			map.put("clustering", "false");
		}
		
		//项目是否要进行数据分析
		boolean statistical = project.isStatistical_analysis();
		Map<String,Object> mapStatistical = new HashMap<>();
		if(statistical) {
			mapStatistical =getStatisticalResult(project_location);
			map.put("statistical", mapStatistical);
		}
		else {
			map.put("statistical", "false");
		}
		
		return map;
	}
	
	//分词工具的结果
	public JSONArray getWordSegmentResult(String []wordSegmentations,String path) throws IOException{
		
		Map<String, Object> map = new HashMap<>();
		
		for(String wordSegmentation: wordSegmentations) {
			
			//存在数据库中是字符串的形式，这里把字符串形式转变成数字形式
			int wordSegmentationIntValue = Integer.valueOf(wordSegmentation);
			String wordSegmentationStringValue = projectMapper.getWordSegmentationTool(wordSegmentationIntValue);
			
			//获得具体的某一分词工具,并且将数据集（dataSet）的路径改为结果集的路径（resultSet）
			String file_location = path+"/wordSegmentation/"+wordSegmentationStringValue;
			file_location = file_location.replaceAll("dataSet", "resultSet");
			
			//调用函数获取结果
			Map<String, Object> map2 = new HashMap<>();
			map2 = getDataFromPath(file_location);
			JSONArray json2 = JSONArray.fromObject(map2);
			map.put(wordSegmentationStringValue, json2);	
		}
		
		return JSONArray.fromObject(map);
	}
		
	
	//获取数据分析的结果
	public Map<String,Object> getStatisticalResult(String path){
		
		Map<String, Object> mapStatistical = new HashMap<>();
		
		//找到存储Statistic的路径，并将数据集路径改为结果集路径
		String file_location = path+"/Statistics/";
		file_location = file_location.replaceAll("dataSet", "resultSet");
		
		//查询特征特征提取的数据。
		String featurePath = file_location+ "featureExtraction";
		
		//查询情感分析的数据
		String sentimentPath = file_location+"sentimentAnalysis";
		
		//查询文本分析的数据
		String textStatisticsPath = file_location + "textStatistics";
	
		mapStatistical.put("featureExtraction", getFeatureExtractionResultFunction(featurePath));
		mapStatistical.put("sentimentAnalysis", getFeatureExtractionResultFunction(sentimentPath));
		mapStatistical.put("textStatistics", getTextStatisticsResultFunction(textStatisticsPath));
		
		return mapStatistical;
	}
	
	public JSONArray getFeatureExtractionResultFunction(String path) {
		//获取featurePath下的文件
		Map<String, Object> map2 = new HashMap<>();
		map2 = getDataFromPath(path);
		JSONArray json2 = JSONArray.fromObject(map2);
		return json2;
	}
	
	public JSONArray getTextStatisticsResultFunction(String path) {
		//获取featurePath下的文件
		Map<String, Object> map2 = new HashMap<>();
		map2 = getPictureNameFromPath(path);
		JSONArray json2 = JSONArray.fromObject(map2);
		return json2;
	}
	
	//获取文本分类的结果
	public JSONArray getClassification(String []classifications,String path){
		
		Map<String, Object> map = new HashMap<>();
		
		for(String classification: classifications) {
			
			//存在数据库中是字符串的形式，这里把字符串形式转变成数字形式
			int classificationIntValue = Integer.valueOf(classification);
			String classificationStringValue = projectMapper.getClassification(classificationIntValue);
			
			//获得具体的某一分词工具,并且将数据集（dataSet）的路径改为结果集的路径（resultSet）
			String file_location = path+"/classification/"+classificationStringValue;
			file_location = file_location.replaceAll("dataSet", "resultSet");
			
			//调用函数获取结果
			Map<String, Object> map2 = new HashMap<>();
			
			map2 = getPictureNameFromPath(file_location);
			JSONArray json2 = JSONArray.fromObject(map2);
			map.put(classificationStringValue, json2);	
		}
		
		return JSONArray.fromObject(map);
	}
	
	//获取文本聚类的结果
	public JSONArray getClusterResult(String []clusterings,String path){
		Map<String, Object> map = new HashMap<>();
		
		for(String clustering: clusterings) {
			
			//存在数据库中是字符串的形式，这里把字符串形式转变成数字形式
			int clusteringIntValue = Integer.valueOf(clustering);
			String clusteringStringValue = projectMapper.getClustering(clusteringIntValue);
			
			//获得具体的某一分词工具,并且将数据集（dataSet）的路径改为结果集的路径（resultSet）
			String file_location = path+"/clustering/"+clusteringStringValue;
			file_location = file_location.replaceAll("dataSet", "resultSet");
			
			//调用函数获取结果
			Map<String, Object> map2 = new HashMap<>();
			map2 = getPictureNameFromPath(file_location);
			JSONArray json2 = JSONArray.fromObject(map2);
			map.put(clusteringStringValue, json2);	
		}
		
		return JSONArray.fromObject(map);
	}
	
	//获取路径下面的文件
	public Map<String,Object> getDataFromPath(String file_location){
		
		//获得某一特征提取下面的文件，或者文件夹的集合。
		File file = new File(file_location);
		File files[] = file.listFiles();
		Map<String, Object> map2 = new HashMap<>();
		
		//判断下面是否有文件
		if(files.length<=0) {
//			map2.put("result", "no file found");
		}
		else {
			//遍历分词工具下面的文件，或者文件夹
			for(File f : files) {
				
				//获得文件名，或者文件夹名
				String fileName = f.getName();
				String filePath = file_location+"/"+fileName;
				Map<String, List<String>> map3 = new HashMap<>();
				//如果只有txt文件,则将文件名设置为空，将文件内容保存下来，并结束。
				if(f.isFile()) {	
					List<String> list = new LinkedList<>();
					
					try {
						list = readFile(filePath);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					JSONArray json = JSONArray.fromObject(list);
					map3.put(fileName, json);
					map2.put("null_value", map3);//表名没有文件名
					break;
				}
				//如果是文件夹，那么就保存文件夹名，以及文件夹下面的文件名和文件内容
				else {
					//获得某一特征提取下面的文件，或者文件夹的集合。
					File file_2 = new File(filePath);			
					File files_2[] = file_2.listFiles();
					for(File f1 : files_2) {
						String fileName_2 = f1.getName();
						String filePath_2 = filePath+"/"+fileName_2;
						List<String> list = new LinkedList<>();
						
						//读取文件中的数据到list中
						try {
							list = readFile(filePath_2);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						//将list转换为json格式，方便前端处理
						map3.put(fileName_2, JSONArray.fromObject(list));
					}
				}
				//将map转为json格式，方便前端处理
				map2.put(fileName, JSONArray.fromObject(map3));	
			}
		}

		return map2;
	}
	
	//获取路径下面的文件
	public Map<String,Object> getPictureNameFromPath(String file_location){
		
		//获得某一特征提取下面的文件，或者文件夹的集合。
		File file = new File(file_location);
		File files[] = file.listFiles();
		Map<String, Object> map2 = new HashMap<>();
		
		//判断下面是否有文件
		if(files.length<=0) {
//			map2.put("result", "no file found");
		}
		else {
			
			//遍历分词工具下面的文件，或者文件夹
			for(File f : files) {
				
				//获得文件名，或者文件夹名
				String fileName = f.getName();
				String filePath = file_location+"/"+fileName;
				
				
				//如果只有picture文件,则将文件名设置为空，将文件内容保存下来，并结束。
				if(f.isFile()) {	
					List<String> list = new LinkedList<>();
					list.add(filePath);
					map2.put("null_value", JSONArray.fromObject(list));//表名没有文件名
					break;
				}
				//如果是文件夹，那么就保存文件夹名，以及文件夹下面的文件名和文件内容
				else {
					//获得某一特征提取下面的文件，或者文件夹的集合。
					File file_2 = new File(filePath);			
					File files_2[] = file_2.listFiles();
					List<String> list = new LinkedList<>();
					for(File f1 : files_2) {
						//获取路径，将路径添加到list中去
						String fileName_2 = f1.getName();
						String filePath_2 = filePath+"/"+fileName_2;
						list.add(filePath_2);
					}

					//将list转换为json格式，方便前端处理
					map2.put(fileName, JSONArray.fromObject(list));
				}		
			}
		}
		return map2;
	}
	
	//读取txt中的文件
	public List<String> readFile(String filePath) throws IOException{
		List<String> list = new LinkedList<>();
		File sFile = new File(filePath);
		Reader r=new FileReader(sFile);//读文件
		BufferedReader br=new BufferedReader(r);//缓冲机制
		String str="";
		while((str=br.readLine())!=null){
			list.add(str);	
		}//(str=br.readLine())很重要，不能写在上面，否则死循环
		r.close();
		br.close();
		return list;
	}
	
	public int updateProjectStatusFinish(long projectId) {
		
		return projectMapper.changeFinishOrNotFinish(projectId); 
	
	}
	
	public int updateProjectStatusHalfFinish(long projectId) {
		
		return projectMapper.changeFinishOrNotHalfFinish(projectId); 
	
	}
	
    /*
     * 解压缩文件，并进行分词
     * */
    @Async
    public String dealFile(long projectId) {
    	
		//获取project
		Project project = projectMapper.getProjectById(projectId);
		
		//获取路径
		String path = project.getProject_location();
		
		File f = new File(path);
		File[] files = f.listFiles();
		
		String file = files[0].getAbsolutePath();
		System.out.println(file);
		System.out.println("hahah");
		String fileName = files[0].getName();
		String fileType = fileName.substring(fileName.length()-3, fileName.length());
    	
        if(fileType.equals("rar")) {
        	//解压缩rar文件
        	decompressionRar.unRar(file);
        }
        else {
        	//解压缩zip文件
			decompressionZip.unZip(file);
        }
        
        //解压缩成功后删除文件
        File tempFile = new File(file);
        tempFile.delete();
        
        //调用分词工具进行分词
        try {
			asyncTaskService.wordSegmentation(projectId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return "success";
    }
    
    /*
     * 解压缩文件，并进行分类和聚类
     * */
    @Async
    public String dealMoreFunctionFile(long projectId) {
    	
		//获取project
		Project project = projectMapper.getProjectById(projectId);
		
		//获取路径
		String path = project.getProject_location();
		
		File f = new File(path);
		File[] files = f.listFiles();
		
		String file = files[0].getAbsolutePath();
		String fileName = files[0].getName();
		String fileType = fileName.substring(fileName.length()-3, fileName.length());
    	
        if(fileType.equals("rar")) {
        	//解压缩rar文件
        	decompressionRar.unRar(file);
        }
        else {
        	//解压缩zip文件
			decompressionZip.unZip(file);
        }
        
        //解压缩成功后删除文件
        File tempFile = new File(file);
        tempFile.delete();
        
        //调用分类算法，聚类算法进行下一步操作
		asyncTaskService.executeAsyncTask(projectId);
	
        return "success";
    }
    
    /*
     * 调用分词工具进行分词，Async设置成异步的形式
     * */
    @Async
    public void wordSegmentation(long projectId) {
    	
    	try {
			asyncTaskService.wordSegmentation(projectId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

	
	
}
