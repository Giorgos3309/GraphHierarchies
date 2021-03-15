
public class LNode{
	private int id;
	private Point p;
	private String label;
	
	public final String template = "\"\"";
 
	public final double w = 40.0000000000;
	public final double h = 40.0000000000;
	public final String fill = "\"#EBE1E1\"";
	public final String fillbg = "\"#000000\"";
	public final String outline = "\"#000000\"";
	public final String pattern = "\"Solid\"";
	public final String stipple = "\"Solid\"";
	public final double lineWidth = 1.0000000000;
	public final String type = "\"Ellipse\"";
	
	public int getId(){return id;}
	public double getx(){return p.getx();}
	public double gety(){return p.gety();}
	public LNode(int id,double x,double y){
		this.id = id;
		this.p = new Point(x,y);
		this.label = "\""+id+"\"";
		//System.out.println("LNode("+x+","+y+")");
	}
	public String getlabel(){return label;}
	public String toString(){
		String node="";
		node+="\tnode\n\t[\n";
		node+=("\t\tid\t"+this.getId()+"\n");
		node+=("\t\ttemplate\t"+this.template+"\n");
		node+=("\t\tlabel\t"+this.label+"\n");
		
		node+=("\t\tgraphics\n\t\t[\n");
		node+=("\t\t\tx\t"+this.getx()+"\n");
		node+=("\t\t\ty\t"+this.gety()+"\n");
		node+=("\t\t\tw\t"+this.w+"\n");
		node+=("\t\t\th\t"+this.h+"\n");
		node+=("\t\t\tfill\t"+this.fill+"\n");
		node+=("\t\t\toutline\t"+this.outline+"\n");
		node+=("\t\t\tpattern\t"+this.pattern+"\n");
		node+=("\t\t\tstipple\t"+this.stipple+"\n");
		node+=("\t\t\tlineWidth\t"+this.lineWidth+"\n");
		node+=("\t\t\ttype\t"+this.type+"\n");
		node+=("\t\t]\n");
		node+=("\t]\n");
		
		
		return node;
	}
}