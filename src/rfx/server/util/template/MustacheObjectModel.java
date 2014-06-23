package rfx.server.util.template;

import java.util.HashMap;
import java.util.Map;

import rfx.server.http.BaseModel;

public class MustacheObjectModel extends HashMap<String, Object> implements BaseModel{

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

	@Override
	public void freeResource() {
		clear();
	}

	@Override
	public boolean isOutputableText() {		
		return false;
	}	
	
}
