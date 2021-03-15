import java.util.LinkedList;

public class LEdge{
	private LNode source;
	private LNode target;
	
	final String type = "line";
	final String arrow = "last";
	final String stipple = "Solid";
	final String lineWidth = "1.0000000000";
	final String fill = "#000000";
	private LinkedList<Point> bends; 

	public LEdge(LNode source,LNode target){
		this.source=source;
		this.target=target;
		bends = new LinkedList<>();
	}
	public void addBend(double x, double y){
		Point p = new Point(x,y);
		this.bends.add(p);
	}
	public LinkedList<Point> getbends(){return this.bends;}
	public LNode getsource(){return source;}
	public LNode gettarget(){return target;}
	public int getsourceId(){return source.getId();}
	public int gettargetId(){return target.getId();}
	
	public String toString(){
		String edge="\tedge\n\t[\n";
		edge+="\t\tsource\t"+this.source.getId()+"\n";
		edge+="\t\ttarget\t"+this.target.getId()+"\n";
		edge+="\t\tgraphics\n\t\t[\n";
		edge+="\t\t\ttype\t\""+this.type+"\"\n";
		edge+="\t\t\ttype\t\""+this.type+"\"\n";
		edge+="\t\t\tarrow\t\""+this.arrow+"\"\n";
		edge+="\t\t\tstipple\t\""+this.stipple+"\"\n";
		edge+="\t\t\tlineWidth\t"+this.lineWidth+"\n";
		
		if(bends.size()!=0){
			edge+="\t\t\tLine [\n";
			edge+="\t\t\t\tpoint [ x "+source.getx()+" y "+source.gety()+ " ]\n";
			for(Point p:bends){
				edge+="\t\t\t\tpoint [ x "+p.getx()+" y "+p.gety()+ " ]\n";
			}
			edge+="\t\t\t\tpoint [ x "+target.getx()+" y "+target.gety()+ " ]\n";
			edge+="\t\t\t]\n";
		}
		
		
		edge+="\t\t\tfill\t\""+this.fill+"\"\n";
		edge+="\t\t]\n";
		edge+="\t]\n";
		
		return edge;
	}
}