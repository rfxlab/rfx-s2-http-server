package rfx.server.configs;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import rfx.server.util.FileUtils;
import rfx.server.util.LogUtil;
import rfx.server.util.StringPool;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class HttpServerConfigs implements Serializable {
	
	static HttpServerConfigs _instance;

	private static final long serialVersionUID = 4936959262031389418L;
	
	private int port = 8080;
	private String ip = "127.0.0.1";
	private int websocketEnable = 0;
	
	private String secretKey;
	private String cookieDomain = "";
	private String defaultPartitioner = "";
	private String accessLogFileName = "access.log";
	private String debugLogFolderPath = "";
	private int accessLogEnable = 0;
	private int writeKafkaLogEnable = 1;
	private int debugModeEnabled = 0;
	private int cacheHttpMaxAge = 7200;
	Map<String,Map<String,String>> kafkaProducerList;
	int numberBatchJob = 20;
	int timeSendKafkaPerBatchJob = 4000;
	int sendKafkaThreadPerBatchJob = 3;
	int sendKafkaMaxRetries = 100000; 
	int kafkaProducerAsyncEnabled = 1;
	int kafkaProducerAckEnabled = 1;
	
	int urlApiGenderDetectionEnabled = 1;
	String urlApiGenderDetection;
	
	String defaultLogHandlerClassPath;
	
	int genderCacheTimeout = 12;//minutes
	int genderCacheMaxSize = 12000000;//unique visitors
	
	public String getUrlApiGenderDetection() {
		return urlApiGenderDetection;
	}
	
	public int getKafkaProducerAsyncEnabled() {
		return kafkaProducerAsyncEnabled;
	}

	public void setKafkaProducerAsyncEnabled(int kafkaProducerAsyncEnabled) {
		this.kafkaProducerAsyncEnabled = kafkaProducerAsyncEnabled;
	}

	public int getKafkaProducerAckEnabled() {
		return kafkaProducerAckEnabled;
	}

	public void setKafkaProducerAckEnabled(int kafkaProducerAckEnabled) {
		this.kafkaProducerAckEnabled = kafkaProducerAckEnabled;
	}

	public int getSendKafkaMaxRetries() {
		return sendKafkaMaxRetries;
	}

	public void setSendKafkaMaxRetries(int sendKafkaMaxRetries) {
		this.sendKafkaMaxRetries = sendKafkaMaxRetries;
	}

	public int getNumberBatchJob() {
		return numberBatchJob;
	}

	public void setNumberBatchJob(int numberBatchJob) {
		this.numberBatchJob = numberBatchJob;
	}

	public String getAccessLogFileName() {
		return accessLogFileName;
	}

	public int getAccessLogEnable() {
		return accessLogEnable;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public int getWriteKafkaLogEnable() {
		return writeKafkaLogEnable;
	}

	public void setWriteKafkaLogEnable(int writeKafkaLogEnable) {
		this.writeKafkaLogEnable = writeKafkaLogEnable;
	}
	
	public String getDebugLogFolderPath() {
		return debugLogFolderPath;
	}

	public void setDebugLogFolderPath(String errorLogFolderPath) {
		this.debugLogFolderPath = errorLogFolderPath;
	}

	public int getDebugModeEnabled() {
		return debugModeEnabled;
	}

	public void setDebugModeEnabled(int debugModeEnabled) {
		this.debugModeEnabled = debugModeEnabled;
	}

	public HttpServerConfigs() {
		super();
	}
	
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

	public Map<String, Map<String, String>> getKafkaProducerList() {
		if(kafkaProducerList == null){
			kafkaProducerList = new HashMap<>();
		}
		return kafkaProducerList;
	}

	public void setKafkaProducerList(
			Map<String, Map<String, String>> kafkaProducerList) {
		this.kafkaProducerList = kafkaProducerList;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();

		return s.toString();
	}
	
	
	public static final HttpServerConfigs load(String configPath) {
		if (_instance == null) {
			try {
				String json = FileUtils.readFileAsString(configPath);
				_instance = new Gson().fromJson(json, HttpServerConfigs.class);
				LogUtil.info("HttpServerConfigs loaded and create new instance from "+ StringPool.S2_CONFIG_FILE);
			} catch (Exception e) {
				if (e instanceof JsonSyntaxException) {
					e.printStackTrace();
					System.err.println("Wrong JSON syntax in file "+StringPool.S2_CONFIG_FILE);
				} else {
					e.printStackTrace();
				}
			}
		}
		return _instance;
	}
	
	public static final HttpServerConfigs load() {
		return load(StringPool.S2_CONFIG_FILE);
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public void setCookieDomain(String cookieDomain) {
		this.cookieDomain = cookieDomain;
	}
	public String getCookieDomain() {
		return cookieDomain;
	}
	public String getDefaultPartitioner() {
		return defaultPartitioner;
	}

	public int getCacheHttpMaxAge() {
		return cacheHttpMaxAge;
	}

	public void setCacheHttpMaxAge(int cacheHttpMaxAge) {
		this.cacheHttpMaxAge = cacheHttpMaxAge;
	}

	public int getSendKafkaThreadPerBatchJob() {
		return sendKafkaThreadPerBatchJob;
	}

	public void setSendKafkaThreadPerBatchJob(int sendKafkaThreadPerBatchJob) {
		this.sendKafkaThreadPerBatchJob = sendKafkaThreadPerBatchJob;
	}

	public int getTimeSendKafkaPerBatchJob() {
		return timeSendKafkaPerBatchJob;
	}

	public void setTimeSendKafkaPerBatchJob(int timeSendKafkaPerBatchJob) {
		this.timeSendKafkaPerBatchJob = timeSendKafkaPerBatchJob;
	}

	public int getGenderCacheTimeout() {
		if(genderCacheTimeout == 0){
			genderCacheTimeout = 12;
		}
		return genderCacheTimeout;
	}

	public void setGenderCacheTimeout(int genderCacheTimeout) {
		this.genderCacheTimeout = genderCacheTimeout;
	}

	public int getGenderCacheMaxSize() {
		if(genderCacheMaxSize == 0){
			genderCacheMaxSize = 12000000;
		}
		return genderCacheMaxSize;
	}

	public void setGenderCacheMaxSize(int genderCacheMaxSize) {
		this.genderCacheMaxSize = genderCacheMaxSize;
	}

	public boolean isWebsocketEnable() {
		return websocketEnable == 1;
	}

	public int getUrlApiGenderDetectionEnabled() {
		return urlApiGenderDetectionEnabled;
	}

	public String getDefaultLogHandlerClassPath() {
		return defaultLogHandlerClassPath;
	}

	
	
}