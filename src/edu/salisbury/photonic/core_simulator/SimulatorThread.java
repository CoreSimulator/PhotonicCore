package edu.salisbury.photonic.core_simulator;

import java.util.HashMap;

import edu.salisbury.photonic.cyclical_core_simulator.CyclicalSimOverseer;

public class SimulatorThread implements Runnable{
	String topology;
	int flitPacketSize;
	int tearDownTime;
	CoreLog basicLog;
	
	public SimulatorThread(String topology, int flitPacketSize, int tearDownTime, CoreLog basicLog) {
		this.topology = topology;
		this.flitPacketSize = flitPacketSize;
		this.tearDownTime = tearDownTime;
		this.basicLog = basicLog;
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
		HashMap<Coordinate, Coordinate> dominantFlowMap = new HashMap<Coordinate, Coordinate>();
		dominantFlowMap.put(new Coordinate(1,5), new Coordinate(1,2));
		dominantFlowMap.put(new Coordinate(1,2), new Coordinate(1,5));
		dominantFlowMap.put(new Coordinate(1,6), new Coordinate(2,5));
		dominantFlowMap.put(new Coordinate(2,5), new Coordinate(1,6));
		dominantFlowMap.put(new Coordinate(2,0), new Coordinate(2,2));
		dominantFlowMap.put(new Coordinate(2,2), new Coordinate(2,0));
		//
		int[] mrrSwitchesTopLeftNodeNumber = {-1};
		
		//Select the topology to simulate
		switch(topology) {
			case "Ring":
				CyclicalSimOverseer test = new CyclicalSimOverseer(flitPacketSize, tearDownTime, switchingMap, dominantFlowMap, mrrSwitchesTopLeftNodeNumber);
				test.simulateWithLog(basicLog);
				break;
			default:
				CyclicalSimOverseer test1 = new CyclicalSimOverseer(flitPacketSize, tearDownTime, switchingMap, mrrSwitchesTopLeftNodeNumber);
				test1.simulateWithLog(basicLog);
				break;
		}//end switch
		
	}
	
}