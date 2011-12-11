package mirroruniverse.G5Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class Search {
	Queue<State> queue, partial, queue2;
	Set<String> seen;
	boolean fullSearch;
	Map m1, m2;
	State start;
	
	public Search(Map m1, Map m2, boolean full) {
		start = new State(m1, m2);
		this.fullSearch = full;
		Comparator<State> s = new CompareStates();
		Comparator<State> s2 = new CompareStates2();
		queue = new PriorityQueue<State>(10, s);
		partial = new LinkedList<State>();
		queue2 = new PriorityQueue<State>(10, s2);
		seen = new HashSet<String>();
		queue.add(start);
		queue2.add(start);
		this.m1 = m1;
		this.m2 = m2;
	}

	public State getEndState() {
		State far = null;
		// stage 1
		
		boolean endGame = (!m1.isStillExplorable() && !m2.isStillExplorable());
		if (fullSearch || endGame) {
			while (!queue2.isEmpty()) {
				if(seen.size() % 1000 == 0 )
					DEBUG.println(seen.size());
				State current = queue2.poll();
				far = current;
				ArrayList<State> neighbors = current.findNeighbors();
				for (State s : neighbors) {
					if (!seen.contains(s.encoded()))
						if (s.isFull())
							return s;
						else if (s.isPartial() && endGame)
							partial.add(s);
						else if (!s.isPartial() && !s.stepsOnUnseen())
							queue2.add(s);
					
					seen.add(s.encoded());
				}
			}
			if(endGame)
				while (!partial.isEmpty()) {
					State current = partial.poll();
					if(seen.size() % 100 == 0)
						DEBUG.println(seen.size());
					ArrayList<State> neighbors = current.findNeighbors();
					for (State s : neighbors)
						if (!seen.contains(s.encoded())) {
							partial.add(s);
							if (s.isFull())
								return s;
							seen.add(s.encoded());
						}
				}
		}
		DEBUG.println("done with endgame search");
		seen = new HashSet<String>();
		if(!endGame)
			while (!queue.isEmpty()) {
				if(seen.size() % 1000 == 0 )
					DEBUG.println(seen.size());
				State current = queue.poll();
				// System.out.println(current);
				if (current.isFull())
					return current;
				if (current.isUnseen())
					return current;
				else {
					ArrayList<State> neighbors = current.findNeighbors();
					for (State s : neighbors)
						if (!seen.contains(s.encoded())) {
							if(s.isFull())
								return s;
							if (!s.isPartial() || start.isPartial())
								queue.add(s);
							else
								far = s;
							seen.add(s.encoded());
						}
				}
			}
		return far;
	}

	class CompareStates implements Comparator<State> {
		public int compare(State s1, State s2) {
			return s1.dist() - s2.dist();
		}
	}
	
	class CompareStates2 implements Comparator<State> {
		public int compare(State s1, State s2) {
			return s1.goaldist() - s2.goaldist();
		}
	}
}