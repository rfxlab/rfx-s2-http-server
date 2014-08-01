package sample.save2dropbox.business;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import redis.clients.jedis.Jedis;
import sample.save2dropbox.model.Item;

import com.google.gson.Gson;

public class UserRecommender {
	
	public static void main(String[] args) {
		Jedis jedis = new Jedis("localhost");
		int userId = 46008353;
		Map<String, String> map = jedis.hgetAll("user:" + userId);
		jedis.close();
		
		
		Map<String, Integer> userKeywordStats = new HashMap<>();
		
		map.values().stream().forEach((String json)->{
			System.out.println(json);
			Item item = new Gson().fromJson(json, Item.class);
			String keywords = item.getKeywords();
			if(keywords == null){
				return;
			}
			int kf = userKeywordStats.getOrDefault(keywords, -1);
			if(kf < 0){
				kf = 1;
				userKeywordStats.put(keywords, kf);
			} else {
				kf++;
				userKeywordStats.put(keywords, kf);
			}
			
			System.out.println(keywords+" -> " + kf);
		});
		System.out.println(userKeywordStats);
		userKeywordStats.put("a", 2);
		userKeywordStats.put("Apple", 3);
		userKeywordStats.put("IBM", 1);
		
		int top3;
		for (Entry<String, Integer> entry  : entriesSortedByValues(userKeywordStats)) {
		    System.out.println(entry.getKey()+":"+entry.getValue());
		}
		
	}
	
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
}
