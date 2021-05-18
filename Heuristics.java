import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Queue;

import java.io.File;
import java.io.FileWriter;

import java.io.IOException;

import graph.*;

public class Heuristics {
	
	class ConcatData{
		
		boolean isFirst = false;
		boolean isLast = false;
		int channel_index=-1;
	}
	private IVertex DFS_look_up(SimpleGraph G,IVertex v/*,ConcatData[] cd*/, int[] adj_no){
		Set<IVertex> visited = new HashSet<IVertex>();
		LinkedList<IVertex> stack = new LinkedList<IVertex>();
		stack.addFirst(v);
		
		while(stack.isEmpty()==false){
			IVertex cur = stack.get(0);//stack.removeFirst();
			int id = (int)cur.getId();
			
			if(!visited.contains(cur)){
				System.out.println(cur.getLabel());
				visited.add(cur);
				continue;
			}
			if(adj_no[(int)cur.getId()]==-1){
				System.out.println("-"+cur.getLabel());
				stack.removeFirst();
			}
			for(int i=adj_no[(int)cur.getId()]; i>=0;--i ){
				if( !visited.contains(cur.getAdjacentSources().get(i)) ){
					stack.addFirst( cur.getAdjacentSources().get(i) );
				}
			}
			--adj_no[id];
		}
		return null;
	}
	
	private IVertex DFS_look_up(SimpleGraph G,IVertex v,boolean []isDeleted,ConcatData[] cd){
		boolean []isVisited = new boolean[G.getVertices().size()];
		//System.out.println( "hereeeeeeeee"+v.getLabel());
		//isVisited[ (int)v.getId() ]=true;
		IVertex res = DFS_look_up_util(v,isVisited,isDeleted,cd);
		
		return res;
	}
	
	private IVertex DFS_look_up_util(IVertex v,boolean []isVisited,boolean []isDeleted,ConcatData[] cd){
		int vid = (int)v.getId();
		//System.out.println( "\t"+v.getLabel() );
		if (cd[vid].isLast){
			return v;
		}
		isVisited[(int)v.getId()]=true;
		//System.out.println( v.getId() );
		for(IVertex suc:v.getAdjacentSources()){
			//System.out.println( suc.getId() );
			int c_id = (int)suc.getId();
			if(isVisited[c_id]==false && isDeleted[c_id]==false){
				IVertex res = DFS_look_up_util(suc,isVisited,isDeleted,cd);
				if(res!=null){
					return res;
				}
			}
		}
		if(cd[vid].isLast==true){
			isDeleted[(int)v.getId()] = true;
		}
		return null;
	}
	
	/*private IVertex lookupPredecessorDFS(IVertex start,ConcatData[] cd,boolean []isDeleted){ //
		LinkedList<IVertex> stack = new LinkedList<IVertex>();
		Set<IVertex> visited = new HashSet<IVertex>();
		visited.add(start);
		stack.addFirst(start);			//stack.add();
		while (!stack.isEmpty()) {		//
			IVertex v = stack.removeFirst();		
			for (IVertex c : v.getAdjacentSources()) {
				if(visited.contains(c)){
					continue;
				}
				if (cd[(int)c.getId()].isLast){
					return c;
				}
				stack.addFirst(c);
				visited.add(c);
			}
		}
		return null;
	}*/
	
	
	private IVertex lookupPredecessorBFS(IVertex start,ConcatData[] cd){
		LinkedList<IVertex> queue = new LinkedList<IVertex>();
		Set<IVertex> visited = new HashSet<IVertex>();
		visited.add(start);
		queue.addLast(start);
		while (!queue.isEmpty()) {
			IVertex v = queue.removeFirst();		
			for (IVertex c : v.getAdjacentSources()) {
				if(visited.contains(c)){
					continue;
				}
				if (cd[(int)c.getId()].isLast){
					return c;
				}
				queue.addLast(c);
				visited.add(c);
			}
		}
		return null;
	}
	private IVertex lookupSuccessorBFS(IVertex start){
		LinkedList<IVertex> queue = new LinkedList<IVertex>();
		Set<IVertex> visited = new HashSet<IVertex>();
		visited.add(start);
		queue.addLast(start);
		while (!queue.isEmpty()) {
			IVertex v = queue.removeFirst();		
			for (IVertex c : v.getAdjacentTargets()) {
				if(visited.contains(c)){
					continue;
				}
				if (!c.getVisited()){
					return c;
				}
				queue.addLast(c);
				visited.add(c);
			}
		}
		return null;
	}
	
