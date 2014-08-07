package sample.http;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import rfx.server.configs.HttpServerConfigs;
import rfx.server.http.HttpServer;
import sample.pollapp.cache.CacheManagerForAllDAO;

public class SampleHttpServer {

	public static void main(String[] args) throws Exception {
		int poolSize = 10000;
	    boolean cacheTemplate = true;
	    boolean debug = false;
		
		CommandLineParser parser = new PosixParser();
		// create the Options
		Options options = new Options();		
		options.addOption( "d", "debug", false, "Enable server debug-mode" );
		options.addOption( "c", "cached", false, "Auto-caching output templates in local Java Memory");
		options.addOption("lcf","load-configs-from", true, "load from specified config-folder name");

		if(args.length == 0){
			args = new String[]{ "-c" , "--load-configs-from=configs/" };	
		}

		try {
		    // parse the command line arguments
		    CommandLine line = parser.parse( options, args );
		    HelpFormatter formatter = new HelpFormatter();
		    formatter.printHelp( "./start-server", options );
		  			   
		    if(line.hasOption("load-configs-from")){
		    	System.out.println( line.getOptionValue( "load-configs-from" ) );
		    	//TODO
		    } else {
		    	System.out.println( "use default configs" );
		    }
		    
		    if(line.hasOption("cached")){
		    	System.out.println("cached");
		    	cacheTemplate = true;
		    } else {
		    	System.out.println("no cached");
		    	cacheTemplate = false;
		    }
		    
		    if(line.hasOption("debug")){
		    	System.out.println("debug");
		    	debug = true;
		    } else {
		    	System.out.println("no debug");
		    	debug = false;
		    }
		}
		catch( ParseException exp ) {
		    System.out.println( "Unexpected exception:" + exp.getMessage() );
		}    	
    	
    	HttpServerConfigs configs = HttpServerConfigs.load();
    	int customPort = 0;    	
    	
        int port = configs.getPort();
        if (customPort != 0 ) {
        	port = customPort;
        }
        String ip = configs.getIp();  
        
        System.out.println("-------------- SAMPLE HTTP SERVER with HOST["+ip+":"+port+"] --------------");        
        
        //MemoryManagementUtil.startMemoryUsageTask();
        String publicClasspath = "sample";       
        boolean cacheAllCompiledTemplates = true;
        
        HttpServer.setDebug(debug);
        CacheManagerForAllDAO.init();
        new HttpServer(ip,port, configs.getPrivatePort(), poolSize,cacheAllCompiledTemplates).run(false,publicClasspath);
	}
}
