import java.util.HashMap; 
import java.util.LinkedList;

public class MaxHeap { 
    private Hierarchical.IntervalNode[] Heap_outdegree;
    private int size_o; 
	private HashMap<Integer,Integer> heapo_index_of;
	
	private Hierarchical.IntervalNode[] Heap_indegree;
	private int size_i;
	private HashMap<Integer,Integer> heapi_index_of;
	
    private int maxsize; 
	
	private HashMap<Integer,Hierarchical.IntervalNode> info;
    //Hierarchical.IntervalEdge e;
    // Constructor to initialize an 
    // empty max heap with given maximum 
    // capacity. 
    public MaxHeap(HashMap<Integer,Hierarchical.IntervalNode> info) 
    { 
		this.maxsize = info.size(); 

        this.size_o = 0;
		this.size_i = 0;		
        Heap_outdegree = new Hierarchical.IntervalNode[this.maxsize + 1]; 
		Heap_indegree = new Hierarchical.IntervalNode[this.maxsize + 1]; 
		
		Hierarchical.IntervalNode firsto = new Hierarchical.IntervalNode(-1);
		firsto.setmaxdegrees();
        Heap_outdegree[0] = firsto;
		Hierarchical.IntervalNode firsti = new Hierarchical.IntervalNode(-1);
		firsti.setmaxdegrees();
        Heap_indegree[0] = firsti;
		
		
		this.info = info;
		heapo_index_of = new HashMap<Integer,Integer>();
		heapi_index_of = new HashMap<Integer,Integer>();
		
		for (HashMap.Entry<Integer,Hierarchical.IntervalNode> entry : info.entrySet()) {
				//System.out.println(entry.getKey() + "/" + entry.getValue());
				
				Hierarchical.IntervalNode tmp = entry.getValue();
				insert(tmp);
		}
		
         
    } 
  
    // Returns position of parent 
    private int parent(int pos) { return pos / 2; } 
  
    // Below two functions return left and 
    // right children. 
    private int leftChild(int pos) { return (2 * pos); } 
    private int rightChild(int pos) 
    { 
        return (2 * pos) + 1; 
    } 
  
    // Returns true of given node is leaf 
    private boolean isLeaf(int pos,int size) 
    { 
		if (pos > (size / 2) && pos <= size) { 
			return true; 
		}			
        return false; 
    } 
  
    private void swap(Hierarchical.IntervalNode []Heap,HashMap<Integer,Integer> heap_index_of,int fpos, int spos) 
    { 
        Hierarchical.IntervalNode tmp; 
        tmp = Heap[fpos]; 
        Heap[fpos] = Heap[spos]; 
        Heap[spos] = tmp; 
		heap_index_of.put(Heap[fpos].getId(), fpos);
		heap_index_of.put(Heap[spos].getId(), spos);
    } 
  
    // A recursive function to max heapify the given 
    // subtree. This function assumes that the left and 
    // right subtrees are already heapified, we only need 
    // to fix the root. 
    private void maxHeapify(int pos,boolean iHeap) 
    { 
		if(iHeap==false){
			if (isLeaf(pos,size_o)){ 
				return; 
			}
			if (Heap_outdegree[pos].getoutdegree() < Heap_outdegree[leftChild(pos)].getoutdegree() 
				|| Heap_outdegree[pos].getoutdegree() < Heap_outdegree[rightChild(pos)].getoutdegree()) { 
	  
				if (Heap_outdegree[leftChild(pos)].getoutdegree()
					> Heap_outdegree[rightChild(pos)].getoutdegree()) { 
					swap(Heap_outdegree,heapo_index_of,pos, leftChild(pos)); 
					maxHeapify(leftChild(pos),iHeap); 
				} 
				else { 
					swap(Heap_outdegree,heapo_index_of,pos, rightChild(pos)); 
					maxHeapify(rightChild(pos),iHeap); 
				} 
			} 
		}else{
			if (isLeaf(pos,size_i)){ 
				return; 
			}
			if (Heap_indegree[pos].getindegree() < Heap_indegree[leftChild(pos)].getindegree() 
				|| Heap_indegree[pos].getindegree() < Heap_indegree[rightChild(pos)].getindegree()) { 
	  
				if (Heap_indegree[leftChild(pos)].getindegree()
					> Heap_indegree[rightChild(pos)].getindegree()) { 
					swap(Heap_indegree,heapi_index_of,pos, leftChild(pos)); 
					maxHeapify(leftChild(pos),iHeap); 
				} 
				else { 
					swap(Heap_indegree,heapi_index_of,pos, rightChild(pos)); 
					maxHeapify(rightChild(pos),iHeap); 
				} 
			} 
		}
    } 
  
