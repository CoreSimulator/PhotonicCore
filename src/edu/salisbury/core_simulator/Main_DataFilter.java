package edu.salisbury.core_simulator;

import java.util.Scanner;

import edu.salisbury.photonic.core_simulator.CoreLog;
import edu.salisbury.photonic.core_simulator.LogReader;
import edu.salisbury.photonic.core_simulator.NonDirectionalPairAnalyzer;

import java.util.ArrayList;

public class Main_DataFilter {

	private static CoreLog coreLog = new CoreLog();
	private static final String FILENAME = "flow_barnes.log";
	//private static final Scanner cin = new Scanner(System.in);
	private static final int INCREMENT = 10000;
	private static ArrayList<NodeActivity> allNodeActivity = new ArrayList<>(15);
	private static final int MOST_ACTIVE_NODE = 1;
	private static int[][] absoluteMaxs;
	private static int[][] relativeMaxs;
	
	/**
	 * @param args
	 * @return 
	 */	
	public static void main(String[] args) {
		System.out.println("Gathering data...\n\n");
		coreLog = LogReader.readLogIgnoreRepeaters(FILENAME);
		absoluteMaxs = new int[coreLog.logSize()/INCREMENT][16];
		relativeMaxs = new int[coreLog.logSize()/INCREMENT][16];
		initAllNodeActivityArrayList();
		
		int[] currentNodesInfo;
		int[] currentNodesRelInfo;
		int nodeID = 0;
		
		System.out.println("Printing results.... \n\n");
		for (NodeActivity node: allNodeActivity) {
			 currentNodesInfo = findAbsoluteMax(node.specificNodeActivity);
			 absoluteMaxs[currentNodesInfo[0]][nodeID] = currentNodesInfo[1]; 
			 currentNodesRelInfo = findRelativeMax(node.specificNodeActivity);
			 for (int i = 0; i < currentNodesRelInfo.length; i ++) {
				 relativeMaxs[i][nodeID] = currentNodesRelInfo[i];
			 }
			nodeID ++;
		}
		
		System.out.println("The absolute maximum of messages sent/received in communication with node1 (most active node): ");
		printAbsoluteMaxsArray();
		System.out.println("\n\nThe relative maxima of messages sent/received in communication with node1 (most active node): ");
		printRelativeMaxsArray();
	}//end main
	
	private static void initAllNodeActivityArrayList() {
		for (int i = 0; i < 16; i ++) {
			if (i != MOST_ACTIVE_NODE) {
				allNodeActivity.add(new NodeActivity(FILENAME, INCREMENT, i, MOST_ACTIVE_NODE));
			}
		}
	}
	
	private static int[] findAbsoluteMax(int[] specificNodeActivity) {
		int prevNumOfMessages = 0;
		int currentNumOfMessages = 0;
		int nextNumOfMessages = 0;
		int absoluteMaxValue = 0;
		int absoluteMax = 0;
		int[] absMaxInfo = new int[2];
		
		for (int i = 0; i < specificNodeActivity.length; i ++) {
			prevNumOfMessages = currentNumOfMessages;
			currentNumOfMessages = specificNodeActivity[i];
			if (currentNumOfMessages > prevNumOfMessages && i != specificNodeActivity.length - 1) {
				nextNumOfMessages = specificNodeActivity[i+1];
				if (currentNumOfMessages > nextNumOfMessages) {
					if (currentNumOfMessages > absoluteMaxValue) { 
						absoluteMaxValue = currentNumOfMessages; 
						absoluteMax = i;
					}
				}
			} else if (currentNumOfMessages > prevNumOfMessages && i == specificNodeActivity.length - 1) {
				if (currentNumOfMessages > absoluteMaxValue) {
					absoluteMaxValue = currentNumOfMessages;
					absoluteMax = i;
				}
			}
		}
		
		absMaxInfo[0] = absoluteMax;
		absMaxInfo[1] = absoluteMaxValue;
		
		return absMaxInfo;
	}
	
	private static int[] findRelativeMax(int[] specificNodeActivity) {
		int prevNumOfMessages = 0;
		int currentNumOfMessages = 0;
		int nextNumOfMessages = 0;
		int relativeMaxValue = 0;
		int relativeMax = 0;
		int[] relMaxInfo = new int[coreLog.logSize()/INCREMENT];
		
		for (int i = 0; i < specificNodeActivity.length; i ++) {
			prevNumOfMessages = currentNumOfMessages;
			currentNumOfMessages = specificNodeActivity[i];
			if (currentNumOfMessages > prevNumOfMessages && i != specificNodeActivity.length - 1) {
				nextNumOfMessages = specificNodeActivity[i+1];
				if (currentNumOfMessages > nextNumOfMessages) { 
					relativeMaxValue = currentNumOfMessages; 
				}
			} else if (currentNumOfMessages > prevNumOfMessages && i == specificNodeActivity.length - 1) {
				relativeMaxValue = currentNumOfMessages;
			} else {
				relativeMaxValue = 0;
			}
			relativeMax = i;
			
			relMaxInfo[relativeMax] = relativeMaxValue;
		}
		
		return relMaxInfo;
	}
	
	private static void printAbsoluteMaxsArray() {
		int nodeID = 0;
		int logIncrement = 1;
		for (int[] row: absoluteMaxs) {
			System.out.println("Log Increment: " + logIncrement + " ...");
			for (int col = 0; col < row.length; col++) {
				if (row[col] > 0) {
					if (nodeID < MOST_ACTIVE_NODE) {
						System.out.println("Node" + nodeID + ": " + "Absolute Max Value = " + row[col] + "\t");
					} else {
						System.out.println("Node" + (nodeID + 1) + ": " + "Absolute Max Value = " + row[col] + "\t");
					}
				}
				nodeID ++;
			}
			nodeID = 0;
			logIncrement ++;
			System.out.println();
		}
	}
	
	private static void printRelativeMaxsArray() {
		int nodeID = 0;
		int logIncrement = 1;
		for (int[] row: relativeMaxs) {
			System.out.println("Log Increment: " + logIncrement + " ...");
			for (int col = 0; col < row.length; col++) {
				if (row[col] > 0) {
					if (nodeID < MOST_ACTIVE_NODE) {
						System.out.println("Node" + nodeID + ": " + "Relative Max Value = " + row[col] + "\t");
					} else {
						System.out.println("Node" + (nodeID + 1) + ": " + "Relative Max Value = " + row[col] + "\t");
					}
				}
				nodeID ++;
			}
			nodeID = 0;
			logIncrement ++;
			System.out.println();
		}
	}
	
}//end class