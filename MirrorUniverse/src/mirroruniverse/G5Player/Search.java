package mirroruniverse.G5Player;

import java.util.ArrayList;
import java.util.Arrays;

public class Search {
	ArrayList<State> queue;
	boolean[][][][] seen;
	int[][] m1, m2;

	public Search(int[][] map1, int[][] map2, int[] pos1, int[] pos2) {
		State start = new State(pos1, pos2, -1, null, false, false);
		queue = new ArrayList<State>();
		queue.add(start);
		this.m1 = map1;
		this.m2 = map2;
	}

	public void resetSeen() {
		seen = new boolean[m1.length][m1[0].length][m2.length][m2[0].length];
	}

	public State getEndState() {
		resetSeen();
		ArrayList<State> partialSolutions = new ArrayList<State>();
		// stage 1
		while (!queue.isEmpty()) {
			State current = queue.remove(0);
			if (current.full)
				return current;
			if (current.partial) {
				partialSolutions.add(current);
				continue;
			}
			ArrayList<State> neighbors = current.findNeighbors(m1, m2);

			for (State s : neighbors)
				if (!seen[s.p1[0]][s.p1[1]][s.p2[0]][s.p2[1]]) {
					queue.add(s);
					seen[s.p1[0]][s.p1[1]][s.p2[0]][s.p2[1]] = true;
				}
		}
		// stage 2
		while (!partialSolutions.isEmpty()) {
			State current = partialSolutions.remove(0);
			
			if (current.full)
				return current;
			
			ArrayList<State> neighbors = current.findNeighbors(m1, m2);
			for (State s : neighbors)
				if (!seen[s.p1[0]][s.p1[1]][s.p2[0]][s.p2[1]]) {
					queue.add(s);
					seen[s.p1[0]][s.p1[1]][s.p2[0]][s.p2[1]] = true;
				}
		}
		return null;
	}
}

class State {
	int[] p1, p2;
	int move = -1;
	boolean full, partial;
	State parent;

	public State(int[] pos1, int[] pos2, int move, State parent, boolean full,
			boolean partial) {
		p1 = Util.copy(pos1);
		p2 = Util.copy(pos2);
		this.parent = parent;
		this.move = move;
		this.full = full;
		this.partial = partial;
	}

	public ArrayList<State> findNeighbors(int[][] map1, int[][] map2) {
		// rules for a neighbor:
		ArrayList<State> toreturn = new ArrayList<State>();
		for (int i = -1; i <= 1; i++)
			for (int j = -1; j <= 1; j++) {
				int[] n1 = Util.nextPos(map1, p1, new int[] { i, j });
				int[] n2 = Util.nextPos(map2, p2, new int[] { i, j });
				if (!(Util.same(n1, p1) && Util.same(n2, p2))) {
					boolean full = false, partial = false;
					if (Util.get(map1, n1) == Util.GOAL
							&& Util.get(map2, n2) == Util.GOAL)
						full = true;
					else if (Util.get(map1, n1) == Util.GOAL
							|| Util.get(map2, n2) == Util.GOAL)
						partial = true;
					State next = new State(n1, n2, Util.M2D[i + 1][j + 1],
							this, full, partial);
					toreturn.add(next);
				}
			}
		return toreturn;
	}

	public String toString() {
		return getCode(p1, p2);
	}

	public static String getCode(int[] p1, int[] p2) {
		return Arrays.toString(p1) + "," + Arrays.toString(p2);
	}
}
