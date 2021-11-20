package graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public interface IGraph {
	
	Collection<IVertex> getVertices();
	
	Collection<IEdge> getEdges();
	
	int getDegreeOf(IVertex v);
	
	Collection<IEdge> getIncidentEdgesOf(IVertex v);
	
	Collection<IVertex> getAdjacentsOf(IVertex v);
	
	boolean areAdjacents(IVertex v, IVertex w);
	
	void add(IVertex v);
	
	boolean add(IEdge e) throws Exception;
	
	void remove(IVertex v);
	
	void remove (IEdge e);
	
	LinkedList<IVertex> bfsVisit(IVertex start);

	Iterator<IVertex> dfsVisit(IVertex start);
	
	IEdge getEdge(IVertex v1, IVertex v2);
	
	void setDescription(String s);
	
	String getDescription();
}
