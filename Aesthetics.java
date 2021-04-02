// Java program to check if two given line segments intersect 
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Set;

class Aesthetics 
{ 
	Hierarchical pbf;
	private final int cross_e = 0;
	private final int path_e = 1;
	private final int path_tr_e = 2;
	private LinkedList<LinkedList<LineSegment>> linesegments;
	public Aesthetics(Hierarchical pbf){
		this.pbf = pbf;
		this.linesegments = new LinkedList<LinkedList<LineSegment>>();
		this.linesegments.add(new LinkedList<LineSegment>());
		this.linesegments.add(new LinkedList<LineSegment>());
		this.linesegments.add(new LinkedList<LineSegment>());
	}
	LinkedList<LineSegment> get_cross_ls(){return this.linesegments.get(cross_e);}
	LinkedList<LineSegment> get_path_ls(){return this.linesegments.get(path_e);}
	LinkedList<LineSegment> get_pathtr_ls(){return this.linesegments.get(path_tr_e);}
	
	LinkedList<LineSegment> bundling(LinkedList<LineSegment> ls){
		LinkedList<LineSegment> res = new LinkedList<LineSegment>();
		LineSegment newls;
		Set<LineSegment> visited = new HashSet<LineSegment>();
		for(LineSegment s1:ls){
			newls=s1;
			Point p1 = s1.getp1();
			Point q1 = s1.getp2(); 
			if(visited.contains(s1)){continue;}
			visited.add(s1);
			for(LineSegment s2:ls){
				if(visited.contains(s2)){continue;}
				Point p2 = s2.getp1(); 
				Point q2 = s2.getp2();
				if( needbundling(p1,q1,p2,q2) ){
					
					if(p1.getx()==p2.getx()	// 1st case of path tr. edges bundling and cross edges bundling
					   &&p1.gety()==p2.gety()
					   &&q1.getx()==q2.getx()
					   &&q1.gety()==q2.gety() )
					{				
						newls = new LineSegment( p1.getx() , p1.gety() , q1.getx() , q1.gety() );
						p1 = newls.getp1();
						q1 = newls.getp2(); 
						System.out.println("bundled1: (["+p1.getx()+","+p1.gety()+"] ,["+q1.getx()+","+q1.gety()+"]))");// (["+p2.getx()+","+p2.gety()+"] ,["+q2.getx()+","+q2.gety()+"])");
					}else if((p1.getx()==q1.getx())&&(p2.getx()==q2.getx())&&(p2.getx()==p1.getx())){ // vertical bundling of path tr. edges
						double x = p1.getx();
						double y1;
						double y2;
						if(p1.gety()<=p2.gety()){
							y1 = p1.gety();
							//y st. cord = p1.gety 
						}else{
							y1 = p2.gety();
							//y st. cord = p2.gety 
						}
						
						if(q1.gety()>=q2.gety()){
							y2 = q1.gety();
							//y f cord = q1.gety()
						}else{
							y2 = q2.gety();
							//y f cord = q2.gety()
						}
						newls = new LineSegment( x,y1,x,y2);
						p1 = newls.getp1();
						q1 = newls.getp2(); 
						System.out.println("bundled2: (["+p1.getx()+","+p1.gety()+"] ,["+q1.getx()+","+q1.gety()+"]))");
					}else{
						System.err.println("Aesthetics::bundling::error1");
						System.exit(0);
					}
					visited.add(s2);
				}
			}
			res.add(newls);
		}
		return res;
	}
	
	static boolean needbundling(Point p1, Point q1, Point p2, Point q2) 
	{ 
		// Find the four orientations needed for general and 
		// special cases 
		int o1 = orientation(p1, q1, p2); 
		int o2 = orientation(p1, q1, q2); 
		int o3 = orientation(p2, q2, p1); 
		int o4 = orientation(p2, q2, q1); 

		// General case 
		if (o1 != o2 && o3 != o4) 
			return false;
			
		// Special Cases 
		// p1, q1 and p2 are colinear and p2 lies on segment p1q1 
		if (o1 == 0 && onSegment(p1, p2, q1)) return true; 

		// p1, q1 and q2 are colinear and q2 lies on segment p1q1 
		if (o2 == 0 && onSegment(p1, q2, q1)) return true; 

		// p2, q2 and p1 are colinear and p1 lies on segment p2q2 
		if (o3 == 0 && onSegment(p2, p1, q2)) return true; 

		// p2, q2 and q1 are colinear and q1 lies on segment p2q2 
		if (o4 == 0 && onSegment(p2, q1, q2)) return true; 

		return false; // Doesn't fall in any of the above cases 
	} 
	