	static private IVertex lookupSuccessorBFS(IVertex start,  int depth){
		LinkedList<LinkedList<IVertex>> queue = new LinkedList<LinkedList<IVertex>>();
		Set<IVertex> visited = new HashSet<IVertex>();
		visited.add(start);
		queue.add(new LinkedList<IVertex>());
		queue.get(0).addLast(start);
		while ( !queue.isEmpty() ) {
			LinkedList<IVertex> newqueue = new LinkedList<IVertex>();
			
			IVertex res = null;
			while ( !queue.get(0).isEmpty() ) {
				IVertex v = queue.get(0).removeFirst();
				
				//System.out.print(""+v.getId()+" ");
				for (IVertex c : v.getAdjacentTargets()) {
					if(visited.contains(c)){
						continue;
					}
					if (!c.getVisited()){
						if(res==null){
							res = c;
						}else if(res.getId()>c.getId()){
							res = c;
						}
						visited.add(c);
						continue;
					}
					newqueue.addLast(c);
					visited.add(c);
				}
			}
			if(res!=null){return res;}
			if(newqueue.isEmpty()==false){
				queue.addLast(newqueue);
			}
			//System.out.println("");
			queue.removeFirst();
			depth--;
			if(depth+1==0){return null;}
		}
		return null;
	}
	static private IVertex lookupPredecessorBFS(IVertex start,ConcatData[] cd,int depth){
		LinkedList<LinkedList<IVertex>> queue = new LinkedList<LinkedList<IVertex>>();
		Set<IVertex> visited = new HashSet<IVertex>();
		visited.add(start);
		queue.add(new LinkedList());
		queue.get(0).addLast(start);
		while (!queue.isEmpty()) {
			LinkedList<IVertex> newqueue = new LinkedList<IVertex>();
			while ( !queue.get(0).isEmpty() ) {
				IVertex v = queue.get(0).removeFirst();
				//System.out.print(""+v.getId()+" ");
				for (IVertex c : v.getAdjacentSources()) {
					if(visited.contains(c)){
						continue;
					}
					if (cd[(int)c.getId()].isLast){
						return c;
					}
					newqueue.addLast(c);
					visited.add(c);
				}
			}
			if(newqueue.isEmpty()==false){
				queue.addLast(newqueue);
			}
			//System.out.println("");
			queue.removeFirst();
			depth--;
			if(depth+1==0){return null;}
		}
		return null;
	}
	/*public static void main(String []args){
		Reader r = new Reader();
		File f = new File("inputGraph.txt");
		SimpleGraph G = r.read(f);
		G.setAdjacency();
		
		IVertex[] array = new IVertex[G.getVertices().size()];
		for(IVertex v: G.getVertices()){
			array[(int)v.getId()]=v;
		}
		
		IVertex res;
		res = lookupSuccessorBFS(array[1],2);
		System.out.println("res:"+res.getId()); res.setVisited(true);
		res = lookupSuccessorBFS(array[1],2);
		System.out.println("res:"+res.getId()); res.setVisited(true);
		res = lookupSuccessorBFS(array[1],2);
		System.out.println("res:"+res.getId()); res.setVisited(true);
		res = lookupSuccessorBFS(array[1],2);
		System.out.println("res:"+res.getId()); res.setVisited(true);
		res = lookupSuccessorBFS(array[1],2);
		System.out.println("res:"+res.getId()); res.setVisited(true);
		res = lookupSuccessorBFS(array[1],2);
		System.out.println("res:"+res.getId()); res.setVisited(true);
		res = lookupSuccessorBFS(array[1],2);
		System.out.println("res:"+res.getId()); res.setVisited(true);
		res = lookupSuccessorBFS(array[1],2);
		System.out.println("res:"+res.getId()); res.setVisited(true);
		res = lookupSuccessorBFS(array[1],2);
		System.out.println("res:"+res.getId()); res.setVisited(true);
		
		System.out.println("");
		//lookupPredecessorBFS(array[19],20);
	}*/
	public LinkedList<Channel> ChainOrderHeuristic(SimpleGraph G, int depth) throws Exception{
		if(depth>1){
			return ChainOrderHeuristic_S(G,depth); 
		}else if(depth<0){
			return ChainOrderHeuristic_S(G,Integer.MAX_VALUE);
		}else{
			return ChainOrderHeuristic_ImS(G);
		}
	}
	public LinkedList<Channel> NodeOrderHeuristic(SimpleGraph G, int depth)throws Exception{
		if(depth>1){
			return NodeOrderHeuristic_P(G,depth); 
		}else if(depth<0){
			return NodeOrderHeuristic_P(G,Integer.MAX_VALUE);
		}else{
			return NodeOrderHeuristic_ImP(G);
		}
	}
	public LinkedList<Channel> MyHeuristic(SimpleGraph G, int depth)throws Exception{
		if(depth>1){
			return Heuristic3_Pred(G,depth); 
		}else if(depth<0){
			return Heuristic3_Pred(G,Integer.MAX_VALUE);
		}else{
			return Heuristic3(G);
		}
	}
	private IVertex lookupSuccessorBFS_lr(IVertex start){
		LinkedList<IVertex> queue = new LinkedList<IVertex>();
		Set<IVertex> visited = new HashSet<IVertex>();
		visited.add(start);
		queue.addLast(start);
		boolean stop = false;
		IVertex res = null;
		while (!queue.isEmpty()) {
			IVertex v = queue.removeFirst();		
			for (IVertex c : v.getAdjacentTargets()) {
				if(visited.contains(c)){
					continue;
				}
				if (!c.getVisited()){
					if(res==null){
						stop = true;
						res = c;	
					}else if(res.getId()>c.getId()){
						res = c;
					}
					
					return c;
				}
				if(!stop){
					queue.addLast(c);
				}
				visited.add(c);
			}
		}
		return res;
	}
	
	private static IVertex isReachableFromBFS(IVertex start,IVertex target){
		LinkedList<IVertex> queue = new LinkedList<IVertex>();
		Set<IVertex> visited = new HashSet<IVertex>();
		visited.add(start);
		queue.addLast(start);
		while (!queue.isEmpty()) {
			IVertex v = queue.removeFirst();		
			for (IVertex c : v.getAdjacentTargets()) {
				if(visited.contains(c)){
					continue;
				}
				if ( c == target){
					return c;
				}
				queue.addLast(c);
				visited.add(c);
			}
		}
		return null;
	}
	
