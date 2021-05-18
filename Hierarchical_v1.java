import java.util.LinkedList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap; 

import org.json.*;

import graph.*;

class Hierarchical_v1{
	private SimpleGraph G;
	private LinkedList<Channel> decomposition;
	IVertex[]  array;
	private int[] y_coordinates ;
	private MutInteger [] x_coordinates ;
	private MutInteger [] x_c;
	LinkedList<Lane> lanes;
	
	private LGraph LG;
	private ChannelColumns columns;
	private ChannelColumns cr_columns;
	
	private int channels_num;
	private int vertices_num;
	
	final double y_dist=80.0;
	final double col_dist = 15.0;
	
	LinkedList<LEdge> cross_edges;
	LinkedList<LEdge> path_edges;
	LinkedList<LEdge> []pathtransitive_edges;

	MutInteger[] getx_c(){return x_c;}
	
	public Hierarchical_v1(SimpleGraph G,LinkedList<Channel> decomposition){
		this.G=G;
		this.decomposition=decomposition;
		//int benddist = 10;
		vertices_num = G.getVertices().size();
		
		LG = new LGraph(vertices_num);
		
		array = new IVertex[G.getVertices().size()];
		y_coordinates = new int[G.getVertices().size()];
		x_coordinates = new MutInteger[G.getVertices().size()];
		x_c = new MutInteger[decomposition.size()];
		channels_num = decomposition.size();
		
		
		cross_edges = new LinkedList<LEdge>();
		path_edges = new LinkedList<LEdge>();
		pathtransitive_edges = new LinkedList[channels_num];
		for(int i=0;i<channels_num;++i){
			pathtransitive_edges[i] = new LinkedList<LEdge>();
			x_c[i] = new MutInteger(i);
		}
	
		
		
		for(int c=0;c<decomposition.size();++c){
			for(IVertex v:decomposition.get(c).getVertices()){
				int index = (int)v.getId();
				array[index]=v;
				x_coordinates[(int)v.getId()] = x_c[c];//c;
				y_coordinates[index]=index;
			}
		}
		
		VerticalCompaction(array);
		
		for(int i=0;i<array.length;++i){
			double x = -1;//x_coordinates[i]*x_dist;
			double y = -1;//y_coordinates[i]*y_dist;
			LG.addnode_c((int)array[i].getId(),x,y,array[i].getLabel());
		}
		
		for(int c=0;c<decomposition.size();++c){
			for(int i=0;i<decomposition.get(c).getVertices().size();++i){
				IVertex s = decomposition.get(c).getVertices().get(i);
				for(IVertex t:s.getAdjacentTargets()){
					int source = (int)s.getId();
					int target = (int)t.getId(); 
					//LEdge e = new LEdge(source,target);
					LEdge e = new LEdge(LG.getnode(source),LG.getnode(target));//LEdge e = new LEdge(LG.getnode(source),LG.getnode(target));
					if(x_coordinates[source]!=x_coordinates[target]){  		//cross edge
						cross_edges.add(e);
					}else if(decomposition.get(c).getVertices().get(i+1)==t){	//path edge
						//undoLG.addedge(e);
						path_edges.add(e);
					}else{														//path transitive edge
						MutInteger channel_index = x_coordinates[source]; 
						pathtransitive_edges[channel_index.val()].add(e);
					}					
				}
			}
		}
		

		Intervals intervals = createIntervals(pathtransitive_edges);
		columns = new ChannelColumns(channels_num);
		for(int c=0;c<channels_num;++c){
			LinkedList<Interval> c_intervals= intervals.getIntervals(c);			
			columns.add(c,c_intervals);
		}
		columns.setRight(channels_num-1);
		//x_dist = columns.getmaxwidth()*5*2+200;

		Intervals cr_intervals = cr_createIntervals(cross_edges);
		cr_columns = new ChannelColumns(channels_num);
		for(int c=0;c<channels_num;++c){
			LinkedList<Interval> c_intervals= cr_intervals.getIntervals(c);			
			cr_columns.add(c,c_intervals);
		}
		
		//setCordinates();
	}
	public int getchannels_num(){return this.channels_num;}
	void setx_c(int []i){
		if(i.length!=x_c.length){
			System.out.println("ERROR:Hierarchical_v1:setx_c");
			System.exit(0);
		}
		for(int tmp=0;tmp<i.length;++tmp){
			x_c[tmp].set(i[tmp]);
		}
	}
	void setColumnsOrientation(int[] i){
		if(i.length!=channels_num-2){System.out.println("Hierarchical_v1:setColumnsOrientation:ERROR");System.exit(0);}
		
		int []mapping = new int[columns.getsize()];
	
		int index=0;
		for(MutInteger mi:x_c){
			mapping[mi.val()] = index;
			index++;
		}
		columns.setLeft( mapping[0] );
		columns.setRight( mapping[columns.getsize()-1] );
	
		for(int j=0;j<i.length;j++){
			//System.out.println("--"+mapping[j+1]);
			if(i[j]==1){
				columns.setRight(mapping[j+1]);
			}else{
				columns.setLeft(mapping[j+1]);
			}
		}
		
	}
	void setCordinates(){
		lanes = new LinkedList<Lane>();
		for(int c=0;c<channels_num;++c){
			LinkedList<Column> columns_right =null;
			if(columns.isRight(c)==false){
				columns_right=columns.getcolumns(c);
			}
			//System.out.println("channel:"+c+"\n");
			LinkedList<Column> columns_left = null;
			if(c>0){
				if(columns.isRight(c-1)){
					columns_left = columns.getcolumns(c-1);
				}
			}
			LinkedList<Column> c_columns = cr_columns.getcolumns(c);
			
			Lane lane = new Lane(columns_left,columns_right,c_columns,c);
			lanes.add(lane);
		}
		
		LinkedList<Column> columns_left = columns.getcolumns(channels_num-1);
		Lane lastlane = new Lane(columns_left,null,null,channels_num);
		lanes.add(lastlane);
		
		//for(Lane lane:lanes){
			//System.out.println("here:\n");
			//System.out.println(lane);
		//}
		
		//VerticalCompaction(array);
		
		
		
		double x_cur_col = col_dist/2;
		double v_x = lanes.get(0).getsize()*col_dist+50.0;
		for(int c=0;c<decomposition.size();++c){
			
			for(IVertex v:decomposition.get(c).getVertices() ){
				int id = (int)v.getId();
				double y = y_coordinates[id]*y_dist;
				LG.getnode(id).setx(v_x);
				LG.getnode(id).sety(y);
			}
			v_x += (lanes.get(c+1).getsize()*col_dist+100.0);
		}
		
		for(LEdge e:path_edges){
			LG.addedge(e);
		}
		for(LinkedList<LEdge> l:pathtransitive_edges){
			for(LEdge e:l){
				LG.addedge(e);
			}
		}
		for(LEdge e:cross_edges){
			LG.addedge(e);
		}
		
		addbends();
		/*Lane lane = lanes.get(c);
			if(lane.getptr_columns_left()!=null){	
				for(Column clm:lane.getptr_columns_left()){
					for(Interval i:clm.getcolumn()){
						for( LEdge e:i.getinterval() ){
							int s = e.getsourceId();
							int t = e.gettargetId();
							if(y_coordinates[t]-y_coordinates[s]>2){
								double y1 = (y_coordinates[s]+1)*y_dist;
								double y2 = (y_coordinates[t]-1)*y_dist;
								e.addBend(x_cur_col,y1);
								e.addBend(x_cur_col,y2);
							}
							x_cur_col+=col_dist;
						}
					}
				}
			}
			if(lane.getcr_columns()!=null){
				for(Column clm:lane.getcr_columns()){
					for(Interval i:clm.getcolumn()){
						for( LEdge e:i.getinterval() ){
						}
					}
				}
				x_cur_col+=(lane.getcr_columns().size()*col_dist);
			}
			if(lane.getptr_columns_right()!=null){
				x_cur_col+=(lane.getptr_columns_right().size()*col_dist);
			}
			x_cur_col+=40+col_dist/2;
			
		*/
		
		/*
		double x_cord = 0.0;
		for(Lane lane:lanes){
			if(lane.getptr_columns_left()!=null){
				
				for(Column clm:lane.getptr_columns_left()){
					for(Interval i:clm.getcolumn()){
						str+=("["+i.getfrom()+","+i.getto()+"],");
					}
				}
			}
		}*/
		/*for(IVertex v:G.getVertices()){
			int id = (int)v.getId();
			double x = x_coordinates[id].val()*x_dist;
			double y = y_coordinates[id]*y_dist;
			LG.getnode(id).setx(x);
			LG.getnode(id).sety(y);
		}
		CrossEdgesToLG(  cross_edges);
		PathEdgesToLG( path_edges);
		PathTrEdgesToLG(pathtransitive_edges,columns);*/
	}
	void addbends(){
		for(int l=0;l<decomposition.size();++l){
			Lane lane = lanes.get(l);
			LNode firstvertexofchannel = LG.getnode((int)decomposition.get(l).getVertices().get(0).getId());
			double x_init = firstvertexofchannel.getx();
			//System.out.println("-->"+decomposition.size()+"-"+x_init);
			//System.out.println(lane.getcr_columns().size());
			double x_tmp = x_init-70;
			if(lane.getptr_columns_right()!=null){	
				for(Column clm:lane.getptr_columns_right()){
					for(Interval i:clm.getcolumn()){
						for( LEdge e:i.getinterval() ){
							int s = e.getsourceId();
							int t = e.gettargetId();
							if(y_coordinates[t]-y_coordinates[s]>2){
								double y1 = (y_coordinates[s]+1)*y_dist;
								double y2 = (y_coordinates[t]-1)*y_dist;
								e.addBend(x_tmp,y1);
								e.addBend(x_tmp,y2);
							}
							if(y_coordinates[t]-y_coordinates[s]==2){
								double y1 = (y_coordinates[s]+1)*y_dist;
								e.addBend(x_tmp,y1);
							}
							//x_tmp+=col_dist;
						}
						//x_tmp+=col_dist;
					}
					x_tmp-=col_dist;
				}
				
			}
			if(lane.getcr_columns()!=null){	
				//System.out.println("ooo"+lane.getcr_columns().size());
				for(Column clm:lane.getcr_columns()){
					//System.out.println("ooo"+lane.getcr_columns().size());
					for(Interval i:clm.getcolumn()){
						for( LEdge e:i.getinterval() ){
							int s = e.getsourceId();
							int t = e.gettargetId();
							//System.out.println("("+s+","+t+")->"+"("+y_coordinates[s]+","+y_coordinates[t]+")");
							if(y_coordinates[t]-y_coordinates[s]>2){
								double y1 = (y_coordinates[s]+1)*y_dist;
								double y2 = (y_coordinates[t]-1)*y_dist;
								e.addBend(x_tmp,y1);
								e.addBend(x_tmp,y2);
							}
							if(y_coordinates[t]-y_coordinates[s]==2){
								double y1 = (y_coordinates[s]+1)*y_dist;
								e.addBend(x_tmp,y1);
							}
							//x_tmp+=col_dist;
						}
					}
					x_tmp-=col_dist;
				}
			}
		}
		
		//last lane
		Lane lane = lanes.get(channels_num);
		LNode firstvertexofchannel = LG.getnode((int)decomposition.get(channels_num-1).getVertices().get(0).getId());
		double x_init = firstvertexofchannel.getx();
		double x_tmp = x_init+70;
		if(lane.getptr_columns_left()!=null){	
			for(Column clm:lane.getptr_columns_left()){
				for(Interval i:clm.getcolumn()){
					for( LEdge e:i.getinterval() ){
						int s = e.getsourceId();
						int t = e.gettargetId();
						if(y_coordinates[t]-y_coordinates[s]>2){
							double y1 = (y_coordinates[s]+1)*y_dist;
							double y2 = (y_coordinates[t]-1)*y_dist;
							e.addBend(x_tmp,y1);
							e.addBend(x_tmp,y2);
						}
						if(y_coordinates[t]-y_coordinates[s]==2){
							double y1 = (y_coordinates[s]+1)*y_dist;
							e.addBend(x_tmp,y1);
						}
						//x_tmp+=col_dist;
					}
					//x_tmp+=col_dist;
				}
				x_tmp+=col_dist;
			}
		}
				
		
	}
	void clearbends(){
		for(LEdge e:cross_edges){
			e.getbends().clear();
		}
		for(LinkedList<LEdge> l:pathtransitive_edges){
			for(LEdge e:l){
				e.getbends().clear();
			}
		}
	}
	void clearLGedges(){
		LG.getEdges().clear();
	}
	
