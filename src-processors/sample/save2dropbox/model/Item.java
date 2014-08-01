package sample.save2dropbox.model;

public class Item {
	int post_id;
	String keywords;
	String dp_link;
	String title;
	String link;
	
	public int getPost_id() {
		return post_id;
	}
	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getDp_link() {
		return dp_link;
	}
	public void setDp_link(String dp_link) {
		this.dp_link = dp_link;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}		
}