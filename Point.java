public class Point{
	double x;
	double y;
	public Point(double x,double y){
		this.x=x;
		this.y=y;
	}
	
	public double getx(){return x;}
	public double gety(){return y;}
	public void setx(double x){this.x=x;}
	public void sety(double y){this.y=y;}
	public String toString(){
		return "("+x+","+y+")";
	}
}