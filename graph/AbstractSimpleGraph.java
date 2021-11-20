package graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;



abstract class AbstractSimpleGraph implements IGraph {
	
	protected final Collection<IVertex> vertices;
	protected final Collection<IEdge> edges;
	protected final Map<IVertex,Collection<IEdge>> incidentEdges;
    
    public AbstractSimpleGraph(){
    	vertices = new HashSet<IVertex>();
        edges = new HashSet<IEdge>();
        incidentEdges = new HashMap<IVertex,Collection<IEdge>>();
    }

	@Override
	public void add(IVertex v) {
		vertices.add(v);
		incidentEdges.put(v, new HashSet<IEdge>());
	}

	@Override
	public boolean add(IEdge e) /*throws Exception*/ {
		if(areAdjacents(e.getSource(), e.getTarget())){
			//throw new Exception("Cannot add multiple edges");
			//System.out.println("Cannot add multiple edges");
			return false;
		}
		IVertex s = e.getSource();
		IVertex t = e.getTarget();
		edges.add(e);
		incidentEdges.get(s).add(e);
		incidentEdges.get(t).add(e);
		return true;
	}

	@Override
	public boolean areAdjacents(IVertex v, IVertex w) {
		return getTargetsOf(v).contains(w);
	}
	public Collection<IVertex> getTargetsOf(IVertex v) {
		Collection<IVertex> list = new HashSet<IVertex>();
		//System.out.println("============"+v.getLabel());
		for(IEdge e: getIncidentEdgesOf(v)){
			//System.out.print(""+e.getOpposite(v).getLabel()+" ");
			if(e.getTarget().getId()==v.getId()){
				//do nothing
			}else{
				list.add(e.getOpposite(v));
			}
		}
		//System.out.println("");
		return list;
	}
	@Override
	public Collection<IVertex> getAdjacentsOf(IVertex v) {
		Collection<IVertex> list = new HashSet<IVertex>();
		System.out.println("============"+v.getLabel());
		for(IEdge e: getIncidentEdgesOf(v)){
			System.out.print(""+e.getOpposite(v).getLabel()+" ");
			list.add(e.getOpposite(v));
		}
		System.out.println("");
		return list;
	}
	
	@Override
	public Collection<IEdge> getIncidentEdgesOf(IVertex v) {
		return incidentEdges.get(v);
	}

	@Override
	public int getDegreeOf(IVertex v) {
		return incidentEdges.get(v).size();
	}

	@Override
	public Collection<IEdge> getEdges() {
		return edges;
	}

	@Override
	public Collection<IVertex> getVertices() {
		return vertices;
	}
	
	@Override
	public void remove(IEdge e) {
		edges.remove(e);
		//incidentEdges.get(e.getSource()).remove(e);
		//incidentEdges.get(e.getTarget()).remove(e);
	}

	@Override
	public void remove(IVertex v) {
		vertices.remove(v);
		//for(IEdge e: getIncidentEdgesOf(v)){
			//remove(e);
		//}
	}
	
	@Override
	public IEdge getEdge(IVertex v1, IVertex v2) {
		IEdge e1 = null;
		for(IEdge e: this.edges) {
			if((e.getSource().equals(v1)&&e.getTarget().equals(v2)||e.getSource().equals(v2)&&e.getTarget().equals(v1))){
				e1=e;
			}
		}
		return e1;
	}
	
	@Override
	public LinkedList<IVertex> bfsVisit(IVertex start) {
		LinkedList<IVertex> queue = new LinkedList<IVertex>();
        Set<IVertex> visited = new HashSet<IVertex>();
        visited.add(start);
        queue.addLast(start);
        LinkedList<IVertex> list = new LinkedList<IVertex>();
        while (!queue.isEmpty()) {
            IVertex v = queue.removeFirst();
            list.add(v);
            for (IVertex c : getAdjacentsOf(v)) {
                if (visited.contains(c))
                    continue;
                visited.add(c);
                queue.addLast(c);
            }
        }
        return list;
	}
	
	@Override
	public Iterator<IVertex> dfsVisit(IVertex start) {
		Set<IVertex> set = new LinkedHashSet<IVertex>();
		doDFS(set, start);
        return set.iterator();
    }

    private void doDFS(Set<IVertex> l, IVertex v) {
        l.add(v);
        for (IVertex c : getAdjacentsOf(v))
            if (!l.contains(c))
                doDFS(l, c);
    }
    
    
    public boolean reachabilityQuery(IVertex start, IVertex end) {
    	boolean output=false;
		LinkedList<IVertex> queue = new LinkedList<IVertex>();
        Set<IVertex> visited = new HashSet<IVertex>();
        visited.add(start);
        queue.addLast(start);
        LinkedList<IVertex> list = new LinkedList<IVertex>();
        while (!queue.isEmpty()) {
            IVertex v = queue.removeFirst();
            list.add(v);
            for (IVertex c : v.getAdjacentTargets()) {
                if (visited.contains(c))
                    continue;
                visited.add(c);
                queue.addLast(c);
                if(c.getId()==end.getId()) {
                	output=true;
                	queue.clear();
                	break;
                }
            }
        }
        return output;
	}


}
