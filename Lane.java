import java.util.LinkedList;

class Lane{
	int no;
	LinkedList<Column> ptr_columns_right;
	LinkedList<Column> ptr_columns_left;
	LinkedList<Column> cr_columns;
	Lane(LinkedList<Column> ptr_columns_left,LinkedList<Column> ptr_columns_right,LinkedList<Column> cr_columns,int lane){
		this.ptr_columns_left=ptr_columns_left;
		this.ptr_columns_right=ptr_columns_right;
		this.cr_columns=cr_columns;
		this.no=lane;
	}
	int getno(){return no;}
	LinkedList<Column> getptr_columns_left(){return ptr_columns_left;}
	LinkedList<Column> getptr_columns_right(){return ptr_columns_right;}
	LinkedList<Column> getcr_columns(){return cr_columns;}
	int getsize(){
		int sum =0;
		if(ptr_columns_left!=null){
			sum+=ptr_columns_left.size();
		}
		if(ptr_columns_right!=null){
			sum+=ptr_columns_right.size();
		}
		if(cr_columns!=null){
			sum=cr_columns.size();
		}
		return sum;
	}
	public String toString(){
		String str="LANE:"+no+"----------------------\nptr_columns_left:\n\t";

		if(ptr_columns_left!=null){
			for(Column clm:ptr_columns_left){
				for(Interval i:clm.getcolumn()){
					str+=("["+i.getfrom()+","+i.getto()+"],");
				}
			}
		}
		str+="\nptr_columns_right:\n\t";
		if(ptr_columns_right!=null){
			for(Column clm:ptr_columns_right){
				for(Interval i:clm.getcolumn()){
					str+=("["+i.getfrom()+","+i.getto()+"],");
				}
			}
		}
		str+="\ncr_columns:\n\t";
		if(cr_columns!=null){
			for(Column clm:cr_columns){
				for(Interval i:clm.getcolumn()){
					str+=("["+i.getfrom()+","+i.getto()+"],");
				}
			}
		}
		return str;
	}
}