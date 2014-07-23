package rfx.server.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import rfx.server.util.Utils;

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;

public class TestJava8Filter {
	static class Tuple {
		List<Integer> data;
		public Tuple(int size) {
			data = new ArrayList<Integer>(size);
		}
		
		public void addData(int i){
			data.add(i);
		}
		
		@Override
		public String toString() {
			return new Gson().toJson(this);
		}
	}
	
	static Function<Integer, Tuple> functor1 = new Function<Integer, Tuple>() {
		@Override
		public Tuple apply(Integer t) {
			
			
			String msg = indexedData.remove(t);
			
			//System.out.println("functor1 "+t + " => " + msg);
			Tuple tuple = new Tuple(1);
			tuple.addData(t);						
			return tuple;
		}
	};
	
	static Function<Tuple, Void> functor2 = new Function<Tuple, Void>() {
		@Override
		public Void apply(Tuple t) {
			//System.out.println("functor2 "+t);			
			return null;
		}
	};
	
	static AtomicLong totalProcessedCount = new AtomicLong(0);
	static AtomicBoolean stopApp = new AtomicBoolean(false);
	static List<Integer> data = new ArrayList<Integer>();
	//static Queue<Integer> queue = new ConcurrentLinkedQueue<Integer>();
	static Map<Integer, String> indexedData = new ConcurrentHashMap<Integer, String>();

	public static void main(String[] args) throws Exception {
		
		Stopwatch stopwatch = Stopwatch.createStarted();
		
		int MAX = 1000000;
		int MAX_STOP = 100000;
		int STREAM_SIZE = 1000;
		int SEED_SIZE = 50;
		for (int i = 0; i < MAX; i++) {
			data.add(Utils.randInt(0, MAX));
		}
		System.out.println(data.size());
		
		new Thread( () -> {
			while(true){		
				if(TestJava8Filter.stopApp.get()){
					break;
				}
				
				int k = Utils.randInt(0, MAX);
				String v = "message "+k;  
				indexedData.put(k,v);
							
				if(indexedData.size() % SEED_SIZE == 0) {
					Utils.sleep(1);	
				}
				
				long allTotal = totalProcessedCount.get();
				if(allTotal > MAX_STOP){
					TestJava8Filter.stopApp.set(true);
				}
			}
		} ).start();
		
		
//		List<Tuple> rsStream = data.parallelStream().map( i -> {
//			System.out.println(i);
//			Tuple tuple = new Tuple(1);
//			tuple.addData(i);
//			return tuple;
//		}).collect(Collectors.toCollection(ArrayList::new));		
//		System.out.println(rsStream);
		
//		long count = data.parallelStream().limit(MAX).map(functor1).map(functor2).count();
//		System.out.println(count);
		
		while(true){		
			if(TestJava8Filter.stopApp.get()){
				break;
			}
			long count = indexedData.keySet().stream().limit(STREAM_SIZE).map(functor1).map(functor2).count();
			totalProcessedCount.addAndGet(count);
			System.out.println("processed count = "+count);
			System.out.println("remain indexedData.size = "+indexedData.size());
			Utils.sleep(10);
		}
		
		System.out.println("Thread.activeCount = "+Thread.activeCount());		
		System.out.println("totalProcessedCount = "+totalProcessedCount.get());
		System.out.println("remain indexedData.size = "+indexedData.size());
		long donetime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
		System.out.println("donetime= "+donetime);
//		
		
	}
}

//totalProcessedCount = 100448
//remain indexedData.size = 0
//donetime= 2175
//=> 1 MILLISECONDS can process 46.1 messages
