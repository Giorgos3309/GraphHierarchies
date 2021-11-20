package graph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class SimpleGraph extends AbstractSimpleGraph{
	private String description;
	//private int[][] adjacencyMatrix; 
	
	//public void setAdjacencyMatrix(int[][]adjacencyMatrix) {
		//this.adjacencyMatrix=adjacencyMatrix;
	//}
	
	//public int[][] getAdjacencyMatrix() {
		//return this.adjacencyMatrix;
	//}
	
	public void setAdjacency() {
		this.resetAdjacency();
		for(IEdge e: this.edges) {
			IVertex v = e.getSource();
			IVertex w = e.getTarget();
			LinkedList<IVertex>targetsOfv= v.getAdjacentTargets();
			LinkedList<IVertex>sourcesOfw= w.getAdjacentSources();
			LinkedList<IEdge>EdgesOfv= v.getIncidentEdges();
			LinkedList<IEdge>EdgesOfw= w.getIncidentEdges();
			EdgesOfv.add(e);
			EdgesOfw.add(e);
			v.setIncidentEdges(EdgesOfv);
			w.setIncidentEdges(EdgesOfw);
			//if(!targetsOfv.contains(w)) {
			targetsOfv.add(w);
			v.setAdjacentTargets(targetsOfv);
			//}
			//if(!sourcesOfw.contains(v)) {
			sourcesOfw.add(v);
			//}
			w.setAdjacentSources(sourcesOfw);
		}
	}
	
	public void setDescription(String s) {
		this.description=s;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	private void resetAdjacency() {
		for(IVertex v: this.vertices) {
			v.setAdjacentSources(new LinkedList<IVertex>());
			v.setAdjacentTargets(new LinkedList<IVertex>());
			v.setIncidentEdges(new LinkedList<IEdge>());
		}
	}

	@Override
	public boolean add(IEdge e) /*throws Exception*/ {
		//if(areAdjacents(e.getSource(), e.getTarget())){
			//throw new Exception("Cannot add multiple edges");
		//}else{
			return super.add(e);
		//}
	}
	
	public int computeWidth() throws Exception{
		SimpleGraph Gt= new SimpleGraph();
		for(IVertex v: this.vertices) {
			Gt.add(v);
		}
		for(IEdge e: this.edges) {
			Gt.add(e);
		}
		//System.out.print("creato Gt");
		Gt.setAdjacency();
		LinkedList<IVertex> sources = Gt.findSources();
		int width =sources.size();
		while(!Gt.vertices.isEmpty()) {
			for(IVertex v: sources) {
				Gt.remove(v);
				for(IEdge e: v.getIncidentEdges()) {
					Gt.remove(e);
				}
			}
			Gt.setAdjacency();
			sources=Gt.findSources();
			//System.out.println("size: "+ sources.size());
			//System.out.print("vertices: ");
			//for(IVertex v: sources) {
				//System.out.print(v.getId()+" ");
			//}
			//System.out.println();
			if(sources.size()>width) {
				width=sources.size();
			}
		}
		return width;
	}
	
	public LinkedList<IVertex> findSources() {
		LinkedList<IVertex>sources = new LinkedList<IVertex>();
		for(IVertex v: this.vertices) {
			if(v.getAdjacentSources().size()==0){
				sources.add(v);
			}
		}
		return sources;
	}
	
	public void transitiveClosure() throws Exception{
		for(IVertex v1: this.getVertices()) {
			for(IVertex v2: this.getVertices()) {
				if(v1.getId()<v2.getId()) {
					if(!this.getAdjacentsOf(v1).contains(v2)) {
						if(this.BFS(v1, v2)) {
							this.add(new Edge(v1,v2));
						}
					}
				}
			}
		}
	}
	
	public boolean BFS(IVertex start, IVertex end){
		this.setAdjacency();
		LinkedList<IVertex> queue = new LinkedList<IVertex>();
		//Lista contenente i nodi esplorati
		Set<IVertex> visited = new HashSet<IVertex>();
		visited.add(start);
		queue.addLast(start);
		LinkedList<IVertex> list = new LinkedList<IVertex>();
		LinkedList<IEdge> exploredEdges = new LinkedList<IEdge>();
		while (!queue.isEmpty()) {
			IVertex v = queue.removeFirst();
			if(!v.equals(end)) {
				list.add(v);
				for (IVertex c : getAdjacentsOf(v)) {
					//System.out.println(c.getId()+" "+c.getLabel());
					if (visited.contains(c))
						continue;
					IEdge e = null;
					for(IEdge a: v.getIncidentEdges()) {
						if(a.getSource().equals(c)||a.getTarget().equals(c)){
							e=a;
						}
					}
					if((e.getSource().equals(v)&&e.getFlow()==0) || (e.getTarget().equals(v)&&e.getFlow()==1)) {
						exploredEdges.add(e);
						visited.add(c);
						queue.addLast(c);
					}
				}
			}else {
				queue.clear();
			}
		}
		boolean b =false;
		if(visited.contains(end)) {
			b=true;
		}
		return b;
	}

}
