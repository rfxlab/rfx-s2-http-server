package sample.pollapp.processor;

import io.netty.handler.codec.http.HttpHeaders;

import java.util.List;

import org.springframework.context.ApplicationContext;

import rfx.server.configs.ContentTypePool;
import rfx.server.http.DataService;
import rfx.server.http.HttpProcessor;
import rfx.server.http.HttpProcessorConfig;
import rfx.server.http.HttpRequestEvent;
import rfx.server.util.DatabaseDomainUtil;
import sample.pollapp.business.dao.PollAppDAO;
import sample.pollapp.model.Poll;

/**
 * @author trieu
 * 
 * simple sample processor 
 *
 */
@HttpProcessorConfig(uriPath= "/polls", contentType = ContentTypePool.JSON)
public class HelloHttpProcessor extends HttpProcessor {
	
	@Override
	protected DataService process(HttpRequestEvent requestEvent) {		
		ApplicationContext context = DatabaseDomainUtil.getContext();
		PollAppDAO pollAppDAO = context.getBean(PollAppDAO.class);
		List<Poll> polls = pollAppDAO.getAllPolls();
		return new MyData(polls);
	}

	static class MyData implements DataService{		
		List<Poll> polls;	
		
		static final String classpath = MyData.class.getName();

		public List<Poll> getPolls() {			
			return polls;
		}
		
		
		public MyData(List<Poll> polls) {
			super();
			this.polls = polls;
		}


		@Override
		public void freeResource() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public String getClasspath() {
			// TODO Auto-generated method stub
			return classpath;
		}

		@Override
		public boolean isOutputable() {
			// TODO Auto-generated method stub
			return false;
		}


		@Override
		public List<HttpHeaders> getHttpHeaders() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}
