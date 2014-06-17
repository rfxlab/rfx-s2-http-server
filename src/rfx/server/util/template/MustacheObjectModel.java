package rfx.server.util.template;

import java.util.HashMap;
import java.util.Map;

public class MustacheObjectModel extends HashMap<String, Object>{

	private static final long serialVersionUID = 5072021270346354454L;
	
	public MustacheObjectModel(){}

	public MustacheObjectModel(int numberField){
		super(numberField);
	}
	
	public MustacheObjectModel(Map<String, Object> obj){
		super(obj);
	}
	
	public void set(String name, Object val){
		this.put(name, val);
	}	
	
}
