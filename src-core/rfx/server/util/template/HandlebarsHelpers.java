package rfx.server.util.template;

import java.io.IOException;

import rfx.server.util.StringPool;
import rfx.server.util.StringUtil;
import rfx.server.util.Utils;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public class HandlebarsHelpers {
	public static final String OPERATOR_EQUALS = "==";    
    public static final String OPERATOR_NOT_EQUALS = "!=";    
    public static final String OPERATOR_NotInList = "NotInList";
    public static final String OPERATOR_InList = "InList";
	
	public static void register(Handlebars handlebars){
		handlebars.registerHelper("ifCond", new Helper<String>() {
			@Override
			public CharSequence apply(String param0, Options options)	throws IOException {				
				if(param0 != null){
					String operator = options.param(0);
					String param1 = options.param(1);
					String[] items;
					switch (operator) {
						case OPERATOR_EQUALS:
							return (param0.equals(param1)) ? options.fn(this) : options.inverse(this);
						case OPERATOR_NOT_EQUALS:
							return (! param0.equals(param1)) ? options.fn(this) : options.inverse(this);
						case OPERATOR_NotInList:							
							items = param1.split(StringPool.SEMICOLON);
							for (String item : items) {
								if(param0.equals(item.trim())){
									return options.inverse(this);	
								}
							}
							return options.fn(this);
						case OPERATOR_InList:							
							items = param1.split(StringPool.SEMICOLON);
							for (String item : items) {
								if(param0.equals(item.trim())){									
									return options.fn(this);
								}
							}
							return options.inverse(this);
						default:
							break;
					}	
				}
				return options.inverse(this);			
			}			
		});
					
		handlebars.registerHelper("ifExist", new Helper<Object>() {
			@Override
			public CharSequence apply(Object param0, Options options)	throws IOException {				
				if(StringUtil.isNotEmpty(param0)){
					return (! param0.toString().equals("0")) ? options.fn(this) : options.inverse(this);	
				}
				return options.inverse(this);			
			}
		});
		
		handlebars.registerHelper("ifHasData", new Helper<Object>() {
			@Override
			public CharSequence apply(Object param0, Options options)	throws IOException {				
				if(StringUtil.isNullOrEmpty(param0)){					
					return options.inverse(this);
				}
				return options.fn(this);
			}
		});
		
		handlebars.registerHelper("randomInteger", new Helper<Object>() {
			@Override
			public CharSequence apply(Object param0, Options options)	throws IOException {
				int min = 1;
				int max =  Integer.MAX_VALUE;
				if(StringUtil.isNotEmpty(param0)){
					min= StringUtil.safeParseInt(param0+"",1);
				}
				if(options.params.length == 1){
					max = options.param(0);
				}
				int r = Utils.randInt(min, max);
				return String.valueOf(r);			
			}
		});
	}
}
