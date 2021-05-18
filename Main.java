
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;

import org.json.*;

import graph.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Main {
	
	static MaximalMatching.BipGraph BipGraphOf(Stratification S,Stratification.Stratification_info s_i,int l1 ,int l2){
		LinkedList<LinkedList<IVertex>> V=S.getStratification();
		MaximalMatching.BipGraph bg = new MaximalMatching.BipGraph(V.get(l1).size(), V.get(l2).size());
		for(int i=0;i<V.get(l1).size();++i){
			IVertex source = V.get(l1).get(i);
			//System.out.print("SOURCE:"+(int)source.getId()+"\n\t");
			for(IVertex target:source.getAdjacentTargets()){
				int t = (int)target.getId();
				//System.out.print("target:"+t+" \n");
				if(s_i.layer[t]==l2){
					bg.addEdge(i+1, s_i.layer_index[t]+1 );
					System.out.println("EDGE: "+(i+1)+" -> " +(s_i.layer_index[t]+1) );
				}
			}
			
		}
		return bg;
	}
	/*public static void listFilesForFolder(final File folder,LinkedList<String> filenames) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry,filenames);
			} else {
				String name = fileEntry.getName();
				filenames.add(name);
				System.out.println(fileEntry.getName());
				//graphs.add(r.read(".\\inputgraphs\\"+name));
			}
		}
	}*/
	public static void listFilesForFolder(final File folder,LinkedList<File> files) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry,files);
			} else {
				files.add(fileEntry);
				//System.out.println(fileEntry.getAbsolutePath()+" , "+fileEntry.getName());
			}
		}
	}
	protected static void printDecomposition(LinkedList<Channel> decomposition){
		int c_counter=0;
		for(Channel C: decomposition) {
			System.out.print(""+c_counter+" CHANNEL:");
			for(IVertex iv: C.getVertices()){
				System.out.print(" "+iv.getLabel());
			}
			System.out.println("");
			c_counter=c_counter+1;
		}
	}
	
	
	
	/*private static void test(DecMethod dm , SimpleGraph G, String hn , JSONObject info,int depth){
		try{
			long endTime;
			long startTime;
			long duration;
			LinkedList<Channel> decomposition;
			
			
			startTime = System.currentTimeMillis();
			decomposition = dm.dec_method(G,depth);
			endTime = System.currentTimeMillis();
			duration = endTime - startTime;
			
			//System.out.println("Size of the decomposition computed by "+hn+": "+decomposition.size());
			//System.out.println("duration= "+ duration);
			//if(Heuristics.checkDecomposition(G,decomposition)!=true){
			//	System.out.print("Decomposition computed by "+hn+" was incorrect\n");
			//	printDecomposition(decomposition);
			//	System.exit(0);
			//}
			JSONObject tmp = info.getJSONObject("dec_heuristics");
			JSONObject h_info = new JSONObject();
			//h_info.put("name",hn);
			h_info.put("width",decomposition.size());
			h_info.put("duration",duration);
			tmp.put(hn, h_info);
			
			
		}catch(Exception e) {
			System.out.print(e);
		}
	}*/
	class Graph_info{
		//JSONObject h_info = new JSONObject();
		private int V;
		private int E;
		private int width;
		private String description;
	}
	
	public static void setTopologicalIds(SimpleGraph G/*,HashMap<Integer,Integer> restore*/){
		TopologicalSort ts = new TopologicalSort(G.getVertices().size());
		IVertex[] array = new IVertex[G.getVertices().size()];
		int c=0;
		for(IVertex v:G.getVertices()){
			int index = (int)v.getId();
			array[index] = v;
			c++;
		}
		
		for( IEdge e:G.getEdges() ){ 
			int source=(int)e.getSource().getId();
			int target=(int)e.getTarget().getId();
			ts.addEdge(source,target);
		}
		LinkedList<Integer> l=ts.topologicalSort();
		
		int counter = 0;
		for(Integer i:l){
			//System.out.println(""+counter+"  "+(int)array[i].getId());
			//restore.put(counter,i);
			array[i].setId(counter);
			counter+=1;
		}
		
	}
	
	public static void main(String[]args) {
		//LinkedList<SimpleGraph>graphs2 = new LinkedList<SimpleGraph>();
		Reader r = new Reader();
		final File folder = new File("F:\\courses\\master_thesis\\Graph decomposition code\\code_for_the_student\\inputgraphs");
		//LinkedList<String> filenames = new LinkedList<String>();
		LinkedList<File> files = new LinkedList<File>();
		//listFilesForFolder(folder,filenames);
		listFilesForFolder(folder,files);
		
		
		Heuristics h = new Heuristics();
		/*DecMethod H2 = h::Heuristic2;
		DecMethod COrderH_S = h::ChainOrderHeuristic_S;
		DecMethod COrderH_S_ls = h::ChainOrderHeuristic_S_lr;
		DecMethod COrderH_ImS = h::ChainOrderHeuristic_ImS;
		DecMethod NOrderH_S = h::NodeOrderHeuristic_S;
		DecMethod NOrderH_ImS = h::NodeOrderHeuristic_ImS;
		DecMethod H3 = h::Heuristic3;
		DecMethod H3_Pred = h::Heuristic3_Pred;
		*/
		//JSONObject jgraphs = new JSONObject();
		int graphs_num = 0;
		JSONinfo metrics = new JSONinfo();
		try {
			for(File f: files) {
				System.out.println("Processing: " + graphs_num +"/"+ files.size());
				SimpleGraph G = r.read(f);
				G.setAdjacency();
				//setTopologicalIds(G);
				/*System.out.println("NODES:");
				for(IVertex v:G.getVertices()){
					System.out.println(""+(int)v.getId());
				}
				System.out.println("EDGES:");
				for(IEdge e:G.getEdges()){
					System.out.println(""+(int)e.getSource().getId()+"->"+(int)e.getTarget().getId());
				}
				System.exit(0);*/
				int width;
				
				System.out.println("Processing: " + graphs_num +"/"+ files.size());
				
				System.out.println("Descritpion: "+G.getDescription());
				System.out.println("nodes,edges= "+G.getVertices().size()+","+G.getEdges().size());
				System.out.flush();
				LinkedList<Channel> Fulk_dec = h.DAG_decomposition_Fulkerson(G);
				//if(h.checkDecomposition(G,Fulk_dec)==false){System.out.print("ERROR"); return;}
				width = Fulk_dec.size();
				System.out.println("Width= "+ width);
				System.out.flush();
				
				JSONObject info = JSONinfo.create( f.getName() , f.getAbsolutePath( ) , G.getVertices().size() , G.getEdges().size() , width );

				/*info.put("filename", f.getName());
				info.put("AbsolutePath", f.getAbsolutePath() );
				info.put("nodes",G.getVertices().size());
				info.put("edges",G.getEdges().size());
				info.put("width",width);
				info.put("dec_heuristics",new JSONObject());
				*/
				
				///////////////////////////////////////////////////////////
				//////////////Test heuristics/////////////////////////////
				//////////////////////////////////////////////////////////
				
				Test coh = new Test(h,G,Test.COH);
				JSONinfo.addTests(info,coh);	
				Test noh = new Test(h,G,Test.NOH);
				JSONinfo.addTests(info,noh);
				Test h3 = new Test(h,G,Test.H3);
				JSONinfo.addTests(info,h3);
				
				Test coh_d1 = new Test(h,G,1,Test.COH);
				JSONinfo.addTests(info,coh_d1);
				Test noh_d1 = new Test(h,G,1,Test.NOH);
				JSONinfo.addTests(info,noh_d1);
				Test h3_d1 = new Test(h,G,1,Test.H3);
				JSONinfo.addTests(info,h3_d1);
				
				Test coh_d3 = new Test(h,G,3,Test.COH);
				JSONinfo.addTests(info,coh_d3);
				Test noh_d3 = new Test(h,G,3,Test.NOH);
				JSONinfo.addTests(info,noh_d3);
				Test h3_d3 = new Test(h,G,3,Test.H3);
				JSONinfo.addTests(info,h3_d3);
				
				//System.out.println("Checks\n");
				metrics.addGraphMetrics(info);
				/*if(h.checkDecomposition(G,h3.getDecomposition())==false){return;}else{System.out.println("OK");}
				if(h.checkDecomposition(G,coh.getDecomposition())==false){return;}else{System.out.println("OK");}
				if(h.checkDecomposition(G,noh.getDecomposition())==false){return;}else{System.out.println("OK");}
				if(h.checkDecomposition(G,h3_d1.getDecomposition())==false){return;}else{System.out.println("OK");}
				if(h.checkDecomposition(G,coh_d1.getDecomposition())==false){return;}else{System.out.println("OK");}
				if(h.checkDecomposition(G,noh_d1.getDecomposition())==false){return;}else{System.out.println("OK");}
				test(H2,G,"Heuristic2",info);
				
				test(COrderH_ImS,G,"ChainOrderHeuristic_ImS",info);
				test(COrderH_S,G,"ChainOrderHeuristic_S",info);
				
				test(NOrderH_ImS,G,"NodeOrderHeuristic_ImS",info);
				test(NOrderH_S,G,"NodeOrderHeuristic_S",info);
				
				test(H3,G,"Heuristic3",info);
				test(H3_Pred,G,"Heuristic3_Pred",info);
				*/
				//jgraphs.put(f.getName(),info);
				graphs_num++;
				
				/*to uncomment
				SimpleGraph SG=r.read("inputGraph.txt");
				SG.setAdjacency();
				
				Stratification S = new Stratification(SG);//LinkedList<LinkedList<IVertex>> S = Stratification.stratification_bottomup(SG);
				
				Stratification.Stratification_info s_info = new Stratification.Stratification_info( S);
				*/
				//MaximalMatching.BipGraph bg = BipGraphOf(S,s_info,0,1);
				
				//System.out.println("Size of maximum matching is " + bg.hopcroftKarp());
				
				//GEN_CHAIN
				/*LinkedList<IVertex> V0 = S.getLayer(0);
				System.out.println("height:"+S.getHeight());
				for(int i=2;i<S.getHeight();++i){
					MaximalMatching.BipGraph bg = BipGraphOf(S,s_info,i-1,i);
					System.out.println("Size of maximum matching is " + bg.hopcroftKarp());
					System.out.println("m:"+bg.getm());
					LinkedList<Integer> fn = bg.getfreenodes();
					//getFreeNodes(bg,i-1);
					System.out.print("freenodes: ");
					for(Integer pos:fn){
						System.out.print(""+(int)S.getLayer(i-1).get(pos-1).getId()+" ");
					}
					System.out.println();
					
					System.out.print("matching: ");
					for(int pos=1;pos<bg.getm()+1;++pos){
						if(bg.getpairU()[pos]!=0){
							//System.out.print("("+pos+","+bg.getpairU()[pos]+")");
							System.out.print("("+(int)S.getLayer(i-1).get(pos-1).getId()+","+(int)S.getLayer(i).get(bg.getpairU()[pos]-1).getId()+")");
						}
					}
					System.out.println();
				}*/
				
				
				/*to uncomment
				MaximalMatching.BipGraph bg = BipGraphOf(S,s_info,0,1);
				bg.hopcroftKarp();
				CompanionGraph cg = new CompanionGraph(S,s_info);
				LinkedList<Integer> fn = bg.getfreenodes();
				System.out.println("hereee-1");
				cg.addvnodes(fn,0,1);
				
				System.out.println("\n\n\nTRANSITIVE CLOSURE:");
				*/
				
				/*SimpleGraph SG=r.read("inputGraph.txt");
				SG.setAdjacency();
				System.out.println("width="+h.DAG_decomposition_Fulkerson(SG));
				*/
			}
			
			//jgraphs.put("gnum",graphs_num);
		}catch(Exception e) {
			for(int i=0;i<e.getStackTrace().length;++i){
				String methodName = e.getStackTrace()[i].getMethodName();
				System.out.print(methodName+"::");
			}
			System.out.println(e);
		}
		
		try {  
            // Writing to a file  
            File file=new File("JsonFile.json");  
            file.createNewFile();  
            FileWriter fileWriter = new FileWriter(file);  
            System.out.println("Writing JSON object to file");  
            System.out.println("-----------------------");  

            fileWriter.write(metrics.toString());  
            fileWriter.flush();  
            fileWriter.close();  
			//System.out.println(metrics);
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
	//	System.out.println(jgraphs.toString());
	}	
}
