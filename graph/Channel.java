package graph;

import java.io.IOException;
import java.util.LinkedList;

public class Channel {
	private int id;
	private LinkedList<IVertex> vertices = new LinkedList<IVertex>();
	private SimpleGraph G;
	
	public Channel (SimpleGraph G, int id){
		this.G=G;
		this.id = id;
	}
	
	public LinkedList<IVertex> getVertices(){
		return this.vertices;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void addVertex(IVertex v) throws IOException {
		//boolean b = true;
		//if(vertices.isEmpty()) {
		vertices.add(v);
		//}else {
			//IVertex w = vertices.getLast();
			//if(G.BFS(w, v)) {
				//vertices.addLast(v);
			//}else {
				//b=false;
				////System.out.println("Vertex "+ v.getId() +" cannot be added to Channel "+this.id);
			//}
		//}   
		//return b;
	}       
		
}
