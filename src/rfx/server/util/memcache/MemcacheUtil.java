package rfx.server.util.memcache;

import java.io.IOException;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.KetamaConnectionFactory;
import net.spy.memcached.MemcachedClient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MemcacheUtil {
	//TODO
	
	public static void main(String[] args) {
        try {
            // init memcache
            String memcache = "14.0.20.169:11213";
            KetamaConnectionFactory con = new KetamaConnectionFactory();
            System.out.println("KetamaConnectionFactory");
            
            MemcachedClient _mc = new MemcachedClient(con, AddrUtil.getAddresses(memcache));
            String key_ = "w1372135767";
            String json = _mc.get(key_).toString();
			//            byte[] data = (byte[]) _mc.get(key_);
//            String strData = new String(data);
            System.out.println(json);
            JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
            System.out.println(jsonObject.get("url").getAsString());
            _mc.shutdown();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}
}
