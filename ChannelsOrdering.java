import graph.*;

class ChannelsOrdering{
	class CO_Node{
		int id;
		LinkedList<CO_Node> targets;
		LinkedList<CO_Node> sources;
	}
	class CO_Edge{
		CO_Node source;
		CO_Node target;
		int weight;
	}
	static void Order(LinkedList<Channel> decomposition,int[] x_coordinates){
		int [][]array = new int[decomposition.size()][decomposition.size()];
		for(int c = 0;c<decomposition.size();++c){
			for(IVertex v:decomposition.get(c).getVertices()){
				for( IVertex target:v.getAdgacentTarget() ){
					int destx = x_coordinates[target.getId()];
					array[c][destx]+=1;
					array[destx][c]+=1;
				}
			}
		}
		
	}
}