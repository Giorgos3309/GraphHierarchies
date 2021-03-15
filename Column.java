import java.util.LinkedList;

class Column{
	int freepos;
	LinkedList<Interval> column;
	Column(){
		freepos=0;
		column = new LinkedList<Interval>();
	}
	boolean add(Interval i){
		int from = i.getfrom();
		if (from>=freepos){
			column.add(i);
			freepos = i.getto();
			return true;
		}
		return false;
	}
	LinkedList<Interval> getcolumn(){return column;}
}