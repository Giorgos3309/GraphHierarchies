import java.util.LinkedList;

class Intervals{
	LinkedList<Interval> []intervals;
	int totalIntervals;
	int channels_num;
	Intervals(int channels_num){
		channels_num = channels_num;
		totalIntervals = 0;
		intervals = new LinkedList[channels_num];
		for(int i=0;i<intervals.length;++i){
			intervals[i] = new LinkedList<Interval>();
		}
	}
	void addInterval(int channel,LinkedList<LEdge> interval){
		intervals[channel].add(new Interval(interval) );
		++totalIntervals;
	}
	LinkedList<Interval> getIntervals(int channel){
		return intervals[channel];
	}
}