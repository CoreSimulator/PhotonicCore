package edu.salisbury.photonic.simulation_gui;

import java.util.HashMap;

import edu.salisbury.photonic.core_simulator.Coordinate;
import edu.salisbury.photonic.core_simulator.CoreLog;
import edu.salisbury.photonic.cyclical_core_simulator.CyclicalSimOverseer;

public class SimulatorThread implements Runnable{
	String topology;
	int flitPacketSize;
	int tearDownTime;
	CoreLog basicLog;
	HashMap<Coordinate, Coordinate> dominantFlowMap;
	int[] defaultValues = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
	
	public SimulatorThread(String topology, int flitPacketSize, int tearDownTime, CoreLog basicLog, int[] nodeArrangment) {
		this.topology = topology;
		this.flitPacketSize = flitPacketSize;
		this.tearDownTime = tearDownTime;
		this.basicLog = basicLog;
		if (nodeArrangment == null) {
			dominantFlowMap = createHashMap(defaultValues);
		} else {
			dominantFlowMap = createHashMap(nodeArrangment);
		}
	}
	
	public void run() {
		
		HashMap<Coordinate, Integer> switchingMap =  new HashMap<Coordinate, Integer>();
		for(int i = 0; i < 8; i++)
		{
			switchingMap.put(new Coordinate(1,i), i);
		}
		for(int i = 7; i >= 0; i--)
		{
			switchingMap.put(new Coordinate(2,7-i), 8+i);
		}
		
		//default for 0 mrrSwitches used
		int[] mrrSwitchesTopLeftNodeNumber = {-1};
		
		//Select the topology to simulate
		switch(topology) {
			case "Ring":
				CyclicalSimOverseer test = new CyclicalSimOverseer(flitPacketSize, tearDownTime, 
						switchingMap, dominantFlowMap, mrrSwitchesTopLeftNodeNumber, false); //TODO change back to true
				test.simulateWithLog(basicLog);
				break;
			default:
				CyclicalSimOverseer test1 = new CyclicalSimOverseer(
						flitPacketSize, tearDownTime, switchingMap, null, 
						mrrSwitchesTopLeftNodeNumber, false); //TODO change back to true
				
				test1.simulateWithLog(basicLog);
				break;
		}//end switch
		
	}

	private HashMap<Coordinate, Coordinate> createHashMap(int[] nodeArrangement) {
		// TODO Auto-generated method stub
		HashMap<Coordinate, Coordinate> dominantFlowMap = new HashMap<Coordinate, Coordinate>();
		for (int position = 0; position < nodeArrangement.length; position ++) {
			if (nodeArrangement[position] < 8 && position < 8) {//first row swaps
				dominantFlowMap.put(new Coordinate(1,nodeArrangement[position]), new Coordinate(1,position));
			} else if (nodeArrangement[position] < 8 && position >= 8) {//first row and second row swap
				dominantFlowMap.put(new Coordinate(1,nodeArrangement[position]), new Coordinate(2,nodeArrangement.length - 1 -position));
			} else if (nodeArrangement[position] >= 8 && position < 8) {//second row and first row swap
				dominantFlowMap.put(new Coordinate(2,nodeArrangement.length - 1 -nodeArrangement[position]), new Coordinate(1,position));
			} else if (nodeArrangement[position] >= 8 && position >= 8) {
				dominantFlowMap.put(new Coordinate(2,nodeArrangement.length - 1 -nodeArrangement[position]), new Coordinate(2,nodeArrangement.length - 1 -position));
			}
		}
		return dominantFlowMap;
	}
	
}