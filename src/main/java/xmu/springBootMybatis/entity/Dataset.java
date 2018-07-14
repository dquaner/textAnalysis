package xmu.springBootMybatis.entity;

public class Dataset {

	private String name;
	private String description;
	private int textnum;
	private int maxlen;
	private int minlen;
	private int averlen;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getTextnum() {
		return textnum;
	}
	public void setTextnum(int textnum) {
		this.textnum = textnum;
	}
	public int getMaxlen() {
		return maxlen;
	}
	public void setMaxlen(int maxlen) {
		this.maxlen = maxlen;
	}
	public int getMinlen() {
		return minlen;
	}
	public void setMinlen(int minlen) {
		this.minlen = minlen;
	}
	public int getAverlen() {
		return averlen;
	}
	public void setAverlen(int averlen) {
		this.averlen = averlen;
	}
	@Override
	public String toString() {
		return "Dataset [name=" + name + ", description=" + description + ", textnum=" + textnum + ", maxlen=" + maxlen
				+ ", minlen=" + minlen + ", averlen=" + averlen + "]";
	}
	
}
