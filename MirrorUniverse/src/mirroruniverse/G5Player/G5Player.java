package mirroruniverse.G5Player;

import java.util.Arrays;

import mirroruniverse.sim.Player;

public class G5Player implements Player {

	boolean DEBUG = true;

	// 2d arrays to hold maps for both players
	int[][] map1;
	int[][] map2;

	// size of map
	int size = 20;

	State end;

	// positions of the two players
	int[] pos1 = new int[2];
	int[] pos2 = new int[2];

	// boolean matrix for whether goals have been reached
	boolean[] reachedGoal = new boolean[2];

	boolean mapModified = false;

	public void setup() {
		map1 = new int[size][size];
		map2 = new int[size][size];
		for (int i = 0; i < map1.length; i++)
			for (int j = 0; j < map1[0].length; j++)
				map2[i][j] = map1[i][j] = Util.UNSEEN;
		pos2[0] = pos1[0] = map1.length / 2;
		pos2[1] = pos1[1] = map1[0].length / 2;
	}

	public void augmentMap(int[][] map, int[][] view, int[] pos) {
		int mid = view.length / 2;
		int changes = 0;
		for (int i = 0; i < view.length; i++)
			for (int j = 0; j < view.length; j++) {
				int x = i - mid + pos[0];
				int y = j - mid + pos[1];
				if (x >= 0 && y >= 0 && x < map.length && y < map[0].length
						&& map[x][y] != Util.WALKED) {
					if (map[x][y] != view[i][j]) {
						map[x][y] = view[i][j];
						changes++;
					}
				}
			}
		mapModified = (changes != 0);
	}

	public int valueAt(int[][] map, int[] pos) {
		if (pos[0] < 0 || pos[0] >= map.length || pos[1] < 0
				|| pos[1] >= map[0].length)
			return 1;
		return map[pos[0]][pos[1]];
	}

	public void update(int[] m) {
		pos1 = Util.nextPos(map1, pos1, m);
		pos2 = Util.nextPos(map2, pos2, m);
	}

	public int lookAndMove(int[][] aintViewL, int[][] aintViewR) {
		if (map1 == null)
			setup();

		//Util.waitForSpace();

		//Util.print(map1, pos1, null);
		//Util.print(map2, pos2, null);

		map1[pos1[0]][pos1[1]] = Util.WALKED;
		map2[pos2[0]][pos2[1]] = Util.WALKED;

		augmentMap(map1, aintViewL, pos1);
		augmentMap(map2, aintViewR, pos2);
		if (mapModified) {
			Search s = new Search(map1, map2, pos1, pos2);
			end = s.getEndState();
			System.out.println(end);
		}

		State k = end, l = null;
		while (k != null && k.parent != null) {
			l = k;
			k = k.parent;
		}
		if (l != null) {
			update(Util.D2M[l.move]);
			l.parent = null;
			return l.move;
		}

		System.out.println("Didn't move by search");

		int[] m = null;
		int moveValue = 12;
		int i = 0, j = 0;
		for (i = -1; i <= 1; i++)
			for (j = -1; j <= 1; j++) {
				int val = Util.moveVal(map1, map2, pos1, pos2,
						new int[] { i, j });
				System.out.println(val + Arrays.toString(new int[] { i, j }));
				if (val < moveValue) {
					m = new int[] { i, j };
					moveValue = val;
				}
			}
		System.out.println(Arrays.toString(m));
		if (m == null)
			return 0;
		int d = Util.M2D[m[0] + 1][m[1] + 1];
		update(m);
		return d;
	}
}