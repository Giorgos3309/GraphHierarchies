import org.json.*;

class JSONinfo{
	JSONObject info;
	public JSONinfo(){
		this.info = new JSONObject();
	}
	public void addGraphMetrics(JSONObject j){
		info.put(j.getString("filename") , j);
	}
	public String toString(){
		return info.toString();
	}
	public static JSONObject create(String filename,String AbsolutePath,int nodes,int edges,int width){
		JSONObject tmp = new JSONObject();
		tmp.put("filename", filename);
		tmp.put("AbsolutePath", AbsolutePath );
		tmp.put("nodes",nodes);
		tmp.put("edges",edges);
		tmp.put("width",width);
		tmp.put("heuristics",new JSONObject());
		return tmp;
	}
	
	public static void addTests(JSONObject j , Test t){
		JSONObject tmp = new JSONObject(); 
		tmp.put("width", t.getwidth() );
		tmp.put("duration" , t.getduration() );
		tmp.put("type" , t.gettype() );
		tmp.put("depth" , t.getdepth() );
		
		((JSONObject)j.get("heuristics")).put("H_"+t.gettype()+"_"+t.getdepth(),tmp);
		
	}
}