	private IVertex BFS_CBeginning(IVertex CLast,ConcatData[] cd){
		LinkedList<IVertex> queue = new LinkedList<IVertex>();
		Set<IVertex> visited = new HashSet<IVertex>();
		visited.add(CLast);
		queue.addLast(CLast);
		LinkedList<IVertex> list = new LinkedList<IVertex>();
		while (!queue.isEmpty()) {
			IVertex v = queue.removeFirst();		
			for (IVertex c : v.getAdjacentTargets()) {
				if (visited.contains(c))
					continue;
				else{
					int i=(int)c.getId();
					if(cd[i].isFirst){
						return c;
					}
					visited.add(c);
					queue.addLast(c);
				}
			}
		}
		return null;
	}
	
	public static boolean checkDecomposition(SimpleGraph G , LinkedList<Channel> decomposition){
		int gv = G.getVertices().size();
		int dv = 0;
		
		Integer[] array = new Integer[G.getVertices().size()];
		boolean res=true;
		
		for(Channel c: decomposition){
			dv+=c.getVertices().size();
			for(IVertex v:c.getVertices() ){		
				int pos = (int)v.getId();
				if(array[pos]!=null){
					res = false;
				}
				array[pos]=new Integer(1);
			}
		}
		if(gv!=dv){res = false;}
		
		for(Channel c: decomposition){
			for(int i=0;i< c.getVertices().size()-1;++i ){
				IVertex start = c.getVertices().get(i);
				IVertex target = c.getVertices().get(i+1);
				if(isReachableFromBFS(start,target)==null){
					res = false;
				}
			}
		}
		if(res==false){
			System.out.println("Graph decomposition is incorrect");
			return false;
		}
		return true;
	}
	
	public LinkedList<Channel> concatanation(LinkedList<Channel> decomposition, ConcatData[] cd )throws IOException{
		
		for(Channel C:decomposition){
			long first_id = C.getVertices().get(0).getId();
			long last_id =  C.getVertices().getLast().getId();
			cd[(int)first_id].isFirst=true;
			cd[(int)last_id].isLast=true;
		}
		int s=0;
		while( s<decomposition.size() ){
			Channel f_c = decomposition.get(s);
			if(f_c.getVertices().size()==0){
				s++;
				continue;
			}
			IVertex last = f_c.getVertices().getLast();
			IVertex beginning = BFS_CBeginning(last , cd);
			//if(last.getId()==7){
			//	System.out.println(""+last.getId()+" ");
			//}
			if(beginning!=null){
				int index=(int)beginning.getId();
				cd[index].isFirst=false;
				int ci = cd[index].channel_index;
				Channel s_c = decomposition.get(ci);
				if(ci<s){
					for(IVertex v:s_c.getVertices()){
						f_c.addVertex(v);
					}
					s_c.getVertices().clear();
				}else if(ci>s){
					for(IVertex v:s_c.getVertices()){
						f_c.addVertex(v);
						--s;
					}
					s_c.getVertices().clear();
					cd[index].channel_index=s;
				}else{
					System.out.println("Unexpected:Cycle was found");
				}
			}
			s++;
		}
		LinkedList<Channel> new_decomposition=new LinkedList<Channel>();
		for(Channel C:decomposition){
			if( C.getVertices().size()!=0 ){
				new_decomposition.add(C);
			}
		}
		return new_decomposition;
	}
	//Node order heuristic
	
	//Instead of method reachabilityQuery, that look for a path from a vertex to another, in the heuritsitc from jagadhish you 
	//semply check if there exist an edge connecting the two vertices. The jagadish version takes less time, because looking
	//for paths require more time, but probably it finds more channels/chains
	
	public LinkedList<Channel>  Heuristic1(SimpleGraph G) throws Exception{
		LinkedList<Channel> decomposition = new LinkedList<Channel>();
		decomposition.add(new Channel(G,0));
		IVertex[] array = new IVertex[G.getVertices().size()];
		for(IVertex v: G.getVertices()){
			array[(int)v.getId()]=v;
		}
		int i=0;
		while(i<G.getVertices().size()) {
    		IVertex v = array[i];
    		int j=0;
    		Channel C = decomposition.get(j);
    		boolean b = false;
    		while(!b && j<=decomposition.size()) {
    			if(C.getVertices().size()==0) {
    				b=true;
    			}else /*if(G.reachabilityQuery(C.getVertices().getLast(), v)
							) */{//Here, as I wrote in the comment before, if you follow jagadish you simply should check if v is in the adjacency list of C.getVertices()
					for(IVertex vv:C.getVertices().getLast().getAdjacentTargets()){
						if(vv==v){
								b=true;
						}
					}
					//b=true;
					
    			}
    			if(b) {
    				C.addVertex(v);
    			}
				if(j!=decomposition.size()){
					C = decomposition.get(j);
				}
    			j++;
    		}
			if(!b) {
    			Channel C_new = new Channel(G, decomposition.size());
    			C_new.addVertex(v);
    			decomposition.addLast(C_new);
    		}
        	i++;
    	}
		//System.out.println("reachabilityQuery_counter="+reachabilityQuery_counter);
		return decomposition;
	}
	
