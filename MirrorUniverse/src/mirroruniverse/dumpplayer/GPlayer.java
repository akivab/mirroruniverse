package mirroruniverse.dumpplayer;

import java.util.Arrays;
import java.util.Random;

import mirroruniverse.sim.MUMap;
import mirroruniverse.sim.Player;

public class GPlayer implements Player
{
	int m = 7;
	int[][] map1;
	int[][] map2;
	int[] posL = new int[2];
	int[] posR = new int[2];
	public void setStarting(int[] sl, int[] sr){
		map1 = new int[sl[1]*2][sl[0]*2];
		map2 = new int[sr[1]*2][sr[0]*2];
		posL = new int[]{sl[1], sl[0]};
		posR = new int[]{sr[1], sr[0]};
		setup(map1);
		setup(map2);
	}
	
	public void setup(int[][] map){
		for(int i = 0; i < map.length; i++)
			for(int j = 0; j < map[0].length; j++)
				map[i][j] = 4;
	}
	
	public void print(int[][] map, int[] pos){
		for(int i = 0; i < map.length; i++){
			for(int j = 0; j < map[0].length; j++)
				if(pos!=null && pos[0] == i && pos[1] == j)
					System.out.print("X ");
				else
					System.out.print(map[i][j] + " ");
			System.out.println();
		}
		System.out.println();
	}
	
		
	public void addTo(int[][] map, int[][] view, int[] pos){
		int x = pos[0], y = pos[1];
		int r = map.length, c= map[0].length;
		int l = view.length;
		for(int i = 0; i < l; i++)
			for(int j = 0; j < l; j++){
				int viewX = i, viewY = j;
				int mapX = i+x-l/2, mapY = j+y-l/2;
				if(mapX >= 0 && mapX < r && mapY >= 0 && mapY < c)
					map[mapX][mapY] = view[viewX][viewY];
			}
	}
	
	public int[] add(int[] pos, int[] dir){
		return new int[]{pos[0] + dir[0], pos[1]+dir[1]};
	}
	
	public int valueAt(int[][]map, int[]pos){
		if(pos[0] < 0 || pos[0] >= map.length || pos[1] < 0 || pos[1] >= map[0].length )
			return 1;
		return map[pos[0]][pos[1]];
	}
	
	public int lookAndMove( int[][] aintViewL, int[][] aintViewR ){
		System.out.println(Arrays.toString(posL) + "\t" + Arrays.toString(posR));
		addTo(map1, aintViewL, posL);
		addTo(map2, aintViewR, posR);
		print(map1,posL);
		print(map2,posR);
		print(aintViewL, null);
		print(aintViewR, null);
		Random r = new Random();
		int dm = r.nextInt(8) + 1;
		System.out.println("moving to: " + Arrays.toString(MUMap.aintDToM[dm]));
		int[] nextL = add(posL,MUMap.aintDToM[dm]);
		int[] nextR = add(posR,MUMap.aintDToM[dm]);
		if(valueAt(map1, nextL) == 0)
			posL = nextL;
		if(valueAt(map2, nextR) == 0)
			posR = nextR;
		return dm;
	}
}

