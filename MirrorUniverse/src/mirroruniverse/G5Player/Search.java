package mirroruniverse.G5Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Search {
	Queue<State> queue, partial, queue2;
	ArrayList<String> seen;
	boolean fullSearch;
	Map m1, m2;
	

	public Search(Map m1, Map m2, boolean full) {
		State start = new State(m1, m2);
		this.fullSearch = full;
		Comparator<State> s = new CompareStates();
		queue = new PriorityQueue<State>(10, s);
		partial = new LinkedList<State>();
		queue2 = new LinkedList<State>();
		seen = new ArrayList<String>();
		queue.add(start);
		queue2.add(start);
		this.m1 = m1;
		this.m2 = m2;
	}
	
	public State getEndState() {
		State far = null;
		// stage 1
		if(!m1.isStillExplorable() && !m2.isStillExplorable()){
			while (!queue2.isEmpty()) {
				State current = queue2.poll();
				far = current;
				ArrayList<State> neighbors = current.findNeighbors();
				for (State s : neighbors){
					if(!seen.contains(s.encoded()))
						if(s.isFull())
							return s;
						else if(s.isPartial())
							partial.add(s);
						else if(!s.isUnseen())
							queue2.add(s);
					seen.add(s.encoded());
				}
			}
			
			while(!partial.isEmpty()){
				State current = partial.poll();
				ArrayList<State> neighbors = current.findNeighbors();
				for (State s : neighbors)
					if (!seen.contains(s.encoded())) {
						partial.add(s);
						if(s.isFull())
							return s;
						seen.add(s.encoded());
					}
			}
		}
		
		else{
			while (!queue.isEmpty() && seen.size() < 10000) {
				State current = queue.poll();
				far = current;
				//System.out.println(current);
				if (current.isFull())
					return current;
				if (current.isUnseen())
					return current;
				else if (!current.isPartial()){
					ArrayList<State> neighbors = current.findNeighbors();
					for (State s : neighbors)
						if (!seen.contains(s.encoded())) {
							if(!s.isPartial())
								queue.add(s);
							seen.add(s.encoded());
						}
				}
			}
		}
		return far;
	}

	class CompareStates implements Comparator<State>{
		public int compare(State s1, State s2) {
			if(!fullSearch)
				return s1.dist() - s2.dist();
			return s1.goaldist() - s2.goaldist();
		}
	}
}