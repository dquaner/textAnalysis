package xmu.springBootMybatis.entity;
import java.util.List;

import net.sf.json.JSONArray;;


public class Info {
	private long project_id;
	private String word_segmentation;
	private String classifier;
	private String cluster;
	private int dataset_num;
	private String dataset_details;
	private String ws_figure;
	private String ws_table;
	private String best_segmention;
	private String sa_figure;
	private String sa_table;
	private String classify_figure;
	private String classify_table;
	private String best_classifier;
	private String cluster_figure;
	private String cluster_table;
	private String best_cluster;
	public long getProject_id() {
		return project_id;
	}
	public void setProject_id(long project_id) {
		this.project_id = project_id;
	}
	public String getWord_segmentation() {
		return word_segmentation;
	}
	public void setWord_segmentation(String word_segmentation) {
		this.word_segmentation = word_segmentation;
	}
	public String getClassifier() {
		return classifier;
	}
	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}
	public String getCluster() {
		return cluster;
	}
	public void setCluster(String cluster) {
		this.cluster = cluster;
	}
	public int getDataset_num() {
		return dataset_num;
	}
	public void setDataset_num(int dataset_num) {
		this.dataset_num = dataset_num;
	}
	public String getDataset_details() {
		return dataset_details;
	}
	public void setDataset_details(String dataset_details) {
		this.dataset_details = dataset_details;
	}
	public String getWs_figure() {
		return ws_figure;
	}
	public void setWs_figure(String ws_figure) {
		this.ws_figure = ws_figure;
	}
	public String getWs_table() {
		return ws_table;
	}
	public void setWs_table(String ws_table) {
		this.ws_table = ws_table;
	}
	public String getBest_segmention() {
		return best_segmention;
	}
	public void setBest_segmention(String best_segmention) {
		this.best_segmention = best_segmention;
	}
	public String getSa_figure() {
		return sa_figure;
	}
	public void setSa_figure(String sa_figure) {
		this.sa_figure = sa_figure;
	}
	public String getSa_table() {
		return sa_table;
	}
	public void setSa_table(String sa_table) {
		this.sa_table = sa_table;
	}
	public String getClassify_figure() {
		return classify_figure;
	}
	public void setClassify_figure(String classify_figure) {
		this.classify_figure = classify_figure;
	}
	public String getClassify_table() {
		return classify_table;
	}
	public void setClassify_table(String classify_table) {
		this.classify_table = classify_table;
	}
	public String getBest_classifier() {
		return best_classifier;
	}
	public void setBest_classifier(String best_classifier) {
		this.best_classifier = best_classifier;
	}
	public String getCluster_figure() {
		return cluster_figure;
	}
	public void setCluster_figure(String cluster_figure) {
		this.cluster_figure = cluster_figure;
	}
	public String getCluster_table() {
		return cluster_table;
	}
	public void setCluster_table(String cluster_table) {
		this.cluster_table = cluster_table;
	}
	public String getBest_cluster() {
		return best_cluster;
	}
	public void setBest_cluster(String best_cluster) {
		this.best_cluster = best_cluster;
	}
	
	public List<Dataset> getDatasets(){
		JSONArray array = JSONArray.fromObject(this.dataset_details);
		List<Dataset> datasets = JSONArray.toList(array, Dataset.class);
		return datasets;
	}
	
	@Override
	public String toString() {
		return "Parameter [\nproject_id=" + project_id + ",\n word_segmentation=" + word_segmentation + ",\n classifier="
				+ classifier + ",\n cluster=" + cluster + ",\n dataset_num=" + dataset_num + ",\n dataset_details="
				+ dataset_details + ",\n ws_figure=" + ws_figure + ",\n ws_table=" + ws_table + ",\n best_segmention="
				+ best_segmention + ",\n sa_figure=" + sa_figure + ",\n sa_table=" + sa_table + ",\n classify_figure="
				+ classify_figure + ",\n classify_table=" + classify_table + ",\n best_classifier=" + best_classifier
				+ ",\n cluster_figure=" + cluster_figure + ",\n cluster_table=" + cluster_table + ",\n best_cluster="
				+ best_cluster + "\n]";
	}
}
