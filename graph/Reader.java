package graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import graph.Edge;
import graph.IEdge;
import graph.IGraph;
import graph.IVertex;
import graph.SimpleGraph;
import graph.Vertex;

public class Reader {
	//public SimpleGraph read(String input) {
	//	File textFile = new File(input);
	//	SimpleGraph G = new SimpleGraph();
	public SimpleGraph read(File textFile) {
		//File textFile = new File(input);
		SimpleGraph G = new SimpleGraph();
		
		G.setDescription(textFile.getName());
		try {
			FileReader fileReader = new FileReader(textFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			bufferedReader.readLine();
			String line= bufferedReader.readLine();
			//System.out.println("read the graph..nodes");
			while(line.contains("Node")){
				String[]temp =line.split(" ");
				int nodeId = Integer.parseInt(temp[1]);
				G.add(new Vertex(nodeId,""+nodeId));
				line = bufferedReader.readLine();
			}
			//System.out.print("-letti i vertici");
			IVertex[] array = new IVertex[G.vertices.size()];
			for(IVertex v: G.vertices){
				array[(int)v.getId()]=v;
			}
			//System.out.print("-creato l'array");
			line=bufferedReader.readLine();
			//System.out.println(line);
			if( line.contains("dge") ){
				line=bufferedReader.readLine();
			}
			if( line.equals("") ){
				line=bufferedReader.readLine();
			}
			//IMPORTANT: the adjacency_matrix is needed only if the graph have multiple edges. 
			//Not with NetworkX graphs, but yes with PathBased graphs.
			int[][]adjacency_matrix= new int[G.getVertices().size()][G.getVertices().size()];
			//System.out.print("-creata l'adjacency matrix");
			//int count =0;
			while(line!=null){
				//System.out.println(line);
				String[]temp1=line.split(",");
				int idVertex1=Integer.parseInt(temp1[1]);
				int idVertex2=Integer.parseInt(temp1[2]);
				IVertex v1=array[idVertex1];
				IVertex v2=array[idVertex2];
				if(adjacency_matrix[(int)v1.getId()][(int)v2.getId()]==0) {
					adjacency_matrix[(int)v1.getId()][(int)v2.getId()]=1;
					G.add(new Edge(v1,v2));
					//count++;
				}
				line= bufferedReader.readLine();
				//System.out.println(line);
			}
			fileReader.close();
			//System.out.println("read the graph..edges and close!");
		}catch(Exception e){
		}
		return G;
	}
    public void readVertices(IGraph G) {
    	System.out.println("### NODES G ###");
    	int i=0;
    	while(i<G.getVertices().size()) {
    		for(IVertex v: G.getVertices()) {
    			if(v.getId()==i) {
    				System.out.print(v.Text()+", ");
    			}
    		}
    		i++;
    	}

    	System.out.println();
    	System.out.println("### NODES G ###");
    }
   
    
    public void readEdges(IGraph G) {
    	System.out.println("### EDGES G ###");
    	LinkedList<IEdge>list = new LinkedList<IEdge>();
		int i=0;
    	while(i<G.getVertices().size()) {
    		for(IEdge e: G.getEdges()) {
        		if(e.getSource().getId()==i){
        			list.add(e);
        		}
        	}
    		if(list.size()>0) {
    			this.readEdgesInTargetOrder(G.getVertices().size(), list);
    			System.out.println();
    		}
    		i++;
    		list.clear();
    	}
    	System.out.println("### EDGES G ###");
    }
   
    private void readEdgesInTargetOrder(int size, LinkedList<IEdge>list) {
    	int i=0;
    	IVertex w = list.getFirst().getSource();
    	System.out.print(w.Text()+ " ---> ");
    	while(i<size) {
    		for(IEdge e: list) {
    			if(e.getTarget().getId()==i)
    				System.out.print(e.getTarget().Text()+"["+e.getFlow()+"]"+", ");
    		}
    		i++;
    	}
    }
}