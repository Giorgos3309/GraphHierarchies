// Java implementation of Hopcroft Karp
// algorithm for maximum matching
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class GFG{

static final int NIL = 0;
static final int INF = Integer.MAX_VALUE;

// A class to represent Bipartite graph 
// for Hopcroft Karp implementation
static class BipGraph
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

	// Returns size of maximum matching
	int hopcroftKarp() 
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
			
			// Find a free vertex
			for(int u = 1; u <= m; u++)
			
				// If current vertex is free and there is
				// an augmenting path from current vertex
				if (pairU[u] == NIL && dfs(u))
					result++;
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
		if (u != NIL)
		{
			for(int i : adj[u])
			{
				
				// Adjacent to u
				int v = i;

				// Follow the distances set by BFS
				if (dist[pairV[v]] == dist[u] + 1)
				{
					
					// If dfs for pair of v also returns
					// true
					if (dfs(pairV[v]) == true) 
					{
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
		Arrays.fill(adj, new ArrayList<>());
	}

	// To add edge from u to v and v to u
	void addEdge(int u, int v) 
	{
		
		// Add u to v’s list.
		adj[u].add(v); 
	}
}

// Driver code
public static void main(String[] args) 
{
	
	BipGraph g = new BipGraph(4, 4);
	g.addEdge(1, 2);
	g.addEdge(1, 3);
	g.addEdge(2, 1);
	g.addEdge(3, 2);
	g.addEdge(4, 2);
	g.addEdge(4, 4);

	System.out.println("Size of maximum matching is " +
					g.hopcroftKarp());
	for(int i=0;i<g.m+1;++i){
		System.out.println(""+i+"->"+g.pairV[i]+"");
		
	}
	
	
	g = new BipGraph(3, 5);
	g.addEdge(1, 1);
	g.addEdge(2, 2);
	g.addEdge(2, 3);
	g.addEdge(2, 4);
	g.addEdge(3, 4);
	g.addEdge(3, 5);
 
	System.out.println("m:"+g.m+" n:"+g.n/*+" pairU:"+g.getpairU().length+" pairV:"+g.getpairV().length*/+" Size of maximum matching is "+g.hopcroftKarp());
	System.out.println(" pairU:"+g.pairU.length+" pairV:"+g.pairV.length);
	int j=1;
	for(int i=0;i<g.n+1;++i){
		System.out.println(""+g.pairV[i]+"->"+i+"");
		++j;
	}
}
}

// This code is contributed by sanjeev2552
