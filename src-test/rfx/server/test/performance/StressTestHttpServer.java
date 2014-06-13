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

import rfx.server.util.HttpClientUtil;

import com.google.common.base.Stopwatch;

public class StressTestHttpServer {
	static AtomicInteger validCount = new AtomicInteger(0);
	static AtomicInteger invalidCount = new AtomicInteger(0);
	static Stopwatch stopwatch = Stopwatch.createUnstarted();

	@Rule
	public ContiPerfRule i = new ContiPerfRule();

	
	@Test
	@PerfTest(invocations = 10000, threads = 200)
	@Required(max = 4000, average = 400)
	public void test() throws Exception {
		String url = "http://localhost:9090/server-info";
		String rs = HttpClientUtil.executeGet(url);

		if (rs.contains("Time:")) {
			//System.out.println(rs+" \n");
			validCount.incrementAndGet();
		} else {
			System.out.println(rs);
			invalidCount.incrementAndGet();
			throw new IllegalArgumentException("Bad response!");
		}
		
//		Throughput:	3,114 / s	
//		Min. latency:	0 ms	
//		Average latency:	190 ms	1,000 ms
//		Median:	172 ms	
//		90%:	366 ms	
//		Max latency:	5,288 ms	6,000 ms


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
