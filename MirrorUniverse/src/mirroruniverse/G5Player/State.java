package mirroruniverse.G5Player;

import java.util.ArrayList;

public class State {
	Map m1, m2;
	int[] p1, p2;
	int dir;
	int level;
	State parent = null;

	public State(Map m1, Map m2) {
		this.m1 = m1;
		this.m2 = m2;
		this.p1 = m1.pos;
		this.p2 = m2.pos;
		this.dir = -1;
		this.level = 1;
	}

	public State(Map m1, Map m2, int[] p1, int[] p2, int dir, State parent) {
		this.m1 = m1;
		this.m2 = m2;
		this.p1 = p1;
		this.p2 = p2;
		this.dir = dir;
		this.parent = parent;
		this.level = this.parent.level + 1;
	}

	public boolean isFull() {
		return m1.valueAt(p1) == Map.GOAL && m2.valueAt(p2) == Map.GOAL;
	}

	public boolean isPartial() {
		return !isFull()
				&& (m1.valueAt(p1) == Map.GOAL || m2.valueAt(p2) == Map.GOAL);
	}

	public boolean isUnseen() {
		return m1.frontVal[p1[0]][p1[1]] == 1 || m2.frontVal[p2[0]][p2[1]] == 1; 
	}

	public boolean isNotWorthGoingTo() {
		return !isFull()
				&& !isUnseen()
				|| (isPartial() && (!m1.goalSeen || !m2.goalSeen)
						&& m1.isModified() && m2.isModified());
	}

	public static String encode(int[] p1, int[] p2) {
		return "" + p1[0] + "," + p1[1] + "," + p2[0] + "," + p2[1];
	}

	public ArrayList<Integer> getDirections() {
		ArrayList<Integer> toreturn = new ArrayList<Integer>();
		State current = this;
		while (current != null) {
			if (current.dir != -1)
				toreturn.add(0, current.dir);
			current = current.parent;
		}
		return toreturn;
	}

	public int[] list() {
		return new int[] { 1, 2, 3, 4, 5, 6, 7, 8};
	}

	public int dist() {
		int i = m1.frontVal[p1[0]][p1[1]];
		int j = m2.frontVal[p2[0]][p2[1]];
		return i*i + j*j + level;
	}
	
	public boolean stepsOnUnseen(){
		return m1.valueAt(m1.pos) == Map.UNSEEN || m2.valueAt(m2.pos) == Map.UNSEEN;
	}
	
	public int goaldist() {
		int i = m1.goalVal[p1[0]][p1[1]];
		int j = m2.goalVal[p2[0]][p2[1]];
		return i*i + j*j + (i-j) * (i-j);
	}
	public ArrayList<State> findNeighbors() {
		ArrayList<State> states = new ArrayList<State>();
		int[] m = list();
		for (int i = 0; i < 8; i++)
			states.add(new State(m1, m2, m1.nextPos(p1, m[i]), m2.nextPos(p2,
					m[i]), m[i], this));
		return states;
	}

	public String encoded() {
		return encode(p1, p2);
	}

	public String toString() {
		return encode(p1, p2) + "; v=" + m1.valueAt(p1) + "," + m2.valueAt(p2)
				+ "," + level;
	}
}
