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

public class Heuristics_v0 {
	private SimpleGraph G;
	private IVertex[] ts;
	private LinkedList<Channel> decomposition;
	private LinkedList<LinkedList<Integer>> channel_decomposition;
	
	
	private Channel []VertexChannel;
	//private HashMap<Integer, Integer> isFirst = new HashMap<Integer, Integer>();
	//private HashMap<Integer, Integer> isLast = new HashMap<Integer, Integer>();
	
	Heuristics_v0(SimpleGraph G,IVertex[] ts){
		this.G=G;
		this.ts=ts;
		VertexChannel = new Channel[ts.length];
	}
	
	Integer DFS_LookUp(IVertex start,boolean []isDeleted,ListIterator[] sources,HashMap<Integer, Integer> isLast){
		LinkedList<IVertex> stack=new LinkedList<IVertex>();
		stack.add(start);
		while(!stack.isEmpty()){
			
			IVertex cur = stack.getLast();
			int cur_id = (int)cur.getId();
			//System.out.println("hereee5==="+cur.getLabel()+" isDel:"+isDeleted[cur_id]);
			if(isDeleted[cur_id]!= true){
				//System.out.print("--"+cur.getLabel());
				Integer path_no=isLast.get(cur_id);
				//System.out.print("  "+path_no+"\n");
				if(path_no!=null){//if(cur.getLabel().equals("7")){				
					//System.out.println("target found! start:"+start.getLabel()+" found:"+cur.getLabel()+" path_no:"+path_no);
					//System.out.println("hereee1");
					while(!stack.isEmpty()){
						IVertex tmp=stack.removeLast();
						ListIterator it=sources[(int)tmp.getId()];
						//System.out.println(tmp.getLabel());
						if(it.hasPrevious()){
							it.previous();
						}
					}
					//System.out.println("hereee2");
					isLast.put(cur_id,null);
					return path_no; 
				}
				
				
				boolean d=true;
				ListIterator it=sources[cur_id];
				//System.out.println("hereee3");
				while(it.hasNext()){
					IVertex s=(IVertex)it.next();
					if(isDeleted[(int)s.getId()]==false){
						stack.add(s);
						d=false;
						break;
					}
				}
				//System.out.println("hereee4");
				if(d){
					isDeleted[cur_id]=d;
					stack.removeLast();
				}
			}else{
				cur = stack.removeLast();
			}
		}
		//for(IVertex v:G.getVertices()){
		//	System.out.println(v.getLabel()+" "+isDeleted[(int)v.getId()]);
		//}
		return null;
	}
	
