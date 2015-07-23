package edu.salisbury.core_simulator;

import java.util.HashMap;

import edu.salisbury.photonic.core_simulator.Coordinate;
import edu.salisbury.photonic.core_simulator.CoordinatePair;
import edu.salisbury.photonic.core_simulator.CoreLog;
import edu.salisbury.photonic.core_simulator.LogReader;
import edu.salisbury.photonic.core_simulator.NonDirectionalPairAnalyzer;

public class NodeActivity {
	
	private CoreLog coreLog = new CoreLog();
	private CoreLog currentLog = new CoreLog();
	NonDirectionalPairAnalyzer dominantFlow = new NonDirectionalPairAnalyzer();
	private int currentIndex;
	private int endIndex;
	private int previousIndex = 0;
	private int increment;
	private int[][] nodeActivity;
	public int[] specificNodeActivity;
	private int nodeNumber;
	private Coordinate nodeCoord;
	
	
	public NodeActivity(String filename, int increment, int nodeNumber) {
		coreLog = LogReader.readLogIgnoreRepeaters(filename);
		nodeActivity = new int[16][coreLog.logSize()/increment];
		specificNodeActivity = null;
		currentIndex = increment;
		endIndex = coreLog.logSize() - 1;
		this.increment = increment;
		this.nodeNumber = nodeNumber;
		nodeCoord = numToCoord(this.nodeNumber);
		updateNodeActivity();
	}
	
	public NodeActivity(String filename, int increment, int nodeNumber, int mostActiveNode) {
		coreLog = LogReader.readLogIgnoreRepeaters(filename);
		nodeActivity = new int[16][coreLog.logSize()/increment];
		specificNodeActivity = new int[coreLog.logSize()/increment];
		currentIndex = increment;
		endIndex = coreLog.logSize() - 1;
		this.increment = increment;
		this.nodeNumber = nodeNumber;
		nodeCoord = numToCoord(this.nodeNumber);
		updateNodeActivity();
		filterTheData(mostActiveNode);
	}
	
	private Coordinate numToCoord(int nodeID) {
		Coordinate node;
		if (nodeID < 8) {
			node = new Coordinate(1, nodeID);
		} else {
			node = new Coordinate(2, 16 - 1 - nodeID);
		}
		return node;
	}
	
	private int coordToNum(Coordinate nodeCoordinate) {
		HashMap<Coordinate, Integer> switchingMap =  new HashMap<Coordinate, Integer>();
		for(int i = 0; i < 8; i++)
		{
			switchingMap.put(new Coordinate(1,i), i);
		}
		for(int i = 7; i >= 0; i--)
		{
			switchingMap.put(new Coordinate(2,7-i), 8+i);
		}
		return switchingMap.get(nodeCoordinate);
	}
	
	private void updateNodeActivity() {
		while (currentIndex <= endIndex) {
			currentLog = coreLog.subLog(previousIndex, currentIndex);
			
			getTheNumbers(dominantFlow.analyzeNoString(currentLog));
			
			previousIndex += increment;
			currentIndex += increment;
		}
	}

	private void getTheNumbers(HashMap<CoordinatePair, Integer> analyze) {
		Coordinate currentCoord;
		for (int i = 1; i <= 2; i++) {
			for (int j = 0; j < 8; j ++) {
			currentCoord = new Coordinate(i, j);
				if (analyze.containsKey(new CoordinatePair(nodeCoord, currentCoord))) {
					
					nodeActivity[coordToNum(currentCoord)][previousIndex/increment] = analyze.get(new CoordinatePair(nodeCoord, currentCoord));
					
				} else if (analyze.containsKey(new CoordinatePair(currentCoord, nodeCoord))) {
					
					nodeActivity[coordToNum(currentCoord)][previousIndex/increment] = analyze.get(new CoordinatePair(currentCoord, nodeCoord));
					
				} else {//key is null (not in the hash map)
					nodeActivity[coordToNum(currentCoord)][previousIndex/increment] = 0;
				}
			}
		}
	}
	
	private void filterTheData(int nodeToFocus) {
		for (int i = 0; i < coreLog.logSize()/increment; i ++) {
			specificNodeActivity[i] = nodeActivity[nodeToFocus][i];
		}
	}
	
	public int[][] getAllNodeActivity() {
		return nodeActivity;
	}
	
	public int getNodeNumber() {
		return nodeNumber;
	}
	
	public void printSpecificNodeActivity() {
		for (int i = 0; i < specificNodeActivity.length; i++) {
			System.out.println("Increment of Log = " + i + ": Messages sent/received = " + specificNodeActivity[i]);
		}
	}
	
	public void printNodeActivity() {
		int nodeID = 0;
		for (int[] row: nodeActivity) {
			for (int col = 0; col < coreLog.logSize()/increment; col++) {
				System.out.println("Node" + nodeID + ": " + row[col]);
			}
			nodeID ++;
		}
	}
	
	public static void printArray(int[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.println("Increment of Log = " + i + ": Messages sent/received = " + array[i]);
			}
	}

}//end class
