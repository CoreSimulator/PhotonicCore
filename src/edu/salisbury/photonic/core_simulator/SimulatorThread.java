package edu.salisbury.photonic.core_simulator;

import java.util.HashMap;

import edu.salisbury.photonic.cyclical_core_simulator.CyclicalSimOverseer;

public class SimulatorThread implements Runnable{
	String topology;
	int flitPacketSize;
	int tearDownTime;
	
	public SimulatorThread(String topology, int flitPacketSize, int tearDownTime) {
		this.topology = topology;
		this.flitPacketSize = flitPacketSize;
		this.tearDownTime = tearDownTime;
	}
	
	public void run() {
		CoreLog basicLog = LogReader.readLogIgnoreRepeaters("flow_barnes.log");
		
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
		dominantFlowMap.put(new Coordinate(1,1), new Coordinate(2,6));
		dominantFlowMap.put(new Coordinate(2,6), new Coordinate(1,1));
		dominantFlowMap.put(new Coordinate(1,5), new Coordinate(2,5));
		dominantFlowMap.put(new Coordinate(2,5), new Coordinate(1,5));
		dominantFlowMap.put(new Coordinate(1,6), new Coordinate(2,7));
		dominantFlowMap.put(new Coordinate(2,7), new Coordinate(1,6));
		dominantFlowMap.put(new Coordinate(2,0), new Coordinate(2,4));
		dominantFlowMap.put(new Coordinate(2,4), new Coordinate(2,0));
		
		//Select the topology to simulate
		switch(topology) {
			case "Ring":
				CyclicalSimOverseer test = new CyclicalSimOverseer(flitPacketSize, tearDownTime, switchingMap, dominantFlowMap);
				test.simulateWithLog(basicLog);
				break;
			default:
				CyclicalSimOverseer test1 = new CyclicalSimOverseer(flitPacketSize, tearDownTime, switchingMap);
				test1.simulateWithLog(basicLog);
				break;
		}//end switch
		String message = "Total requesting time = " + MainGUI.totalRequestingTime + " | Total tasks = " + MainGUI.totalTasks + " | Total latency = " + (MainGUI.totalRequestingTime - (2*MainGUI.totalTasks) +
				" | Log Size = " + basicLog.logSize());
		//MainGUI.printToConsole(message);
		System.out.println(message);
		MainGUI.totalTasks = 0;
		MainGUI.totalRequestingTime = 0;
	}
	
}