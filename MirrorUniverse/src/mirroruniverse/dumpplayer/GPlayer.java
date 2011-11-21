package mirroruniverse.dumpplayer;

import java.util.Arrays;

import mirroruniverse.sim.MUMap;
import mirroruniverse.sim.Player;

public class GPlayer implements Player {
	int[][] map1;
	int[][] map2;
	int[] pos1 = new int[2];
	int[] pos2 = new int[2];
	boolean[] reachedGoal = new boolean[2];

	public void print(int[][] map, int[] pos, int[] nextPos) {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++)
				if (pos != null && pos[0] == i && pos[1] == j)
					System.out.print("X ");
				else if (nextPos != null && nextPos[1]+pos[0] == i && nextPos[0]+pos[1] == j)
					System.out.print("Z ");
				else
					System.out.print(map[i][j] + " ");
			System.out.println();
		}
		System.out.println();
	}

	public void setup() {
		map1 = new int[20][20];
		map2 = new int[20][20];
		for (int i = 0; i < map1.length; i++)
			for (int j = 0; j < map1[0].length; j++)
				map2[i][j] = map1[i][j] = 4;
		pos2[0] = pos1[0] = map1.length / 2;
		pos2[1] = pos1[1] = map1[0].length / 2;
	}
	
	public int getD(int dx, int dy){
		for(int i = 1; i <= 8; i++){
			int[] m = MUMap.aintDToM[i];
			if(m[0] == dx && m[1] == dy)
				return MUMap.aintMToD[dx][dy];
		}
		return -1;
	}

	public void addTo(int[][] map, int[][] view, int[] pos) {
		int mid = view.length / 2;
		for (int i = 0; i < view.length; i++)
			for (int j = 0; j < view.length; j++) {
				int x = i - mid + pos[0];
				int y = j - mid + pos[1];
				if (x >= 0 && y >= 0 && x < map.length && y < map[0].length)
					map[x][y] = view[i][j];
			}
	}

	public int valueAt(int[][] map, int[] pos) {
		if (pos[0] < 0 || pos[0] >= map.length || pos[1] < 0
				|| pos[1] >= map[0].length)
			return 1;
		return map[pos[0]][pos[1]];
	}
	
	public int get(int[][] map, int i, int j){
		if(i < 0 || j < 0 || i >= map.length || j >= map[0].length)
			return -1;
		return map[i][j];
	}
	
	public int set(int[][] map, int i, int j, int val){
		if(i < 0 || j < 0 || i >= map.length || j >= map[0].length)
			return 0;
		if(map[i][j] != val){
			map[i][j] = val;
			return 1;
		}
		return 0;
	}
	
	public int[][] copy(int[][] map){
		int[][] toreturn = new int[map.length][map[0].length];
		for(int i = 0; i < map.length; i++)
			toreturn[i] = Arrays.copyOf(map[i], map[i].length);
		return toreturn;
	}
	
	public int[] copy(int[] pos){
		return Arrays.copyOf(pos, pos.length);
	}
	
	public void add(int[] pos, int[] dir){
		pos[0] += dir[1];
		pos[1] += dir[0];
	}
	
//	public int explore(int[][] map, int[] pos){
//		int[][] newmap = copy(map);
//		for(int i = 1; i <= 8; i++){
//			int[] newpos = copy(pos);
//			add(newpos, MUMap.aintDToM[i]);
//			if(get(map, newpos[0], newpos[1]) == 4)
//				return i;
//		}
//		int ncount = 0;
//		for(int i = 0; i < map.length; i++)
//			for(int j = 0; j < map[0].length; j++)
//				if(get(newmap,i,j)==4)
//					for(int k=1; k<=8; k++){						
//						int[] tmp = MUMap.aintDToM[k];
//						int[] tpos = new int[]{i,j};
//						add(tpos, tmp);
//						ncount += set(newmap, tpos[0], tpos[1], 4);
//					}
//		if(ncount != 0)
//			return explore(newmap, pos);
//		return -1;
//	}
	
	int dm = 1;
	public int lookAndMove(int[][] aintViewL, int[][] aintViewR) {
		if (map1 == null)
			setup();
		int mid = aintViewL.length / 2;
		//dm = explore(map1, pos1);
		//if(dm < 0)
		//	dm = explore(map2, pos2);
		//if(dm < 0)
		dm = (int) (Math.random() * 8 + 1);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int[] m = MUMap.aintDToM[dm];
		
		addTo(map1, aintViewL, pos1);
		addTo(map2, aintViewR, pos2);

		print(map1, pos1, m);
		print(map2, pos2, m);
		if(aintViewL[m[1]+mid][m[0]+mid] == 0 && !reachedGoal[0])
			add(pos1, m);
		if(aintViewR[m[1]+mid][m[0]+mid] == 0 && !reachedGoal[1])
			add(pos2, m);
		if(aintViewL[m[1]+mid][m[0]+mid] == 2){
			reachedGoal[0] = true;
			add(pos1, m);
		}
		if(aintViewR[m[1]+mid][m[0]+mid] == 2){
			reachedGoal[1] = true;
			add(pos2, m);
		}	
		if(reachedGoal[0] && reachedGoal[1])
			System.out.println("Goal reached");
		return dm;
	}
}