	/*private void CrossEdgesToLG( LinkedList<LEdge> cross_edges){
		for(LEdge e:cross_edges){
			int source = e.getsource().getId();
			int target = e.gettarget().getId();
			if(x_coordinates[source].val()<x_coordinates[target].val()){
				double yfactor = (x_coordinates[target].val()-x_coordinates[source].val())*5;
				//e.addBend((x_coordinates[source]*x_dist + x_dist/2)-factor, y_coordinates[target]*y_dist-y_dist/2 );
				e.addBend((x_coordinates[source].val()*x_dist + x_dist/2)-20, (y_coordinates[target]*y_dist-20)-yfactor );
			}else{
				double yfactor = (x_coordinates[source].val()-x_coordinates[target].val())*5;
				e.addBend((x_coordinates[source].val()*x_dist - x_dist/2)+20 , (y_coordinates[target]*y_dist-20)- yfactor);
			} 
			this.LG.addedge(e);
		}
	}
	private void PathEdgesToLG( LinkedList<LEdge> path_edges){
		for(LEdge e:path_edges){
			LG.addedge(e);
		}
	}
	private void PathTrEdgesToLG( LinkedList<LEdge> []pathtransitive_edges,ChannelColumns columns){
		for(int c=0;c<channels_num;++c){
			LinkedList<Column> c_columns = columns.getcolumns(c);
			int tmp=1;
			boolean right = columns.isRight(c);
			for(Column col:c_columns){
				for(Interval i:col.getcolumn()){
					for(LEdge e:i.getinterval()){
						double s_x;
						double t_x;
						if(right){
							s_x = e.getsource().getx()+25+tmp*10;
							t_x = e.gettarget().getx()+25+tmp*10;
						}else{
							s_x = e.getsource().getx()-25-tmp*10;
							t_x = e.gettarget().getx()-25-tmp*10;
						}
						double s_y = e.getsource().gety()+y_dist/2;
						double t_y = e.gettarget().gety()-y_dist/2;
						e.addBend(s_x,s_y);
						e.addBend(t_x,t_y);
						this.LG.addedge(e);
					}
				}
				++tmp;
			}
		}
	}
	*/
	
