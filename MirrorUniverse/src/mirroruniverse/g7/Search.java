package mirroruniverse.g7;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/* 2-level BFS search
 * The algorithm works on two levels. First it searches for final states.
 * The final states are not expanded but are kept in a separate queue.
 * When the 1st level search space is exhausted  then the final states are
 * expanded to reach the final state. Returns the one with the smallest path.
 * This guarantees for our problem that we will get an optimal solution.
 * For our problem a final state is defined to be when at least one player
 * has reached the exit point. The path length of final states directly
 * corresponds to the difference at which the players exit the board.
 * So when one reaches his exit he is not expanded anymore until we get
 * all those states than one has exited. Then the second one moves to reach
 * his destination and the path length is the difference between terminations.
 * If two final states have same length in the 2nd level of the search, the
 * first one in the queue is the winner as it was produced earlier from the
 * 1st level search therefore it took less moves to get there.
 * A small optimization is that we may immediately terminate if we know
 * that a path state is optimal. When an optimal path state is encountered
 * during the first level of the search, we keep only that as the final set.
 */

public class Search {

	/* Solve the problem */
	public static State[] solve(State startState) {
		/* Set of states to be expanded */
		IntQueue openSet = new IntQueue();
		/* Keep a mapping between child and parent state
		 * We need this for 2 reasons:
		 *   1) To know the states we do not
		 *      want to place in open set again
		 *   2) To re-create the traversed path
		 *      once we reach the bottom final state
		 */
		int allStates = State.allStates();
		byte nullCode = State.nullMoveCode;
		byte[] closedSet = new byte [allStates];
		for (int i = 0 ; i != allStates ; ++i)
			closedSet[i] = nullCode;
		/* Keep all states when one has reached partial goal
		 * We will not expand these states until we have
		 * exhausted the search space to find them all
		 * We will terminate the search if during the
		 * first level of search we find the optimal
		 */
		Queue <State> finalSet = new LinkedList <State> ();
		/* Allocate space for next states and directions */
		byte[] moves = new byte [State.maxNextStates];
		State[] nextStates = new State [State.maxNextStates];
		for (int i = 0 ; i != nextStates.length ; ++i) {
			moves[i] = nullCode;
			nextStates[i] = new State();
		}
		/* Add first state in the open and closed set */
		openSet.push(startState.id());
		closedSet[startState.id()] = State.unusedMoveCode;
		State currentState = new State();
		do {
			/* Get state from open set */
			State.byId(currentState, openSet.pop());
			/* Check if one of final state */
			if (currentState.isFinal()) {
				finalSet.offer(new State(currentState));
				/* If you find the optimal final state */
				if (currentState.isOptimalFinal()) {
					/* Clear the whole final set
					 * and add only this element
					 */
					finalSet.clear();
					finalSet.offer(new State(currentState));
					break;
				}
			} else {
				/* Expand state and examine its children */
				int validNextStates = currentState.getNext(moves, nextStates);
				for (int i = 0 ; i != validNextStates ; ++i) {
					int id = nextStates[i].id();
					if (closedSet[id] == nullCode) {
						openSet.push(id);
						closedSet[id] = moves[i];
					}
				}
			}
			/* Terminate when the open set is empty
			 * This indicates we failed to solve it
			 */
		} while (!openSet.isEmpty());
		/* The one with the least moves to optimal wins */
		State[] bestPartialPath = null;
		/* Search for each final move the path to the goal */
		do {
			State partialGoal = finalSet.poll();
			/* Start from the final state */
			openSet.push(partialGoal.id());
			do {
				/* Get state from open set */
				State.byId(currentState, openSet.pop());
				/* If state if final */
				if (currentState.isGoal()) {
					/* Keep the smallest partial path */
					State[] partialPath = path(partialGoal,
					                           new State(currentState),
					                           closedSet);
					if (bestPartialPath == null ||
					    partialPath.length < bestPartialPath.length)
						bestPartialPath = partialPath;
					break;
				}
				/* Expand state and examine its children
				 * Same as first level search
				 */
				int validNextStates = currentState.getNext(moves, nextStates);
				for (int i = 0 ; i != validNextStates ; ++i) {
					int id = nextStates[i].id();
					if (closedSet[id] == nullCode) {
						openSet.push(id);
						closedSet[id] = moves[i];
					}
				}
			/* Normally the open set will never be emptied */
			} while (!openSet.isEmpty());
		/* Check all final states of the set in FIFO order
		 * FIFO ensures that ties take the fastest one
		 */
		} while (!finalSet.isEmpty());
		/* If no solution found */
		if (bestPartialPath == null)
			return null;
		/* Keep last state and re-create whole path */
		State lastState = bestPartialPath[bestPartialPath.length-1];
		return path(startState, lastState, closedSet);
	}

	/* Re-create the solution path */
	private static State[] path(State start, State end,
	                            byte[] closedSet) {
		/* A stack to reverse the path */
		Stack <State> pathStack = new Stack <State> ();
		/* Get first point */
		pathStack.push(end);
		/* Traverse the search space up by using the closed set */
		while (!start.equals(end)) {
			end = State.retrace(end, closedSet[end.id()]);
			pathStack.push(end);
		}
		/* Empty the stack and create an array of states */
		int i = 0;
		State[] path = new State [pathStack.size()];
		do {
			path[i++] = pathStack.pop();
		} while (!pathStack.isEmpty());
		return path;
	}
}
