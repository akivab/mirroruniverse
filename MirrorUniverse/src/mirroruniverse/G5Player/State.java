package mirroruniverse.G5Player;

import java.util.ArrayList;

public class State {
	Map m1, m2;
	int[] p1, p2;
	int dir;
	State parent = null;
	public State(Map m1, Map m2){
		this.m1 = m1;
		this.m2 = m2;
		this.p1 = m1.pos;
		this.p2 = m2.pos;
		this.dir = -1;
	}
	
	public State(Map m1, Map m2, int[] p1, int[] p2, int dir, State parent){
		this.m1 = m1;
		this.m2 = m2;
		this.p1 = p1;
		this.p2 = p2;
		this.dir = dir;
		this.parent = parent;
	}
	
	public boolean isFull(){
		return m1.valueAt(p1)== Map.GOAL && m2.valueAt(p2)== Map.GOAL;  
	}
	
	public boolean isPartial(){
		return !isFull() && (m1.valueAt(p1)== Map.GOAL || m2.valueAt(p2)== Map.GOAL); 
	}
	
	public boolean isUnseen(){
		return (m1.valueAt(p1)== Map.UNSEEN || m2.valueAt(p2)== Map.UNSEEN);
	}
	
	public boolean isNotWorthGoingTo(){
		return !isUnseen() && !isFull() && !isPartial();
	}
	
	public static String encode(int[] p1, int[] p2) {
		return "" + p1[0] + "," + p1[1] + "," + p2[0] + "," + p2[1];
	}
	
	public ArrayList<Integer> getDirections(){
		ArrayList<Integer> toreturn = new ArrayList<Integer>();
		State current = this;
		while(current != null){
			if(current.dir != -1)
				toreturn.add(0,current.dir);
			current = current.parent;
		}
		return toreturn;
	}
	
	public ArrayList<State> findNeighbors(){
		ArrayList<State> states = new ArrayList<State>();
		for(int i = 1; i <= 8; i++)
			states.add(new State(m1, m2, m1.nextPos(p1, i), m2.nextPos(p2, i), i, this));
		return states;
	}
	
	public String encoded(){
		return encode(p1, p2);
	}
}