	ChannelColumns getColumns(){return this.columns;}
	
	
	private Intervals createIntervals(LinkedList<LEdge> []pathtransitive_edges){
		Intervals intervals = new Intervals(channels_num);
		for(int c=0;c<channels_num;++c){
			int c_size = pathtransitive_edges[c].size();
			HashMap<Integer,Hierarchical.IntervalNode> info = new HashMap<Integer, Hierarchical.IntervalNode>();
			//HashMap<Integer, LinkedList<LEdge>> id_outgoing = new HashMap<Integer, LinkedList<LEdge>>();
			//HashMap<Integer, LinkedList<LEdge>> id_incoming = new HashMap<Integer, LinkedList<LEdge>>();
			//System.out.println("hereeeeee20\n");
			for(LEdge e:pathtransitive_edges[c]){
				Integer source = new Integer(e.getsourceId());
				Integer target = new Integer(e.gettargetId());
				 
				if(info.get(source)==null){
					info.put(source,new Hierarchical.IntervalNode(source));
				}
				info.get(source).addoutgoing(e);
				
				if(info.get(target)==null){
					info.put(target,new Hierarchical.IntervalNode(target));
				}
				info.get(target).addincoming(e);
				
			}
			MaxHeap heap= new MaxHeap( info );
			LinkedList<LEdge> interval = heap.extractMax();
			while(interval!=null){
				intervals.addInterval(c,interval);
				interval = heap.extractMax();
			}			
		}
		return intervals;
	}
	
