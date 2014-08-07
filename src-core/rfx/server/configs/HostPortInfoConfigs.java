package rfx.server.configs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import rfx.server.util.StringUtil;

public class HostPortInfoConfigs {
	
	static Map<String, ServerInfo> pools = new HashMap<>();
	
	public final static class ServerInfo {
		public String host;
		public int port;
		
		public ServerInfo(String host, int port) {
			super();
			this.host = host;
			this.port = port;
		}		
		@Override
		public String toString() {			
			return StringUtil.toString(host,":",port);
		}
	}
	
	protected static HostPortInfoConfigs _instance = null;
	
	public static HostPortInfoConfigs theInstance(){
		if(_instance == null){
			_instance = (new HostPortInfoConfigs()).loadConfigFile();
		}
		return _instance;
	}
	
	HostPortInfoConfigs loadConfigFile(){
		Properties props = new Properties();
		InputStream input = null;			 
		try {			 
			input = new FileInputStream(ConfigManager.HOST_PORT_CONFIG_FILE);
	 
			// load a properties file
			props.load(input);
			
			Enumeration<?> e = props.propertyNames();
			while (e.hasMoreElements()) {
				String key = e.nextElement().toString();
								
				String value = props.getProperty(key);
				String[] toks = value.split(":");
				
				String host = toks[0];
				int port = StringUtil.safeParseInt(toks[1]);
				pools.put(key, new ServerInfo(host, port));				
			}
	  
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return this;
	}
	
	public static ServerInfo getServerInfo(String key){
		theInstance();
		return pools.get(key);
	}
}