		public LinkedList<Channel>  Heuristic1_v2_ts(SimpleGraph G) throws Exception{
		LinkedList<Channel> decomposition = new LinkedList<Channel>();
		decomposition.add(new Channel(G,0));
		IVertex[] array = new IVertex[G.getVertices().size()];
		ConcatData[] cd = new ConcatData[G.getVertices().size()]; 
		for(IVertex v: G.getVertices()){
			int index = (int)v.getId();
			array[index]=v;
			cd[index]=new ConcatData();
		}
		decomposition.get(0).addVertex(array[0]);
		for(int i=1;i<G.getVertices().size();++i){
    		IVertex v = array[i];
			boolean b=false;
			int pos=-1;
    		for(int j=0;j<decomposition.size();++j){
				Channel C = decomposition.get(j);
    			//if(v.getId()==9){System.out.println("last node:"+C.getVertices().getLast().getId()+" "+decomposition.size()+" "+decomposition.get(5).getVertices().getLast().getId());}
				for(IVertex vv:C.getVertices().getLast().getAdjacentTargets()){
					if(vv==v){
						if(pos==-1){
							pos=j;
							break;
						}else{
							if(decomposition.get(j).getVertices().getLast().getId() <decomposition.get(pos).getVertices().getLast().getId()){ //?>?
								pos=j;
								//break;
							}
						}
					}
    			}
    		}
			if(pos!=-1){
				decomposition.get(pos).addVertex(v);
			}else{
    			Channel C_new = new Channel(G, decomposition.size());
    			C_new.addVertex(v);
				cd[(int)v.getId()].channel_index=decomposition.size();
    			decomposition.addLast(C_new);
    		}
    	}
		//concatanation

		decomposition=concatanation(decomposition,cd);
		//System.out.println("reachabilityQuery_counter="+reachabilityQuery_counter);
		return decomposition;
	}

	public LinkedList<Channel>  NodeOrderHeuristic_P(SimpleGraph G,int depth) throws Exception{
		LinkedList<Channel> decomposition = new LinkedList<Channel>();
		decomposition.add(new Channel(G,0));
		IVertex[] array = new IVertex[G.getVertices().size()];
		ConcatData[] cd = new ConcatData[G.getVertices().size()]; 
		for(IVertex v: G.getVertices()){
			int index = (int)v.getId();
			array[index]=v;
			cd[index]=new ConcatData();
		}
		decomposition.get(0).addVertex(array[0]);
		cd[(int)array[0].getId()].channel_index=0;
		for(int i=1;i<G.getVertices().size();++i){
    		IVertex v = array[i];
			boolean b=false;
    		for(IVertex pred:v.getAdjacentSources()){
				int ci = cd[(int)pred.getId()].channel_index;
				Channel C = decomposition.get(ci);
				if(decomposition.get(ci).getVertices().getLast()==pred){
					C.addVertex(v);
					cd[(int)v.getId()].channel_index=ci;
					cd[(int)v.getId()].isLast=true;
					cd[(int)pred.getId()].isLast=false;
					b=true;
					break;
				}
				if(b){break;}
    		}
			if(!b){
				IVertex tmp=lookupPredecessorBFS(v,cd,depth);
				if(tmp!=null){
					int ci = cd[(int)tmp.getId()].channel_index;
					decomposition.get(ci).getVertices().addLast(v);
					cd[(int)v.getId()].channel_index = ci;
					cd[(int)v.getId()].isLast = true;
					cd[(int)tmp.getId()].isLast = false;
					b=true;
					
				}
			}
			if(!b) {
    			Channel C_new = new Channel(G, decomposition.size());
    			C_new.addVertex(v);
				cd[(int)v.getId()].channel_index=decomposition.size();
				cd[(int)v.getId()].isFirst=true;
				cd[(int)v.getId()].isLast=true;
    			decomposition.addLast(C_new);
    		}
    	}
		
		//concatanation
		//decomposition=concatanation(decomposition,cd);
		//System.out.println("reachabilityQuery_counter="+reachabilityQuery_counter);
		return decomposition;
	}
	

	public LinkedList<Channel>  NodeOrderHeuristic_ImP(SimpleGraph G) throws Exception{
		LinkedList<Channel> decomposition = new LinkedList<Channel>();
		decomposition.add(new Channel(G,0));
		IVertex[] array = new IVertex[G.getVertices().size()];
		ConcatData[] cd = new ConcatData[G.getVertices().size()]; 
		for(IVertex v: G.getVertices()){
			int index = (int)v.getId();
			array[index]=v;
			cd[index]=new ConcatData();
		}
		decomposition.get(0).addVertex(array[0]);
		cd[(int)array[0].getId()].channel_index=0;
		for(int i=1;i<G.getVertices().size();++i){
    		IVertex v = array[i];
			boolean b=false;
    		for(IVertex pred:v.getAdjacentSources()){//;j<decomposition.size();++j){
				int ci = cd[(int)pred.getId()].channel_index;
				Channel C = decomposition.get(ci);
				if(decomposition.get(ci).getVertices().getLast()==pred){
					C.addVertex(v);
					cd[(int)v.getId()].channel_index=ci;
					b=true;
					break;
				}
				/*Channel C = decomposition.get(j);
    			for(IVertex vv:C.getVertices().getLast().getAdjacentTargets()){
					if(vv==v){
						C.addVertex(v);
						b=true;
						break;
					}
    			}*/
				if(b){break;}
    		}
			if(!b) {
    			Channel C_new = new Channel(G, decomposition.size());
    			C_new.addVertex(v);
				cd[(int)v.getId()].channel_index=decomposition.size();
    			decomposition.addLast(C_new);
    		}
    	}
		
		//concatanation
		//decomposition=concatanation(decomposition,cd);
		//System.out.println("reachabilityQuery_counter="+reachabilityQuery_counter);
		return decomposition;
	}
	
	//Chain order heuristic
	
	//Here I use the adjacency list and I do not use the method reachability query, as jagadish says.
	
