import java.util.LinkedList;
import graph.*;

class Test{
	private long duration;
	private LinkedList<Channel> decomposition;
	private int depth;
	private int h_type; 
	static final int COH = 1;
	static final int NOH = 2;
	static final int H3 = 3;
	Test(Heuristics h,SimpleGraph G,int h_type)throws Exception{
		this.depth=-1;
		this.h_type=h_type;
		
		long endTime;
		long startTime;
		startTime = System.currentTimeMillis();
		if(h_type==COH){
			this.decomposition = h.ChainOrderHeuristic(G,depth);
		}else if(h_type==NOH){
			this.decomposition = h.NodeOrderHeuristic(G,depth);
		}else if(h_type==H3){
			this.decomposition = h.MyHeuristic(G,depth);
		}
		endTime = System.currentTimeMillis();
		this.duration = endTime - startTime;
		System.out.println("width:"+decomposition.size()+" duration:"+duration);
	}
	Test(Heuristics h,SimpleGraph G,int depth,int h_type)throws Exception{
		if(depth<1){
			System.out.println("Invalid depth! depth>=1");
			return;
		}
		this.depth=depth;
		this.h_type=h_type;
		
		long endTime;
		long startTime;
		startTime = System.currentTimeMillis();
		if(h_type==COH){
			this.decomposition = h.ChainOrderHeuristic(G,depth);
		}else if(h_type==NOH){
			this.decomposition = h.NodeOrderHeuristic(G,depth);
		}else if(h_type==H3){
			this.decomposition = h.MyHeuristic(G,depth);
		}
		endTime = System.currentTimeMillis();
		this.duration = endTime - startTime;
		System.out.println("width:"+decomposition.size()+" duration:"+duration);
	}
	LinkedList<Channel> getDecomposition(){
		return this.decomposition;
	}

	int getwidth(){return decomposition.size();}
	int getdepth(){return depth;}
	int gettype(){return h_type;}
	long getduration(){return duration;}
}