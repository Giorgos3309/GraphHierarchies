import java.util.LinkedList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap; 


import graph.*;

class Hierarchical{
	private SimpleGraph G;
	private int[] y_coordinates ;
	private MutInteger [] x_coordinates ;
	private MutInteger [] x_c;
	private int channels_num;
	private LGraph LG;
	private ChannelColumns columns;
	
	private double x_dist;
	private double y_dist;
	private int vertices_num;
	
	LinkedList<LEdge> cross_edges;
	LinkedList<LEdge> path_edges;
	LinkedList<LEdge> []pathtransitive_edges;

	MutInteger[] getx_c(){return x_c;}
	
	public Hierarchical(SimpleGraph G,LinkedList<Channel> decomposition){
		this.G=G;
		int benddist = 10;
		vertices_num = G.getVertices().size();
		y_dist = 80+decomposition.size()*benddist;
		//x_dist = 200;
		LG = new LGraph(vertices_num);
		Main.printDecomposition(decomposition);
		
		IVertex[]  array = new IVertex[G.getVertices().size()];
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
					LEdge e = new LEdge(LG.getnode(source),LG.getnode(target));
					if(x_coordinates[source]!=x_coordinates[target]){  		//cross edge
						cross_edges.add(e);
						//int hor_dist = x_coordinates[source]-x_coordinates[target];
						//if(hor_dist<0){hor_dist=hor_dist*(-1);}
						//if(hor_dist>1){
						/*undoif(x_coordinates[source]<x_coordinates[target]){
							//double factor = (channels_num-x_coordinates[target]+1)*15;
							//e.addBend((x_coordinates[source]*x_dist + x_dist/2)-factor, y_coordinates[target]*y_dist-y_dist/2 );
							e.addBend((x_coordinates[source]*x_dist + x_dist/2)-20, y_coordinates[target]*y_dist-y_dist/2 );
						}else{
							//double factor = (x_coordinates[target]+1)*15;
							e.addBend((x_coordinates[source]*x_dist - x_dist/2)+20 , y_coordinates[target]*y_dist-y_dist/2 );
						} 
						LG.addedge(e);*/
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
		x_dist = columns.getmaxwidth()*5*2+200;

			
		//setCordinates();
	}
	public int getchannels_num(){return this.channels_num;}
	void setx_c(int []i){
		if(i.length!=x_c.length){
			System.out.println("ERROR:Hierarchical:setx_c");
			System.exit(0);
		}
		for(int tmp=0;tmp<i.length;++tmp){
			x_c[tmp].set(i[tmp]);
		}
	}
	void setColumnsOrientation(int[] i){
		if(i.length!=channels_num-2){System.out.println("Hierarchical:setColumnsOrientation:ERROR");System.exit(0);}
		
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
		for(IVertex v:G.getVertices()){
			int id = (int)v.getId();
			double x = x_coordinates[id].val()*x_dist;
			double y = y_coordinates[id]*y_dist;
			LG.getnode(id).setx(x);
			LG.getnode(id).sety(y);
		}
		CrossEdgesToLG(  cross_edges);
		PathEdgesToLG( path_edges);
		PathTrEdgesToLG(pathtransitive_edges,columns);
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
	private void CrossEdgesToLG( LinkedList<LEdge> cross_edges){
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
	ChannelColumns getColumns(){return this.columns;}
	static class IntervalNode{
		int id;
		int indegree;
		int outdegree;
		LinkedList<IntervalEdge> outgoing;
		LinkedList<IntervalEdge> incoming;
		IntervalNode(int id){
			outgoing = new LinkedList<IntervalEdge>();
			incoming = new LinkedList<IntervalEdge>();
			indegree = 0;
			outdegree = 0;
		}
		int getoutdegree(){return this.outdegree;}
		int getindegree(){return this.indegree;}
		void setmaxdegrees(){
			this.indegree=Integer.MAX_VALUE;
			this.outdegree=Integer.MAX_VALUE;
		} 
		void addoutgoing(LEdge e){
			this.outgoing.add( new IntervalEdge(e) );
			++outdegree;
		}
		void addincoming(LEdge e){
			this.incoming.add( new IntervalEdge(e) );
			++indegree;
		}
		void decrease_ind(){--indegree;}
		void decrease_outd(){--outdegree;}
		int getId(){return this.id;}
	}
	static class IntervalEdge{
		LEdge e;
		boolean visited;
		IntervalEdge(LEdge e){
			this.e = e;
			this.visited=false;
		}
		int getsourceId(){return e.getsourceId();}
		int gettargetId(){return e.gettargetId();}
		void setVisited(){this.visited=true;}
		boolean isVisited(){return this.visited;}
	}
	
	private Intervals createIntervals(LinkedList<LEdge> []pathtransitive_edges){
		Intervals intervals = new Intervals(channels_num);
		for(int c=0;c<channels_num;++c){
			int c_size = pathtransitive_edges[c].size();
			HashMap<Integer,IntervalNode> info = new HashMap<Integer, IntervalNode>();
			//HashMap<Integer, LinkedList<LEdge>> id_outgoing = new HashMap<Integer, LinkedList<LEdge>>();
			//HashMap<Integer, LinkedList<LEdge>> id_incoming = new HashMap<Integer, LinkedList<LEdge>>();
			//System.out.println("hereeeeee20\n");
			for(LEdge e:pathtransitive_edges[c]){
				Integer source = new Integer(e.getsourceId());
				Integer target = new Integer(e.gettargetId());
				 
				if(info.get(source)==null){
					info.put(source,new IntervalNode(source));
				}
				info.get(source).addoutgoing(e);
				
				if(info.get(target)==null){
					info.put(target,new IntervalNode(target));
				}
				info.get(target).addincoming(e);
				
				/*if(id_outgoing.get(source)==null){
					id_outgoing.put(source,new LinkedList<LEdge>() );
					id_outgoing.get(source).add(e);
				}else{					
					id_outgoing.get(source).add(e);
				}
				
				if(id_incoming.get(target)==null){
					id_incoming.put(target,new LinkedList<LEdge>() );
					id_incoming.get(target).add(e);
				}else{					
					id_incoming.get(target).add(e);
				}*/
			}
			
			//System.out.println("hereeeeee10\n");
			MaxHeap heap= new MaxHeap( info );
			//System.out.println("hereeeeee11\n");
			LinkedList<LEdge> interval = heap.extractMax();
			//System.out.println("hereeeeee12\n");
			//System.out.println("-----------CHANNEL"+c+"---------------");
			while(interval!=null){
				intervals.addInterval(c,interval);
				//System.out.print("Interval[");
				//for(LEdge e:interval){
				//	System.out.print(""+e.getsourceId()+"->"+e.gettargetId()+",");
				//}
				//System.out.println("]");
				interval = heap.extractMax();
			}
			
		}
		
		return intervals;
		/*for(int c=0;c<decomposition.size();++c)
		{
			int channel_size = decomposition.get(c).getVertices().size();
			int []outdegree_ids = new int[channel_size];
			int []indegree_ids = new int[channel_size];
			int[] outdegree_counter = new int[channel_size];
			int[] indegree_counter = new int[channel_size];
			
			for(int i=0;i<channel_size;++i)
			{
				IVertex s = decomposition.get(c).getVertices().get(i);
				int s_id = (int)s.getId();
				
				if(i<decomposition.get(c).getVertices().size()-1)
				{
					IVertex suc = decomposition.get(c).getVertices().get(i+1);
					if(s.getAdjacentTargets().contain(suc))
					{
						outdegree_counter[i] = s.getAdjacentTargets().size()-1;
					}else
					{
						outdegree_counter[i] = s.getAdjacentTargets().size();
					}
				}else
				{
					outdegree_counter[i] = s.getAdjacentTargets().size();
				}
				outdegree_ids[i] = s_id;
				
				if(i>0)
				{
					IVertex pred = decomposition.get(c).getVertices().get(i-1);
					if(s.getAdjacentSources().contain(pred))
					{
						indegree_counter[i] = s.getAdjacentSources().size()-1;
					}else
					{
						indegree_counter[i] = s.getAdjacentSources().size();
					}
				}else
				{
					indegree_counter[i] = s.getAdjacentSources().size();
				}
				indegree_ids[i] = s_id;
				
			}
			
			
			
		}*/
	}
	
	/*public void printEdges(){
		System.out.println("cross edges:");
		for(LEdge e:cross_edges){
			System.out.println(""+e.getsource()+"->"+e.gettarget());
		}
		
		System.out.println("\npath edges:");
		for(LEdge e:path_edges){
			System.out.println(""+e.getsource()+"->"+e.gettarget());
		}
		
		System.out.println("\npath transitive edges:");
		for(LEdge e:pathtransitive_edges){
			System.out.println(""+e.getsource()+"->"+e.gettarget());
		}
	}
	*/
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
		
		for(int i=0;i<array.length;++i){
			System.out.println("("+x_coordinates[i]+","+y_coordinates[i]+")");
		}
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
	
	public static void print_gml(LGraph LG){
		try {
			/*File myObj = new File("filename.txt");
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			} else {
				System.out.println("File already exists.");
			}*/
			
			String gml = "graph\n[\n\tdirected\t1\n";
			
			for(int i=0;i<LG.getnodessize();++i){
				gml+=LG.toString_n(i);
			}
			for(int i=0;i<LG.getedgessize();++i){
				gml+=LG.toString_e(i);
			}
			
			
			gml+="]\n";
			//System.out.println(gml);
			FileWriter myWriter = new FileWriter("GMLoutput.gml");
			myWriter.write(gml);
			myWriter.close();
			
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	
	public static void print_gml(LGraph LG,String name,String Directory){
		try {
			
			String gml = "graph\n[\n\tdirected\t1\n";
			
			for(int i=0;i<LG.getnodessize();++i){
				gml+=LG.toString_n(i);
			}
			for(int i=0;i<LG.getedgessize();++i){
				gml+=LG.toString_e(i);
			}
			
			
			gml+="]\n";
			//System.out.println(gml);
			FileWriter myWriter = new FileWriter(Directory+name);//"GMLoutput.gml");
			myWriter.write(gml);
			myWriter.close();
			
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	
	static LinkedList<LineSegment> concatanation_ls(LinkedList<LineSegment> l1,LinkedList<LineSegment> l2){
		LinkedList<LineSegment> res = new LinkedList<LineSegment>();
		for(LineSegment s:l1){
			res.add(s);
		}
		for(LineSegment s:l2){
			res.add(s);
		}
		return res;
	}
	
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
	
	static void mainTest(){
		Reader r = new Reader();
		final File folder = new File("F:\\courses\\master_thesis\\Graph decomposition code\\code_for_the_student\\inputgraphs");
		LinkedList<File> files = new LinkedList<File>();
		Main.listFilesForFolder(folder,files);
		
		
		Heuristics h = new Heuristics();
		
		int graphs_num = 0;
		
		JSONinfo metrics = new JSONinfo();
		
		try {
			for(File f: files) {
				System.out.println("Processing: " + graphs_num +"/"+ files.size());
				SimpleGraph G = r.read(f);
				G.setAdjacency();
				
				Main.setTopologicalIds(G);
				
				LinkedList<Channel> decomposition = h.DAG_decomposition_Fulkerson(G);
				
				Hierarchical pbf = new Hierarchical(G,decomposition);
				pbf.setCordinates();
				
				
				Aesthetics aesthetics= new Aesthetics(pbf);
			
				LinkedList<LineSegment> bundled_ptr_ls = aesthetics.bundling( aesthetics.get_pathtr_ls() );
				LinkedList<LineSegment> bundled_cr_ls = aesthetics.bundling( aesthetics.get_cross_ls() );
				LinkedList<LineSegment> path_ls = aesthetics.get_path_ls();
		
				int cr_cr=Aesthetics.countCrossings(bundled_cr_ls);
				
				int cr_p=Aesthetics.countCrossings(bundled_cr_ls,path_ls);
				int cr_ptr = Aesthetics.countCrossings(bundled_cr_ls,bundled_ptr_ls);
				int ptr_ptr=Aesthetics.countCrossings(bundled_ptr_ls);
				int total_crossings=ptr_ptr+cr_ptr+cr_p+cr_cr;
				
				int p_b = aesthetics.pathtr_bends();
				int c_b = aesthetics.cross_bends(bundled_cr_ls.size());
				int total_bends = p_b+c_b;
				
				int height = aesthetics.Height();
				int width = aesthetics.Width();
				int erea = width*height;
					
				String dir = "F:\\courses\\master_thesis\\Graph decomposition code\\code_for_the_student\\Drawings\\";
				String fname = ""+f.getName()+"_b"+total_bends+"_c"+total_crossings+"_a"+erea+".gml";
				
				JSONObject g = new JSONObject(); 
				JSONObject info = new JSONObject();
				g.put(fname, info );
				info.put("width",decomposition.size());
				info.put("V",G.getVertices().size());
				info.put("E",G.getEdges().size())
				
				
				
				print_gml(pbf.getLG(),fname,dir);
				
			}
		}catch(Exception e){
			System.out.println(e.toString());
		}
	}
	
	public static void main(String[] args){
		mainTest();
		/*Reader r = new Reader();
		try{
			final File f = new File("F:\\courses\\master_thesis\\Graph decomposition code\\code_for_the_student\\inputGraph.txt");
			SimpleGraph G = r.read(f);
			G.setAdjacency();
			Main.setTopologicalIds(G);
			Heuristics h = new Heuristics();
			
			LinkedList<Channel> decomposition = h.DAG_decomposition_Fulkerson(G);
			
			Hierarchical pbf = new Hierarchical(G,decomposition);
			pbf.setCordinates();
			
			Permutations permutation = new Permutations(pbf);
			
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
			//pbf.printEdges();
		}catch(Exception e) {
			System.out.print(e);
		}*/
	}
}
