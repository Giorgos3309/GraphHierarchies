import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Queue;
import java.util.HashMap;
import java.util.ListIterator;

import java.io.File;
import java.io.FileWriter;

import java.io.IOException;

import graph.*;
class HVertex{
		//boolean isFirst = false;
		//boolean isLast = false;
		HashMap<Integer, IVertex> isFirst = new HashMap<Integer, IVertex>();
		HashMap<Integer, IVertex> isLast = new HashMap<Integer, IVertex>();
		int channel_index=-1;
}
public class Heuristics_v0 {
	private SimpleGraph G;
	private LinkedList<Channel> decomposition;
	
	private int VertexChannel[];
	private HashMap<Integer, Boolean> isFirst = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> isLast = new HashMap<Integer, Boolean>();
	
	Heuristics_v0(SimpleGraph G){this.G=G;}
	
	IVertex DFS_LookUp(IVertex start,boolean []isDeleted,ListIterator[] sources){
		LinkedList<IVertex> stack=new LinkedList<IVertex>();
		stack.add(start);
		while(!stack.isEmpty()){
			IVertex cur = stack.getLast();
			int cur_id = (int)cur.getId();
			if(isDeleted[cur_id]!= true){
				System.out.println(""+cur.getLabel());
				Boolean islast=isLast.get(cur_id);
				if(cur.getLabel().equals("7")){
					System.out.println("target found!");
					while(!stack.isEmpty()){
						IVertex tmp=stack.removeLast();
						ListIterator it=sources[(int)tmp.getId()];
						System.out.println(tmp.getLabel());
						if(it.hasPrevious()){
							it.previous();
						}
					}
					return cur; 
				}
				
				boolean d=true;
				ListIterator it=sources[cur_id];
				while(it.hasNext()){
					IVertex s=(IVertex)it.next();
					if(isDeleted[(int)s.getId()]==false){
						stack.add(s);
						d=false;
						break;
					}
				}
				if(d){
					isDeleted[cur_id]=d;
					stack.removeLast();
				}
			}
		}
		for(IVertex v:G.getVertices()){
			System.out.println(v.getLabel()+" "+isDeleted[(int)v.getId()]);
		}
		return null;
	}
	
	public void  concatenation() throws Exception{
		int[] adj_no;
		LinkedList<Channel> channel_decomposition=new LinkedList<Channel>();
		boolean []isDeleted = new boolean[G.getVertices().size()];
		boolean []isVisited = new boolean[G.getVertices().size()];
		
		int[] id_counter = new int[G.getVertices().size()]; //indegree counter
		//int[] od_counter = new int[G.getVertices().size()];//outdegree counter
		//Queue<IVertex> queue = new LinkedList<>();
		for(IVertex v: G.getVertices()){
			int index=(int)v.getId();
			//od_counter[index]=v.getAdjacentTargets().size();
			id_counter[index]=v.getAdjacentSources().size();
		}
		for(Channel c:decomposition){
			IVertex f = c.getVertices().get(0);
			IVertex l = c.getVertices().get( c.getVertices().size()-1 );
			isFirst.put(  (int)f.getId()  , true ) ;
			isLast.put(    (int)l.getId() , true );
		}
	}
	
