import java.util.LinkedList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap; 

//import org.json.*;

import graph.*;

public class Demo{
	public static void main(String[] args){
		Reader r = new Reader();
		
		final File folder = new File("F:\\courses\\master_thesis\\build\\inputgraphs");  //todo:replace the source path
		LinkedList<File> files = new LinkedList<File>();
		Main.listFilesForFolder(folder,files);
			
		try{
			int graphs_num = 0;
			for(File f: files) {
				graphs_num +=1;
				System.out.println("Processing: " + f.getName() );
				SimpleGraph G = r.read(f);
				G.setAdjacency();
				System.out.println("Nodes:"+G.getVertices().size()+" Edges:"+G.getEdges().size());
				IVertex[] ts = Main.setTopologicalIds(G);
				System.out.println("Topological sorting finished");
				Heuristics_v0 h = new Heuristics_v0(G,ts);
			
				LinkedList<Channel> decomposition = h.Heuristic3();
				System.out.println("Decomposition finished");
				//Main.printDecomposition(decomposition);
				Hierarchical_v1 pbf = new Hierarchical_v1(G,decomposition);
				pbf.setCordinates();
				System.out.println("Hierarchical Drawing finished");
				
				String dir = "F:\\courses\\master_thesis\\build\\Drawings\\";     //todo:replace the destination path
				String fname = ""+f.getName()+".gml";
				
				Hierarchical.print_gml(pbf.getLG(),fname,dir);
				System.out.println("GML file finished");
			}
			
			
		}catch(Exception e) {
			System.out.print(e);
		}
	}
}