	public void  concatenation(LinkedList<Channel> path_decomposition) throws Exception{
		boolean []isDeleted = new boolean[G.getVertices().size()];
		ListIterator[] sources=new ListIterator[G.getVertices().size()];
		for(IVertex v:ts){
			sources[(int)v.getId()]=v.getAdjacentSources().listIterator();
		}
		
		HashMap<Integer, Integer> isLast = new HashMap<Integer, Integer>();
		int channel_no=0;
		for(Channel c:path_decomposition){
			//IVertex f = c.getVertices().getFirst();
			IVertex l = c.getVertices().getLast();
			//isFirst.put(  (int)f.getId()  , channel_no ) ;
			isLast.put(    (int)l.getId() , channel_no );
			//System.out.println("isLast "+l.getLabel()+":"+isLast.get((int)l.getId()));
			channel_no++;
		}
		
		Integer []next_path=new Integer[path_decomposition.size()];
		Integer []prev_path=new Integer[path_decomposition.size()];
		int path_index=0;
		for(Channel path:path_decomposition){
			IVertex fv = path.getVertices().getFirst();
			for(IVertex s:fv.getAdjacentSources()){
				//System.out.println("look up start from "+s.getLabel()+" root:"+fv.getLabel());
				Integer pred_path_index=DFS_LookUp(s,isDeleted,sources,isLast);
				//System.out.println("look up finished");
				if(pred_path_index!=null){
					prev_path[path_index] = pred_path_index;
					next_path[pred_path_index]=path_index;
					break;
				}
			}
			
			++path_index;
		}
		//for(Integer i:prev_path){
		//	System.out.print(i+" ");
		//}
		channel_decomposition = formChannels(prev_path,next_path);
		//printChannelDecomposition();
	}
	public int channels_num(){return channel_decomposition.size();}
	private void printChannelDecomposition(){
		int c_no=0;
		for(LinkedList<Integer> c:channel_decomposition){
			System.out.print("channel"+c_no+": ");
			for(Integer p:c){
				for( IVertex v:decomposition.get(p).getVertices() ){
					System.out.print(v.getLabel()+" ");
				}
			}
			++c_no;
			System.out.println("");
		}
	}
	private LinkedList<LinkedList<Integer>> formChannels(Integer []prev_path,Integer []next_path){
		LinkedList<LinkedList<Integer>> channels=new LinkedList<LinkedList<Integer>>();
		boolean []visited=new boolean[decomposition.size()];
		for(int i=0;i<decomposition.size();++i){
			if(visited[i]==false){
				LinkedList<Integer> channel=new LinkedList<Integer>();
				channel.addFirst(i);
				visited[i]=true;
				Integer tmp = prev_path[i];
				while(tmp!=null){
					channel.addFirst(tmp);
					visited[tmp]=true;
					tmp=prev_path[tmp];
				}
				
				tmp = next_path[i];
				while(tmp!=null){
					channel.addLast(tmp);
					visited[tmp]=true;
					tmp=next_path[tmp];
				}
				channels.addLast(channel);
			}
		}
		return channels;
	}
	
