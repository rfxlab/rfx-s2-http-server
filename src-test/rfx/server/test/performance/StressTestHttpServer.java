package rfx.server.test.performance;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import rfx.server.util.http.HttpClientUtil;

import com.google.common.base.Stopwatch;

public class StressTestHttpServer {
	static AtomicInteger validCount = new AtomicInteger(0);
	static AtomicInteger invalidCount = new AtomicInteger(0);
	static Stopwatch stopwatch = Stopwatch.createUnstarted();

	@Rule
	public ContiPerfRule i = new ContiPerfRule();

	
	@Test
	@PerfTest(invocations = 10000, threads = 250)
	@Required(max = 5000, average = 500)
	public void test1() throws Exception {
		String url = "http://delivery.adnetwork.vn/247/inpagead/zid_1340349210/wid_1286789098/div_InPage_728_90/sc_1600x900/cd_24/fl_14/lg_ZW4tVVM=/jv_1/urf_/cb_417152/";
		String rs = HttpClientUtil.executeGet(url);

		if (rs.contains("AbdVNPC")) {
			//System.out.println(rs+" \n");
			validCount.incrementAndGet();
		} else {
			System.out.println(rs);
			invalidCount.incrementAndGet();
			throw new IllegalArgumentException("Bad response!");
		}
		
//		Throughput:	3,835 / s	
//		Min. latency:	0 ms	
//		Average latency:	128 ms	400 ms
//		Median:	80 ms	
//		90%:	286 ms	
//		Max latency:	2,516 ms	4,000 ms
	}
	
	@Test
	@PerfTest(invocations = 50000, threads = 500)
	@Required(max = 4000, average = 400)
	public void test2() throws Exception {
		String url = "http://localhost:9090/server-info?filter=all";
		String rs = HttpClientUtil.executeGet(url);

		if (rs.contains("Time:")) {
			//System.out.println(rs+" \n");
			validCount.incrementAndGet();
		} else {
			System.out.println(rs);
			invalidCount.incrementAndGet();
			throw new IllegalArgumentException("Bad response!");
		}
		
//		Throughput:	3,835 / s	
//		Min. latency:	0 ms	
//		Average latency:	128 ms	400 ms
//		Median:	80 ms	
//		90%:	286 ms	
//		Max latency:	2,516 ms	4,000 ms
	}
	
	@Before
	public void beginTest(){
		stopwatch.start();
		System.out.println("-------------------------------------");
		System.out.println("valid " + validCount.get());
		System.out.println("invalid " + invalidCount.get());
		System.out.println("-------------------------------------");
	}
	
	@After
	public void finishTest(){
		System.out.println("-------------------------------------");
		System.out.println("valid " + validCount.get());
		System.out.println("invalid " + invalidCount.get());
		System.out.println("-------------------------------------");
		stopwatch.stop();
		System.out.println("finished in milliseconds: "+stopwatch.elapsed(TimeUnit.MILLISECONDS));
	}
	
	//Throughput: 2,351 messages / second ~ 203,126,400 messages / day

}
