class LineSegment{
	private Point p1;
	private Point p2;
	LineSegment(Point p1,Point p2){
		this.p1=p1;
		this.p2=p2;
	}
	LineSegment(double x1,double y1,double x2,double y2){
		this.p1=new Point(x1,y1);
		this.p2=new Point(x2,y2);
	}
	Point getp1(){return p1;}
	Point getp2(){return p2;}
	void setp1(Point p1){ this.p1=p1;}
	void setp2(Point p2){ this.p2=p2;}
	public String toString(){
		return "[ "+p1.toString()+" , "+p2.toString()+" ]";
	}
}