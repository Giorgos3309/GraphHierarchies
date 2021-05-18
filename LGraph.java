import java.util.LinkedList;

public class LGraph{
	private LinkedList<LEdge> edges;
	private LNode[] nodes;
	public LGraph(int vertices){
		this.edges = new LinkedList<LEdge>();
		this.nodes = new LNode[vertices];
	}
	public void addnode(LNode n){
		this.nodes[n.getId()] = n;
	}
	public void addnode_c(int id,double x,double y,String label){
		LNode ln = new LNode(id,x,y,label); 
		this.nodes[id] = ln;
	}
	public LNode getnode(int id){return nodes[id];}
	public LNode[] getnodes(){return nodes;}
	public void addedge(LEdge e){
		this.edges.add(e);
	}
	public int getnodessize(){return this.nodes.length;}
	public int getedgessize(){return this.edges.size();}
	public LinkedList<LEdge> getEdges(){return this.edges;}
	public String toString_n(int i){
		String node=this.nodes[i].toString();
		return node;
	}
	public String toString_e(int i){
		String e=this.edges.get(i).toString();
		return e;
	}
}