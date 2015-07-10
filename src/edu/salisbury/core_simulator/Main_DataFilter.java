package edu.salisbury.core_simulator;

import edu.salisbury.photonic.core_simulator.CoreLog;
import edu.salisbury.photonic.core_simulator.LogEntry;
import edu.salisbury.photonic.core_simulator.LogReader;

//import java.util.ArrayList;

public class Main_DataFilter {

	public static final String FILENAME = "flow_barnes.log";
	
	/**
	 * @param args
	 */	
	public static void main(String[] args) {
		
		CoreLog coreLog = new CoreLog();
		coreLog = LogReader.readLogIgnoreRepeaters(FILENAME);
		
		LogEntry currentEntry;
		//final int DOMINANT_NODE_ID = 13;
		int numberOfNodes = 16;
		//ArrayList<Integer> nodesInteractingWithDominant = new ArrayList<>();
		//nodesInteractingWithDominant = initList(numberOfNodes);
		int sourceNodeID;
		int destNodeID;
		
		for (int node = 1; node <= numberOfNodes; node++) {
			int numOfDominantInteractions = 0;
			double numOfNonInteractions = 0;
			for (int i = 0; i < coreLog.logSize(); i++) {
				currentEntry = coreLog.getEntry(i);
				sourceNodeID = nodeID(currentEntry.sourceX(), currentEntry.sourceY());
				destNodeID = nodeID(currentEntry.destX(), currentEntry.destY());
				
				if (sourceNodeID == node) {
					//nodesInteractingWithDominant.set(destNodeID, destNodeID);
					numOfDominantInteractions ++;
				} else if (destNodeID == node) {
					//nodesInteractingWithDominant.set(sourceNodeID, sourceNodeID);
					numOfDominantInteractions ++;
				} else {
					numOfNonInteractions ++;
				}
			}//end for
			double dominantDensity = (numOfDominantInteractions/(numOfDominantInteractions + numOfNonInteractions)/2)*100;
			//printList(nodesInteractingWithDominant);
			System.out.println("Node ID: " + node + " Dominant node activity density = " + dominantDensity);
		}
		
	}//end main
	
//	private static ArrayList<Integer> initList(int numberOfNodes) {
//		ArrayList<Integer> fullOfZeros = new ArrayList<>();
//		for (int i = 0; i < numberOfNodes + 1; i ++) {
//			fullOfZeros.add(0);
//		}
//		return fullOfZeros;
//	}
//
//	private static void printList(ArrayList<Integer> nodesInteractingWithDominant) {
//		for (int i = 0; i < nodesInteractingWithDominant.size(); i ++) {
//			if (nodesInteractingWithDominant.get(i) != 0){
//				System.out.println("Node ID: " + nodesInteractingWithDominant.get(i));
//			}
//		}
//	}

	public static int nodeID(int x, int y) {
		if (x == 1) {
			return y + 1;
		} else if (x == 2) {
			return y + 1 + 8;
		} else {
			return -1;
		}
		
	}

}
