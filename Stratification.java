
import graph.*;
import java.util.LinkedList;


public class Stratification{
	private SimpleGraph G;
	private int size;
	private LinkedList<LinkedList<IVertex>> S;
	private boolean isTopdown;
	public static class Stratification_info{
		int[] layer;
		int[] layer_index;
		//IVertex[] array;
		
		public Stratification_info( Stratification V){
			layer = new int[V.getsize()];
			layer_index = new int[V.getsize()];
			//array = new IVertex[G.getVertices().size()];
			
			int l=0;
			int li;
			for(LinkedList<IVertex> VI:V.getStratification()){
				li=0;
				for(IVertex v:VI){
					int index = (int)v.getId(); 
					layer[index]= l;
					layer_index[index] = li;
					//array[index]=v;
					System.out.print("("+index+","+l+","+li+")");
					++li;
				}
				System.out.println("");
				++l;
			}
		}
	}
	
	public Stratification(SimpleGraph G,boolean isTopdown){
		this.isTopdown=isTopdown;
		this.G=G;
		this.size=G.getVertices().size();
		
		if(isTopdown){
			S = stratification_topdown(G);
		}else{
			S = stratification_bottomup(G);
		}
	}
	public Stratification(SimpleGraph G){
		this.isTopdown=false;
		this.G=G;
		this.size=G.getVertices().size();	
		S = stratification_bottomup(G);
	}
	public LinkedList<LinkedList<IVertex>> getStratification(){
		return this.S;
	}
	public LinkedList<IVertex> getLayer(int l){
		return S.get(l);
	}
	public int getsize(){return size;}
	public int getHeight(){return S.size();}
	public boolean getisTopdown(){return this.isTopdown;}
	private LinkedList<LinkedList<IVertex>> stratification_bottomup(SimpleGraph G) {
		LinkedList<Channel> decomposition = new LinkedList<Channel>();
		int[] id_counter = new int[G.getVertices().size()]; //indegree counter
		int[] od_counter = new int[G.getVertices().size()];//outdegree counter
		LinkedList<LinkedList<IVertex>> V = new LinkedList<LinkedList<IVertex>>(); 
		//System.out.println("here1");
		LinkedList<IVertex> V0 = new LinkedList<IVertex>();
		LinkedList<IVertex> q1 = new LinkedList<IVertex>();
		LinkedList<IVertex> q2 = new LinkedList<IVertex>();
		LinkedList<IVertex> queue;
		LinkedList<IVertex> queue_helper;
		for(IVertex v: G.getVertices()){
			int index = (int)v.getId();
			od_counter[index]=v.getAdjacentTargets().size();
			id_counter[index]=v.getAdjacentSources().size();
			if(id_counter[index]==0){
				q1.add(v);
				V0.add(v);
			}
		}
		if(V0.size()==0){return V;}
		V.add(V0);
		//queue.add(q1);
		//System.out.println("here2");
		
		
		while(q1.isEmpty()==false||q2.isEmpty()==false){
			if(q1.isEmpty()){
				queue=q2;
				queue_helper=q1;
			}else{
				queue=q1;
				queue_helper=q2;
			}
			//V.add(new LinkedList<IVertex>());
			LinkedList<IVertex> VI = new LinkedList<IVertex>();
			while(queue.isEmpty()==false){
				IVertex cur = queue.remove();
				//System.out.println("here3");
				for (IVertex tmp_v : cur.getAdjacentTargets()) {
					int tmp_index = (int)tmp_v.getId();
					--id_counter[tmp_index];
					if(id_counter[tmp_index]==0){
						queue_helper.add(tmp_v);
						VI.add(tmp_v);
					}
				}
			}
			if(VI.size()!=0){
				V.add(VI);
			}
			//VI.clear();
		}
		
		
		return V;
	}

	private LinkedList<LinkedList<IVertex>> stratification_topdown(SimpleGraph G) {
		LinkedList<Channel> decomposition = new LinkedList<Channel>();
		int[] id_counter = new int[G.getVertices().size()]; //indegree counter
		int[] od_counter = new int[G.getVertices().size()];//outdegree counter
		LinkedList<LinkedList<IVertex>> V = new LinkedList<LinkedList<IVertex>>(); 
		//System.out.println("here1");
		LinkedList<IVertex> V0 = new LinkedList<IVertex>();
		LinkedList<IVertex> q1 = new LinkedList<IVertex>();
		LinkedList<IVertex> q2 = new LinkedList<IVertex>();
		LinkedList<IVertex> queue;
		LinkedList<IVertex> queue_helper;
		for(IVertex v: G.getVertices()){
			int index = (int)v.getId();
			od_counter[index]=v.getAdjacentTargets().size();
			if(od_counter[index]==0){
				q1.add(v);
				V0.add(v);
			}
		}
		if(V0.size()==0){return V;}
		V.add(V0);
		
		while(q1.isEmpty()==false||q2.isEmpty()==false){
			if(q1.isEmpty()){
				queue=q2;
				queue_helper=q1;
			}else{
				queue=q1;
				queue_helper=q2;
			}
			LinkedList<IVertex> VI = new LinkedList<IVertex>();
			while(queue.isEmpty()==false){
				IVertex cur = queue.remove();
				for (IVertex tmp_v : cur.getAdjacentSources()) {
					int tmp_index = (int)tmp_v.getId();
					--od_counter[tmp_index];
					if(od_counter[tmp_index]==0){
						queue_helper.add(tmp_v);
						VI.add(tmp_v);
					}
				}
			}
			if(VI.size()!=0){
				V.add(VI);
			}
		}
		
		
		return V;
	}
}