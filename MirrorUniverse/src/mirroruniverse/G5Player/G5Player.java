package mirroruniverse.G5Player;

import java.util.ArrayList;
import java.util.Scanner;

import mirroruniverse.sim.Player;

public class G5Player implements Player {
	public static boolean ON = true;
	Map leftMap, rightMap;
	State end;
	ArrayList<Integer> moves;
	ArrayList<String> seen;

	public void updateMaps(int[][] lMap, int[][] rMap) {
		if (leftMap == null && rightMap == null) {
			leftMap = new Map(lMap);
			rightMap = new Map(rMap);
			seen = new ArrayList<String>();
		} else {
			leftMap.augment(lMap);
			rightMap.augment(rMap);
		}
	}

	public void pause() {
		Scanner in = new Scanner(System.in);
		in.nextLine();
	}

	/**
	 * Returns a direction to move in (0 if nothing to return)
	 */
	public int getMove() {
		if (end == null || end.isNotWorthGoingTo() || moves.isEmpty()) {
			System.out.println("need to find a new goal!");
			System.out.println(end);
			System.out.println(moves);
			Search s = new Search(leftMap, rightMap);
			end = s.getEndState();
			if (end != null)
				moves = end.getDirections();
		}
		System.out.println(State.encode(leftMap.pos, rightMap.pos));
		System.out.println(end);
		System.out.println(moves);

		if (moves != null && !moves.isEmpty())
			return moves.remove(0);
		System.out.println("There was a problem");
		// pause();
		return 0;
	}

	/**
	 * Implementation of look and move
	 */
	public int lookAndMove(int[][] aintViewL, int[][] aintViewR) {
		updateMaps(aintViewL, aintViewR);
		System.out.println("maps are updated. moving.");
		int move = getMove();
		leftMap.setNext(move);
		rightMap.setNext(move);
		// pause();
		DEBUG.println(leftMap, DEBUG.MEDIUM);
		DEBUG.println(rightMap, DEBUG.MEDIUM);
		return move;
	}

	/**
	 * Old method (not in use any more -- check out lookAndMove)
	 * 
	 * @param aintViewL
	 *            left view
	 * @param aintViewR
	 *            right view
	 * @return directions to move in
	 */
	public int oldlookAndMove(int[][] aintViewL, int[][] aintViewR) {
		DEBUG.println("Looking and moving", DEBUG.LOW);
		updateMaps(aintViewL, aintViewR);
		DEBUG.println("Done updating maps", DEBUG.LOW);
		// pause();
		int[] p1 = leftMap.getPosition();
		int[] p2 = rightMap.getPosition();
		DEBUG.println(leftMap, DEBUG.MEDIUM);
		DEBUG.println(rightMap, DEBUG.MEDIUM);
		if (!seen.contains(State.encode(p1, p2)))
			seen.add(State.encode(p1, p2));
		boolean[] lDir = leftMap.validDirections();
		boolean[] rDir = rightMap.validDirections();

		ArrayList<Integer> directions = new ArrayList<Integer>();

		for (int i = 1; i < 9; i++)
			if (lDir[i] || rDir[i]) {
				int[] np1 = leftMap.nextPos(i);
				int[] np2 = rightMap.nextPos(i);
				directions.add(i);
				if (!seen.contains(State.encode(np1, np2))) {
					leftMap.setNext(i);
					rightMap.setNext(i);
					return i;
				}

			}

		int[] dir = leftMap.getDirections();
		int j = 0;
		for (int i : directions)
			if (i != dir[j++])
				System.out.println("NOT EQUAL");
		int index = directions.get((int) (Math.random() * directions.size()));
		leftMap.setNext(index);
		rightMap.setNext(index);

		pause();
		return index;
	}
}

class DEBUG {
	public static int LOW = 1, MEDIUM = 2, HIGH = 3;
	public static int THRESHOLD = LOW;

	public static void println(Object obj) {
		if (G5Player.ON)
			System.out.println(obj);
	}

	public static void println(Object obj, int level) {
		if (G5Player.ON && level >= THRESHOLD)
			System.out.println(obj);
	}
}