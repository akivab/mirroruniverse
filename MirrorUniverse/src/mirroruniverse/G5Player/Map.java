package mirroruniverse.G5Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Vector;

public class Map {
	int[][] grid;
	int[] pos;

	public Vector<ComparablePoint> frontierPoints;
/*	public Vector<FrontierSearchNode> toExplore;
	public Vector<FrontierSearchNode> explored;*/
	private int distanceToGoal;
	private int distanceToFrontier;
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
	public static int[][] M2D = { { 4, 3, 2 }, { 5, 0, 1 }, { 6, 7, 8 } };
	public static int[][] D2M = { { 0, 0 }, { 0, 1 }, { -1, 1 }, { -1, 0 },
			{ -1, -1 }, { 0, -1 }, { 1, -1 }, { 1, 0 }, { 1, 1 } };

	public Map(int[][] view) {
		DEBUG.println("Initializing this new map", DEBUG.LOW);
		grid = new int[WIN_SIZE][WIN_SIZE];
		pos = new int[] { WIN_SIZE / 2, WIN_SIZE / 2 };
		goal = new int[]{0,0};
		avg = new int[]{0,0};
		initialize();
		frontierPoints = new Vector<Map.ComparablePoint>();
		VIEW_SIZE = 20;
		augment(view);
		DEBUG.println("Done setting up", DEBUG.LOW);
	}

	public String toString() {
		String toReturn = "";
		int mid = VIEW_SIZE / 2;
		for (int i = 0; i < VIEW_SIZE; i++) {
			for (int j = 0; j < VIEW_SIZE; j++) {
				int[] current = new int[] { i - mid + pos[0],
						j - mid + pos[1] };
				int value = valueAt(current);
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
		int avgcount = 0;
		avg[0] = avg[1] = 0;
		changed = 0;
		for (int i = 0; i < view.length; i++)
			for (int j = 0; j < view.length; j++) {
				int x = i - mid + pos[0];
				int y = j - mid + pos[1];
				if (x >= 0 && y >= 0 && x < WIN_SIZE && y < WIN_SIZE) {
					if(grid[x][y] == 1 && view[i][j] == 0){
						throw new IllegalArgumentException("View inconsistent");
					}
					
					ComparablePoint thisPoint = new ComparablePoint(new int[] {x, y});
					if (frontierPoints.contains(thisPoint))
					{
						frontierPoints.remove(thisPoint);
					}
					if (grid[x][y] != view[i][j]) {
						grid[x][y] = view[i][j];
						changed++;
					}
					if (valueAt(thisPoint.position) == FLOOR)
					{
						for (int h = -1; h <= 1; ++h)
						{
							for (int k = -1; k <= 1; ++k)
							{
								int[] next = new int[] { thisPoint.position[0]+h, thisPoint.position[1]+k };
								ComparablePoint nextPoint = new ComparablePoint(next);
								if (valueAt(nextPoint.position) == UNSEEN)
								{
									if (!frontierPoints.contains(nextPoint))
										frontierPoints.add(nextPoint);
								}
							}
						}
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
		for(int i = 0; i < grid.length; i++)
			for(int j = 0; j < grid.length; j++)
				if(grid[i][j] == FLOOR){
					avgcount++;
					avg[0] += i;
					avg[1] += j;
				}
		if(avgcount > 0){
		avg[0] /= avgcount;
		avg[1] /= avgcount;
		}
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
	
	public int distanceToGoal(int[] pos){
		int a = pos[0] - goal[0];
		int b = pos[1] - goal[1];
		return a*a+b*b;
//		return distanceToGoal;
	}

	private void initialize() {
		DEBUG.println("Initializing", DEBUG.LOW);
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[0].length; j++)
				grid[i][j] = UNSEEN;
	}
	
	public int value(int[] pos){
		int count = 0;
		for(int i = -1; i <= 1; i++)
			for(int j = -1; j <= 1; j++)
				if(valueAt(new int[]{pos[0]+i, pos[1]+j}) == UNSEEN)
					count++;
		
//		count += distanceToClosestFrontier(pos);
		return count;
//		if(frontierPoints.contains(new ComparablePoint(new int[] {pos[0]+i, pos[1]+j})))
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
				&& pos[1] <= grid[0].length)
			return grid[pos[0]][pos[1]];
		return -1;
	}
	
	public int[] getPosition(){
		return pos;
	}
	
	public boolean atFrontier(int[] position)
	{
		if (valueAt(position) == UNSEEN)
		{
			for (int i = 1; i < 9; ++i)
			{
				if (valueAt(nextPos(position, i)) == FLOOR)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public int distanceToClosestFrontier()
	{
		return distanceToClosestFrontier(this.pos);
	}
	
	public int distanceToClosestFrontier(int[] position)
	{
		if (isFullyExplored())
		{
			return -1;
		}
		int a, b, distance;
		int bestDistance = Integer.MAX_VALUE;
		for (ComparablePoint cp : frontierPoints)
		{
			a = position[0] - cp.position[0];
			b = position[1] - cp.position[1];
			distance = a*a+b*b;
			if (distance < bestDistance)
			{
				bestDistance = distance;
			}
		}
		return bestDistance;
	}
	
/*	public int locateFrontier() {
		distanceToFrontier = -1;
		frontierNodes = new Vector<int[]>();
		toExplore = new Vector<FrontierSearchNode>();
		explored = new Vector<FrontierSearchNode>();
		
		toExplore.add(new FrontierSearchNode(pos, 0));
		while (!toExplore.isEmpty())
		{
//			System.out.println("Exploring...");
			FrontierSearchNode thisNode = toExplore.get(0);
			int[] thisPos = thisNode.position;
			int thisDist = thisNode.distance;
			if (atFrontier(thisPos))
			{
				if (frontierNodes.isEmpty())
				{
					distanceToFrontier = thisDist;
				}
				frontierNodes.add(thisPos);
			}
			if (valueAt(thisPos) == GOAL)
			{
				distanceToGoal = thisDist;
			}
			explored.add(thisNode);
			int nextDist = ++thisDist;
			for (int i = 1; i < 9; ++i)
			{
				int[] newpos = nextPos(thisPos, i);
				if (!explored.contains(new FrontierSearchNode(newpos, 0)) && !toExplore.contains(new FrontierSearchNode(newpos, 0)))
				{
					toExplore.add(new FrontierSearchNode(newpos, nextDist));
				}
			}
			toExplore.remove(0);
		}*/
		
/*		System.out.print("Frontier: ");
		for (int i = 0; i < frontierNodes.size(); ++i)
		{
			int[] frontierPos = frontierNodes.get(i);
			System.out.print("(" + frontierPos[0] + "," + frontierPos[1] + ")   ");
		}
		System.out.println();
		
		return distanceToFrontier;
	}*/
	
	public boolean isFullyExplored()
	{
		return (frontierPoints.isEmpty());
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
	
	private class ComparablePoint
	{
		int[] position;
		
		public ComparablePoint(int[] pos)
		{
			position = pos;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (!(o instanceof ComparablePoint))
			{
				return false;
			}
			ComparablePoint other = (ComparablePoint) o;
			if (other.position[0] == this.position[0] && other.position[1] == this.position[1])
			{
				return true;
			}
			return false;
		}
		
		public String toString()
		{
			return ("(" + position[0] + "," + position[1] + ")");
		}
	}
}
