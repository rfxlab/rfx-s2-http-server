package sample.pollapp.business.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import rfx.server.util.DatabaseDomainUtil;
import rfx.server.util.sql.CommonSpringDAO;
import rfx.server.util.sql.SqlTemplateString;
import rfx.server.util.sql.SqlTemplateUtil;
import sample.pollapp.model.Choice;
import sample.pollapp.model.Poll;

public class PollAppDAOImpl extends CommonSpringDAO implements PollAppDAO {
	
	@SqlTemplateString
	String SQL_getAllPolls = SqlTemplateUtil.getSql("SQL_getAllPolls");

	@Override
	public List<Poll> getAllPolls() {
		Map<Integer, Poll> polls = new HashMap<>();
		SqlRowSet rowSet = jdbcTpl.queryForRowSet(SQL_getAllPolls);		
		while (rowSet.next()) {
			int poll_id = rowSet.getInt("poll_id");
			Poll poll = polls.get(poll_id);
			if(poll == null){
				poll = new Poll();
				poll.setId(poll_id);
				poll.setQuestion(rowSet.getString("question"));			
				polls.put(poll_id,poll);
			}
			Choice choice = new Choice();
			choice.setId(rowSet.getInt("choice_id"));
			choice.setPollId(poll_id);
			choice.setText(rowSet.getString("choice_text"));
			choice.setVotes(rowSet.getInt("votes"));
			poll.addChoice(choice);
		}
		return new ArrayList<Poll>(polls.values());
	}
	
	public static void main(String[] args) {
		ApplicationContext context = DatabaseDomainUtil.getContext();
		PollAppDAO pollAppDAO = context.getBean(PollAppDAO.class);
		List<Poll> polls = pollAppDAO.getAllPolls();
		polls.parallelStream().forEach(new Consumer<Poll>(){
			@Override
			public void accept(Poll t) {
				System.out.println(t);
			} 
		});
		//System.out.println(new Gson().toJson(polls));
	}

}