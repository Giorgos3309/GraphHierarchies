import graph.*;
import java.util.LinkedList;

//to delete
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
//-----

class SCC{
	
	void explore(boolean []visited,LinkedList<IVertex> postorder,IVertex v){
		int id=(int)v.getId();
		//System.out.println(v.getLabel());
		visited[id]=true;
		for(IVertex t:v.getAdjacentTargets()){
			int tid=(int)t.getId();
			if(visited[tid]==false){
				explore(visited,postorder,t);
			}
		}
		postorder.addFirst(v);
	}
	
	private LinkedList<IVertex> find_postorder(SimpleGraph G){
		LinkedList<IVertex> postorder = new LinkedList<IVertex>();
		boolean []visited=new boolean[G.getVertices().size()];
		for(IVertex v:G.getVertices()){
			int id=(int)v.getId();
			if(visited[id]==false){
				explore(visited,postorder,v);
			}
		}
		return postorder;
	}
	
	private SimpleGraph createGraph(LinkedList<LinkedList<IVertex>> scc,SimpleGraph G){
		IVertex[] scc_vertices=new IVertex[scc.size()];
		SimpleGraph sccGraph=new SimpleGraph();
		
		int []v_scc=new int[G.getVertices().size()];
		int scc_no=0;
		for(LinkedList<IVertex> comp:scc){
			for(IVertex v:comp){
				//System.out.print(v.getLabel()+" ");
				v_scc[(int)v.getId()]=scc_no;
			}
			IVertex comp_v=new Vertex(scc_no,""+scc_no);
			scc_vertices[scc_no]=comp_v;
			sccGraph.add(comp_v);
			scc_no++;
			//System.out.println("");
		}
		
		for(LinkedList<IVertex> comp:scc){
			for(IVertex v:comp){
				int sid=(int)v.getId();
				int s_scc=v_scc[sid];
				//System.out.println(" id"+sid+" s_scc:"+s_scc);
				for(IVertex target:v.getAdjacentTargets()){
					int tid=(int)target.getId();
					int t_scc=v_scc[tid];
					//System.out.println(" tid"+tid+" t_scc:"+t_scc);
					if(t_scc!=s_scc){
						//System.out.println(scc_vertices[s_scc].getLabel()+"  "+scc_vertices[t_scc].getLabel());
						sccGraph.add( new Edge(scc_vertices[s_scc],scc_vertices[t_scc]) );
					}
				}
			}
		}
		
		return sccGraph;
	}
	
	private LinkedList<LinkedList<IVertex>> sccGraph(SimpleGraph G){
		LinkedList<LinkedList<IVertex>> scc = new LinkedList<LinkedList<IVertex>>();
		LinkedList<IVertex> postorder=find_postorder(G);
		boolean []visited=new boolean[G.getVertices().size()];
		for(IVertex cur:postorder){
			int curid=(int)cur.getId();
			if(visited[curid]==false){
				LinkedList<IVertex> component=new LinkedList<IVertex>();
				explore_scc(cur,visited,component);
				scc.add(component);
			}
		}
		
		return scc;
	}
	
	private void explore_scc(IVertex cur,boolean[] visited,LinkedList<IVertex> component){
		int curid=(int)cur.getId();
		visited[curid]=true;
		component.add(cur);
		for(IVertex adj:cur.getAdjacentSources()){
			if(visited[(int)adj.getId()]==false){
				explore_scc(adj,visited,component);
			}
		}
	}
	
	public static void main(String[]args) {
		final File folder = new File("F:\\courses\\master_thesis\\Graph decomposition code\\java\\inputgraphs");
		LinkedList<File> files = new LinkedList<File>();
		Main.listFilesForFolder(folder,files);
		
		Heuristics h = new Heuristics();
		
		try{
			int counter = 0;
			for(File f: files) {
				System.out.println(""+(++counter)+":"+f.getName());
				Reader r = new Reader();
				SimpleGraph G = r.readEdgeList(f);//r.read(f);
				G.setAdjacency();
				System.out.println("G(n="+G.getVertices().size()+" , m="+G.getEdges().size()+")");
				SCC scc=new SCC();
				//IVertex[] ts = Main.setTopologicalIds(G);
				/*LinkedList<IVertex> po=scc.find_postorder(G);
				for(IVertex tmp:po){
					System.out.print(tmp.getLabel()+" ");
				}*/
				//scc.sccGraph(G);
				SimpleGraph sccG=scc.createGraph(scc.sccGraph(G),G);
				sccG.setAdjacency();
				System.out.println("----------");
				System.out.println("sccG(n="+sccG.getVertices().size()+" , m="+sccG.getEdges().size()+")");
				/*for(IVertex v:sccG.getVertices()){
					System.out.println(v.getLabel());
					for(IVertex t:v.getAdjacentTargets()){
						System.out.print(t.getLabel()+" ");
					}
					System.out.println("");
				}*/
				//break;
			}
				
		}catch (Exception e) {  
            e.printStackTrace();  
        }
	}

}
