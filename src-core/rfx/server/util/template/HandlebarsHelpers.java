package rfx.server.util.template;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import rfx.server.util.StringPool;
import rfx.server.util.StringUtil;
import rfx.server.util.Utils;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public class HandlebarsHelpers {
	public static final String OPERATOR_EQUALS = "==";    
    public static final String OPERATOR_NOT_EQUALS = "!=";
    public static final String OPERATOR_LARGER_THAN = ">";
    public static final String OPERATOR_LARGER_THAN_OR_EQUAL = ">=";
    public static final String OPERATOR_LESS_THAN = "<";
    public static final String OPERATOR_LESS_THAN_OR_EQUAL = "<=";    
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
						{
							items = param1.split(StringPool.SEMICOLON);
							for (String item : items) {
								if(param0.equals(item.trim())){
									return options.inverse(this);	
								}
							}
							return options.fn(this);
						}
						case OPERATOR_InList:
						{
							items = param1.split(StringPool.SEMICOLON);
							for (String item : items) {
								if(param0.equals(item.trim())){									
									return options.fn(this);
								}
							}
							return options.inverse(this);
						}
						case OPERATOR_LARGER_THAN:
						{
							int p0 = StringUtil.safeParseInt(param0);
							int p1 = StringUtil.safeParseInt(param1);
							return (p0 > p1) ? options.fn(this) : options.inverse(this);
						}							
						case OPERATOR_LARGER_THAN_OR_EQUAL:
						{
							int p0 = StringUtil.safeParseInt(param0);
							int p1 = StringUtil.safeParseInt(param1);
							return (p0 >= p1) ? options.fn(this) : options.inverse(this);
						}
						case OPERATOR_LESS_THAN:
						{
							int p0 = StringUtil.safeParseInt(param0);
							int p1 = StringUtil.safeParseInt(param1);
							return (p0 < p1) ? options.fn(this) : options.inverse(this);
						}
						case OPERATOR_LESS_THAN_OR_EQUAL:
						{
							int p0 = StringUtil.safeParseInt(param0);
							int p1 = StringUtil.safeParseInt(param1);
							return (p0 <= p1) ? options.fn(this) : options.inverse(this);
						}
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
		
		handlebars.registerHelper("ifListHasData", new Helper<Object>() {
			@Override
			public CharSequence apply(Object param0, Options options)	throws IOException {				
				if(param0 != null){
					List list = (List) param0;
					return (list.size()>0 ) ? options.fn(this) : options.inverse(this);
				}
				return options.inverse(this);
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
		
		handlebars.registerHelper("eachInMap", new Helper<Object>() {
			@Override
			public CharSequence apply(Object param0, Options options)	throws IOException {				
				if(param0 instanceof Map){					
					@SuppressWarnings("unchecked")
					Map<String,Object> map = (Map<String, Object>) param0;
					StringBuilder out = new StringBuilder();
					map.forEach(new BiConsumer<String,Object>() {
						@Override
						public void accept(String key, Object value) {
							Map<String,Object> context = new HashMap<String, Object>(2);
							context.put(StringPool.KEY, key);
							context.put(StringPool.VALUE, value);
							try {
								String s = options.fn(context).toString();								
								out.append(s);
							} catch (Exception e) {}
							context.clear();
						}
					});
					return out.toString();
				}
				return StringPool.BLANK;
			}
		});
	}
}
