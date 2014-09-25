package rfx.server.test;

import java.io.File;
import java.util.concurrent.ConcurrentNavigableMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;

public class TestMapDB {

	public static void main(String[] args) {
		// configure and open database using builder pattern.
	    // all options are available with code auto-completion.
		File dir = new File("data/mapdb");
		if( ! dir.isDirectory() ){
			dir.mkdir();
		}
		
		File file = new File(dir.getAbsolutePath()+"/testdb");
	    DB db = DBMaker.newFileDB(file)
	               .closeOnJvmShutdown()
	               .encryptionEnable("password")
	               .make();

	    // open existing an collection (or create new)
	    ConcurrentNavigableMap<Integer,String> map = db.getTreeMap("collectionName");

	    map.put(1, "one");
	    map.put(2, "two");
	    // map.keySet() is now [1,2]

	    db.commit();  //persist changes into disk

	    map.put(3, "three");
	    // map.keySet() is now [1,2,3]
	    db.rollback(); //revert recent changes
	    // map.keySet() is now [1,2]

	    db.close();

	}

}
