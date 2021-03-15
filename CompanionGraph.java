import java.util.LinkedList;
import graph.*;

public class CompanionGraph{
	Stratification S;
	LinkedList<LinkedList<IVertex>> vn;
	Stratification.Stratification_info s_info;
	
	public CompanionGraph(Stratification S , Stratification.Stratification_info s_info){
		this.S=S;
		this.s_info=s_info;
		vn = new LinkedList<LinkedList<IVertex>>();
		for(int i=0;i<S.getHeight();++i){
			vn.add(new LinkedList<IVertex>() );
		}
	}
	
	public void addvnodes(LinkedList<Integer> freenodes , int l1, int l2){
		for(int i=0;i<freenodes.size();++i){
			int id = (int)S.getLayer(l1).get(freenodes.get(i)-1).getId();
			int li = s_info.layer_index[id];
			
			IVertex v = new Vertex(id,"");
			LinkedList<IVertex> adjacentTarget = new LinkedList<IVertex>();
			
			System.out.print(id+":\n\t");
			for(IVertex t: S.getLayer(l1).get(li).getAdjacentTargets()){
				int t_id = (int)t.getId();
				int t_level = s_info.layer[t_id];
				
				if(t_level>l2){				//add inherited arcs
					if(adjacentTarget.contains(t)==false){
						adjacentTarget.add(t);
						System.out.print("inh:"+t_id+" ");
					}
				}else if(t_level==l2){		//add transitive arcs
					for(IVertex m: t.getAdjacentTargets()){
						if(adjacentTarget.contains(m)==false){
							System.out.print("trans"+(int)m.getId()+" ");
							adjacentTarget.add(m);
						}
					}
				}
				//System.out.print(""+t_id+" ");
			}
			
			v.setAdjacentTargets( adjacentTarget );
			vn.get(l2).add(v);
			System.out.println("");		
			
			
			//vn.get(l2).add(v);
		}
	}
	
}