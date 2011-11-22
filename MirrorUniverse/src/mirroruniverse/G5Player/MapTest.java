package mirroruniverse.G5Player;

import mirroruniverse.sim.MUMap;
import mirroruniverse.sim.MUMapConfig;

public class MapTest {
	public static void main(String args[]){
		String str = "5, 5, 3, -1, maps/testMap.txt";
		MUMapConfig config = new MUMapConfig(str);
		MUMap mp = new MUMap(config);
		G5Player p = new G5Player();
		int[][] map = new int[10][10];
		int[] pos = new int[]{5,5};
		move(1,1);
		p.print(mp.getView(), null, null);
		mp.move(8);//4 is northwest
		p.print(mp.getView(), null, null);
	}
}

// 1 is east
// 2 is northeast
// 3 is north
// 4 is northwest
// 5 is west
// 6 is southwest
// 7 is south
// 8 is southeast