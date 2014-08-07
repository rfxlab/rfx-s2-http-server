package rfx.server.util.cache;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import rfx.server.util.StringUtil;
import rfx.server.util.memcache.MemcacheCommand;

import com.google.gson.Gson;

public class MemcacheLoadingImpl extends NoSqlCacheLoading{
	String poolname;
	
	public MemcacheLoadingImpl(String poolname) {
		if(StringUtil.isEmpty(poolname)){
			throw new IllegalArgumentException("Memcache poolname can NOT Empty");
		}
		this.poolname = poolname;
	}
	
	public boolean flush(){
		boolean rs = false;
		try {
			rs = (new MemcacheCommand<Boolean>(poolname) {
				@Override
				protected Boolean build() {
					try {
						return mcClient.flush().get();
					} catch (Exception e) {		}
					return false;
				}
			}).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	@Override
	public void invalidateAll() {
		flush();
	}
	
	@Override
	public Object get(String key) throws ExecutionException {
		String json = null;
		try {
			json = (new MemcacheCommand<String>(poolname) {
				@Override
				protected String build() {
					return String.valueOf(mcClient.get(key));
				}
			}).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	@Override
	public void put(String key, Object obj) {
		try {
			(new MemcacheCommand<Boolean>(poolname) {
				@Override
				protected Boolean build() {
					mcClient.set(key, 10000, new Gson().toJson(obj));
					return true;
				}
			}).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public long size() {		
		try {
			return (new MemcacheCommand<Long>(poolname) {
				@Override
				protected Long build() {
					long size = StringUtil.safeParseLong(mcClient.getStats().values().iterator().next().get("total_items"));
					return size;
				}
			}).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
