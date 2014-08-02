package sample.save2dropbox.business;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import redis.clients.jedis.Jedis;
import rfx.server.configs.NoSqlServerInfoConfigs;
import sample.save2dropbox.model.Item;

import com.google.gson.Gson;

/**
 * @author trieu
 * 
 * the recommend items, the core idea is, modeling interest's user by using most used keywords from past to current 
 *
 */
public class UserRecommender {
	
	static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
            new Comparator<Map.Entry<K,V>>() {
                @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                    int res = (-1)*e1.getValue().compareTo(e2.getValue());
                    return res != 0 ? res : 1; // preserve items with equal values
                }
            }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
	
	public static List<String> getTop5KeywordsOfUser(int userId){
		String host = NoSqlServerInfoConfigs.getServerInfo("REDIS_SERVER1").host;
		int port = NoSqlServerInfoConfigs.getServerInfo("REDIS_SERVER1").port;
		Jedis jedis = new Jedis(host, port);
		
		Map<String, String> map = jedis.hgetAll("user:" + userId);
		jedis.close();
				
		ConcurrentHashMap<String, Integer> userKeywordStats = new ConcurrentHashMap<>();
		
		map.values().parallelStream().forEach((String json)->{
			try {
				//System.out.println(json);
				Item item = new Gson().fromJson(json, Item.class);				
				if(item.getKeywords() == null){
					return;
				}
				String[] keywords = item.getKeywords().split(",");
				for (String keyword : keywords) {
					keyword = keyword.trim();
					if(!keyword.isEmpty()){
						int kf = userKeywordStats.getOrDefault(keyword, -1);
						if(kf < 0){
							kf = 1;
							userKeywordStats.put(keyword, kf);
						} else {
							kf++;
							userKeywordStats.put(keyword, kf);
						}
					}					
					
					//System.out.println(keyword+" -> " + kf);
				}				
			} catch (Exception e) {	}
		});
		//System.out.println(userKeywordStats);
//		userKeywordStats.put("a", 2);
//		userKeywordStats.put("Apple", 3);
//		userKeywordStats.put("IBM", 1);
//		userKeywordStats.put("Google", 23);
//		userKeywordStats.put("Facebook", 5);
//		userKeywordStats.put("Dell", 3);
		
		int top5 = 5;
		SortedSet<Entry<String, Integer>> sortedset = entriesSortedByValues(userKeywordStats);
		List<String> top5Keywords = new ArrayList<String>(5);
		for (Entry<String, Integer> entry  : sortedset) {
		    //System.out.println(entry.getKey()+" => "+entry.getValue());
			top5Keywords.add(entry.getKey());
		    top5--;
		    if(top5 <= 0){
		    	break;
		    }
		}
		return top5Keywords;
	}
	
	public static List<Item> recomendItems(int userId){
		return SearchEngineLucene.searchItemsByKeywords(getTop5KeywordsOfUser(userId),userId);
	}
	
	public static void main(String[] args) {
		int userId = 55455908;
		//System.out.println(getTop5KeywordsOfUser(userId));
		
		String host = NoSqlServerInfoConfigs.getServerInfo("REDIS_SERVER1").host;
		int port = NoSqlServerInfoConfigs.getServerInfo("REDIS_SERVER1").port;
		Jedis jedis = new Jedis(host, port);
		Set<String> userKeys = jedis.keys("user:*");
		userKeys.parallelStream().forEach((String userkey)->{
			Map<String, String> map = jedis.hgetAll(userkey);
			List<Item> userItems = new ArrayList<>();
			map.values().stream().forEach((String json)->{
				try {
					Item item = new Gson().fromJson(json, Item.class);
					userItems.add(item);
					System.out.println(item.getDp_link());
				} catch (Exception e) {}
			});
			SearchEngineLucene.indexItems(userItems);
		});
		jedis.close();
	}
	
	
}
