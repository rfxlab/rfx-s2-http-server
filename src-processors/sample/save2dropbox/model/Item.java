package sample.save2dropbox.model;

import rfx.server.util.StringUtil;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

/**
 * @author trieu
 * 
 * general object for recommender engine
 *
 */
public class Item {
	
	@Expose
	int post_id;
	@Expose
	String keywords;
	@Expose
	String dp_link;
	@Expose
	String title;
	@Expose
	String link;
	@Expose
	int user_id;
	
	public Item() {
	}	
	
	public Item(int post_id, String keywords, String dp_link, String title,
			String link, int user_id) {
		super();
		this.post_id = post_id;
		this.keywords = keywords;
		this.dp_link = dp_link;
		this.title = title;
		this.link = link;
		this.user_id = user_id;
	}

	public int getPost_id() {
		return post_id;
	}
	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}
	public String getKeywords() {
		return StringUtil.safeString(keywords);
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getDp_link() {		
		return StringUtil.safeString(dp_link);
	}
	public void setDp_link(String dp_link) {
		this.dp_link = dp_link;
	}
	public String getTitle() {
		return StringUtil.safeString(title);
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLink() {
		return StringUtil.safeString(link);
	}
	public void setLink(String link) {
		this.link = link;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}