	public LinkedList<Channel>  Heuristic2(SimpleGraph G) throws Exception{
		LinkedList<Channel> decomposition = new LinkedList<Channel>();
		IVertex[] array = new IVertex[G.getVertices().size()];
		for(IVertex v: G.getVertices()){
			array[(int)v.getId()]=v;
		}
		int i=0;
		//int reachabilityQuery_counter=0;
		while(i<G.getVertices().size()) {
    		IVertex start = array[i];
    		if(!start.getVisited()) {
    			Channel C = new Channel(G,i);
    			decomposition.add(C);
    			C.addVertex(start);
    			start.setVisited(true);
    			boolean needNewChannel = false;
    			while(!needNewChannel) {
    				boolean vertexAdded=false;
    				LinkedList<IVertex> queue = new LinkedList<IVertex>();
    				Set<IVertex> visited = new HashSet<IVertex>();
    				visited.add(start);
    				queue.addLast(start);
					LinkedList<IVertex>verticesThatCanBeAdded = new LinkedList<IVertex>();
    				while (!queue.isEmpty()) {
    					IVertex v = queue.removeFirst();
    					for (IVertex c : v.getAdjacentTargets()) {
    						if (visited.contains(c))
    							continue;
    						visited.add(c);
    		                queue.addLast(c);
    						if(!c.getVisited()) {
    							verticesThatCanBeAdded.add(c);
    						}
    					}
    				}
    				if(verticesThatCanBeAdded.size()>0) {
    					queue.clear();
    					IVertex toAdd = verticesThatCanBeAdded.get(0);
    					for(IVertex w: verticesThatCanBeAdded) {
    						if(toAdd.getId()>w.getId()) {   //GK: does the graph producer gives vertex IDs based on topological sorting?
    							toAdd=w;						//???
    						}
    					}
    					C.addVertex(toAdd);
    					vertexAdded=true;
    					toAdd.setVisited(true);
		                start=toAdd;
    				}else {
            			needNewChannel=!vertexAdded;// GK:True in every execution
    				}
    			}
    		}
    		i++;
    	}
		return decomposition;
	}
	
	
	public LinkedList<Channel>  ChainOrderHeuristic_ImS(SimpleGraph G) throws Exception{
		LinkedList<Channel> decomposition = new LinkedList<Channel>();
		IVertex[] array = new IVertex[G.getVertices().size()];
		ConcatData[] cd = new ConcatData[G.getVertices().size()];
		//boolean[] visited = new boolean[G.getVertices().size()];
		for(IVertex v: G.getVertices()){
			int index = (int)v.getId();
			array[index]=v;
			cd[index]=new ConcatData();
			//to be removed
			v.setVisited(false);
			//--------------
			//visited[index]=false;
		}
		int i=0;
		while(i<G.getVertices().size()) {
    		IVertex start = array[i];
    		if(!start.getVisited() /*!visited[(int)start.getId()]*/   ) {
    			Channel C = new Channel(G,i);
				C.addVertex(start);
				cd[(int)start.getId()].channel_index=decomposition.size();
    			decomposition.add(C);
    			start.setVisited(true);
				//visited[(int)start.getId()] = true;
    			boolean needNewChannel = false;
    			while(!needNewChannel) {
					needNewChannel=true;
					IVertex toAdd=null;//Max node ID
					for (IVertex c : start.getAdjacentTargets()) {
						if (c.getVisited()/*visited[(int)c.getId()]*/){
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
						toAdd.setVisited(true);
						//visited[(int)toAdd.getId()]=true;
						start=toAdd;
						needNewChannel=false;
					}
					
    				
    			}
    		}
			//concatanation
			//decomposition=concatanation(decomposition,cd);
    		i++;
    	}
		return decomposition;
	}
	
	
	public LinkedList<Channel>  ChainOrderHeuristic_S(SimpleGraph G,int depth) throws Exception{
		LinkedList<Channel> decomposition = new LinkedList<Channel>();
		IVertex[] array = new IVertex[G.getVertices().size()];
		ConcatData[] cd = new ConcatData[G.getVertices().size()];
		//boolean[] visited = new boolean[G.getVertices().size()];
		
		for(IVertex v: G.getVertices()){
			int index = (int)v.getId();
			array[index]=v;
			cd[index]=new ConcatData();
			//to be removed
			v.setVisited(false);
			//--------------
			//visited[index]=false;
		}
		int i=0;
		while(i<G.getVertices().size()) {
    		IVertex start = array[i];
    		if(!start.getVisited() /*!visited[(int)start.getId()]*/) {
    			Channel C = new Channel(G,i);
				C.addVertex(start);
				cd[(int)start.getId()].channel_index=decomposition.size();
    			decomposition.add(C);
    			start.setVisited(true);
				//visited[(int)start.getId()]=true;
    			boolean needNewChannel = false;
    			while(!needNewChannel) {
					needNewChannel=true;
					IVertex toAdd=null;//Max node ID
					for (IVertex c : start.getAdjacentTargets()) {
						if (c.getVisited() /*visited[(int)c.getId()]*/){
							continue;
						}else{
							if(toAdd==null){
								toAdd = c;
							}else if (toAdd.getId()>c.getId()){
								toAdd = c;
							}
						}
					}
					if(toAdd==null){
						toAdd = lookupSuccessorBFS(start,depth);
						/*IVertex tmp=lookupSuccessorBFS(start);
						if(tmp!=null){
							System.out.println("suc of"+start.getId()+" is:"+lookupSuccessorBFS(start).getId());
						}*/
					}
					if(toAdd!=null){
						C.addVertex(toAdd);
						toAdd.setVisited(true);
						//visited[(int)toAdd.getId()]=true;
						start=toAdd;
						needNewChannel=false;
					}
					
    				
    			}
    		}
			//concatanation
			//decomposition=concatanation(decomposition,cd);
    		i++;
    	}
		return decomposition;
	}
	
	public LinkedList<Channel>  ChainOrderHeuristic_S_lr(SimpleGraph G) throws Exception{
	LinkedList<Channel> decomposition = new LinkedList<Channel>();
	IVertex[] array = new IVertex[G.getVertices().size()];
	ConcatData[] cd = new ConcatData[G.getVertices().size()];
	for(IVertex v: G.getVertices()){
		int index = (int)v.getId();
		array[index]=v;
		cd[index]=new ConcatData();
		//to be removed
		v.setVisited(false);
		//--------------
	}
	int i=0;
	while(i<G.getVertices().size()) {
		IVertex start = array[i];
		if(!start.getVisited()) {
			Channel C = new Channel(G,i);
			C.addVertex(start);
			cd[(int)start.getId()].channel_index=decomposition.size();
			decomposition.add(C);
			start.setVisited(true);
			boolean needNewChannel = false;
			while(!needNewChannel) {
				needNewChannel=true;
				IVertex toAdd=null;//Max node ID
				for (IVertex c : start.getAdjacentTargets()) {
					if (c.getVisited()){
						continue;
					}else{
						if(toAdd==null){
							toAdd = c;
						}else if (toAdd.getId()>c.getId()){
							toAdd = c;
						}
					}
				}
				if(toAdd==null){
					toAdd = lookupSuccessorBFS_lr(start);
					/*IVertex tmp=lookupSuccessorBFS(start);
					if(tmp!=null){
						System.out.println("suc of"+start.getId()+" is:"+lookupSuccessorBFS(start).getId());
					}*/
				}
				if(toAdd!=null){
					C.addVertex(toAdd);
					toAdd.setVisited(true);
					start=toAdd;
					needNewChannel=false;
				}
				
				
			}
		}
		//concatanation
		//decomposition=concatanation(decomposition,cd);
		i++;
	}
	return decomposition;
}


	//heuristic based on BFS and layering
	public LinkedList<Channel>  Heuristic3(SimpleGraph G) throws Exception{
		LinkedList<Channel> decomposition = new LinkedList<Channel>();
		IVertex[] array = new IVertex[G.getVertices().size()];
		ConcatData[] cd = new ConcatData[G.getVertices().size()];
		int[] id_counter = new int[G.getVertices().size()]; //indegree counter
		int[] od_counter = new int[G.getVertices().size()];//outdegree counter
		Queue<IVertex> queue = new LinkedList<>();
		for(IVertex v: G.getVertices()){
			int index = (int)v.getId();
			array[index]=v;
			od_counter[index]=v.getAdjacentTargets().size();
			id_counter[index]=v.getAdjacentSources().size();
			cd[index]=new ConcatData();
			if(id_counter[index]==0){
				queue.add(v);
			}
		}
		
		while(queue.isEmpty()==false){
			IVertex cur = queue.remove();
			IVertex toAdd=null;
			int min_outdegree=G.getEdges().size()+1;
			boolean belongToC = false;
			
			if(cd[(int)cur.getId()].channel_index!=-1){
				belongToC=true;
			}else{
				//todo : chooses the vertex with the lowest outdegree if none of my predecessors has acquired it
				for(IVertex tmp_v : cur.getAdjacentSources()){ //choose the immediate predecessor with the lowest outdegree
					int tmp_index = (int)tmp_v.getId();
					int ci = cd[tmp_index].channel_index;
					if(decomposition.get(ci).getVertices().getLast()==tmp_v){
						if(od_counter[tmp_index]<min_outdegree){
							min_outdegree=od_counter[tmp_index];
							toAdd=tmp_v;
						}
					}
				}
			}
			
			if(toAdd!=null){
				int ci = cd[(int)toAdd.getId()].channel_index;
				decomposition.get(ci).addVertex(cur);
				
				cd[(int)cur.getId()].channel_index=ci;
			}else if(!belongToC){
				Channel C = new Channel(G,decomposition.size());
				C.addVertex(cur);
				cd[(int)cur.getId()].channel_index=decomposition.size();
				decomposition.add(C);
			}
			
			boolean assignC = false;
			for (IVertex tmp_v : cur.getAdjacentTargets()) {
				int tmp_index = (int)tmp_v.getId();
				--id_counter[tmp_index];
				if(id_counter[tmp_index]==0){
					queue.add(tmp_v);
				}
				
				if(tmp_v.getAdjacentSources().size()==1 && !assignC){ //if indegree=1 then the vertex and his succesor belong to the same path
					int index=cd[(int)cur.getId()].channel_index;
					decomposition.get(index).getVertices().addLast(tmp_v);
					cd[(int)tmp_v.getId()].channel_index=index;
					assignC=true;
				}
			}
		}
		
		
		return decomposition;
	}
	
	//heuristic based on BFS and layering
	public LinkedList<Channel>  Heuristic3_Pred(SimpleGraph G,int depth) throws Exception{
		LinkedList<Channel> decomposition = new LinkedList<Channel>();
		IVertex[] array = new IVertex[G.getVertices().size()];
		boolean []isDeleted = new boolean[G.getVertices().size()];
		ConcatData[] cd = new ConcatData[G.getVertices().size()];
		int[] id_counter = new int[G.getVertices().size()]; //indegree counter
		int[] od_counter = new int[G.getVertices().size()];//outdegree counter
		Queue<IVertex> queue = new LinkedList<>();
		for(IVertex v: G.getVertices()){
			int index = (int)v.getId();
			array[index]=v;
			od_counter[index]=v.getAdjacentTargets().size();
			id_counter[index]=v.getAdjacentSources().size();
			cd[index]=new ConcatData();
			if(id_counter[index]==0){
				queue.add(v);
			}
		}
	
		
		while(queue.isEmpty()==false){
			IVertex cur = queue.remove();
			IVertex toAdd=null;
			int min_outdegree=G.getEdges().size()+1;
			boolean belongToC = false;
			//if((int)cur.getId()==3){System.out.println("3--->"+cd[(int)cur.getId()].channel_index);}
			if(cd[(int)cur.getId()].channel_index!=-1){
				belongToC=true;
			}else{
				//chooses the vertex with the lowest outdegree if none of my predecessors has acquired it
				for(IVertex tmp_v : cur.getAdjacentSources()){ //choose the immediate predecessor with the lowest outdegree
					int tmp_index = (int)tmp_v.getId();
					int ci = cd[tmp_index].channel_index;
					if(decomposition.get(ci).getVertices().getLast()==tmp_v){
						if(od_counter[tmp_index]<min_outdegree){
							min_outdegree=od_counter[tmp_index];
							toAdd=tmp_v;
						}
					}
				}
				//if((int)cur.getId()==3){System.out.println("3--->"+(toAdd==null));}
				//for(boolean b:isDeleted){System.out.println(b);}
				if(toAdd==null){
					//toAdd = DFS_look_up(G,cur,isDeleted,cd);
					toAdd = lookupPredecessorBFS(cur,cd,depth);
				}
			}
			
			if(toAdd!=null){
				int ci = cd[(int)toAdd.getId()].channel_index;
				cd[(int)cur.getId()].isLast=true;
				cd[(int)toAdd.getId()].isLast=false;
				decomposition.get(ci).addVertex(cur);
				
				cd[(int)cur.getId()].channel_index=ci;
			}else if(!belongToC){
				Channel C = new Channel(G,decomposition.size());
				C.addVertex(cur);
				cd[(int)cur.getId()].channel_index=decomposition.size();
				cd[(int)cur.getId()].isFirst=true;
				cd[(int)cur.getId()].isLast=true;
				decomposition.add(C);
			}
			
			boolean assignC = false;
			for (IVertex tmp_v : cur.getAdjacentTargets()) {
				int tmp_index = (int)tmp_v.getId();
				--id_counter[tmp_index];
				if(id_counter[tmp_index]==0){
					queue.add(tmp_v);
					//i have to reduce the od_counter here.?<--noo
					/*for(IVertex tmp_s : tmp_v.getAdjacentSources()){
						--od_counter[ (int)tmp_s.getId()];
					}*/
				}
				
				if(tmp_v.getAdjacentSources().size()==1 && !assignC){ //if indegree=1 then the vertex and his succesor belong to the same path
					int index=cd[(int)cur.getId()].channel_index;
					decomposition.get(index).getVertices().addLast(tmp_v);
					cd[(int)tmp_v.getId()].channel_index=index;
					cd[(int)tmp_v.getId()].isLast=true;
					cd[(int)cur.getId()].isLast=false;
					assignC=true;
				}
			}
		}
		
		
		return decomposition;
	}


	
	public LinkedList<LinkedList<IVertex>> DAG_decomposition(SimpleGraph G) throws Exception{
		LinkedList<Channel> decomposition = new LinkedList<Channel>();

		LinkedList<IVertex> W = new LinkedList<>();

		int[] layer = new int[G.getVertices().size()];
		int[] id_counter = new int[G.getVertices().size()]; //indegree counter
		int[] od_counter = new int[G.getVertices().size()];//outdegree counter
		LinkedList<IVertex> V0 = new LinkedList<>();
		LinkedList<LinkedList<IVertex>> V = new LinkedList<LinkedList<IVertex>>();
		int i = 0;
		for(IVertex v: G.getVertices()){
			int index = (int)v.getId();
			//array[index]=v;//?? do i need it
			od_counter[index]=v.getAdjacentTargets().size();
			id_counter[index]=v.getAdjacentSources().size();
			if(od_counter[index]==0){
				V0.add(v);
				layer[index]=0;
				//System.out.println("WOW"+index);
				for(IVertex adj:v.getAdjacentSources()){
					if(adj.getVisited()==false){
						W.add(adj);
						adj.setVisited(true);
					}
				}
			}else{
				layer[index]=-1;
			}
		}
		V.add( V0 );
		
		while(!W.isEmpty()){
			for(int j=0;j<W.size();++j){
				//System.out.println("aaaaaaaa3="+j);
				IVertex v = W.get(j);
				//System.out.println("aaaaaaaa4");
				int k=0;
				LinkedList<IVertex> toremove=new LinkedList<>();
				for(IVertex ch:v.getAdjacentTargets()){
					//System.out.println("aaaaaaaa8");
					int index = (int)ch.getId();
					if(layer[index]==i){
						k=k+1;
						if((int)v.getId()==10){
							System.out.println("dddddddddddeka "+index);
						}
						//System.out.println("aaaaaaaa6");
						toremove.add(ch);
						//System.out.println("aaaaaaaa7");
					}
				}
				System.out.println("aaaaaaaa5 "+k);
				int index = (int)v.getId();
				if(od_counter[index]>k){  //if d+(v) > k then remove v from W
					W.remove(j);
				}
				
				for(IVertex vrt:toremove){  //G := G\{v -> v1, ..., v -> vk};
					v.getAdjacentTargets().remove(vrt);
					vrt.getAdjacentSources().remove(v);
				}
				od_counter[index] = od_counter[index] - k;
			}
			//System.out.println("aaaaaaaa2");
			LinkedList<IVertex> VI = new LinkedList<>();
			for(IVertex v:W){
				VI.add(v);
				layer[(int)v.getId()]=i+1;
			}
			V.add(VI);
			i=i+1;
			//System.out.println("aaaaaaaa1");
			W.clear();
			//W:= all the nodes that have at least one child in Vi;
			for( IVertex v:V.get(V.size()-1) ){
				for(IVertex adj:v.getAdjacentSources()){
					adj.setVisited(false);
				}
			}
			for( IVertex v:V.get(V.size()-1) ){
				for(IVertex adj:v.getAdjacentSources()){
					if(adj.getVisited()==false){
						W.add(adj);
						adj.setVisited(true);
					}
				}
			}	
			
		}
		
		return V;
	}

	public LinkedList<Channel> DAG_decomposition_Fulkerson(SimpleGraph G)throws Exception{
		
		IVertex[] vertices_array = new IVertex[G.getVertices().size()];
		Transitive_closure tc = new Transitive_closure(G.getVertices().size());
		for(IVertex v: G.getVertices()){
			int index = (int)v.getId();
			vertices_array[index]=v;
			
			for(IVertex t:v.getAdjacentTargets()){
				int target_v = (int)t.getId(); 
				tc.addEdge(index, target_v); 
			}
		}
		
		tc.transitiveClosure(); 
		
		MaximalMatching.BipGraph bg = new MaximalMatching.BipGraph(tc.getvertices(), tc.getvertices());
		
		for(int i=0;i<tc.getvertices();++i){
			for(int j=0;j<tc.getvertices();++j){
				//System.out.print(""+tc.isconnected(i,j)+" ");
				if(i!=j&&tc.isconnected(i,j)==1){
					bg.addEdge(i+1, j+1 );
				}
			}
			//System.out.print("\n");
		}
		bg.hopcroftKarp();
		//System.out.print("\n");
		int M=0;
		int []left_side = bg.getpairU();
		for(int i=1;i<tc.getvertices()+1;++i){
			//System.out.print(" "+left_side[i]);
			if(left_side[i]!=0){
				++M;
			}
		}
		
		
		LinkedList<Channel>  decomposition = new LinkedList<Channel> ();
		
		int counter=0;
		for(int i:left_side){
			//System.out.print(""+counter+":"+i+" ");
			counter+=1;
		}
		//System.out.println("FULKERSON:");
		boolean []visited = new boolean[tc.getvertices()+1];
		for(int i=1;i<tc.getvertices()+1;++i){
			if(left_side[i]!=0){
				//System.out.println("");
				Channel channel = new Channel(G,decomposition.size());
				int tmp = left_side[i];
				visited[i]=true;
				channel.addVertex(vertices_array[i-1]);
				left_side[i]=0;
				//System.out.println("FULKERSON: "+i);
				while(tmp!=0){
					//System.out.println("FULKERSON: "+tmp);
					visited[tmp]=true;
					channel.addVertex(vertices_array[tmp-1]);
					int z=tmp;
					tmp = left_side[tmp];
					left_side[z]=0;
					
				}
				decomposition.add(channel);
			}else{
				//System.out.println("HERE: "+i+" "+visited[i]);
				if(visited[i]==false){
					Channel channel = new Channel(G,decomposition.size());
					visited[i]=true;
					//System.out.println("\nFULKERSON: "+i);
					channel.addVertex( vertices_array[i-1] );
					decomposition.add(channel);
				}
			}
		}
		if(decomposition.size()!=tc.getvertices()-M){
			System.out.println("ERROR1::Hierarchical::Fulkerson   M:"+(tc.getvertices()-M)+" dec:"+decomposition.size());
			System.exit(0);
		}
		//return tc.getvertices()-M;
		return decomposition;
	}
	
	public static void main(String[]args) {
		//LinkedList<SimpleGraph>graphs2 = new LinkedList<SimpleGraph>();
		Reader r = new Reader();
		final File folder = new File("F:\\courses\\master_thesis\\Graph decomposition code\\code_for_the_student\\inputgraphs");
		//LinkedList<String> filenames = new LinkedList<String>();
		LinkedList<File> files = new LinkedList<File>();
		//listFilesForFolder(folder,filenames);
		Main.listFilesForFolder(folder,files);

		Heuristics h = new Heuristics();
		try{
			int graphs_num = 0;
			int counter = 0;
			long sum=0;
			for(File f: files) {
				//System.out.println(""+(++counter)+":"+f.getName());
				graphs_num +=1;
				SimpleGraph G = r.read(f);
				G.setAdjacency();
				Main.setTopologicalIds(G);
				long startTime = System.currentTimeMillis(); 
				LinkedList<Channel> decomposition =  h.MyHeuristic(G,-1);//h.Heuristic3(G);//h.DAG_decomposition_Fulkerson(G);
				long stopTime = System.currentTimeMillis();
				sum+=(stopTime-startTime);
				checkDecomposition(G,decomposition);
				Main.printDecomposition(decomposition);
				System.out.println(decomposition.size());

				int[] adj_no = new int[ G.getVertices().size() ];
				IVertex[] array = new IVertex[ G.getVertices().size() ];
				for( IVertex v:G.getVertices() ){
					adj_no[(int)v.getId()] = v.getAdjacentSources().size()-1;
					array[Integer.valueOf(v.getLabel())]=v;
				}
				h.DFS_look_up(G,array[16],adj_no);
			}
			//	private IVertex DFS_look_up(SimpleGraph G,IVertex v/*,ConcatData[] cd*/, int[] adj_no){
			System.out.println("time:"+sum);
		}catch (Exception e) {  
            e.printStackTrace();  
        }
	}		
}
