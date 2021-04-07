class BC{
	private int []array;
	private int pos;
	private Hierarchical h;
	BC(Hierarchical h){
		this.h=h;
		int s=h.getchannels_num()-2;
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
			System.out.println(array_s());
			//count crossings
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