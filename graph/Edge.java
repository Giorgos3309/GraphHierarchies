package graph;

public class Edge implements IEdge{
	
	private static long nextId=0;
	private int flow =0;
	
	private static long nextId(){
		return ++nextId;
	}
	
	private long id;
	
	private IVertex source,target;
	
	private boolean isDirected;
	
	private double weight;
	
	public Edge(IVertex source, IVertex target){
		id = nextId();
		this.source = source;
		this.target = target;
		isDirected = false;
		weight=0;
	}
	
	public Edge(IVertex source, IVertex target, boolean isDirected){
		this(source, target);
		this.isDirected = isDirected;
	}

	@Override
	public IVertex getOpposite(IVertex v) {
		if(source == v)
			return target;
		if(target == v)
			return source;
		return null;
	}

	@Override
	public boolean isDirected() {
		return isDirected;
	}

	@Override
	public IVertex getSource() {
		return source;
	}

	@Override
	public IVertex getTarget() {
		return target;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public double getWeight() {
		return weight;
	}

	@Override
	public void setWeight(double w) {
		weight=w;
	}
	
	@Override
	public void setFlow() {
		if(this.flow==0) {
			this.flow=1;
		}else {
			this.flow=0;
		}
	}
	
	@Override
	public int getFlow() {
		return this.flow;
	}
	
	public String Text() {
		return "("+this.getSource().Text()+","+this.getTarget().Text()+")";
	}


}