	public class ChainOrderHeuristic{
		public LinkedList<Channel>  ChainOrderHeuristic_ImS(IVertex[] ts) throws Exception{
			decomposition = new LinkedList<Channel>();
			VertexChannel = new int[ts.length];
			boolean[] visited = new boolean[ts.length];
			//for(IVertex v: ts){
				//v.setVisited(false);
			//}
			int i=0;
			while(i<ts.length) {
				IVertex start = ts[i];
				if(/*!start.getVisited() */!visited[(int)start.getId()]   ) {
					Channel C = new Channel(i);
					C.addVertex(start);
					//cd[(int)start.getId()].channel_index=decomposition.size();
					VertexChannel[(int)start.getId()]=decomposition.size();
					decomposition.add(C);
					//start.setVisited(true);
					visited[(int)start.getId()] = true;
					boolean needNewChannel = false;
					while(!needNewChannel) {
						needNewChannel=true;
						IVertex toAdd=null;//Max node ID
						for (IVertex c : start.getAdjacentTargets()) {
							if (/*c.getVisited()*/visited[(int)c.getId()]){
								continue;
							}else{
								if(toAdd==null){
									toAdd=c;
								}else if (toAdd.getId()>c.getId()){
									toAdd=c;
								}
							}
						}
						if(toAdd!=null){
							C.addVertex(toAdd);
							//toAdd.setVisited(true);
							visited[(int)toAdd.getId()]=true;
							start=toAdd;
							needNewChannel=false;
						}
						
						
					}
				}
				i++;
			}
			return decomposition;
		}

	}
	
	
	public static void main(String[]args) {
		//LinkedList<SimpleGraph>graphs2 = new LinkedList<SimpleGraph>();
		Reader r = new Reader();
		final File folder = new File("F:\\courses\\master_thesis\\Graph decomposition code\\java\\inputgraphs");
		//LinkedList<String> filenames = new LinkedList<String>();
		LinkedList<File> files = new LinkedList<File>();
		//listFilesForFolder(folder,filenames);
		Main.listFilesForFolder(folder,files);
		
		Heuristics h = new Heuristics();
		
		try{
			int counter = 0;
			long decomposition_time=0 , scheme_time=0;
			for(File f: files) {
				System.out.println(""+(++counter)+":"+f.getName());
				SimpleGraph G = r.read(f);
				G.setAdjacency();
				IVertex[] ts = Main.setTopologicalIds(G);
				
				Heuristics_v0 h_v0=new Heuristics_v0(G);
				Heuristics_v0.ChainOrderHeuristic co_h = h_v0.new ChainOrderHeuristic();
				
				//for(IVertex v:G.getVertices()){
				//	System.out.println("ID: "+v.getId()+" LABEL:"+v.getLabel());
				//}
				
				System.out.println("G(n="+G.getVertices().size()+" , m="+G.getEdges().size()+")");
				
				long startTime = System.currentTimeMillis(); 
				LinkedList<Channel> decomposition = co_h.ChainOrderHeuristic_ImS(ts);//h.ChainOrderHeuristic_ImS(G);//h.MyHeuristic(G,0);//h.newMethod1(G,adj_no);//h.DAG_decomposition_Fulkerson(G);h.newMethod1_fastest(G,adj_no);//
				long stopTime = System.currentTimeMillis();
				decomposition_time=(stopTime-startTime);
				System.out.println("dec size:"+decomposition.size()+ " time:"+decomposition_time);
				
				startTime = System.currentTimeMillis(); 
				decomposition = h.ChainOrderHeuristic_ImS(G);//h.ChainOrderHeuristic_ImS(G);//h.MyHeuristic(G,0);//h.newMethod1(G,adj_no);//h.DAG_decomposition_Fulkerson(G);h.newMethod1_fastest(G,adj_no);//
				stopTime = System.currentTimeMillis();
				decomposition_time=(stopTime-startTime);
				System.out.println("dec size:"+decomposition.size()+ " time:"+decomposition_time);
				
				System.out.println("finish processing");
				System.gc();
				System.out.println("gc finish processing");
				
				boolean []isDeleted=new boolean[ts.length];
				
				ListIterator []il=new ListIterator[ts.length];
				for(IVertex v:ts){
					il[(int)v.getId()]=v.getAdjacentSources().listIterator();
				}
				Main.printDecomposition(decomposition);
				IVertex tmp=decomposition.get(0).getVertices().getLast();
				h_v0.DFS_LookUp(tmp , isDeleted ,il);
				h_v0.DFS_LookUp(tmp , isDeleted ,il);
			}
		}catch (Exception e) {  
            e.printStackTrace();  
        }
	}
}