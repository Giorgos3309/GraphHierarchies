import graph.*;
import java.util.LinkedList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap; 


class TransitiveClosureTS{
	
	void DFS(SimpleGraph G,IVertex v){
		boolean []isVisited = new boolean[G.getVertices().size()];
		//System.out.println( v.getId() );
		//isVisited[ (int)v.getId() ]=true;
		DFS_util(v,isVisited);
	}
	void DFS(SimpleGraph G){
		boolean []isVisited = new boolean[G.getVertices().size()];
		for(IVertex v:G.getVertices() ){
			if(isVisited[(int)v.getId()]==false){
				DFS_util(v,isVisited);
			}
			
		}
		
	}

	void DFS_util(IVertex v,boolean []isVisited){
		isVisited[(int)v.getId()]=true;
		System.out.println( v.getId() );
		for(IVertex suc:v.getAdjacentTargets()){
			//System.out.println( suc.getId() );
			if(isVisited[(int)suc.getId()]==false){
				DFS_util(suc,isVisited);
			}
		}
	}

	private static class TS_Node{
		IVertex v;
		int TS_BOTTOMUP_LR_ID;
		int TS_BOTTOMUP_RL_ID;
		TS_Node(IVertex v){
			this.v=v;
		}
	}

	private void TS_BOTTOMUP_LR_util(TS_Node[] ts_nodes,IVertex s,Integer tp_id,int incoming_counter[]){
		for(IVertex v:s.getAdjacentTargets()){
			int id = (int)v.getId();
			int tmp = (--incoming_counter[id]);
			if(tmp==0){
				ts_nodes[id].TS_BOTTOMUP_LR_ID = tp_id;
				TS_BOTTOMUP_LR_util(ts_nodes,v, (++tp_id),incoming_counter);
			}
		}
	}


	public void TS_BOTTOMUP_LR(SimpleGraph G,TS_Node[] ts_nodes,IVertex sourse){
		int incoming_counter[] = new int[G.getVertices().size()];
		for(IVertex v:G.getVertices()){
			incoming_counter[(int)v.getId()] = v.getAdjacentSources().size(); 
		}
		
		Integer tp_id = 1;
		ts_nodes[(int)sourse.getId()].TS_BOTTOMUP_LR_ID = tp_id;
		TS_BOTTOMUP_LR_util(ts_nodes,sourse,++tp_id,incoming_counter);
	}

	static void print_ts_nodes(TS_Node[] ts_nodes){
		for(TS_Node tn:ts_nodes){
			System.out.println(""+(int)tn.v.getId()+" TS_BOTTOMUP_LR_ID:"+tn.TS_BOTTOMUP_LR_ID);
		}
	}
	public static void main(String[] args){
		Reader r = new Reader();
		final File f = new File("F:\\courses\\master_thesis\\Graph decomposition code\\code_for_the_student\\inputGraph.txt");
		SimpleGraph G = r.read(f);
		G.setAdjacency();
		
		TransitiveClosureTS t = new TransitiveClosureTS();
		
		TS_Node[] ts_nodes = new TS_Node[G.getVertices().size()];
		//add a single source
		LinkedList<IVertex> sources = new LinkedList<IVertex>();
		LinkedList<IVertex> syncs = new LinkedList<IVertex>();
		for(IVertex v:G.getVertices()){
			ts_nodes[(int)v.getId()] = new TS_Node(v);
			if(v.getAdjacentSources().size()==0){
				sources.add(v);
			}
			if(v.getAdjacentTargets().size()==0){
				syncs.add(v);
			}
		}
		
		IVertex source = new Vertex();
		source.setAdjacentTargets(sources);
		
		
		
		t.DFS(G,sources.get(0));
		System.out.println("\n\n");
		t.DFS(G);
		
		for(IVertex v:sources){
			System.out.print(""+(int)v.getId()+" ");
		}
		t.TS_BOTTOMUP_LR(G, ts_nodes,sources.get(0) );
		t.print_ts_nodes(ts_nodes);
	}
}