	public LinkedList<Channel>  NodeOrderHeuristic_ImP() throws Exception{
		this.ts=ts;
		decomposition = new LinkedList<Channel>();
		Channel first_c=new Channel(0);
		first_c.addVertex(ts[0]);
		int f_id=(int)ts[0].getId();
		this.VertexChannel[f_id]=first_c;
		decomposition.add(first_c);
		//this.isLast.put(f_id,0);
		//this.isFirst.put(f_id,0);
		for(int i=1;i<G.getVertices().size();++i){
    		IVertex v = ts[i];
			int v_id=(int)v.getId();
			boolean b=false;
    		for(IVertex pred:v.getAdjacentSources()){//;j<decomposition.size();++j){
				//int ci = cd[(int)pred.getId()].channel_index;
				int pred_id=(int)pred.getId();
				Channel C = this.VertexChannel[pred_id];
				if(C.getVertices().getLast()==pred){
					C.addVertex(v);
					this.VertexChannel[v_id]=C;//.channel_index=ci;
					b=true;
					break;
				}
				
				if(b){break;}
    		}
			if(!b) {
    			Channel C_new = new Channel( decomposition.size());
    			C_new.addVertex(v);
				this.VertexChannel[v_id]=C_new;
    			decomposition.addLast(C_new);
    		}
    	}
		return decomposition;
	}
	
	
	public LinkedList<Channel>  ChainOrderHeuristic_ImS() throws Exception{
		this.ts=ts;
		decomposition = new LinkedList<Channel>();
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
				VertexChannel[(int)start.getId()]=C;
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
		
		/*int channel_no=0;
		for(Channel c:decomposition){
			IVertex f = c.getVertices().getFirst();
			IVertex l = c.getVertices().getLast();
			isFirst.put(  (int)f.getId()  , channel_no ) ;
			isLast.put(    (int)l.getId() , channel_no );
			//System.out.println("isLast "+l.getLabel()+":"+isLast.get((int)l.getId()));
			channel_no++;
		}*/
		
		return decomposition;
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
			long decomposition_time=0, decomposition_time1 =0, scheme_time=0;
			for(File f: files) {
				System.out.println(""+(++counter)+":"+f.getName());
				SimpleGraph G = r.read(f);
				G.setAdjacency();
				IVertex[] ts = Main.setTopologicalIds(G);
				
				
				//Heuristics_v0.ChainOrderHeuristic co_h = h_v0.new ChainOrderHeuristic();
				
				//for(IVertex v:G.getVertices()){
				//	System.out.println("ID: "+v.getId()+" LABEL:"+v.getLabel());
				//}
				
				System.out.println("G(n="+G.getVertices().size()+" , m="+G.getEdges().size()+")");
				
				for(int i=0;i<10;++i){
					Heuristics_v0 h_v2=new Heuristics_v0(G,ts);
					LinkedList<Channel> decomposition2 = h_v2.ChainOrderHeuristic_ImS();
				}
				
				Heuristics_v0 h_v0=new Heuristics_v0(G,ts);
				long startTime = System.currentTimeMillis(); 
				LinkedList<Channel> decomposition = h_v0.ChainOrderHeuristic_ImS();//h.ChainOrderHeuristic_ImS(G);//h.MyHeuristic(G,0);//h.newMethod1(G,adj_no);//h.DAG_decomposition_Fulkerson(G);h.newMethod1_fastest(G,adj_no);//
				long stopTime = System.currentTimeMillis();
				decomposition_time=(stopTime-startTime);
				System.out.println("ChainOrderHeuristic_ImS: dec size: "+decomposition.size()+ " time:"+decomposition_time);
				
				startTime = System.currentTimeMillis();
				h_v0.concatenation(decomposition);
				stopTime = System.currentTimeMillis();
				System.out.println("concatenation:  channels:"+h_v0.channels_num()+" time:"+(stopTime-startTime)+" agregate time:"+decomposition_time+(stopTime-startTime));
				
				System.out.println("");			
				
				Heuristics_v0 NO=new Heuristics_v0(G,ts);
				startTime = System.currentTimeMillis();
				LinkedList<Channel> decompositionNO = NO.NodeOrderHeuristic_ImP();
				stopTime = System.currentTimeMillis();
				decomposition_time=(stopTime-startTime);
				System.out.println("NodeOrderHeuristic: dec size: "+decompositionNO.size()+ " time:"+decomposition_time);
				startTime = System.currentTimeMillis();
				NO.concatenation(decompositionNO);
				stopTime = System.currentTimeMillis();
				System.out.println("concatenation:  channels:"+h_v0.channels_num()+" time:"+(stopTime-startTime)+" agregate time:"+decomposition_time+(stopTime-startTime));
				
				System.out.println("");
				
				for(int i=0;i<10;++i){
					LinkedList<Channel> decomposition1 = h.ChainOrderHeuristic_ImS(G);
				}
				startTime = System.currentTimeMillis(); 
				LinkedList<Channel> decomposition1 = h.ChainOrderHeuristic_ImS(G);//h.ChainOrderHeuristic_ImS(G);//h.MyHeuristic(G,0);//h.newMethod1(G,adj_no);//h.DAG_decomposition_Fulkerson(G);h.newMethod1_fastest(G,adj_no);//
				stopTime = System.currentTimeMillis();
				decomposition_time=(stopTime-startTime);
				System.out.println("ChainOrderHeuristic_S:dec size:"+decomposition1.size()+ " time:"+decomposition_time);
				
				//System.out.println("finish processing");
				//System.gc();
				//System.out.println("gc finish processing");
				
				/*boolean []isDeleted=new boolean[ts.length];
				
				ListIterator []il=new ListIterator[ts.length];
				for(IVertex v:ts){
					il[(int)v.getId()]=v.getAdjacentSources().listIterator();
				}*/
				//Main.printDecomposition(decomposition);
				//IVertex tmp=decomposition.get(0).getVertices().getLast();
				//h_v0.DFS_LookUp(tmp , isDeleted ,il);
				//h_v0.DFS_LookUp(tmp , isDeleted ,il);
				/*h_v0.concatenation(decomposition);
				System.out.println("dec:"+h_v0.channels_num()+" dec1:"+decomposition1.size());
				if(h_v0.channels_num()!=decomposition1.size()){
					h_v0.printChannelDecomposition();
					Main.printDecomposition(decomposition1);
					//break;
				}*/
				//h_v0.printChannelDecomposition();
				//Main.printDecomposition(decomposition1);
			}
		}catch (Exception e) {  
            e.printStackTrace();  
        }
	}
}