import java.util.LinkedList;

class Interval{
	LinkedList<LEdge> interval;
	int from;
	int to;
	Interval(LinkedList<LEdge> interval){
		this.interval = interval;
		int min=Integer.MAX_VALUE;
		int max=-1;
		for(LEdge e:interval){
			int s=e.getsourceId();
			int t=e.gettargetId();
			if(s<min){min=s;}
			if(t>max){max=t;}
		}
		if(min==Integer.MAX_VALUE){
			min=-1;
		}
		this.from=min;
		this.to=max;
	}
	LinkedList<LEdge> getinterval(){return interval;}
	int getfrom(){return from;}
	int getto(){return to;}
}