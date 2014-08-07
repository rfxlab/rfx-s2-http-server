package rfx.server.util.cache;

import rfx.server.util.StringPool;

public class CachableMethod {
	long expireAfter;
	String cachePoolName = StringPool.BLANK;
	int cacheType;
	String keyPrefix = StringPool.BLANK;
	
	public long getExpireAfter() {
		return expireAfter;
	}
	public void setExpireAfter(long expireAfter) {
		this.expireAfter = expireAfter;
	}
	public String getCachePoolName() {
		return cachePoolName;
	}
	public void setCachePoolName(String cachePoolName) {
		this.cachePoolName = cachePoolName;
	}
	public int getCacheType() {
		return cacheType;
	}
	public void setCacheType(int cacheType) {
		this.cacheType = cacheType;
	}
	public String getKeyPrefix() {
		return keyPrefix;
	}
	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}
	
}