	/*static class Point 
	{ 
		int x; 
		int y; 

			public Point(int x, int y) 
			{ 
				this.x = x; 
				this.y = y; 
			} 
	};*/ 

	// Given three colinear points p, q, r, the function checks if 
	// point q lies on line segment 'pr' 
	static boolean onSegment(Point p, Point q, Point r) 
	{ 
		if (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) && 
			q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y)) 
		return true; 

		return false; 
	} 

	// To find orientation of ordered triplet (p, q, r). 
	// The function returns following values 
	// 0 --> p, q and r are colinear 
	// 1 --> Clockwise 
	// 2 --> Counterclockwise 
	static int orientation(Point p, Point q, Point r) 
	{ 
		// See https://www.geeksforgeeks.org/orientation-3-ordered-points/ 
		// for details of below formula. 
		double val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y); 

		if (val == 0) return 0; // colinear 

		return (val > 0)? 1: 2; // clock or counterclock wise 
	} 

	// The main function that returns true if line segment 'p1q1' 
	// and 'p2q2' intersect. 
	static boolean doIntersect(Point p1, Point q1, Point p2, Point q2) 
	{ 
		// Find the four orientations needed for general and 
		// special cases 
		int o1 = orientation(p1, q1, p2); 
		int o2 = orientation(p1, q1, q2); 
		int o3 = orientation(p2, q2, p1); 
		int o4 = orientation(p2, q2, q1); 

		// General case 
		if (o1 != o2 && o3 != o4) 
			return true; 

		// Special Cases 
		// p1, q1 and p2 are colinear and p2 lies on segment p1q1 
		if (o1 == 0 && onSegment(p1, p2, q1)) return true; 

		// p1, q1 and q2 are colinear and q2 lies on segment p1q1 
		if (o2 == 0 && onSegment(p1, q2, q1)) return true; 

		// p2, q2 and p1 are colinear and p1 lies on segment p2q2 
		if (o3 == 0 && onSegment(p2, p1, q2)) return true; 

		// p2, q2 and q1 are colinear and q1 lies on segment p2q2 
		if (o4 == 0 && onSegment(p2, q1, q2)) return true; 

		return false; // Doesn't fall in any of the above cases 
	} 
	
	void getLineSegments(){
		LinkedList<LineSegment> path_edges = get_path_ls();//new LinkedList<LineSegment>();
		LinkedList<LineSegment> path_tr_edges = get_pathtr_ls();//new LinkedList<LineSegment>();
		LinkedList<LineSegment> cross_edges = get_cross_ls();//new LinkedList<LineSegment>();
		double d = 0.00001;
		for(LEdge e:pbf.getLG().getEdges()){
			
			if( e.getsource().getx()!=e.gettarget().getx() ){ //cross edge
				if(e.getbends().isEmpty()==false){
					if(e.getbends().size()!=1){System.out.println("\nAesthetics:LineSegment:error1");System.exit(0);}
					
					
					double sx = e.getsource().getx();
					double sy = e.getsource().gety();
					double bx = e.getbends().get(0).getx();
					double by = e.getbends().get(0).gety();
					//if(e.getsource.getx()<e.gettarget.getx()){
					double slope = ( by-sy )/( bx-sx );
					double c = sy-slope*sx; 
					Point s1 = new Point( calc_x(slope,c,sy+d) , sy+d );
					Point b1 = new Point(calc_x(slope,c,by-d) , by-d );
					cross_edges.add( new LineSegment(s1,b1) );
					
					Point bs1 = new Point( bx, by );
					double tx =  e.gettarget().getx();
					double ty = e.gettarget().gety();
					slope = ( ty-by )/( tx-bx );
					c = by-slope*bx;
					
					Point t1 = new Point( calc_x(slope,c,ty-d) , ty-d );
					cross_edges.add( new LineSegment(bs1,t1) );
					
				}else{					 
					double ty = e.gettarget().gety();
					double sx = e.getsource().gety();
					double tx = e.gettarget().getx();
					double sy = e.getsource().getx();
					//if(e.getsource.getx()<e.gettarget.getx()){
					double slope = ( ty-sy )/( tx-sx );
					double c = sy-slope*sx;
					Point p1 = new Point( ((sy+d)-c)/slope , sy+d );
					Point p2 = new Point( ((ty-d)-c)/slope , ty-d );	
					cross_edges.add( new LineSegment(p1,p2) );
				}
			}else if(e.getbends().size()==0 ){	//path edge
				LineSegment ls = new LineSegment(e.getsource().getx(),e.getsource().gety()+d,
												e.gettarget().getx(),e.gettarget().gety()-d);
				path_edges.add(ls);
			}else{								//path transitive edges
				double sx = e.getsource().getx();
				double sy = e.getsource().gety();
				double b1x = e.getbends().get(0).getx();
				double b1y = e.getbends().get(0).gety();
				double b2x = e.getbends().get(1).getx();
				double b2y = e.getbends().get(1).gety();
				double tx = e.gettarget().getx();
				double ty = e.gettarget().gety();
				
				double slope = (b1y-sy)/(b1x-sx);
				double c = sy-slope*sx;
				Point p1 = new Point(calc_x(slope,c,sy+d) , sy+d);
				Point p2 = new Point(calc_x(slope,c,b1y-d) , b1y-d);
				LineSegment ls1=new LineSegment( p1 , p2);
				path_tr_edges.add(ls1);
				
				Point p3 = new Point( b1x , b1y);
				Point p4 = new Point(b2x , b2y);
				LineSegment ls2=new LineSegment( p3 , p4);
				path_tr_edges.add(ls2);
				
				double slope1 = (ty-b2y)/(tx-b2x);
				double c1 = b2y-slope1*b2x;
				//System.out.println(e.getbends().get(0).toString()+"--"+e.getbends().get(1).toString()+"--"+e.gettarget().toString()+" slope:"+slope1+" c:"+c);
				Point p5 = new Point(calc_x(slope1,c1,b2y+d) , b2y+d);
				Point p6 = new Point(calc_x(slope1,c1,ty-d) , ty-d);
				LineSegment ls3=new LineSegment( p5 , p6);
				path_tr_edges.add(ls3);
				
			}
		}
		//this.linesegments.add(path_tr_edges);
		//this.linesegments.add(path_edges);
		//this.linesegments.add(cross_edges);
	}
	private double calc_x(double slope,double c,double y){
		return 	(y-c)/slope;			//y=slope*x+c => x=(y-c)/slope
	}
	
	
	
	public static int countCrossings(LinkedList<LineSegment> l1,LinkedList<LineSegment> l2){
		int crossings = 0;
		for(LineSegment s1:l1){
			Point p1 = s1.getp1();
			Point q1 = s1.getp2();
			for(LineSegment s2:l2){
				Point p2 = s2.getp1();
				Point q2 = s2.getp2();
				//System.out.println("do intersect:"+p1+q1+p2+q2+"\t"+doIntersect(p1,q1,p2,q2));
				if(doIntersect(p1,q1,p2,q2)){
					++crossings;
				}
			}
		}
		return crossings;
	}
	
	public static int countCrossings(LinkedList<LineSegment> l){
		Set<LineSegment> visited = new HashSet<LineSegment>();
		int crossings = 0;
		for(LineSegment s1:l){
			Point p1 = s1.getp1();
			Point q1 = s1.getp2();
			if(visited.contains(s1)){continue;}
			visited.add(s1);
			for(LineSegment s2:l){
				if( visited.contains(s2) ){continue;}
				Point p2 = s2.getp1();
				Point q2 = s2.getp2();
				if(doIntersect(p1,q1,p2,q2)){
					++crossings;
				}
				//visited.add(s2);
			}
		}
		return crossings;
	}
	// Driver code 
	public static void main(String[] args) 
	{ 
		Point p1 = new Point(1, 4); 
		Point q1 = new Point(10, 10);
		Point p2 = new Point(10, 10); 
		Point q2 = new Point(12, 12); 

		if(doIntersect(p1, q1, p2, q2)) 
			System.out.println("Yes"); 
		else
			System.out.println("No"); 

		p1 = new Point(10, 1); q1 = new Point(0, 10); 
		p2 = new Point(0, 0); q2 = new Point(10, 10); 
		if(doIntersect(p1, q1, p2, q2)) 
				System.out.println("Yes"); 
		else
			System.out.println("No"); 

		p1 = new Point(-5, -5); q1 = new Point(0, 0); 
		p2 = new Point(1, 1); q2 = new Point(10, 10);; 
		if(doIntersect(p1, q1, p2, q2)) 
			System.out.println("Yes"); 
		else
			System.out.println("No"); 
	} 
} 

// This code is contributed by Princi Singh 
