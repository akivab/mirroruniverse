package mirroruniverse.G5Player;

import java.util.ArrayList;
import java.util.Scanner;

import mirroruniverse.sim.Player;

public class G5Player implements Player {
	public static boolean ON = false;
	Map leftMap, rightMap;
	State end;
	int seenCount = 0;
	static ArrayList<String> seen = new ArrayList<String>();
	ArrayList<Integer> moves;

	public void updateMaps(int[][] lMap, int[][] rMap) {
		if (leftMap == null && rightMap == null) {
			leftMap = new Map(lMap);
			rightMap = new Map(rMap);
		} else {
			leftMap.augment(lMap);
			rightMap.augment(rMap);
		}
	}

	public void pause() {
		Scanner in = new Scanner(System.in);
		in.nextLine();
	}

	boolean full = false;

	/**
	 * Returns a direction to move in (0 if nothing to return)
	 */
	public int getMove() {
		if ((end == null || moves.isEmpty())) {
//			DEBUG.println("here");
			if (full && !moves.isEmpty())
				return moves.remove(0);
			if (seenCount != 0 && !leftMap.isStillExplorable()
					&& !rightMap.isStillExplorable() || leftMap.goalSeen
					&& rightMap.goalSeen && leftMap.goalReachable()
					&& rightMap.goalReachable() && seenCount++ == 0)
				full = true;
			else
				full = false;
			Search s = new Search(leftMap, rightMap, full);
			end = s.getEndState();

			if (end != null)
				moves = end.getDirections();
//			DEBUG.println(end);
//			DEBUG.println(moves);
//			DEBUG.println(leftMap.arr2str(leftMap.goalVal, leftMap.pos));
		}
//		DEBUG.println(State.encode(leftMap.pos, rightMap.pos));
//		DEBUG.println(end);
//		DEBUG.println(moves);

		if (moves != null && !moves.isEmpty())
			return moves.remove(0);
		pause();
		DEBUG.println("There was a problem");
		return (int) (Math.random() * 8 + 1);
	}

	/**
	 * Implementation of look and move
	 */
	public int lookAndMove(int[][] aintViewL, int[][] aintViewR) {
		updateMaps(aintViewL, aintViewR);
	//	DEBUG.println("maps are updated. moving.");
		int move = getMove();
		seen.add(State.encode(leftMap.pos, rightMap.pos));
		leftMap.setNext(move);
		rightMap.setNext(move);
		// pause();
	//	DEBUG.println(leftMap, DEBUG.MEDIUM);
	//	DEBUG.println(rightMap, DEBUG.MEDIUM);
		return move;
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