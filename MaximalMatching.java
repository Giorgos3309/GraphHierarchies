// Java implementation of Hopcroft Karp
// algorithm for maximum matching
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


 
public class MaximalMatching{
 
	static final int NIL = 0;
	static final int INF = Integer.MAX_VALUE;
	 
	 
	// A class to represent Bipartite graph 
	// for Hopcroft Karp implementation
	/*public static class BipGraph
	{
		 
		// m and n are number of vertices on left
		// and right sides of Bipartite Graph
		int m, n;
	 
		// adj[u] stores adjacents of left side
		// vertex 'u'. The value of u ranges 
		// from 1 to m. 0 is used for dummy vertex
		List<Integer>[] adj;
	 
		// These are basically pointers to arrays
		// needed for hopcroftKarp()
		int[] pairU, pairV;
		
		public int hopcroftKarp() 
		{
			 
			// pairU[u] stores pair of u in matching where u
			// is a vertex on left side of Bipartite Graph.
			// If u doesn't have any pair, then pairU[u] is NIL
			pairU = new int[m];
	 
			// pairV[v] stores pair of v in matching. If v
			// doesn't have any pair, then pairU[v] is NIL
			pairV = new int[n];
	 
			// Initialize NIL as pair of all vertices
			Arrays.fill(pairU, NIL);
			Arrays.fill(pairV, NIL);
	 
			LinkedList<Integer> QU=new LinkedList<Integer>();
			LinkedList<Integer> QV=new LinkedList<Integer>();
			for(int i=0;i<m;i++){QU.add(i);}
			for(int i=0;i<n;i++){QV.add(i);}
			// Initialize result
			int result = 0;
			while(true){
				int[] visitedBFS = new int[m];
				BFS(QU){
					
				}
				
			}
		}
		
		boolean BFS(LinkedList<Integer> QU){
			LinkedList<Integer> QV = new LinkedList<Integer> QV 
			boolean[] isvisitedU = new int[m];
			boolean[] isvisitedV = new int[n];
			while(!QU.isEmpty()){
				int v = Q.get(0);
				isvisitedU[v]=true;
				for(int a:adj(v)){
					if( pairV(a)==false ){
						
					}else{
						
					}
				}
			}
		}
	}		*/
	
	
	// A class to represent Bipartite graph 
	// for Hopcroft Karp implementation
	public static class BipGraph
	{
		 
		// m and n are number of vertices on left
		// and right sides of Bipartite Graph
		int m, n;
	 
		// adj[u] stores adjacents of left side
		// vertex 'u'. The value of u ranges 
		// from 1 to m. 0 is used for dummy vertex
		List<Integer>[] adj;
	 
		// These are basically pointers to arrays
		// needed for hopcroftKarp()
		int[] pairU, pairV, dist;
	 
		public int getm(){return m;} //todo:change to protected
		public int getn(){return n;} //todo:change to protected
		public int[] getpairU(){return pairU;} //todo:change to protected
		public int[] getpairV(){return pairV;} //todo:change to protected
		
		public LinkedList<Integer> getfreenodes(){
			LinkedList<Integer> fn = new LinkedList<Integer>();
			int[] l1 = getpairU();
			for(int i=1;i<getm()+1;++i){
				if(l1[i]==0){
					fn.add(i);
				}
			}
			return fn;
		}
		
		// Returns size of maximum matching
		public int hopcroftKarp() 
		{
			 
			// pairU[u] stores pair of u in matching where u
			// is a vertex on left side of Bipartite Graph.
			// If u doesn't have any pair, then pairU[u] is NIL
			pairU = new int[m + 1];
	 
			// pairV[v] stores pair of v in matching. If v
			// doesn't have any pair, then pairU[v] is NIL
			pairV = new int[n + 1];
	 
			// dist[u] stores distance of left side vertices
			// dist[u] is one more than dist[u'] if u is next
			// to u'in augmenting path
			dist = new int[m + 1];
	 
			// Initialize NIL as pair of all vertices
			Arrays.fill(pairU, NIL);
			Arrays.fill(pairV, NIL);
	 
			// Initialize result
			int result = 0;
	 
			// Keep updating the result while
			// there is an augmenting path.
			while (bfs()) 
			{
				/*System.out.println("BFS: ");
				for(int i=1;i<pairU.length;++i){
					System.out.println("("+i+","+pairU[i]+")");
				}*/
				// Find a free vertex
				for(int u = 1; u <= m; u++)
				 
					// If current vertex is free and there is
					// an augmenting path from current vertex
					if (pairU[u] == NIL && dfs(u)){
						result++;
						/*System.out.println("DFS "+u+": ");
						for(int i=1;i<pairU.length;++i){
							System.out.print("("+i+","+pairU[i]+")");
						}*/
					}
			}
			return result;
		}
	 
