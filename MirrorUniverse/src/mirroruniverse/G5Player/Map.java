package mirroruniverse.G5Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Vector;

public class Map {
	int[][] grid;
	int[] pos;

	private int VIEW_SIZE;
	private int changed;
	public static final int WIN_SIZE = 200;
	public static final int UNSEEN = 3;
	public static final int GOAL = 2;
	public static final int WALL = 1;
	public static final int FLOOR = 0;
	public boolean goalSeen = false;
	int[] avg;
	int[] goal;
	int[][] frontVal;
	int[][] goalVal;
	public static int[][] M2D = { { 4, 3, 2 }, { 5, 0, 1 }, { 6, 7, 8 } };
	public static int[][] D2M = { { 0, 0 }, { 0, 1 }, { -1, 1 }, { -1, 0 },
			{ -1, -1 }, { 0, -1 }, { 1, -1 }, { 1, 0 }, { 1, 1 } };

	public Map(int[][] view) {
		DEBUG.println("Initializing this new map", DEBUG.LOW);
		grid = new int[WIN_SIZE][WIN_SIZE];
		frontVal = new int[WIN_SIZE][WIN_SIZE];
		goalVal = new int[WIN_SIZE][WIN_SIZE];
		pos = new int[] { WIN_SIZE / 2, WIN_SIZE / 2 };
		goal = new int[]{0,0};
		avg = new int[]{0,0};
		initialize();
		VIEW_SIZE = 20;
		augment(view);
		DEBUG.println("Done setting up", DEBUG.LOW);
	}

	public String toString(){
		return arr2str(grid, pos);
	}
	public String arr2str(int[][] grid, int[] pos) {
		String toReturn = "";
		int mid = VIEW_SIZE / 2;
		for (int i = 0; i < VIEW_SIZE; i++) {
			for (int j = 0; j < VIEW_SIZE; j++) {
				int[] current = new int[] { i - mid + pos[0],
						j - mid + pos[1] };
				int value = grid[current[0]][current[1]];
				if(value > 9998)
					value = valueAt(current);
				if (Arrays.equals(current, pos))
					toReturn += "*" + value;
				else
					toReturn += " " + value;
			}
			toReturn += "\n";
		}
		return toReturn;
	}

	public void augment(int[][] view) {
		int mid = view.length / 2;
		DEBUG.println("augmenting view");
		changed = 0;
		for (int i = 0; i < view.length; i++)
			for (int j = 0; j < view.length; j++) {
				int x = i - mid + pos[0];
				int y = j - mid + pos[1];
				if (x >= 0 && y >= 0 && x < WIN_SIZE && y < WIN_SIZE) {
					if(grid[x][y] == 1 && view[i][j] == 0){
						throw new IllegalArgumentException("View inconsistent");
					}
					
					if (grid[x][y] != view[i][j]) {
						grid[x][y] = view[i][j];
						changed++;
					}				
					
					if(grid[x][y] == GOAL){
						goal[0] = x;
						goal[1] = y;
						goalSeen = true;
					}
					
					if(grid[x][y] == -1)
						throw new IllegalArgumentException("-1 WTF");
				}
			}
		setup(frontVal, UNSEEN);
		bubble(frontVal);
		if(goalSeen){
			setup(goalVal, GOAL);
			bubble(goalVal);
		}
	}
	
	public boolean isStillExplorable(){
		return frontVal[pos[0]][pos[1]] < 9000;
	}

	public int[] nextPos(int[] pos, int direction){
		int value = valueAt(pos);
		int[] dir = D2M[direction];
		int[] nextPos = new int[] { pos[0] + dir[0], pos[1] + dir[1] };
		int nextValue = valueAt(nextPos);
		if (isValid(value, nextValue))
			return nextPos;
		return pos;
	}
	
	public int[] nextPos(int direction) {
		return nextPos(pos, direction);
	}

	private void initialize() {
		DEBUG.println("Initializing", DEBUG.LOW);
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[0].length; j++)
				grid[i][j] = UNSEEN;
	}

	private boolean isValid(int currentValue, int nextValue) {
		if(currentValue != -1 && nextValue != -1)
			if(currentValue != GOAL || nextValue == GOAL)
				if(currentValue != WALL && nextValue != WALL)
					if(currentValue != UNSEEN || nextValue != UNSEEN)
						return true;
		return false;
	}
	
	private boolean isValid(int dir){
		int[] next = nextPos(dir);
		
		return !Arrays.equals(next, pos) && isValid(valueAt(pos), valueAt(nextPos(dir)));
	}
	
	public boolean isModified(){
		return (changed != 0);
	}
	
	public void setNext(int dir){
		int[] npos = nextPos(dir);
		pos[0] = npos[0];
		pos[1] = npos[1];
	}

	public int valueAt(int[] pos) {
		if (pos[0] >= 0 && pos[0] < grid.length && pos[1] >= 0
				&& pos[1] < grid[0].length)
			return grid[pos[0]][pos[1]];
		return -1;
	}
	
	public int[] getPosition(){
		return pos;
	}
	
	public int[] getDirections(){
		boolean[] vals = validDirections();
		int count = 0;
		for(boolean v : vals)
			if(v) count++;
		int[] toreturn = new int[count];
		int i = 0, j = 0;
		for(boolean v : vals){
			if(v)
				toreturn[i++] = j;
			j++;
		}
		return toreturn;
	}
	
	public boolean[] validDirections(){
		boolean[] toreturn = new boolean[9];
		for(int i = 1; i <= 8; i++){		
			toreturn[i] = isValid(i);
			if(toreturn[i])
				DEBUG.println(Arrays.toString(nextPos(i)) + " " + i + " " + valueAt(nextPos(i)));
		}
		return toreturn;
	}
	
	public void setup(int[][] front, int value){
		for(int i = 0; i < WIN_SIZE; i++)
			for(int j = 0; j < WIN_SIZE; j++){
				front[i][j] = 9999;
				if(valueAt(new int[]{i,j}) == FLOOR)
					for(int k = -1; k <= 1; k++)
						for(int l = -1; l <=1; l++)
							if(valueAt(new int[]{i+k,j+l}) == value)
								front[i][j] = 1;
			}
	}
	
	public boolean goalReachable(){
		return goalVal[pos[0]][pos[1]] < 9999;
	}
	
	public void bubble(int[][] front){
		Vector<int[]> pos = new Vector<int[]>();
		for(int i = 0; i < WIN_SIZE; i++)
			for(int j = 0; j < WIN_SIZE; j++)
				if(front[i][j] == 1)
					pos.add(new int[]{i,j});
		while(!pos.isEmpty()){
			int[] m = pos.remove(0);
			if(valueAt(m) != -1)
				for(int k = 1; k <= 8; k++){
					int[] tmpPos = nextPos(m, k);
					if(valueAt(tmpPos) == FLOOR){
						if(front[tmpPos[0]][tmpPos[1]] > front[m[0]][m[1]] + 1){
							front[tmpPos[0]][tmpPos[1]] = front[m[0]][m[1]] + 1;
							pos.add(tmpPos);
						}
					}
				}
		}
	}
}