	private Intervals cr_createIntervals(LinkedList<LEdge> cross_edges){
		Intervals intervals = new Intervals(channels_num);
		
		HashMap<Integer,Hierarchical.IntervalNode> info = new HashMap<Integer, Hierarchical.IntervalNode>();
		
		for(LEdge e:cross_edges){
			Integer source = new Integer(e.getsourceId());
			Integer target = new Integer(e.gettargetId());
			 
			if(info.get(source)==null){
				info.put(source,new Hierarchical.IntervalNode(source));
			}
			info.get(source).addoutgoing(e);
			
			if(info.get(target)==null){
				info.put(target,new Hierarchical.IntervalNode(target));
			}
			info.get(target).addincoming(e);
			
		}
		MaxHeap heap= new MaxHeap( info );
		LinkedList<LEdge> interval = heap.extractMaxInDegree();
		
		while(interval!=null){
			int tmp_pos=helper_vertical_pos(interval);//x_coordinates[interval.get(0).gettargetId()].val();
			boolean next_to_dest = true;
			
			/*if(interval.size()==1){
				if(x_coordinates[interval.get(0).getsourceId()].val()>x_coordinates[interval.get(0).gettargetId()].val()){
					next_to_dest = false;
				}
			}else if(x_coordinates[interval.get(0).getsourceId()].val()==x_coordinates[interval.get(1).getsourceId()].val()){
				next_to_dest=false;
			}
			if(next_to_dest){
				tmp_pos = x_coordinates[interval.get(0).gettargetId()].val();
			}else{
				tmp_pos = x_coordinates[interval.get(0).getsourceId()].val();
			}*/
			intervals.addInterval(tmp_pos,interval);
			interval = heap.extractMaxInDegree();
		}
			
		
		
		return intervals;
		
	}
	
