import java.util.LinkedList;
class Permutations
{
	private Hierarchical h;
	long counter = 0;
	long tmp = 0;
	int []p;
	
	private int[] opt_i_or;
	private int min_crossings;
	private int []opt_c_or;
	
	int []get_opt_i_or(){return opt_i_or;}
	int []get_opt_p(){return opt_c_or;}
	
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
		opt_i_or = new int[h.getchannels_num()-2];
		opt_c_or = new int[h.getchannels_num()];
		min_crossings = Integer.MAX_VALUE;
		permute( 0, h.getchannels_num()-1);
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
				//System.out.println(""+counter+"\t:"+printIntArray(str) );
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
				//System.out.println(""+counter+"\t:"+printIntArray(str));
				
				
				h.setx_c(str);
				
				BC bc = new BC(h);
				
				if(bc.min_cr()<min_crossings){
					min_crossings = bc.min_cr();
					System.out.println("min cr:"+min_crossings);
					int c=0;
					for(int j:bc.getarray()){
						opt_i_or[c]=j;
						++c;
					}
					for(int j=0;j<str.length;++j){
						opt_c_or[j]=str[j];
					}
				}
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
