package graph;

public interface IEdge {
	
	long getId();

	IVertex getSource();
	
	IVertex getTarget();
	
	boolean isDirected();
	
	IVertex getOpposite(IVertex v);
	
	double getWeight();
	
	void setFlow();
	
	int getFlow();
	
	void setWeight(double w);
	
	String Text();
	
}
