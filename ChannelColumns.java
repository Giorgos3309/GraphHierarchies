import java.util.LinkedList;
import java.util.Collections;
import java.util.Comparator;

class ChannelColumns{
	LinkedList<Column> []columns;
	ChannelColumns(int channels_num){
		columns = new LinkedList[channels_num];
		for(int c=0;c<channels_num;++c){
				columns[c] = new LinkedList<Column>();
		}
	}
	void add(int channel,LinkedList<Interval> intervals){
		Collections.sort(intervals, new Comparator<Interval>() {
			@Override
			public int compare(Interval i1, Interval i2) {
				int from1 = i1.getfrom();
				int from2 = i2.getfrom();
				return from1-from2;
			}
		});
		if(intervals.size()==0){return;}
		if(columns[channel].size()==0){
			columns[channel].add(new Column());
		}
		
		for(Interval i:intervals){
			
			boolean added = false;
			for(int col=0;col<columns[channel].size();++col){
				if(columns[channel].get(col).add(i)==true){
					added = true;
					break;
				}
			}
			if(added){continue;}
			columns[channel].addLast(new Column());
			columns[channel].getLast().add(i);
		}
		/*System.out.println("\nColumns------------");
		for(Column c:columns[channel]){
			for(Interval i:c.getcolumn()){
				System.out.print("["+i.getfrom()+","+i.getto()+"] ");
			}
			System.out.println("");
		}*/
	}

	LinkedList<Column> getcolumns(int c){return columns[c];}
}