package graph;

import java.util.LinkedList;

public interface IVertex {
	
	long getId();
	void setId(long id);
	double getWeight();
	
	void setWeight(double w);
	
	String getLabel();
	
	void setLabel(String l);
	
	boolean getVisited();
	
	void setVisited(boolean b);
	
	void setAdjacentTargets(LinkedList<IVertex> adjacentTarget);
	void setAdjacentSources(LinkedList<IVertex> adjacentSources);
	LinkedList<IVertex> getAdjacentSources();
	LinkedList<IVertex> getAdjacentTargets();
	
	void setIncidentEdges(LinkedList<IEdge> incidentEdges);
	LinkedList<IEdge> getIncidentEdges();
	
	String Text();

}