	int helper_vertical_pos(LinkedList<LEdge> interval){
		int counter_l = 0;
		int counter_r = 0;
		for( LEdge e:interval ){
			int s = e.getsourceId();
			int t = e.gettargetId();
			if(x_coordinates[s].val()>x_coordinates[t].val()){
				counter_r+=1;
			}else{
				counter_l+=1;
			}
		}
		int t = interval.get(0).gettargetId();
		if(counter_r>counter_l){
			return x_coordinates[t].val()+1;
		}
		return x_coordinates[t].val();
	}
	
	private void VerticalCompaction(IVertex[]  array){
		for(IVertex v: array ){
			int new_y = -1;
			for(IVertex pred : v.getAdjacentSources()){
				if(y_coordinates[(int)pred.getId()]>new_y){
					new_y = y_coordinates[(int)pred.getId()];
				}
			}
			new_y+=1;
			y_coordinates[(int)v.getId()] = new_y;
		}
	}
	
	public LGraph PBF_layout(IVertex[]  array){
		
		/*for(int i=0;i<array.length;++i){
			System.out.println("("+x_coordinates[i]+","+y_coordinates[i]+")");
		}*
		System.out.println("LG nodes:"+LG.getnodessize()+" edges:"+LG.getedgessize());
		/*for(int i=0;i<array.length;++i){
			double x = x_coordinates[i]*x_dist;
			double y = y_coordinates[i]*y_dist;
			LG.addnode_c((int)array[i].getId(),x,y);
		}*/
		
		//LG.setedges(edges);
		return LG;
	}
	
