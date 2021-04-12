class BC{
	private int []array;
	private int pos;
	private Hierarchical h;
	
	private int[] result;
	private int min_crossings;
	
	private static int cases=0;
	
	int[] getarray(){return this.result;}
	int min_cr(){return this.min_crossings;}
	BC(Hierarchical h){
		int s=h.getchannels_num()-2;
		
		min_crossings = Integer.MAX_VALUE;
		result = new int[s];
		this.h=h;
		
		pos=s-1;
		array = new int[s];
		count(pos);
	}
	BC(int s){
		pos=s-1;
		array = new int[s];
		count(pos);
	}
	private String array_s(){
		String tmp = "";
		for(int i=0;i<array.length;++i){
			tmp+=(""+array[i]+" ");
		}
		return tmp;
	}
	
	private void count(int pos){
		if(pos==-1){
			//System.out.println(array_s());
			//count crossings
			h.setColumnsOrientation(array);
			h.clearLGedges();
			h.clearbends();
			h.setCordinates();
			
			Aesthetics aesthetics= new Aesthetics(h);
			
			int cr_num = h.calcTotalCrosings(aesthetics);
			cases++;
			//System.out.print("No"+cases+" ");
			if(cr_num<min_crossings){
				min_crossings=cr_num;
				for(int i=0;i<array.length;++i){
					result[i]=array[i];
				}
				//System.out.println("min crossings:"+cr_num);
			}
			return;
		}
		if(array[pos]==0){
			count(pos-1);
		}
		array[pos]=1;
		count(pos-1);
		array[pos]=0;
		
	}
	
	public static void main(String []args){
		BC bc = new BC(3);
		//bc.count(2);
	}
}