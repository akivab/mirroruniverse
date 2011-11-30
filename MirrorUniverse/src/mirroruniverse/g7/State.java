package mirroruniverse.g7;

public class State {

	/* State that works with 2-level BFS search
	 *
	 *  getNext:
	 *   Returns all possible next states from a state
	 *   if one player has reached his goal then he does
	 *   not move anymore while the other player moves
	 *
	 *  isFinal:
	 *   Returns if the state can progress to the next
	 *   level of search, that is if one of the two
	 *   players has reached his goal
	 *
	 *  isGoal:
	 *   Both players have reached their goal
	 *
	 *  isFinalOptimal:
	 *   The state is a final state and is also the
	 *   optimal final state, so the search can
	 *   discard all other final stages and keep
	 *   only this one to expand until the goal
	 */

	public static final int maxNextStates = 8;
	public static final byte unusedMoveCode = 30;
	public static final byte nullMoveCode = 0;

	private static boolean[][] leftMap;
	private static boolean[][] rightMap;

	private static int leftRows;
	private static int leftCols;

	private static int rightRows;
	private static int rightCols;
	private static int rightSize;

	private static State end;

	private byte i1, j1, i2, j2;

	public static int allStates() {
		return leftRows * leftCols * rightRows * rightCols;
	}

	public static State init(int[][] left, int[][] right) {
		/* Keep starting state */
		State start = new State();
		State end = new State();
		/* Start and end flags */
		boolean found_2 = false;
		boolean found_3 = false;
		/* Set left map */
		leftRows = left.length;
		leftCols = left[0].length;
		leftMap = new boolean [leftRows][leftCols];
		for (int i = 0 ; i != leftRows ; ++i)
			for (int j = 0 ; j != leftCols ; ++j)
				if (left[i][j] == 1)
					leftMap[i][j] = false;
				else {
					leftMap[i][j] = true;
					if (left[i][j] == 2) {
						end.i1 = (byte) i;
						end.j1 = (byte) j;
						if (found_2)
							throw new IllegalArgumentException();
						found_2 = true;
					} else if (left[i][j] == 3) {
						start.i1 = (byte) i;
						start.j1 = (byte) j;
						if (found_3)
							throw new IllegalArgumentException();
						found_3 = true;
					}
				}
		/* Reset start and end flags */
		found_2 = false;
		found_3 = false;
		/* Set right map */
		rightRows = right.length;
		rightCols = right[0].length;
		rightSize = rightRows * rightCols;
		rightMap = new boolean [rightRows][rightCols];
		for (int i = 0 ; i != rightRows ; ++i)
			for (int j = 0 ; j != rightCols ; ++j)
				if (right[i][j] == 1)
					rightMap[i][j] = false;
				else {
					rightMap[i][j] = true;
					if (right[i][j] == 2) {
						end.i2 = (byte) i;
						end.j2 = (byte) j;
						if (found_2)
							throw new IllegalArgumentException();
						found_2 = true;
					} else if (right[i][j] == 3) {
						start.i2 = (byte) i;
						start.j2 = (byte) j;
						if (found_3)
							throw new IllegalArgumentException();
						found_3 = true;
					}
				}
		/* Store the goal and return the start */
		State.end = end;
		return start;
	}

	public static State end() {
		return new State(end);
	}

	public State() {
		this(-1, -1, -1, -1);
	}

	public State(int i1, int j1, int i2, int j2) {
		this.i1 = (byte) i1;
		this.j1 = (byte) j1;
		this.i2 = (byte) i2;
		this.j2 = (byte) j2;
	}

	public State(State par) {
		i1 = par.i1;
		j1 = par.j1;
		i2 = par.i2;
		j2 = par.j2;
	}

	public boolean isGoal() {
		return i1 == end.i1 && j1 == end.j1 &&
		       i2 == end.i2 && j2 == end.j2;
	}

	public boolean isOptimalFinal() {
		return isGoal();
	}

	public boolean isFinal() {
		return (i1 == end.i1 && j1 == end.j1) ||
		       (i2 == end.i2 && j2 == end.j2);
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof State))
			return false;
		State p = (State) obj;
		return i1 == p.i1 && j1 == p.j1 && i2 == p.i2 && j2 == p.j2;
	}

	public int id() {
		return (i1 * leftCols + j1) * rightSize + (i2 * rightCols + j2);
	}

	public static void byId(State s, int id) {
		int p1 = id / rightSize;
		int p2 = id % rightSize;
		s.i1 = (byte) (p1 / leftCols);
		s.j1 = (byte) (p1 % leftCols);
		s.i2 = (byte) (p2 / rightCols);
		s.j2 = (byte) (p2 % rightCols);
	}

	private static final int[][] dirToCode = {{ 0, 0}, { 1, 0}, { 1,-1},
	                                          { 0,-1}, {-1,-1}, {-1, 0},
	                                          {-1, 1}, { 0, 1}, { 1, 1}};

	private static boolean isValidLeft(int i, int j) {
		if (i < 0 || i >= leftRows || j < 0 || j >= leftCols)
			return false;
		return leftMap[i][j];
	}

	private static boolean isValidRight(int i, int j) {
		if (i < 0 || i >= rightRows || j < 0 || j >= rightCols)
			return false;
		return rightMap[i][j];
	}

	public int getNext(byte[] nextDir, State[] nextState) {
		// if one has reached his goal he will not moves
		boolean f1 = i1 == end.i1 && j1 == end.j1;
		boolean f2 = i2 == end.i2 && j2 == end.j2;
		int c = 0;
		for (int d = 1 ; d != 9 ; ++d) {
			int di = dirToCode[d][0];
			int dj = dirToCode[d][1];
			boolean m = false;
			boolean im1 = false;
			boolean im2 = false;
			if (!f1 && isValidLeft(i1 + di, j1 + dj)) {
				nextState[c].i1 = (byte) (i1 + di);
				nextState[c].j1 = (byte) (j1 + dj);
				m = true;
			} else {
				nextState[c].i1 = i1;
				nextState[c].j1 = j1;
				im1 = true;
			}
			if (!f2 && isValidRight(i2 + di, j2 + dj)) {
				nextState[c].i2 = (byte) (i2 + di);
				nextState[c].j2 = (byte) (j2 + dj);
				m = true;
			} else {
				nextState[c].i2 = i2;
				nextState[c].j2 = j2;
				im2 = true;
			}
			if (m) nextDir[c++] = (byte) (im1 ? d + 10 : (im2 ? d + 20 : d));
		}
		return c;
	}

	private static final int[][] codeToDir = {{4,3,2}, {5,0,1}, {6,7,8}};

	public static int direction(State from, State to) {
		int di = to.i1 - from.i1;
		int dj = to.j1 - from.j1;
		if (di != 0 || dj != 0)
			return codeToDir[di+1][dj+1];
		di = to.i2 - from.i2;
		dj = to.j2 - from.j2;
		return codeToDir[di+1][dj+1];
	}

	public static State retrace(State to, int dir) {
		boolean im1 = dir > 10 && dir < 20;
		boolean im2 = dir > 20;
		dir %= 10;
		int di = dirToCode[dir][0];
		int dj = dirToCode[dir][1];
		State from = new State(to);
		if (!im1 && isValidLeft(to.i1 - di, to.j1 - dj)) {
			from.i1 -= di;
			from.j1 -= dj;
		}
		if (!im2 && isValidRight(to.i2 - di, to.j2 - dj)) {
			from.i2 -= di;
			from.j2 -= dj;
		}
		return from;
	}

	public String toString() {
		return "[" + i1 + "," + j1 + "][" + i2 + "," + j2 + "]";
	}
}
