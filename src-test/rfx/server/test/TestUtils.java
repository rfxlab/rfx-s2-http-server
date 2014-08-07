package rfx.server.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rfx.server.util.memcache.MemcacheCommand;
import rfx.server.util.memcache.MemcacheUtil;
import sample.pollapp.model.Choice;
import sample.pollapp.model.Poll;

import com.google.gson.Gson;

public class TestUtils {
	static void testMemcache() {
		final String key = "key1";
		String poolname = "MEMCACHE_SERVER";
		try {
			// init memcache
		
			
			final Poll poll = new Poll();
			poll.setId(1);
			poll.setPublishedDate(new Date());
			poll.setQuestion("what do you want to do ?");
			List<Choice> choices = new ArrayList<>();
			choices.add(new Choice(1, 1, "eat", 1));
			choices.add(new Choice(2, 1, "sleep", 22));
			poll.setChoices(choices );
			
//			(new MemcacheCommand<Boolean>(poolname) {
//				@Override
//				protected Boolean build() {
//					mcClient.set(key, 10000, new Gson().toJson(poll));
//					return true;
//				}
//			}).execute();

			String json = (new MemcacheCommand<String>(poolname) {
				@Override
				protected String build() {
					System.out.println((mcClient.getStats().values().iterator().next().get("total_items")) );
					return String.valueOf(mcClient.get(key));
				}
			}).execute();

			//System.out.println(json);
			Poll poll2 = new Gson().fromJson(json, Poll.class);
			System.out.println("from memcache: "+poll2);
			
//			JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
//			System.out.println(jsonObject.get("url").getAsString());
			//MemcacheUtil.freeMemcachedResource(poolname);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		MemcacheUtil.freeMemcachedResource(poolname);
	}
	
	public static void main(String[] args) {
		testMemcache();
	}
}
