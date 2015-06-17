package edu.salisbury.core_simulator;

import java.util.ArrayList;

/**
 * Basic building block of the simulator. For the centralized ONoC
 * there is a head core (HC) which controls the traffic flow
 * of the packets between the body cores (BC).
 * 
 * In this class, the head core is called the Core Node.
 * 
 * @author malonem3
 * 
 */
public class CoreNode {

	private final int NUMBER_OF_NODES = 16;
	//private final int MAX_NUMBER_OF_ACTIVE_TASKS = NUMBER_OF_NODES / 2;
	private final String COUNTER_CLOCKWISE = "counter clockwise";
	private final String CLOCKWISE = "clockwise";
	public int delay = 0;
	
	public class CurrentTask {
		public int sourceNode;
		public int destinationNode;
		public int clockCyclesRemaining;
		public String routeDirection;

		CurrentTask(int sn, int dn, int ccr, String d) {
			this.sourceNode = sn;
			this.destinationNode = dn;
			this.clockCyclesRemaining = ccr;
			this.routeDirection = d;
		}//end CurrentTask constructor
		
	}// end CurrentTask class

	private void updateTaskQueue(ArrayList<CurrentTask> activeTasks) {
		for (int i = 0; i < activeTasks.size(); i++) {
			if (activeTasks.get(i).clockCyclesRemaining <= 0) {
				MainTest.activeTasks.remove(i);
			}
		}
	}

	public String findRouteForPacket(int[] currentLogsInfo, ArrayList<CurrentTask> activeTasks) {
		//assumes source node is free
		ArrayList<Integer> busyNodes = new ArrayList<>();
		boolean waiting = true;
		delay = 0; //reset delay
		while (waiting) {
			if (activeTasks.size() == 0) {//all nodes are free
				return COUNTER_CLOCKWISE;
			}
			
			busyNodes = getBusyNodes(activeTasks);
			for (int node: busyNodes){
				if (currentLogsInfo[1] == node) {//if destNode is busy, then wait
					waiting = true;
					delay++;
					forwardAClockCycle(activeTasks); //advance one clock cycle, active tasks transfer 1b
					break;
				} else { waiting = false; }//destNode is not busy, so need to find an open path
			} //end for
		}
		waiting = true;
		while (waiting) {//now, need to check for a path
			if(clockWiseRouteAvailable(currentLogsInfo, busyNodes)) { return CLOCKWISE; }
			if(counterClockwiseRouteAvailable(currentLogsInfo, busyNodes)) { return COUNTER_CLOCKWISE; }
			waiting = true; //has not returned a direction, so run through the loop again
			delay++;
			forwardAClockCycle(activeTasks); //advance one clock cycle, active tasks transfer 1b
			busyNodes = getBusyNodes(activeTasks); //check for busy nodes again
		}//end while
		return null;
	}

	private boolean counterClockwiseRouteAvailable(int[] currentLogsInfo, ArrayList<Integer> busyNodes) {
		int i = currentLogsInfo[0];
		while(i != currentLogsInfo[1]) {
			for(int node: busyNodes) {
				if (i == node) { return false; }
			}//end for
			i = iterateOnceCounterClockwise(i);
		}//end while
			
		return true;
	}
	
	private boolean clockWiseRouteAvailable(int[] currentLogsInfo, ArrayList<Integer> busyNodes) {
		int i = currentLogsInfo[0];
		while(i != currentLogsInfo[1]) {
			for(int node: busyNodes) {
				if (i == node) { return false; }
			}//end for
			i = iterateOnceClockwise(i);
		}//end while
			
		return true;
	}

	public void forwardAClockCycle(ArrayList<CurrentTask> activeTasks) {
		for (CurrentTask task: activeTasks){
			task.clockCyclesRemaining--;
		}
		updateTaskQueue(activeTasks);
	}

	private ArrayList<Integer> getBusyNodes(ArrayList<CurrentTask> activeTasks) {
		ArrayList<Integer> busyNodes = new ArrayList<>();
		for (CurrentTask task: activeTasks) {
			int currentNode = task.sourceNode;
			while (currentNode != task.destinationNode) {
				busyNodes.add(currentNode);
				
				if (task.routeDirection.equals(CLOCKWISE)) {
					currentNode = iterateOnceClockwise(currentNode);
				} else {//counter clockwise direction 
					currentNode = iterateOnceCounterClockwise(currentNode);
				}//end else-if
				busyNodes.add(task.destinationNode); //need to add in the last busy node
			}//end while
			
		}//end for
		return busyNodes;
	}
	
	private int iterateOnceClockwise(int i) {
		if (i == NUMBER_OF_NODES/2) { i = NUMBER_OF_NODES; } //start to reverse
		else if (i == NUMBER_OF_NODES/2 + 1) { i = 1; } //start at the top again
		else if (i < NUMBER_OF_NODES/2) { i++; } //top half of circle 
		else if (i > NUMBER_OF_NODES/2 ) { i--; } //bottom half of circle
		
		return i;
	}
	
	private int iterateOnceCounterClockwise(int i) {
		if (i == 1) { i = NUMBER_OF_NODES/2 + 1; } //start to reverse
		else if (i == NUMBER_OF_NODES) { i = NUMBER_OF_NODES/2; } //start to repeat
		else if (i <= NUMBER_OF_NODES/2) { i--; }//top half of circle
		else if (i > NUMBER_OF_NODES/2) { i++; } //bottom half of circle
		
		return i;
	}

	public int[] getNextLog(CoreLog tasksToStart, int taksStarted) {
		int[] infoNeeded = new int[3];
		LogEntry currentLog = tasksToStart.getEntry(taksStarted);
		infoNeeded[0] = getNodeID(currentLog.sourceX(), currentLog.sourceY());
		infoNeeded[1] = getNodeID(currentLog.destX(), currentLog.destY());
		infoNeeded[2] = currentLog.packetSize();
		return infoNeeded;
	}

	private int getNodeID(int sourceX, int sourceY) {
		if (sourceX == 1) {
			return sourceY + 1;
		} else { //assume sourceX == 2
			return sourceY + 1 + 8;
		}
	}

	public boolean tasksStillRunning(ArrayList<CurrentTask> activeTasks) {
		if (activeTasks.isEmpty()) {//no more active tasks
			return false;
		} else {//some tasks still running
			return true;
		}
	}

	public boolean hasNextLog(CoreLog tasksToStart, int tasksStarted) {
		if(tasksToStart.logSize() - tasksStarted > 0) {//still have tasks to start
			return true;
		} else {//no more tasks to start
			return false;
		}
	}

}// end CoreNode class
