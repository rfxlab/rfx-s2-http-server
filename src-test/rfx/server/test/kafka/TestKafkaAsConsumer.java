package rfx.server.test.kafka;

import java.nio.charset.Charset;

import kafka.message.MessageAndOffset;

import org.junit.Test;

public class TestKafkaAsConsumer {
	
	public static String messageToString(MessageAndOffset msg){		
		StringBuffer response = new StringBuffer();
		Charset charset = Charset.forName("UTF-8");
		response.append( charset.decode( msg.message().payload() ) );		
		return response.toString();
	}
	
	@Test 
	public void timeseriesQuery(){
		//offsetList1 = ZRANGEBYSCORE s1 1 2 LIMIT 0 1
		//offsetList2 = ZRANGEBYSCORE s1 (2 3 LIMIT 0 1
		//offsetT1_T2 = offsetList1 + offsetList2 
		//SimpleConsumer, startOffset: offsetT1_T2[0] , maxsize: offsetT1_T2[1] - offsetT1_T2[0]
	}

	@Test
	public void consumeData() {
		
//		Pentium(R) Dual-Core CPU T4300 @ 2.10GHz × 2 
//		kafka message fetched: 388.308 processingTime(SECONDS): 585 (~9.75 minutes) ~ AVG: 664 messages/second
		
//		Intel® Core™2 Duo CPU E7500 @ 2.93GHz × 2 
//		kafka message fetched: 388.308 processingTime(SECONDS): 568 (~9.46 minutes) ~ AVG: 684 messages/second
	}
}
