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
			if(isDeleted[cur_id]!= true){
				Integer path_no=isLast.get(cur_id);
				if(path_no!=null){
					while(!stack.isEmpty()){
						IVertex tmp=stack.removeLast();
						ListIterator it=sources[(int)tmp.getId()];
						if(it.hasPrevious()){
							it.previous();
						}
					}
					isLast.put(cur_id,null);
					return path_no; 
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
			}else{
				cur = stack.removeLast();
			}
		}
		return null;
	}
	IVertex DFS_LookUp2(IVertex start,boolean []isDeleted,ListIterator[] sources,HashMap<Integer, Channel> isLast){
		LinkedList<IVertex> stack=new LinkedList<IVertex>();
		stack.add(start);
		while(!stack.isEmpty()){
			IVertex cur = stack.getLast();
			int cur_id = (int)cur.getId();
			if(isDeleted[cur_id]!= true){
				Channel C=isLast.get(cur_id);
				//if(cur.getLabel().contains("7")){System.out.println("here:"+C);}
				if(C!=null){
					while(!stack.isEmpty()){
						IVertex tmp=stack.removeLast();
						ListIterator it=sources[(int)tmp.getId()];
						if(it.hasPrevious()){
							it.previous();
						}
					}
					//isLast.put(cur_id,null);
					//return path_no;
					return cur;
				}
				
				
				boolean d=true;
				ListIterator it=sources[cur_id];
				//System.out.println("-----");
				while(it.hasNext()){
					IVertex s=(IVertex)it.next();
					//System.out.println("--"+s.getId());
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
			}else{
				cur = stack.removeLast();
			}
		}
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
			IVertex l = c.getVertices().getLast();
			isLast.put(    (int)l.getId() , channel_no );
			channel_no++;
		}
		Integer []next_path=new Integer[path_decomposition.size()];
		Integer []prev_path=new Integer[path_decomposition.size()];
		int path_index=0;
		for(Channel path:path_decomposition){
			IVertex fv = path.getVertices().getFirst();
			//System.out.println("--"+fv.getId());
			for(IVertex s:fv.getAdjacentSources()){
				Integer pred_path_index=DFS_LookUp(s,isDeleted,sources,isLast);
				if(pred_path_index!=null){
					prev_path[path_index] = pred_path_index;
					next_path[pred_path_index]=path_index;
					break;
				}
			}
			
			++path_index;
		}
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
	public LinkedList<Channel> returnChannelDecomposition()throws Exception{
		int c_no=0;
		LinkedList<Channel> channels=new LinkedList<Channel>();
		for(LinkedList<Integer> c:channel_decomposition){
			Channel C=new Channel(c_no);
			for(Integer p:c){
				for( IVertex v:decomposition.get(p).getVertices() ){
					C.addVertex(v);
				}
			}
			channels.add(C);
			++c_no;
		}
		return channels;
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
		for(int i=1;i<G.getVertices().size();++i){
    		IVertex v = ts[i];
			int v_id=(int)v.getId();
			boolean b=false;
    		for(IVertex pred:v.getAdjacentSources()){
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

		int i=0;
		int channelsId=0;
		while(i<ts.length) {
			IVertex start = ts[i];
			if(/*!start.getVisited() */!visited[(int)start.getId()]   ) {
				Channel C = new Channel(channelsId);
				channelsId++;
				C.addVertex(start);
				VertexChannel[(int)start.getId()]=C;
				decomposition.add(C);
				visited[(int)start.getId()] = true;
				boolean needNewChannel = false;
				while(!needNewChannel) {
					needNewChannel=true;
					IVertex toAdd=null;//Max node ID
					for (IVertex c : start.getAdjacentTargets()) {
						if (visited[(int)c.getId()]){
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

	public LinkedList<Channel>  Heuristic3() throws Exception{
		decomposition = new LinkedList<Channel>();
		
		for(IVertex cur:ts){
			int min_outdegree=G.getEdges().size()+1;
			boolean belongToC = false;
			IVertex toAdd=null;
			int vid=(int)cur.getId();
			if(VertexChannel[vid]!=null){
				belongToC=true;
			}else{
				for(IVertex s : cur.getAdjacentSources()){ //choose the immediate predecessor with the lowest outdegree
					int sid = (int)s.getId();
					Channel C = VertexChannel[sid];
					if(C.getVertices().getLast()==s){
						int s_outdegree=s.getAdjacentTargets().size();
						if(s_outdegree<min_outdegree){
							min_outdegree=s_outdegree;
							toAdd=s;
						}
					}
				}
			}
			if(toAdd!=null){
				//if(vid==9){System.out.println("----->"+toAdd.getId());}
				Channel C = VertexChannel[(int)toAdd.getId()];
				C.addVertex(cur);
				VertexChannel[(int)cur.getId()]=C;
			}else if(!belongToC){
				Channel C = new Channel(decomposition.size());
				C.addVertex(cur);
				VertexChannel[(int)cur.getId()]=C;
				decomposition.add(C);
			}
			
			//boolean assignC = false;
			for (IVertex tv : cur.getAdjacentTargets()) {
				int tv_id = (int)tv.getId();
				if(tv.getAdjacentSources().size()==1 /*&& !assignC*/){ //if indegree=1 then the vertex and his succesor belong to the same path
					Channel C=VertexChannel[(int)cur.getId()];
					C.getVertices().addLast(tv);
					VertexChannel[(int)tv.getId()]=C;
					//assignC=true;
					break;
				}
			}
		}
		
		return decomposition;
	}
	
	
	
	public LinkedList<Channel>  Heuristic3_Pred() throws Exception{
		decomposition = new LinkedList<Channel>();
		boolean []isDeleted=new boolean[ts.length];
		ListIterator[] sources=new ListIterator[ts.length];
		HashMap<Integer, Channel> isLast=new HashMap();
		for(IVertex v:ts){
			sources[(int)v.getId()]=v.getAdjacentSources().listIterator();
		}
		
		for(IVertex cur:ts){
			int min_outdegree=G.getEdges().size()+1;
			boolean belongToC = false;
			IVertex toAdd=null;
			int cur_id=(int)cur.getId();
			if(VertexChannel[cur_id]!=null){
				//System.out.println("indegree=1----->"+cur.getLabel());
				belongToC=true;
			}else{
				for(IVertex s : cur.getAdjacentSources()){ //choose the immediate predecessor with the lowest outdegree
					int sid = (int)s.getId();
					Channel C = VertexChannel[sid];
					if(C.getVertices().getLast()==s){
						int s_outdegree=s.getAdjacentTargets().size();
						if(s_outdegree<min_outdegree){
							min_outdegree=s_outdegree;
							toAdd=s;
						}
					}
				}
				if(toAdd==null){
					//System.out.println("----->"+cur.getLabel());
					for(IVertex s:cur.getAdjacentSources()){
						toAdd = DFS_LookUp2(s,isDeleted,sources,isLast);
						if(toAdd!=null){
							break;
						}
					}
					//toAdd = DFS_LookUp2(cur,isDeleted,sources,isLast);
					//System.out.println("----->"+toAdd);
				}
			}
			if(toAdd!=null){
				//if(vid==9){System.out.println("----->"+toAdd.getId());}
				int lv_id=(int)toAdd.getId();
				Channel C = VertexChannel[lv_id];
				C.addVertex(cur);
				VertexChannel[cur_id]=C;
				isLast.put(lv_id,null);
				isLast.put(cur_id,C);
				
			}else if(!belongToC){
				Channel C = new Channel(decomposition.size());
				C.addVertex(cur);
				VertexChannel[(int)cur.getId()]=C;
				decomposition.add(C);
				isLast.put(cur_id,C);
			}
			
			//boolean assignC = false;
			for (IVertex tv : cur.getAdjacentTargets()) {
				int tv_id = (int)tv.getId();
				if(tv.getAdjacentSources().size()==1 /*&& !assignC*/){ //if indegree=1 then the vertex and his succesor belong to the same path
					Channel C=VertexChannel[(int)cur.getId()];
					C.getVertices().addLast(tv);
					VertexChannel[(int)tv.getId()]=C;
					//assignC=true;
					isLast.put(cur_id,null);
					isLast.put(tv_id,C);
					break;
				}
			}
		}
		
		return decomposition;
	}
	
	
	public static void testNO(SimpleGraph G,IVertex[] ts)throws Exception{
		for(int i=0;i<10;++i){
			Heuristics_v0 h_v2=new Heuristics_v0(G,ts);
			LinkedList<Channel> decomposition2 = h_v2.NodeOrderHeuristic_ImP();
			h_v2.concatenation(decomposition2);
		}
		System.gc();
		long startTime=0,stopTime=0;
		Heuristics_v0 NO=new Heuristics_v0(G,ts);
		startTime = System.currentTimeMillis();
		LinkedList<Channel> decomposition = NO.NodeOrderHeuristic_ImP();//NO.ChainOrderHeuristic_ImS();//NO.NodeOrderHeuristic_ImP();
		stopTime = System.currentTimeMillis();
		long decomposition_time=(stopTime-startTime);
		System.out.println("NO: dec:"+decomposition.size()+" time:"+decomposition_time);
		
		startTime = System.currentTimeMillis();
		NO.concatenation(decomposition);
		//LinkedList<Channel> decompositionConc = NO.returnChannelDecomposition();
		stopTime = System.currentTimeMillis();
		long concatenation_time = (stopTime-startTime);
		System.out.println("concatenation:  channels:"+NO.channels_num()+" concatenation_time:"+concatenation_time);				
		
	}
	
	public static void testCO(SimpleGraph G,IVertex[] ts)throws Exception{
		for(int i=0;i<10;++i){
			Heuristics_v0 h_v2=new Heuristics_v0(G,ts);
			LinkedList<Channel> decomposition2 = h_v2.ChainOrderHeuristic_ImS();
			h_v2.concatenation(decomposition2);
		}
		System.gc();
		long startTime=0,stopTime=0;
		Heuristics_v0 NO=new Heuristics_v0(G,ts);
		startTime = System.currentTimeMillis();
		LinkedList<Channel> decomposition = NO.ChainOrderHeuristic_ImS();//NO.ChainOrderHeuristic_ImS();//NO.NodeOrderHeuristic_ImP();
		stopTime = System.currentTimeMillis();
		long decomposition_time=(stopTime-startTime);
		System.out.println("CO: dec:"+decomposition.size()+" time:"+decomposition_time);
		
		startTime = System.currentTimeMillis();
		NO.concatenation(decomposition);
		//LinkedList<Channel> decompositionConc = NO.returnChannelDecomposition();
		stopTime = System.currentTimeMillis();
		long concatenation_time = (stopTime-startTime);
		System.out.println("concatenation:  channels:"+NO.channels_num()+" concatenation_time:"+concatenation_time);				
	}
	
	public static void testH3(SimpleGraph G,IVertex[] ts)throws Exception{
		for(int i=0;i<10;++i){
			Heuristics_v0 h_v2=new Heuristics_v0(G,ts);
			LinkedList<Channel> decomposition2 = h_v2.Heuristic3();
		}
		System.gc();
		long startTime=0,stopTime=0;
		Heuristics_v0 NO=new Heuristics_v0(G,ts);
		startTime = System.currentTimeMillis();
		LinkedList<Channel> decomposition = NO.Heuristic3();//NO.ChainOrderHeuristic_ImS();//NO.NodeOrderHeuristic_ImP();
		stopTime = System.currentTimeMillis();
		long decomposition_time=(stopTime-startTime);
		System.out.println("H3: dec:"+decomposition.size()+" time:"+decomposition_time);

	}
	public static void testH3_pred(SimpleGraph G,IVertex[] ts)throws Exception{
		for(int i=0;i<10;++i){
			Heuristics_v0 h_v2=new Heuristics_v0(G,ts);
			LinkedList<Channel> decomposition2 = h_v2.Heuristic3_Pred();
		}
		System.gc();
		long startTime=0,stopTime=0;
		Heuristics_v0 NO=new Heuristics_v0(G,ts);
		startTime = System.currentTimeMillis();
		LinkedList<Channel> decomposition = NO.Heuristic3_Pred();//NO.ChainOrderHeuristic_ImS();//NO.NodeOrderHeuristic_ImP();
		stopTime = System.currentTimeMillis();
		long decomposition_time=(stopTime-startTime);
		System.out.println("H3_pred: dec:"+decomposition.size()+" time:"+decomposition_time);

	}
	
	public static void main(String[]args) {
		//LinkedList<SimpleGraph>graphs2 = new LinkedList<SimpleGraph>();
		//Reader r = new Reader();
		final File folder = new File("F:\\courses\\master_thesis\\Graph decomposition code\\java\\inputgraphs");
		//LinkedList<String> filenames = new LinkedList<String>();
		LinkedList<File> files = new LinkedList<File>();
		//listFilesForFolder(folder,filenames);
		Main.listFilesForFolder(folder,files);
		
		
		try{
			int counter = 0;
			double av_width=0,av_dec=0,av_cdec=0;
			long av_decomposition_time=0,av_concatenation_time=0;
			for(File f: files) {
				long decomposition_time=0, concatenation_time = 0, scheme_time=0;
				System.out.print(""+(++counter)+":"+f.getName());
				Reader r = new Reader();
				SimpleGraph G = r.read(f);//r.readEdgeList(f);
				G.setAdjacency();
				System.out.println("  G(n="+G.getVertices().size()+" , m="+G.getEdges().size()+")");
				IVertex[] ts = Main.setTopologicalIdsLayered(G);
				
				System.out.println("CO==========================");
				testCO(G,ts);
				System.out.println("NO==========================");
				testNO(G,ts);
				System.out.println("H3==========================");
				testH3(G,ts);
				System.out.println("H3_pred==========================");
				testH3_pred(G,ts);
				/*Heuristics h = new Heuristics();
				LinkedList<Channel> decompositionOpt=h.DAG_decomposition_Fulkerson(G);
				System.out.println("width: "+decompositionOpt.size());
				av_width+=decompositionOpt.size();
				h=null;
				decompositionOpt=null;
				*/
				
				
				/*for(int i=0;i<10;++i){
					Heuristics_v0 h_v2=new Heuristics_v0(G,ts);
					LinkedList<Channel> decomposition2 = h_v2.NodeOrderHeuristic_ImP();
					h_v2.concatenation(decomposition2);
				}
				System.gc();*/
				/*Heuristics_v0 h_v0=new Heuristics_v0(G,ts);
				long startTime = System.currentTimeMillis(); 
				LinkedList<Channel> decomposition = h_v0.ChainOrderHeuristic_ImS();//h.ChainOrderHeuristic_ImS(G);//h.MyHeuristic(G,0);//h.newMethod1(G,adj_no);//h.DAG_decomposition_Fulkerson(G);h.newMethod1_fastest(G,adj_no);//
				long stopTime = System.currentTimeMillis();
				decomposition_time=(stopTime-startTime);
				System.out.println("ChainOrderHeuristic_ImS: dec size: "+decomposition.size()+ " time:"+decomposition_time);
				
				startTime = System.currentTimeMillis();
				h_v0.concatenation(decomposition);
				stopTime = System.currentTimeMillis();
				System.out.println("concatenation:  channels:"+h_v0.channels_num()+" time:"+(stopTime-startTime)+" agregate time:"+decomposition_time+(stopTime-startTime));
				
				System.out.println("");	*/		
				/*long startTime=0,stopTime=0;
				Heuristics_v0 NO=new Heuristics_v0(G,ts);
				startTime = System.currentTimeMillis();
				LinkedList<Channel> decomposition = NO.NodeOrderHeuristic_ImP();//NO.ChainOrderHeuristic_ImS();//NO.NodeOrderHeuristic_ImP();
				stopTime = System.currentTimeMillis();
				decomposition_time=(stopTime-startTime);
				System.out.println("H3: dec:"+decomposition.size()+" time:"+decomposition_time);
				//System.out.println("NodeOrderHeuristic: dec size: "+decompositionNO.size()+ " time:"+decomposition_time);
				//av_dec+=decompositionNO.size();
				//av_decomposition_time+=0;
				
				startTime = System.currentTimeMillis();
				NO.concatenation(decomposition);
				//LinkedList<Channel> decompositionConc = NO.returnChannelDecomposition();
				stopTime = System.currentTimeMillis();
				concatenation_time = (stopTime-startTime);
				System.out.println("concatenation:  channels:"+NO.channels_num()+" concatenation_time:"+concatenation_time);				
				av_cdec+=NO.channels_num();
				av_concatenation_time+=concatenation_time;
				*/
				//System.out.println("agregate time:"+(decomposition_time+concatenation_time)+" channels reduced by:"+(decompositionNO.size()-NO.channels_num() ));
				/*Heuristics oldh=new Heuristics();
				LinkedList<Channel> decompositionOld = oldh.MyHeuristic(G,-1);	
				if(Heuristics.checkDecomposition(G,decompositionNew)!=true){
					System.out.println("new dec is wrong");
					System.exit(0);
				}else if(Heuristics.checkDecomposition(G,decompositionOld)!=true){
					System.out.println("old dec is wrong");
					System.exit(0);
				}
				System.out.print("new: channels:"+NO.channels_num()+" paths:"+decompositionNO.size());
				System.out.println(" old: paths"+decompositionOld.size());
				*/
				/*if(decompositionNO.size()!=decompositionOld.size()){
					System.out.println("ooooops");
					Main.printDecomposition(decompositionNO);
					System.out.println("");
					Main.printDecomposition(decompositionOld);
					System.exit(0);
				}*/
				//Main.printDecomposition(decompositionNO);
				System.out.println("");
				//Main.printDecomposition(decompositionOld);
				Heuristics h=new Heuristics();
				long startTime = System.currentTimeMillis(); 
				LinkedList<Channel> decomposition3 =h.DAG_decomposition_Fulkerson(G);//h.newMethod1_fastest(G,adj_no); h_3.Heuristic3();//h.ChainOrderHeuristic_ImS(G);//h.MyHeuristic(G,0);//h.newMethod1(G,adj_no);//h.DAG_decomposition_Fulkerson(G);h.newMethod1_fastest(G,adj_no);//
				long stopTime = System.currentTimeMillis();
				decomposition_time=(stopTime-startTime);
				System.out.println("Width: dec size: "+decomposition3.size()+ " time:"+decomposition_time);
				
				/*startTime = System.currentTimeMillis();
				h_3.concatenation(decomposition3);
				stopTime = System.currentTimeMillis();
				System.out.println("concatenation:  channels:"+h_v0.channels_num()+" time:"+(stopTime-startTime)+" agregate time:"+decomposition_time+(stopTime-startTime));
				
				System.out.println("");
				*/
				
				//h_v0.printChannelDecomposition();
				//Main.printDecomposition(decomposition1);
			}
			//System.out.println("average:\n width:"+(av_width/counter)+" dec:"+(av_dec/counter)+" cdec:"+(av_cdec/counter));
			//System.out.println("av de time:"+(av_decomposition_time/counter)+" av_concatenation_time:"+(av_concatenation_time/counter));
		}catch (Exception e) {  
            e.printStackTrace();  
        }
	}
}