	public LGraph getLG(){return LG;}
	public SimpleGraph getG(){return G;}
	public int totalLNodes(){return LG.getnodessize();}
	
	
	
	
	
	
	void calcAesthetics(Aesthetics aesthetics){
		//LinkedList<LineSegment> edges = concatanation_ls(aesthetics.get_pathtr_ls(),aesthetics.get_cross_ls());
			LinkedList<LineSegment> bundled_ptr_ls = aesthetics.bundling( aesthetics.get_pathtr_ls() );
			LinkedList<LineSegment> bundled_cr_ls = aesthetics.bundling( aesthetics.get_cross_ls() );
			LinkedList<LineSegment> path_ls = aesthetics.get_path_ls();
	
			//cross-cross cross-path cross-pathtr pathtr-pathtr
			int cr_cr=Aesthetics.countCrossings(bundled_cr_ls);
			System.out.println("cross-cross:"+cr_cr);
			int cr_p=Aesthetics.countCrossings(bundled_cr_ls,path_ls);
			System.out.println("cross-path:"+cr_p);
			int cr_ptr = Aesthetics.countCrossings(bundled_cr_ls,bundled_ptr_ls);
			System.out.println("cross-pathtr:"+cr_ptr);
			int ptr_ptr=Aesthetics.countCrossings(bundled_ptr_ls);
			System.out.println("pathtr-pathtr:"+ptr_ptr);
			int total=ptr_ptr+cr_ptr+cr_p+cr_cr;
			System.out.println("TOTAL CROSSINGS:"+total);
			
			
			int p_b = aesthetics.pathtr_bends();
			int c_b = aesthetics.cross_bends(bundled_cr_ls.size());
			System.out.println("TOTAL BENDS p_b + c_b = "+p_b+" + "+c_b +" = "+(p_b+c_b));
			int height = aesthetics.Height();
			int width = aesthetics.Width();
			System.out.println("Height:"+height);
			System.out.println("Width:"+width);
			System.out.println("Area:"+(height*width));
	}
	int calcTotalCrosings(Aesthetics aesthetics){
		LinkedList<LineSegment> bundled_ptr_ls = aesthetics.bundling( aesthetics.get_pathtr_ls() );
		LinkedList<LineSegment> bundled_cr_ls = aesthetics.bundling( aesthetics.get_cross_ls() );
		LinkedList<LineSegment> path_ls = aesthetics.get_path_ls();

		//cross-cross cross-path cross-pathtr pathtr-pathtr
		int cr_cr=Aesthetics.countCrossings(bundled_cr_ls);
		int cr_p=Aesthetics.countCrossings(bundled_cr_ls,path_ls);
		int cr_ptr = Aesthetics.countCrossings(bundled_cr_ls,bundled_ptr_ls);
		int ptr_ptr=Aesthetics.countCrossings(bundled_ptr_ls);
		int total=ptr_ptr+cr_ptr+cr_p+cr_cr;
		//System.out.println("TOTAL CROSSINGS:"+total);
		return total;
	}
	
