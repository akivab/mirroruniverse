package mirroruniverse.G5Player;

import java.util.ArrayList;

public class Search {
	ArrayList<State> queue;
	ArrayList<String> seen;
	Map m1, m2;

	public Search(Map m1, Map m2) {
		State start = new State(m1, m2);
		queue = new ArrayList<State>();
		seen = new ArrayList<String>();
		queue.add(start);
		this.m1 = m1;
		this.m2 = m2;
	}
	
	

	public State getEndState() {
		ArrayList<State> unseen = new ArrayList<State>();
		ArrayList<State> partial = new ArrayList<State>();
		// stage 1
		while (!queue.isEmpty()) {
			State current = queue.remove(0);
			DEBUG.println(current.encoded());
			if (current.isFull())
				return current;
			if (current.isUnseen())
				unseen.add(current);
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

		if(!unseen.isEmpty())
			return unseen.get(0);
		while(!partial.isEmpty()){
			State current = partial.remove(0);
			if(current.isFull())
				return current;
			ArrayList<State> neighbors = current.findNeighbors();
			for (State s : neighbors)
				if (!seen.contains(s.encoded())) {
					partial.add(s);
					seen.add(s.encoded());
				}
		}
		return null;
	}
}