package mirroruniverse.G5Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Search {
	PriorityQueue<State> queue, partial;
	ArrayList<String> seen;
	Map m1, m2;
	

	public Search(Map m1, Map m2) {
		State start = new State(m1, m2);
		Comparator<State> s = new CompareStates();
		queue = new PriorityQueue<State>(10, s);
		partial = new PriorityQueue<State>(10,s);
		seen = new ArrayList<String>();
		queue.add(start);
		this.m1 = m1;
		this.m2 = m2;
	}
	
	

	public State getEndState() {
		State far = null;
		// stage 1
		while (!queue.isEmpty() && seen.size() < 10000) {
			State current = queue.poll();
			far = current;
			if (current.isFull())
				return current;
			if (current.isUnseen() && (!m1.goalSeen || !m2.goalSeen))
				return current;
			else if (current.isPartial())
				partial.add(current);
			else{
				ArrayList<State> neighbors = current.findNeighbors();
				for (State s : neighbors)
					if (!seen.contains(s.encoded())) {
						queue.add(s);
						seen.add(s.encoded());
					}
			}
		}
		
				
		while(!partial.isEmpty()){
			State current = partial.poll();
			if(current.isFull())
				return current;
			ArrayList<State> neighbors = current.findNeighbors();
			for (State s : neighbors)
				if (!seen.contains(s.encoded())) {
					partial.add(s);
					seen.add(s.encoded());
				}
		}
		return far;
	}
}

class CompareStates implements Comparator<State>{
	public int compare(State s1, State s2) {
		return s1.dist() -s2.dist();
	}
}