package mirroruniverse.G5Player;

import java.util.Arrays;
import java.util.Scanner;

public class Util {

	// fixed Shen's problems
	static int[][] M2D = {{4, 3, 2}, {5, 0, 1}, {6, 7, 8}};
	static int[][] D2M = { { 0, 0 }, { 0, 1 }, { -1, 1 }, { -1, 0 }, { -1, -1 },  { 0, -1 }, { 1, -1 }, { 1, 0 }, { 1, 1 } };
	
	// designation of different types of grid values
	static int FLOOR = 0;
	static int WALL = 1;
	static int GOAL = 2;
	static int UNSEEN = 4;
	static int WALKED = 5;
	
	public static void waitForSpace() {
		Scanner in = new Scanner(System.in);
		in.nextLine();
	}

	public static void print(int[][] map, int[] pos, int[] npos) {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++)
				if (pos != null && pos[0] == i && pos[1] == j)
					System.out.print("X ");
				else if (npos != null && npos[0] == i && npos[1] == j)
					System.out.print("Z ");
				else
					System.out.print(map[i][j] + " ");
			System.out.println();
		}
		System.out.println();
	}
	
	public static int get(int[][] map, int i, int j){
		if(i < 0 || j < 0 || i >= map.length || j >= map[0].length)
			return -1;
		return map[i][j];
	}
	
	public static int get(int[][] map, int[] pos){
		return get(map, pos[0], pos[1]);
	}
	
	public static int set(int[][] map, int i, int j, int val){
		if(i < 0 || j < 0 || i >= map.length || j >= map[0].length)
			return 0;
		if(map[i][j] != val){
			map[i][j] = val;
			return 1;
		}
		return 0;
	}
	
	public static boolean same(int[] n, int[] p){
		boolean b = true;
		for(int i = 0; i < n.length; i++)
			b = b && (n[i] == p[i]);
		return b;
	}
	
	public static int moveVal(int[][] m1, int[][] m2, int[] p1, int[] p2, int[] mv){
		int[] n1 = nextPos(m1, p1, mv);
		int[] n2 = nextPos(m2, p2, mv);
		
		
		int v1 = get(m1, n1);
		int v2 = get(m2, n2);
		if( v1 == GOAL  && v2 == GOAL )
			return -999;
		else if ( (v1 == FLOOR || v2 == FLOOR) && (v1 == WALKED || v2 == WALKED) )
			return 0;
		else if ((( v1 == FLOOR || v1 == WALKED ) && ( v2 != GOAL )) ||
				(( v2 == FLOOR || v2 == WALKED ) && ( v1 != GOAL )))
			return 10;
		else
			return 999;
	}
	
	public static int[] nextPos(int[][] map, int pos[], int[] mov){
		int nextVal = get(map, pos[0]+mov[0], pos[1]+mov[1]);
		int thisVal = get(map, pos);
		if(!(nextVal == FLOOR || nextVal == UNSEEN || nextVal == WALKED || nextVal == GOAL) || thisVal == GOAL)
			return pos;
		return new int[]{pos[0]+mov[0], pos[1]+mov[1]};
	}
	
	public static int[][] copy(int[][] map){
		int[][] toreturn = new int[map.length][map[0].length];
		for(int i = 0; i < map.length; i++)
			toreturn[i] = Arrays.copyOf(map[i], map[i].length);
		return toreturn;
	}
	
	public static int[] copy(int[] pos){
		return Arrays.copyOf(pos, pos.length);
	}
}
