package rfx.server.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.Timer;
import java.util.TimerTask;

public class Utils {

	public static void sleep(long time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {			
		}
	}
	
	public static void exit(){
		System.exit(1);
	}
	
	public static void exit(long delay){
		Timer timer = new Timer(); 
		timer.schedule(new TimerTask() {			
			@Override
			public void run() {
				Utils.exit();
			}
		}, delay);
	}
	
	public static String exec(String cmd) {
		String pid = ManagementFactory.getRuntimeMXBean().getName();
		System.out.println(pid);
				
		String rs = StringPool.BLANK;
		Process p;
		try {
			p = Runtime.getRuntime().exec(cmd);
			p.waitFor();			
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream())); 
			StringBuffer sb = new StringBuffer(); 
			String line = reader.readLine();
			sb.append(line);
			while (line != null) {
				if(line != null){
					System.out.println(line);
					line = reader.readLine();
					sb.append(line);
				}				
			}
			rs = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		return rs;
	}
	
	
}
