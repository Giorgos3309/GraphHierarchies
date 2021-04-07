import java.util.LinkedList;
class Permutations
{
	private Hierarchical h;
	long counter = 0;
	long tmp = 0;
	int []p;
	public static void main(String[] args)
	{
		String str = "ABCD";
		int []intArray = {1,2,3,4};
		int n = str.length();
		Permutations permutation = new Permutations();
		permutation.permute( 0, n-1);
		//counter=0;
	}
	Permutations(){
	}
	Permutations(Hierarchical h){
		this.h=h;
		p=new int[h.getchannels_num()];
		for(int i=0;i<h.getchannels_num();++i ){
			p[i]=i;
		}
	}
	private String printIntArray(int []a){
		String str = "";
		for(int i:a){
			str+=(""+i+" ");
		}
		return str;
	}

	/**
	* permutation function
	* @param str string to calculate permutation for
	* @param l starting index
	* @param r end index
	*/
	void permute(String str, int l, int r)
	{
		if (l == r){
			counter+=1;
			//if(tmp>str.charAt(str.length()-1) )
				System.out.println(""+counter+"\t:"+str);
		}else
		{
			for (int i = l; i <= r; i++)
			{
				tmp+=1;
				str = swap(str,l,i);
				permute2(str, l+1, r,tmp);
				str = swap(str,l,i);
			}
		}
	}
	
	void permute( int l, int r)
	{
		int []str=p;
		if (l == r){
			counter+=1;
			//if(tmp>str.charAt(str.length()-1) )
				System.out.println(""+counter+"\t:"+printIntArray(str) );
		}else
		{
			for (int i = l; i <= r; i++)
			{
				tmp+=1;
				swap(str,l,i);
				permute2(str, l+1, r,tmp);
				swap(str,l,i);
			}
		}
	}
	
	private void permute2(String str, int l, int r,long tmp)
	{
		if (l == r){
			if( (tmp+(int)'A')>(int)str.charAt(str.length()-1) ){
				counter+=1;
				BC bc = new BC(2);
				//System.out.println(""+counter+"\t:"+str);
			}
		}else
		{
			for (int i = l; i <= r; i++)
			{
				str = swap(str,l,i);
				permute2(str, l+1, r,tmp);
				str = swap(str,l,i);
			}
		}
	}
	
	private void permute2(int[] str, int l, int r,long tmp)
	{
		if (l == r){
			if( (tmp>(int)str[str.length-1] )){
				counter+=1;
				System.out.println(""+counter+"\t:"+printIntArray(str));
				h.setx_c(str);
				h.clearbends();
				h.setCordinates();
				Aesthetics aesth = new Aesthetics(h);
				LinkedList<LineSegment> bundled_ptr_ls = aesth.bundling( aesth.get_pathtr_ls() );
				LinkedList<LineSegment> bundled_cr_ls = aesth.bundling( aesth.get_cross_ls() );
				LinkedList<LineSegment> path_ls = aesth.get_path_ls();
				
				BC bc = new BC(h);
			}
		}else
		{
			for (int i = l; i <= r; i++)
			{
				swap(str,l,i);
				permute2(str, l+1, r,tmp);
				swap(str,l,i);
			}
		}
	}

	/**
	* Swap Characters at position
	* @param a string value
	* @param i position 1
	* @param j position 2
	* @return swapped string
	*/
	public String swap(String a, int i, int j)
	{
		char temp;
		char[] charArray = a.toCharArray();
		temp = charArray[i] ;
		charArray[i] = charArray[j];
		charArray[j] = temp;
		return String.valueOf(charArray);
	}
	public int[] swap(int[] intArray, int i, int j)
	{
		int temp;
		temp = intArray[i] ;
		intArray[i] = intArray[j];
		intArray[j] = temp;
		return intArray;
	}

}

// This code is contributed by Mihir Joshi