    void restoreIds(SimpleGraph G,LGraph LG ,HashMap<Integer, Integer> restoreIds){
		for(IVertex v:G.getVertices()){
			//System.out.println( ""+(int)v.getId() +"    "+ restoreIds.get((int)v.getId()) );
			v.setId(restoreIds.get((int)v.getId()));
		}
		
		for(LNode n:LG.getnodes()){
			n.setId(restoreIds.get((int)n.getId()));
			n.setLabel(restoreIds.get((int)n.getId()));
		}
	}
	public static void main(String[] args){
		//mainTest();
		Reader r = new Reader();

		final File folder = new File("F:\\courses\\master_thesis\\Graph decomposition code\\code_for_the_student\\inputgraphs");
		LinkedList<File> files = new LinkedList<File>();
		Main.listFilesForFolder(folder,files);
			
		Heuristics h = new Heuristics();
		try{
			int graphs_num = 0;
			for(File f: files) {
				graphs_num +=1;
				System.out.println("Processing: " + graphs_num +"/"+ files.size());
				SimpleGraph G = r.read(f);
				G.setAdjacency();
				System.out.println("Edges:"+G.getEdges().size());
				System.out.println("Nodes:"+G.getVertices().size());
				HashMap<Integer, Integer> restoreIds = new HashMap<Integer, Integer>();
				Main.setTopologicalIds(G);
				//System.out.println("Processing");
				
				LinkedList<Channel> decomposition = h.MyHeuristic(G,-1);//h.DAG_decomposition_Fulkerson(G);
				//System.out.println(decomposition.size());
				Main.printDecomposition(decomposition);
				Hierarchical_v1 pbf = new Hierarchical_v1(G,decomposition);
				//System.out.println("Processing1");
				pbf.setCordinates();
				//System.out.println("Processing2");
				
				String dir = "F:\\courses\\master_thesis\\Graph decomposition code\\code_for_the_student\\Drawings\\";
				String fname = ""+f.getName()+".gml";
				
				
				Hierarchical.print_gml(pbf.getLG(),fname,dir);
			}
			
			/*final File f = new File("F:\\courses\\master_thesis\\Graph decomposition code\\code_for_the_student\\inputGraph.txt");
			SimpleGraph G = r.read(f);
			G.setAdjacency();
			
			HashMap<Integer, Integer> restoreIds = new HashMap<Integer, Integer>();
			Main.setTopologicalIds(G,restoreIds);
			//Heuristics h = new Heuristics();
			
			LinkedList<Channel> decomposition = h.DAG_decomposition_Fulkerson(G);
			System.out.println(decomposition.size());
			Hierarchical_v1 pbf = new Hierarchical_v1(G,decomposition);
			pbf.setCordinates();
			
			
			LGraph LG = pbf.getLG();
			
			pbf.restoreIds(G,LG,restoreIds);
			Hierarchical.print_gml(LG);
			*/
			/*Permutations permutation = new Permutations(pbf);
			
			int[] per = permutation.get_opt_p();
			pbf.setx_c(per);
			int []or = permutation.get_opt_i_or();
			pbf.setColumnsOrientation(or);
			pbf.clearLGedges();
			pbf.clearbends();
			pbf.setCordinates();
			
			Aesthetics aesthetics= new Aesthetics(pbf);
			
			//LinkedList<LineSegment> edges = concatanation_ls(aesthetics.get_pathtr_ls(),aesthetics.get_cross_ls());
			LinkedList<LineSegment> bundled_ptr_ls = aesthetics.bundling( aesthetics.get_pathtr_ls() );
			LinkedList<LineSegment> bundled_cr_ls = aesthetics.bundling( aesthetics.get_cross_ls() );
			LinkedList<LineSegment> path_ls = aesthetics.get_path_ls();
	
			//cross-cross cross-path cross-pathtr pathtr-pathtr
			int cr_cr=Aesthetics.countCrossings(bundled_cr_ls);
			System.out.println("cross-cross:"+cr_cr);
			int cr_p=Aesthetics.countCrossings(bundled_cr_ls,path_ls);
			System.out.println("cross-path:"+cr_p);
			int cr_ptr = Aesthetics.countCrossings(bundled_cr_ls,bundled_ptr_ls);
			System.out.println("cross-pathtr:"+cr_ptr);
			int ptr_ptr=Aesthetics.countCrossings(bundled_ptr_ls);
			System.out.println("pathtr-pathtr:"+ptr_ptr);
			int total=ptr_ptr+cr_ptr+cr_p+cr_cr;
			System.out.println("TOTAL CROSSINGS:"+total);
			
			
			int p_b = aesthetics.pathtr_bends();
			int c_b = aesthetics.cross_bends(bundled_cr_ls.size());
			System.out.println("TOTAL BENDS p_b + c_b = "+p_b+" + "+c_b +" = "+(p_b+c_b));
			int height = aesthetics.Height();
			int width = aesthetics.Width();
			System.out.println("Height:"+height);
			System.out.println("Width:"+width);
			System.out.println("Area:"+(height*width));
			//pbf.VerticalCompaction();
			LGraph LG = pbf.getLG();
			print_gml(LG);
			//System.out.println("decomposition size:"+decomposition.size());
			//pbf.printEdges();*/
		}catch(Exception e) {
			System.out.print(e);
		}
	}
}