    // Inserts a new element to max heap 
    private void insert(Hierarchical.IntervalNode element) 
    { 
        Heap_outdegree[++size_o] = element; 
		heapo_index_of.put(element.getId(),size_o);
		
		Heap_indegree[++size_i] = element; 
		heapi_index_of.put(element.getId(),size_i);
  
        // Traverse up and fix violated property 
        int current = size_o; 
        while (Heap_outdegree[current].getoutdegree() > Heap_outdegree[parent(current)].getoutdegree() ) { 
            swap(Heap_outdegree,heapo_index_of,current, parent(current)); 
            current = parent(current); 
        }

		current = size_i; 
        while (Heap_indegree[current].getindegree() > Heap_indegree[parent(current)].getindegree() ) { 
            swap(Heap_indegree,heapi_index_of ,current, parent(current)); 
            current = parent(current); 
        }		
    } 
  
   /* public void print() 
    { 
        for (int i = 1; i <= size / 2; i++) { 
            System.out.print( 
                " PARENT : " + Heap[i] 
                + " LEFT CHILD : " + Heap[2 * i] 
                + " RIGHT CHILD : " + Heap[2 * i + 1]); 
            System.out.println(); 
        } 
    } */
  
	public LinkedList<LEdge> extractMaxInDegree() 
    { 
		LinkedList<LEdge> interval = new LinkedList<LEdge>();
		
		if(size_i==0){return null;}
        Hierarchical.IntervalNode poppedi = Heap_indegree[1];
		Hierarchical.IntervalNode popped;
	
		//System.out.println("hereeeeee300\n");
		popped = poppedi;
		if(popped.getindegree()==0){return null;}
		
		Heap_indegree[1] = Heap_indegree[size_i--];
		heapi_index_of.put(Heap_indegree[1].getId(),1);			
		maxHeapify(1,true);
		for(Hierarchical.IntervalEdge ie:popped.incoming){
			//add free edges
			if(ie.isVisited()==false){
				interval.add(ie.e);
			}
			
			ie.setVisited();
			info.get(ie.getsourceId()).decrease_outd();
		}
		//System.out.println("hereeeeee400\n");
	
        return interval; 
    }
  
  
	public LinkedList<LEdge> extractMaxOutDegree() 
    {
		LinkedList<LEdge> interval = new LinkedList<LEdge>();
		
		if(size_o==0){return null;}
		Hierarchical.IntervalNode poppedo = Heap_outdegree[1];
		Hierarchical.IntervalNode popped;
		popped = poppedo;
		if(popped.getoutdegree()==0){return null;}
		
		Heap_outdegree[1] = Heap_outdegree[size_o--];
		heapo_index_of.put(Heap_outdegree[1].getId(),1);
		maxHeapify(1,false);
		for(Hierarchical.IntervalEdge oe:popped.outgoing){
			//add free edges
			if(oe.isVisited()==false){
				interval.add(oe.e);
			}
			
			oe.setVisited();
			info.get(oe.gettargetId()).decrease_ind();
		}
		return interval;
	}
    // Remove an element from max heap 
    public LinkedList<LEdge> extractMax() 
    { 
		LinkedList<LEdge> interval = new LinkedList<LEdge>();
		
		if(size_o==0&&size_i==0){return null;}
        Hierarchical.IntervalNode poppedi = Heap_indegree[1];
		Hierarchical.IntervalNode poppedo = Heap_outdegree[1];
		Hierarchical.IntervalNode popped;
		if (poppedo.getoutdegree()>poppedi.getindegree()){
			popped = poppedo;
			if(popped.getoutdegree()==0){return null;}
			
			Heap_outdegree[1] = Heap_outdegree[size_o--];
			heapo_index_of.put(Heap_outdegree[1].getId(),1);
			maxHeapify(1,false);
			for(Hierarchical.IntervalEdge oe:popped.outgoing){
				//add free edges
				if(oe.isVisited()==false){
					interval.add(oe.e);
				}
				
				oe.setVisited();
				info.get(oe.gettargetId()).decrease_ind();
			}
		}else{
			//System.out.println("hereeeeee300\n");
			popped = poppedi;
			if(popped.getindegree()==0){return null;}
			
			Heap_indegree[1] = Heap_indegree[size_i--];
			heapi_index_of.put(Heap_indegree[1].getId(),1);			
			maxHeapify(1,true);
			for(Hierarchical.IntervalEdge ie:popped.incoming){
				//add free edges
				if(ie.isVisited()==false){
					interval.add(ie.e);
				}
				
				ie.setVisited();
				info.get(ie.getsourceId()).decrease_outd();
			}
			//System.out.println("hereeeeee400\n");
		}
        return interval; 
    }
  
    /*public static void main(String[] arg) 
    { 
        System.out.println("The Max Heap is "); 
        MaxHeap maxHeap = new MaxHeap(15,null); 
        maxHeap.insert(5); 
        maxHeap.insert(3); 
        maxHeap.insert(17); 
        maxHeap.insert(10); 
        maxHeap.insert(84); 
        maxHeap.insert(19); 
        maxHeap.insert(6); 
        maxHeap.insert(22); 
        maxHeap.insert(9); 
  
        maxHeap.print(); 
        System.out.println("The max val is "
                           + maxHeap.extractMax()); 
    } */
}