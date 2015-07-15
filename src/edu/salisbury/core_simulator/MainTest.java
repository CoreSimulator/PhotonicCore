package edu.salisbury.core_simulator;

import java.util.HashMap;

import edu.salisbury.photonic.cyclical_core_simulator.CyclicalSimOverseer;
import edu.salisbury.photonic.core_simulator.Coordinate;
import edu.salisbury.photonic.core_simulator.CoreLog;
import edu.salisbury.photonic.core_simulator.LogReader;

public class MainTest {

	public static int totalRequestingTime = 0;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
		int[] mrrSwitchesTopLeftNodeNumbers = null;
		CyclicalSimOverseer test = new CyclicalSimOverseer(64, 1, switchingMap, mrrSwitchesTopLeftNodeNumbers);
		test.simulateWithLog(basicLog);
		
		System.out.println(totalRequestingTime - 2*86836);
	}

}