package edu.salisbury.core_simulator;

import java.util.ArrayList;

import edu.salisbury.core_simulator.CoreNode.CurrentTask;


public class MainTest {
	
	public static final String FILENAME = "flow_barnes.log";
	public static final String DATA_OUTPUT = "data_output1.txt";
	public static final float NUMBER_OF_LOGS = 62455;
	public static ArrayList<CurrentTask> activeTasks = new ArrayList<>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Will be used to output log data
		StringBuilder outputData = new StringBuilder();
		
		//Create the head core (core node)
		CoreNode headCore = new CoreNode();
		
		//Get logs for head core; logs become tasks once they have been started (ie activeTasks)
		CoreLog tasksToStart = new CoreLog();
		tasksToStart = LogReader.readLogIgnoreRepeaters(FILENAME);
		int tasksStarted = 0;
		
		//[0]: Source Node, [1]: Destination Node, [2]: Packet Size
		int[] currentLogsInfo = new int[3];
		
		int dataTransferDuration; 
		
		//TODO need to implement T(task) = T(waiting) + T(request) + T(approved) + T(data) + T(teardown) [cc]
		//where T(task) = time for task to complete, T(waiting) = headCore.delay, T(request) = ~1cc
		//T(approved) = ~1cc, T(data) = dataTransferDuration, T(teardown) = ~1cc
		//Very first task would be sequential, every task after that (except very last task) would be in parallel
		//very last task would be sequential again
		int clockCycle = 0; //(some parts sequential, some parts in parallel)
		
		int numberOfIterations = 0;
		float estimatedPercentComplete;
		
		//Main loop (executes the simulation until all logs have become completed tasks)
		while (headCore.hasNextLog(tasksToStart, tasksStarted) || headCore.tasksStillRunning(activeTasks)) {
			clockCycle ++;
			numberOfIterations ++;
			
			//HC: Phase1 = Subtask distribution
			if(tasksToStart.logSize() > tasksStarted){
				currentLogsInfo = headCore.getNextLog(tasksToStart, tasksStarted);
				tasksStarted ++;
				//Main: how long task will take based on 1 flit = 32b, and 1b transmitted per clock cycle (cc)
				dataTransferDuration = howLongIsDataTransfer(currentLogsInfo[2]);
				
				 //HC: Phase2 = Subtask processing and forwarding
				//TODO GET CLOCK CYLCE REPRESENTATION WORKING PROPERLY!!!!!!!
				String routeDirection = headCore.findRouteForPacket(currentLogsInfo, activeTasks);
				//HC: Queue active tasks
				activeTasks.add(0, headCore.new CurrentTask(currentLogsInfo[0], currentLogsInfo[1], dataTransferDuration, routeDirection));	
				clockCycle += headCore.delay; //comes from waiting on route to deliver packet (busy network)
			}

			
			//HC: Phase3 = Final result delivery 
			//(Update task info and reset nodes if task is complete)
			headCore.forwardAClockCycle(activeTasks);
			
			//Main: gather data to be displayed
			estimatedPercentComplete =  (tasksStarted/NUMBER_OF_LOGS)*100;
			System.out.println("Simulating...        (" + estimatedPercentComplete + "%)");
			outputData.append(buildStringOfData(clockCycle, headCore.delay, numberOfIterations, activeTasks));
			
		}//end while
		
		//Output data to a file
		LogReader.writeToFile(DATA_OUTPUT, outputData.toString());
		
	}//end main
	
	private static String buildStringOfData(int clockCycle, int delay,
			int numberOfIterations, ArrayList<CurrentTask> activeTasks) {
		String cycle = "Cycle of Simulation:     " + numberOfIterations;
		String clock = "Clock Cycle:             " + clockCycle;
		String delayCycle = "Delay for this cycle:    " + delay;
		String nextLine = "\n"; //extra space
		String logActivity = cycle + nextLine + clock + nextLine + delayCycle + nextLine;
		String printTasks = printActiveTasks(activeTasks);
		String spacer = "---------------------------\n";
		String endOfLog = nextLine + spacer;
		return logActivity + printTasks + endOfLog;
		
	}

	private static String printActiveTasks(ArrayList<CurrentTask> activeTasks) {
		int i = 1;
		StringBuilder printTasks = new StringBuilder();
		for (CurrentTask task: activeTasks) {
			String index = i + ".";
			String sNode = "Source Node: " + task.sourceNode;
			String dNode = " | Destination Node: " + task.destinationNode;
			String clock = " | Clock Cycles remaining: " + task.clockCyclesRemaining;
			String direction = " | Direction: " + task.routeDirection;
			String nextLine = "\n"; //extra space
			i++;
			printTasks.append(index + sNode + dNode + clock + direction + nextLine);
		}
		return printTasks.toString();
	}
	
//	private static void logActivityToConsole(int clockCycle, int delay,
//			int numberOfIterations, ArrayList<CurrentTask> activeTasks) {
//		System.out.println("Cycle of Simulation:     " + numberOfIterations);
//		System.out.println("Clock Cycle:             " + clockCycle);
//		System.out.println("Delay for this cycle:    " + delay);
//		System.out.println(""); //extra space
//		printActiveTasksToConsole(activeTasks);
//		System.out.println("---------------------------\n");
//	}
	
//	private static void printActiveTasksToConsole(ArrayList<CurrentTask> activeTasks) {
//		int i = 1;
//		for (CurrentTask task: activeTasks) {
//			System.out.println(i + ".");
//			System.out.println("Source Node: " + task.sourceNode);
//			System.out.println(" | Destination Node: " + task.destinationNode);
//			System.out.println(" | Clock Cycles remaining: " + task.clockCyclesRemaining);
//			System.out.println(" | Direction: " + task.routeDirection);
//			System.out.println(""); //extra space
//			i++;
//		}
//	}

	private static int howLongIsDataTransfer(int packetSize) {
		//packet size = either 1 or 5
		return 32*packetSize; //32 optic clock cycles for a flit equal to 32b (1b/cc)
	}
	
}//end main class
