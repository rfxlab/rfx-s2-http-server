package sample.pollapp.model;

import com.google.gson.Gson;

public class Choice {
	int id, pollId;
	String text;
	int votes;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPollId() {
		return pollId;
	}
	public void setPollId(int pollId) {
		this.pollId = pollId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getVotes() {
		return votes;
	}
	public void setVotes(int votes) {
		this.votes = votes;
	}
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