		// Returns true if there is an augmenting 
		// path, else returns false
		boolean bfs() 
		{
			 
			// An integer queue
			Queue<Integer> Q = new LinkedList<>();
	 
			// First layer of vertices (set distance as 0)
			for(int u = 1; u <= m; u++) 
			{
				 
				// If this is a free vertex, 
				// add it to queue
				if (pairU[u] == NIL) 
				{
					 
					// u is not matched
					dist[u] = 0;
					Q.add(u);
				}
	 
				// Else set distance as infinite 
				// so that this vertex is 
				// considered next time
				else
					dist[u] = INF;
			}
	 
			// Initialize distance to
			// NIL as infinite
			dist[NIL] = INF;
	 
			// Q is going to contain vertices 
			// of left side only.
			while (!Q.isEmpty()) 
			{
				 
				// Dequeue a vertex
				int u = Q.poll();
	 
				// If this node is not NIL and 
				// can provide a shorter path to NIL
				if (dist[u] < dist[NIL])
				{
					 
					// Get all adjacent vertices of
					// the dequeued vertex u
					for(int i : adj[u])
					{
						int v = i;
	 
						// If pair of v is not considered 
						// so far (v, pairV[V]) is not yet
						// explored edge.
						if (dist[pairV[v]] == INF)
						{
							 
							// Consider the pair and add
							// it to queue
							dist[pairV[v]] = dist[u] + 1;
							Q.add(pairV[v]);
						}
					}
				}
			}
	 
			// If we could come back to NIL using 
			// alternating path of distinct vertices
			// then there is an augmenting path
			return (dist[NIL] != INF);
		}
	 
		// Returns true if there is an augmenting 
		// path beginning with free vertex u
		boolean dfs(int u)
		{
			//System.out.println("");
			if (u != NIL)
			{
				for(int i : adj[u])
				{
					 
					// Adjacent to u
					int v = i;
	 
					// Follow the distances set by BFS
					if (dist[pairV[v]] == dist[u] + 1)
					{
						// System.out.print(""+v+"-");
						// If dfs for pair of v also returns
						// true
						if (dfs(pairV[v]) == true) 
						{
							//System.out.print("["+u+"-"+v+"]");
							pairV[v] = u;
							pairU[u] = v;
							return true;
						}
					}
				}
	 
				// If there is no augmenting path
				// beginning with u.
				dist[u] = INF;
				return false;
			}
			return true;
		}
	 
		// Constructor
		@SuppressWarnings("unchecked")
		public BipGraph(int m, int n)
		{
			this.m = m;
			this.n = n;
			adj = new ArrayList[m + 1];
			//Arrays.fill(adj, new ArrayList<>());
			for(int i=0;i<m+1;++i){
				adj[i]=new ArrayList<>();
			}
		}
	 
		// To add edge from u to v and v to u
		public void addEdge(int u, int v) 
		{
			 
			// Add u to v’s list.
			adj[u].add(v); 
		}
	}
	 
	// Driver code
	/*public static void main(String[] args) 
	{
		BipGraph g = new BipGraph(6, 5);
		int tmp1=0;
		g.addEdge(1, 2);
		g.addEdge(1, 3);
		g.addEdge(2, 1);
		g.addEdge(3, 2);
		g.addEdge(4, 2);
		g.addEdge(4, 4);
	 
		int tmp=0;
		System.out.println("Size of maximum matching is " +
						   g.hopcroftKarp());
		for(int i=1;i<g.pairU.length;++i){
			System.out.println("("+i+","+g.pairU[i]+")");
		}


		BipGraph g2 = new BipGraph(3, 5);
		g2.addEdge(1, 1);
		g2.addEdge(2, 2);
		g2.addEdge(2, 3);
		g2.addEdge(2, 4);
		g2.addEdge(3, 4);
		g2.addEdge(3, 5);
		System.out.println("Size of maximum matching is " +
						   g2.hopcroftKarp());
		for(int i=1;i<g2.pairU.length;++i){
			System.out.println("("+i+","+g2.pairU[i]+")");
		}
	}*/
}