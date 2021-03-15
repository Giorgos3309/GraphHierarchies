package graph;
import java.util.LinkedList;

public class Vertex implements IVertex{

	private static long nextId=0;
	private LinkedList<IVertex> adjacentSources = new LinkedList<IVertex>();
	private LinkedList<IVertex> adjacentTargets = new LinkedList<IVertex>();
	private LinkedList<IEdge> incidentEdges= new LinkedList<IEdge>();
		
	
	private static long nextId(){
		return ++nextId;
	}
	
	private long id;
	
	private double weight;
	
	private String label;
	
	private boolean visited=false;
	
	
	public Vertex(){
		id = nextId();
		label = "";
		weight= 0;
	}
	
	public Vertex(String label){
		this();
		this.label = label;
	}
	
	
	public void setId(long id){this.id=id;}
	public Vertex(int id, String label){
		this.id=id;
		this.label = label;
	}
	
	
	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void setLabel(String l) {
		label = l;
	}

	@Override
	public double getWeight() {
		return weight;
	}

	@Override
	public void setWeight(double w) {
		weight=w;
	}
	
	public String Text() {
		return this.id+this.label;
	}
	
	public boolean getVisited() {
		return this.visited;
	}
	
	public void setVisited(boolean b) {
		this.visited=b;
	}
	
	public void setAdjacentSources(LinkedList<IVertex> adjacentSources) {
		this.adjacentSources=adjacentSources;
	}
	
	public void setAdjacentTargets(LinkedList<IVertex> adjacentTarget) {
		this.adjacentTargets=adjacentTarget;
	}
	
	public LinkedList<IVertex> getAdjacentSources(){
		return this.adjacentSources;
	}
	
	public LinkedList<IVertex> getAdjacentTargets(){
		return this.adjacentTargets;
	}
	
	public void setIncidentEdges(LinkedList<IEdge> incidentEdges) {
		this.incidentEdges=incidentEdges;
	}
	public LinkedList<IEdge> getIncidentEdges(){
		return this.incidentEdges;
	}

}
